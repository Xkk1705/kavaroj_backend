package com.xukang.codesandbox;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.xukang.codesandbox.model.*;
import com.xukang.codesandbox.utils.JudgeUtils;
import lombok.extern.slf4j.Slf4j;



import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Docker实现java代码沙箱
 */
@Slf4j
public class JavaDockerCodesSandBox implements CodeSandBox {
    public static void main(String[] args) throws IOException, InterruptedException {
        ExecuteRequest executeRequest = new ExecuteRequest();
        // 从文件中获取code信息
        FileReader fileReader = new FileReader("/home/kang/codesandbox/src/main/java/tsetcode/Main.java");
        String code = fileReader.readString();
        executeRequest.setCode(code);
        JudgeCase judgeCase = new JudgeCase();
        judgeCase.setInput("1 2");
        judgeCase.setInput("2 3");
        ArrayList<JudgeCase> list = new ArrayList<>();
        list.add(judgeCase);
        executeRequest.setInputList(list);
        JavaDockerCodesSandBox javaDockerCodesSandBox = new JavaDockerCodesSandBox();
        ExecuteResponse executeResponse = javaDockerCodesSandBox.executeCode(executeRequest);
    }

    public static final String GLOBAL_USER_PATH_NAME = "usercode";
    public static final String GLOBAL_USER_CODE_NAME = "Main.java";

    // 是否初始化镜像标志
    private Boolean INIT_IMAGE_AND_CONTAINER = false;

    public static final Long TIME_OUT = 1000 * 50L;// 5秒


