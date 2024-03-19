package com.xukang.codesandbox.model;

import lombok.Data;

/**
 * 执行返回结果
 */
@Data
public class ExecuteMessage {
    /**
     * 执行输出
     */
    private String output;
    /**
     * 执行状态
     */
    private Integer status; //0 为正常 1 为失败
    /**
     * 执行时间
     */
    private Long runTime;
    /**
     * 执行内存
     */
    private Long runMemory;
}
