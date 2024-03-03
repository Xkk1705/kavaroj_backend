package com.xukang.kavarioj.model.common;

import lombok.Data;

/**
 * 题目信息配置
 */
@Data
public class JudgeConfig {
    /**
     * 执行时间
     */
    private Long limitTime;
    /**
     * 占用内存
     */
    private Long limitMemory;

}
