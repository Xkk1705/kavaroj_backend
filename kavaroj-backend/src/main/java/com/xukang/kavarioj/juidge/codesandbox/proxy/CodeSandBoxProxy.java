package com.xukang.kavarioj.juidge.codesandbox.proxy;

import com.xukang.kavarioj.juidge.codesandbox.CodesSandBox;
import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteRequest;
import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码沙箱代理类
 * 增强代码功能
 */
@Slf4j
public class CodeSandBoxProxy implements CodesSandBox {
    private final CodesSandBox sandBox;

    public CodeSandBoxProxy(CodesSandBox sandBox) {
        this.sandBox = sandBox;
    }

    @Override
    public ExecuteResponse executeCode(ExecuteRequest executeRequest) {
        log.info("代码沙箱执行前");
        ExecuteResponse executeResponse = sandBox.executeCode(executeRequest);
        log.info("代码沙箱执行后日志" + executeResponse.getJudgeInfo().getExeInfo());
        return executeResponse;
    }
}
