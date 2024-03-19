package com.xukang.codesandbox.utils;


import com.xukang.codesandbox.model.ExecuteMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 判题工具类
 */
public class JudgeUtils {

    public static final Long TIME_OUT_NUM = 1000 * 10L; // 超时时间10秒

    /**
     * 执行终端命令
     *
     * @param runtime
     * @param cmdString
     * @param cmdName
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ExecuteMessage execProcess(Runtime runtime, String cmdString, String cmdName) throws IOException, InterruptedException {
        StopWatch stopWatch = new StopWatch();// 用于记录程序执行时间
        stopWatch.start();
        Process process = runtime.exec(cmdString);
        stopWatch.stop();
//        new Thread(() -> {
//            try {
//                Thread.sleep(TIME_OUT_NUM);
//                process.destroy();
//                throw new RuntimeException("执行超时");
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        //如果执行命令超时 kill 掉线程

        // 创建BufferedReader来读取进程的标准输出流
        BufferedReader stdOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        // 分别读取标准输出和错误输出
//        String s;
//        StringBuilder output = new StringBuilder();
//        while ((s = stdOutput.readLine()) != null) {
//            output.append(s).append("\n");
//        }
        List<String> outputStrList = new ArrayList<>();
        // 逐行读取
        String s;
        while ((s = stdOutput.readLine()) != null) {
            outputStrList.add(s);
        }

        // 处理错误输出（`java -version`的版本信息通常在标准错误流输出）
        StringBuilder error = new StringBuilder();
        while ((s = stdError.readLine()) != null) {
            error.append(s).append("\n");
        }

        // 等待进程结束
        int exitValue = process.waitFor();
        ExecuteMessage executeMessage = new ExecuteMessage();

        // 返回代码沙箱执行的信息
        if (exitValue != 0) { //执行失败
            executeMessage.setOutput(error.toString());
            executeMessage.setStatus(1);
            executeMessage.setRunTime(totalTimeMillis);
            executeMessage.setRunMemory(1000L);
            return executeMessage;
        }
//        executeMessage.setOutput(output.toString());
        executeMessage.setOutput(StringUtils.join(outputStrList, "\n"));
        executeMessage.setStatus(0);
        executeMessage.setRunTime(totalTimeMillis);
        executeMessage.setRunMemory(1000L);
        // 输出结果
        System.out.println(cmdName + ":\n" + error.toString());
        // 注意：关闭缓冲区和进程 关闭资源
        stdOutput.close();
        stdError.close();
        return executeMessage;
    }
}
