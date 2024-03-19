package com.xukang.codesandbox.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.xukang.codesandbox.CodeSandBox;
import com.xukang.codesandbox.model.ExecuteRequest;
import com.xukang.codesandbox.model.ExecuteResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 代码沙箱判题接口
 */
@RestController
@RequestMapping("/code_sand_box")
public class CodeSandBoxController {

    @Resource
    private CodeSandBox codeSandBox;

    @PostMapping("/do_judge")
    public ExecuteResponse duJudge(@RequestBody ExecuteRequest executeRequest, HttpServletRequest request) {
        if (executeRequest == null) {
            throw new RuntimeException("请求参数错误！");
        }
        //校验请求是否合法
        String requestAk = request.getHeader("auth");
        String ak = "xuknag";
        // 字符加密
        String trueAk = DigestUtil.md5Hex(ak);
        if (!trueAk.equals(requestAk)) {
            throw new RuntimeException("该用户没有访问权限");
        }
        ExecuteResponse executeResponse = null;
        try {
            executeResponse = codeSandBox.executeCode(executeRequest);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return executeResponse;
    }
}
