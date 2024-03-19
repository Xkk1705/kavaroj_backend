package com.xukang.kavarioj.juidge.strategy;

import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteResponse;
import com.xukang.kavarioj.model.common.JudgeCase;
import com.xukang.kavarioj.model.common.JudgeConfig;
import com.xukang.kavarioj.model.common.JudgeInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * java判题策略
 */
public class JavaLanguageStrategyImpl implements LanguageStrategy {


    public static final Long JAVA_EXEC_TIME = 1000L;

    @Override
    public JudgeInfo judge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        JudgeConfig judgeConfig = judgeContext.getJudgeConfig();
        ExecuteResponse executeResponse = judgeContext.getExecuteResponse();
        // 4. 判断题目限制（内存、时间限制）
        Long limitTime = judgeConfig.getLimitTime();// 问题限制时间
        Long limitMemory = judgeConfig.getLimitMemory();// 问题限制内存
        Long execTime = executeResponse.getJudgeInfo().getLimitTime();// 代码沙箱执行时间
        Long execMemory = executeResponse.getJudgeInfo().getLimitMemory();// 代码沙箱执行内存
        String exeInfo = executeResponse.getJudgeInfo().getExeInfo();// 代码沙箱执返回信息
        judgeInfo.setExeInfo(exeInfo);
// 这里的执行时间根据不同的编程语言 有不同的效率

        if (execTime - JAVA_EXEC_TIME > limitTime) {
            judgeInfo.setMessage("执行题目超时");
            return judgeInfo;
        }
        if (limitMemory > execMemory) {
            judgeInfo.setMessage("内存过大");
            return judgeInfo;
        }
        // 5. 判断输出用例和输入用例是否匹配（数量、结果）
        List<String> outputCaseStrList = judgeCaseList.stream().map(JudgeCase::getOutput).collect(Collectors.toList());//答案的输出用例
        List<String> exeOutputStrList = executeResponse.getOutputList().stream().map(JudgeCase::getOutput).collect(Collectors.toList());//代码沙箱的输出用例
        if (!listsHaveSameContent(outputCaseStrList, exeOutputStrList)) {
            judgeInfo.setMessage("输出用例不正确");
            return judgeInfo;
        }
        judgeInfo.setMessage("AC");
        return judgeInfo;
    }


    /**
     * 判断两个字符串数组是否相同
     *
     * @param list1
     * @param list2
     * @return
     */
    public boolean listsHaveSameContent(List<String> list1, List<String> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        return list1.containsAll(list2);
    }

}
