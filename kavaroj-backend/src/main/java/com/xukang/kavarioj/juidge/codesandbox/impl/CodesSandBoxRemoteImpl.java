package com.xukang.kavarioj.juidge.codesandbox.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.xukang.kavarioj.common.ErrorCode;
import com.xukang.kavarioj.exception.BusinessException;
import com.xukang.kavarioj.juidge.codesandbox.CodesSandBox;
import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteRequest;
import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteResponse;


/**
 * 远程调用自己实现的代码沙箱
 */
public class CodesSandBoxRemoteImpl implements CodesSandBox {
    @Override
    public ExecuteResponse executeCode(ExecuteRequest executeRequest) {
        //代码沙箱请求地址
        String url = "http://127.0.0.1:8102/code_sand_box/do_judge";
        //调用方和服务方约定一个字符串 放在请求头中校验
        String ak = "xuknag";
        // 字符加密
        String md5Hex1 = DigestUtil.md5Hex(ak);
        String executeRequestJsonStr = JSONUtil.toJsonStr(executeRequest);
        String result = HttpRequest.post(url)
                .header("auth", md5Hex1)
                .body(executeRequestJsonStr)
                .execute().body();
        if (StrUtil.isBlank(result)) {
            throw new BusinessException(ErrorCode.REMOTE_ERROR);
        }
        return JSONUtil.toBean(result, ExecuteResponse.class);
    }
}
