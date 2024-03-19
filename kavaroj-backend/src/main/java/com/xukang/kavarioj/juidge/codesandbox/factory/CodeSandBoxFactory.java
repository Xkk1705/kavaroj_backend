package com.xukang.kavarioj.juidge.codesandbox.factory;

import com.xukang.kavarioj.juidge.codesandbox.CodesSandBox;
import com.xukang.kavarioj.juidge.codesandbox.impl.CodesSandBoxExampleImpl;
import com.xukang.kavarioj.juidge.codesandbox.impl.CodesSandBoxRemoteImpl;
import com.xukang.kavarioj.juidge.codesandbox.impl.CodesSandBoxThreadPartImpl;


/**
 * 代码沙箱简单工厂
 */
public class CodeSandBoxFactory {
    public CodesSandBox getInstance(String type) {
        switch (type) {
            case "example":
                return new CodesSandBoxExampleImpl();
            case "remote":
                return new CodesSandBoxRemoteImpl();
            case "thirdPart":
                return new CodesSandBoxThreadPartImpl();
            default:
                return new CodesSandBoxExampleImpl();
        }
    }
}
