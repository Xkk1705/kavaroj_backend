package com.xukang.kavarioj.codesandbox.impl;

import com.xukang.kavarioj.juidge.codesandbox.CodesSandBox;
import com.xukang.kavarioj.juidge.codesandbox.factory.CodeSandBoxFactory;
import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteRequest;
import com.xukang.kavarioj.juidge.codesandbox.proxy.CodeSandBoxProxy;
import com.xukang.kavarioj.model.common.JudgeCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
class CodesSandBoxExampleImplTest {

    @Value("${codesandbox.type:example}")
    private String type;

    @Test
    void test() {
        ExecuteRequest.builder()
                .code("int main{}")
                .language("java")
                .inputList(new ArrayList<JudgeCase>())
                .build();
        CodeSandBoxFactory codeSandBoxFactory = new CodeSandBoxFactory();
        CodesSandBox codesSandBox = codeSandBoxFactory.getInstance(type);
        CodeSandBoxProxy codeSandBoxProxy = new CodeSandBoxProxy(codesSandBox);
        codeSandBoxProxy.executeCode(null);
    }

}