package com.xukang.kavarioj.juidge.codesandbox.impl;

import com.xukang.kavarioj.juidge.codesandbox.CodesSandBox;
import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteRequest;
import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteResponse;
import com.xukang.kavarioj.model.common.JudgeInfo;


/**
 * 代码沙箱示例实现类
 */
public class CodesSandBoxExampleImpl implements CodesSandBox {
    @Override
    public ExecuteResponse executeCode(ExecuteRequest executeRequest) {
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setLimitTime(10000L);
        judgeInfo.setLimitMemory(100000L);
        judgeInfo.setExeInfo("test");
        System.out.println("example");
        return ExecuteResponse.builder().judgeInfo(judgeInfo).build();
    }
}
