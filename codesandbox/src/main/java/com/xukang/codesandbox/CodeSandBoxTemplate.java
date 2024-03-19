package com.xukang.codesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import cn.hutool.json.JSONUtil;
import com.xukang.codesandbox.model.*;
import com.xukang.codesandbox.utils.JudgeUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 代码沙箱模板方法
 */
@Slf4j
public abstract class CodeSandBoxTemplate implements CodeSandBox{


    public static final String GLOBAL_USER_PATH_NAME = "usercode";
    public static final String GLOBAL_USER_CODE_NAME = "Main.java";

    @Override
    public ExecuteResponse executeCode(ExecuteRequest executeRequest) throws IOException, InterruptedException {
        // 创建用户代码文件
        String userCodePath = createUserCodeFile(executeRequest);
        // 编译文件
        JudgeInfo judgeInfo = compileFile(userCodePath);
        // 执行文件
        List<ExecuteMessage> RunExecuteMessages = runCode(executeRequest, userCodePath);

        //封装用户响应
        ExecuteResponse executeResponse = fullExecuteResponse(RunExecuteMessages, judgeInfo);
        // 删除文件
        deleteUserFile(userCodePath);

        return executeResponse;
    }

    /**
     * 删除用户文件
     *
     * @param userCodePath
     */
    public void deleteUserFile(String userCodePath) {
        File file = new File(userCodePath);
        String userParentPath = file.getParentFile().getPath();
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

    /**
     * 封装运行响应信息
     *
     * @param executeMessages
     * @param judgeInfo
     * @return
     */
    public ExecuteResponse fullExecuteResponse(List<ExecuteMessage> executeMessages, JudgeInfo judgeInfo) {
        long maxTime = 0;
        long maxMemory = 0;
        ExecuteResponse executeResponse = new ExecuteResponse();
        List<JudgeCase> outputList = new ArrayList<>();
        for (ExecuteMessage executeMessage : executeMessages) {
            String output = executeMessage.getOutput();
            Integer status = executeMessage.getStatus();
            if (status != 0) {
                executeResponse.setErrMessage("运行失败");
                log.info("运行失败");
            }
            Long runTime = executeMessage.getRunTime();
            if (runTime > maxTime) {//取最大的运行时间
                maxTime = runTime;
            }
            Long runMemory = executeMessage.getRunMemory();
            if (runMemory > maxMemory) {//取最大的运行时间
                maxMemory = runMemory;
            }
            executeResponse.setMessage("运行成功");
            JudgeCase judgeCase = new JudgeCase();
            judgeCase.setOutput(output);
            outputList.add(judgeCase);
        }
        judgeInfo.setExeInfo(JSONUtil.toJsonStr(outputList));
        judgeInfo.setLimitTime(maxTime);
        judgeInfo.setLimitMemory(maxMemory);
        executeResponse.setOutputList(outputList);
        executeResponse.setJudgeInfo(judgeInfo);
        return executeResponse;
    }

    /**
     * 执行用户代码文件
     *
     * @param executeRequest
     * @param userParentPath
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public List<ExecuteMessage> runCode(ExecuteRequest executeRequest, String userCodePath) throws IOException, InterruptedException {
        // 获取userparentpath
        File file = new File(userCodePath);
        String userParentPath = file.getParentFile().getPath();
        // 执行文件
        ArrayList<ExecuteMessage> executeMessages = new ArrayList<>();
        // 获取参数用例
        List<JudgeCase> inputList = executeRequest.getInputList();
        for (JudgeCase judgeCase : inputList) {
            String inputArgs = judgeCase.getInput();
            String runCommend = "java -Xmx2048m -Dfile.encoding=UTF-8 " + "-cp " + userParentPath + " Main " + inputArgs;
            //执行命令
            // java -Dfile.encoding=UTF-8 -cp D:\IdeaProjects\kavaroj\45ddf48c-6f0e-46ad-be83-de5a3a851a97 Main 1 2
            // 创建一个新的进程以执行命令
            Runtime runtime = Runtime.getRuntime();
            //  e. 获取返回结果
            ExecuteMessage executeMessage = JudgeUtils.execProcess(runtime, runCommend, "运行");// 编译返回信息
            executeMessages.add(executeMessage);
        }
        return executeMessages;
    }

    /**
     * 编译问及那
     *
     * @param userCodePath
     * @return
     */
    public JudgeInfo compileFile(String userCodePath) {
        JudgeInfo judgeInfo = new JudgeInfo();
        ExecuteResponse executeResponse = new ExecuteResponse();
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
                judgeInfo.setMessage("编译失败");
                executeResponse.setJudgeInfo(judgeInfo);
                executeResponse.setMessage("编译失败");
                log.info("编译失败");
                return judgeInfo;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return judgeInfo;
    }

    /**
     * 创建用户代码文件
     *
     * @param executeRequest
     * @return
     */
    public String createUserCodeFile(ExecuteRequest executeRequest) {
        // 判断code是否合法
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
        return userCodePath;
    }
}