    @Override
    public ExecuteResponse executeCode(ExecuteRequest executeRequest) throws IOException, InterruptedException {
        // 初始化文件目录
        ExecuteResponse executeResponse = new ExecuteResponse();
        String code = executeRequest.getCode();
        // 获取项目根目录
        String projectPath = System.getProperty("user.dir");
        String globalCodePathName = projectPath + File.separator + GLOBAL_USER_PATH_NAME;
        //用户code父目录
        String userParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        // 用户code目录
        String userCodePath = userParentPath + File.separator + GLOBAL_USER_CODE_NAME;
        // 创建存放code文件夹
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }
        File file = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);//把code写入文件夹下的文件中
        if (!file.exists()) {
            log.info("用户code写入失败");
            throw new RuntimeException("用户代码写入文件失败");
        }
        String image = "openjdk:8-alpine";
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        // 初始化docker客户端
        if (INIT_IMAGE_AND_CONTAINER) {
            // 拉取镜像
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
            //拉取镜像回调函数
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    // 打印完成拉取后的状态
                    System.out.println(item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd
                        .exec(pullImageResultCallback)
                        .awaitCompletion();// 等待完成
                System.out.println("镜像拉取完成");
                INIT_IMAGE_AND_CONTAINER = false; // 初始化完成标志置为false
            } catch (InterruptedException e) {
                log.error("拉取镜像失败");
                throw new RuntimeException(e);
            }

        }
        // 创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(100 * 1000 * 1000L); //限制内存
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpuCount(1L);
//        hostConfig.withSecurityOpts(Arrays.asList("seccomp=安全管理配置字符串"));// 限制权限
        hostConfig.setBinds(new Bind(userParentPath, new Volume("/app"))); // 文件夹映射到docker数据卷目录
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
//                .withCmd("echo", "container crete success!")
                .withNetworkDisabled(true)
                .withReadonlyRootfs(true)
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true) // 交互使用创建的容器 不会重新创建容器
                .exec();
        System.out.println(createContainerResponse);
        // 启动容器
        String containerId = createContainerResponse.getId();
        dockerClient.startContainerCmd(containerId).exec();

        // 封装返回信息
        JudgeInfo judgeInfo = new JudgeInfo();
        // 编译文件
        try {
            //javac "-encoding utf-8" /path/to/output/directory/Main.java
            String compileCommend = "javac" + " -encoding utf-8" + " " + userCodePath;
            // 创建一个新的进程以执行命令
            Runtime runtime = Runtime.getRuntime();

            ExecuteMessage executeMessage = JudgeUtils.execProcess(runtime, compileCommend, "编译");// 编译返回信息
            String output = executeMessage.getOutput();
            Integer status = executeMessage.getStatus();
            Long runTime = executeMessage.getRunTime();
            if (status != 0) {
                judgeInfo.setExeInfo(output);
                judgeInfo.setLimitTime(runTime);
                executeResponse.setJudgeInfo(judgeInfo);
                executeResponse.setMessage("编译失败");
                log.info("编译失败");
                return executeResponse;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        // 执行文件
        ArrayList<ExecuteMessage> executeMessages = new ArrayList<>();
        ArrayList<JudgeCase> outputList = new ArrayList<>();
        try {
            // 获取参数用例
            List<JudgeCase> inputList = executeRequest.getInputList();
            for (JudgeCase judgeCase : inputList) {
                StopWatch stopWatch = new StopWatch();
                String inputArgs = judgeCase.getInput();
                String[] inputArgsArray = inputArgs.split(" ");
                String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"}, inputArgsArray);
                //docekr 执行java字节码文件 在容器中执行终端命令
                ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                        .withCmd(cmdArray)
                        .withAttachStderr(true)
                        .withAttachStdin(true)
                        .withAttachStdout(true)
                        .exec();
                System.out.println("创建执行命令：" + execCreateCmdResponse);
                // 收集返回结果
                ExecuteMessage executeMessage = new ExecuteMessage();
                final String[] message = {null};
                final String[] errorMessage = {null};
                long time = 0L;
                // 判断是否超时
                final boolean[] timeout = {true};
                String execId = execCreateCmdResponse.getId();
                ExecStartResultCallback  execStartResultCallback  = new ExecStartResultCallback() {
                    @Override
                    public void onComplete() {
                        // 如果执行完成，则表示没超时
                        timeout[0] = false;
                        super.onComplete();
                    }

                    @Override
                    public void onNext(Frame frame) {
                        StreamType streamType = frame.getStreamType();
                        if (StreamType.STDERR.equals(streamType)) {
                            errorMessage[0] = new String(frame.getPayload());
                            System.out.println("输出错误结果：" + errorMessage[0]);
                        } else {
                            message[0] = new String(frame.getPayload());
                            System.out.println("输出结果：" + message[0]);
                        }
                        super.onNext(frame);
                    }
                };

                final long[] maxMemory = {0L};

                // 获取占用的内存
                StatsCmd statsCmd = dockerClient.statsCmd(containerId);
                ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {

                    @Override
                    public void onNext(Statistics statistics) {
                        System.out.println("内存占用：" + statistics.getMemoryStats().getUsage());
                        // 在执行程序过程中 内存maxMemory是动态变化的，我们取其中最大的内存数
                        maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(), maxMemory[0]);
                    }

                    @Override
                    public void close() throws IOException {

                    }

                    @Override
                    public void onStart(Closeable closeable) {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
                //启动内存监控
                statsCmd.exec(statisticsResultCallback);
                try {
                    stopWatch.start();
                    dockerClient.execStartCmd(execId)
                            .exec(execStartResultCallback)
                            .awaitCompletion();
                    stopWatch.stop();
                    time = stopWatch.getLastTaskTimeMillis();
                    statsCmd.close();
                } catch (InterruptedException e) {
                    System.out.println("程序执行异常");
                    throw new RuntimeException(e);
                }
                if (StrUtil.isNotBlank(message[0])) {// 输出返回结果
                    executeMessage.setOutput(message[0]);
                    executeMessage.setStatus(0);
                } else {
                    executeMessage.setOutput(errorMessage[0]);
                    executeMessage.setStatus(1);
                }
                executeMessage.setRunTime(time);
                executeMessage.setRunMemory(maxMemory[0]);
                executeMessages.add(executeMessage);
            }


            long maxTime = 0; //统计每次用例最大使用时间
            long maxMemory = 0; //统计每次用例最大使用内存
            for (ExecuteMessage executeMessage : executeMessages) {
                String output = executeMessage.getOutput();
                Integer status = executeMessage.getStatus();
                if (status != 0) {
                    executeResponse.setMessage("运行失败");
                    log.info("运行失败");
                }
                Long runTime = executeMessage.getRunTime();
                Long runMemory = executeMessage.getRunMemory();
                if (runTime > maxTime) {//取最大的运行时间
                    maxTime = runTime;
                }
                if (runMemory > maxMemory) {//取最大的运行时间
                    maxMemory = runMemory;
                }
                executeResponse.setMessage("运行成功");
                JudgeCase judgeCase = new JudgeCase();
                judgeCase.setOutput(output);
                outputList.add(judgeCase);
            }
            judgeInfo.setLimitTime(maxTime);
            judgeInfo.setLimitMemory(maxMemory);
            executeResponse.setOutputList(outputList);
            executeResponse.setJudgeInfo(judgeInfo);
            return executeResponse;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 删除文件
            Path directoryPath = Paths.get(userParentPath);
            try {
                Files.walk(directoryPath)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                // 处理异常
                log.info("文件删除失败");
            }
        }
        return executeResponse;
    }
}
