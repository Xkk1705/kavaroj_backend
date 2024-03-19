package com.xukang.codesandbox.model;

import lombok.Data;

/**
 * 测试用例对象
 */
@Data
public class JudgeCase {
    /**
     * 输入用例
     */
    private String input;
    /**
     * 输出用例
     */
    private String output;
}
