package com.xukang.kavarioj.juidge.codesandbox;

import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteRequest;
import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteResponse;

/**
 * 代码沙箱接口
 */
public interface CodesSandBox {
    /**
     * 代码沙箱执行代码方法
     *
     * @param executeRequest 执行代码接收的参数
     * @return
     */
    ExecuteResponse executeCode(ExecuteRequest executeRequest);
}
