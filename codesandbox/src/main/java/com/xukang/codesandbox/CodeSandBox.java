package com.xukang.codesandbox;

import com.xukang.codesandbox.model.ExecuteRequest;
import com.xukang.codesandbox.model.ExecuteResponse;

import java.io.IOException;

/**
 * 代码沙箱接口类
 */
public interface CodeSandBox {
    /**
     * 执行代码沙箱方法
     * @param executeRequest
     * @return
     */
    ExecuteResponse executeCode(ExecuteRequest executeRequest) throws IOException, InterruptedException;
}
