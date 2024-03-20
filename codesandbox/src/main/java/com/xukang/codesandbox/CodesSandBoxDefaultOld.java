package com.xukang.codesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
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
 * 默认自己实现的代码沙箱
 */
@Slf4j
@Deprecated
//@Service
public class CodesSandBoxDefaultOld implements CodeSandBox {
    public static void main(String[] args) throws IOException, InterruptedException {
        ExecuteRequest executeRequest = new ExecuteRequest();
        // 从文件中获取code信息
        FileReader fileReader = new FileReader("/home/kang/codesandbox/src/main/java/tsetcode/Main.java");
        String code = fileReader.readString();
        executeRequest.setCode(code);
        JudgeCase judgeCase = new JudgeCase();
        judgeCase.setInput("1 2");
        ArrayList<JudgeCase> list = new ArrayList<>();
        list.add(judgeCase);
        executeRequest.setInputList(list);
        CodesSandBoxDefaultOld codesSandBoxDefault = new CodesSandBoxDefaultOld();
        ExecuteResponse executeResponse = codesSandBoxDefault.executeCode(executeRequest);
    }

    public static final String GLOBAL_USER_PATH_NAME = "usercode";
    public static final String GLOBAL_USER_CODE_NAME = "Main.java";
    private static final WordTree WORD_TREE;


    /**
     * code字典黑名单
     */
    public static final List<String> blackList = Arrays.asList("File", "Runtime", "exec", "fileReader");

    static {
        // 初始化字典树
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(blackList);
    }

    @Override
    public ExecuteResponse executeCode(ExecuteRequest executeRequest) throws IOException, InterruptedException {
        ExecuteResponse executeResponse = new ExecuteResponse();
        String code = executeRequest.getCode();
        //校验代码中是否包含黑名单中的命令
        FoundWord foundWord = WORD_TREE.matchWord(code);
        if (foundWord != null) {
            System.out.println("包含禁止词：" + foundWord.getFoundWord());
            throw new RuntimeException("code 非法");
        }

        // 判断code是否合法
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
            long maxTime = 0;
            long maxMemory = 0;
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
        } catch (IOException | InterruptedException e) {
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
