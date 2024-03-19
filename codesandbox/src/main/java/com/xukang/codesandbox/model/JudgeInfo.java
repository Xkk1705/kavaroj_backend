package com.xukang.codesandbox.model;

import lombok.Data;

/**
 * 提交判题信息（json 对象）
 */
@Data
public class JudgeInfo {
    /**
     * 执行时间
     */
    private Long limitTime;
    /**
     * 占用内存
     */
    private Long limitMemory;

    /**
     * 代码返回信息
     */
    private String exeInfo;

    /**
     * 执行提示
     */
    private String message;

}
