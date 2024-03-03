package com.xukang.kavarioj.juidge.codesandbox.factory;

import com.xukang.kavarioj.juidge.codesandbox.CodesSandBox;
import com.xukang.kavarioj.juidge.codesandbox.impl.CodesSandBoxDefaultImpl;
import com.xukang.kavarioj.juidge.codesandbox.impl.CodesSandBoxExampleImpl;
import com.xukang.kavarioj.juidge.codesandbox.impl.CodesSandBoxThreadPartImpl;


/**
 * 代码沙箱简单工厂
 */
public class CodeSandBoxFactory {
    public CodesSandBox getInstance(String type) {
        switch (type) {
            case "example":
                return new CodesSandBoxExampleImpl();
            case "default":
                return new CodesSandBoxDefaultImpl();
            case "thirdPart":
                return new CodesSandBoxThreadPartImpl();
            default:
                return new CodesSandBoxExampleImpl();
        }
    }
}
