package com.xukang.kavarioj.juidge.codesandbox.impl;

import com.xukang.kavarioj.juidge.codesandbox.CodesSandBox;
import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteRequest;
import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteResponse;


/**
 * 默认自己实现的代码沙箱
 */
public class CodesSandBoxDefaultImpl implements CodesSandBox {
    @Override
    public ExecuteResponse executeCode(ExecuteRequest executeRequest) {
        System.out.println("default");
        return null;
    }
}
