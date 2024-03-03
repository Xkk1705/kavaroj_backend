package com.xukang.kavarioj.juidge.codesandbox.impl;

import com.xukang.kavarioj.juidge.codesandbox.CodesSandBox;
import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteRequest;
import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteResponse;


/**
 * 第三方代码沙箱
 */
public class CodesSandBoxThreadPartImpl implements CodesSandBox {
    @Override
    public ExecuteResponse executeCode(ExecuteRequest executeRequest) {
        System.out.println("thirdPart");
        return null;
    }
}
