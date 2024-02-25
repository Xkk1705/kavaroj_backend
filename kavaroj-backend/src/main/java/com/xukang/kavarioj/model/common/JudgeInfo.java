package com.xukang.kavarioj.model.common;

import lombok.Data;

/**
 * 提交判题信息（json 对象）
 */
@Data
public class JudgeInfo {
    /**
     * 执行时间
     */
    private Long LimitTime;
    /**
     * 占用内存
     */
    private Long LimitMemory;

    /**
     * 执行返回信息
     */
    private String exeInfo;

}
