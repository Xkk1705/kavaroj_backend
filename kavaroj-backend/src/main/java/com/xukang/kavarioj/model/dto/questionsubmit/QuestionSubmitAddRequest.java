package com.xukang.kavarioj.model.dto.questionsubmit;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 */
@Data
public class QuestionSubmitAddRequest implements Serializable {

    /**
     * 问题 id
     */
    private Long questionId;

    /**
     * 答题用户 id
     */
    private Long userId;

    /**
     * 提交答案
     */
    private String code;

    /**
     * 编程语言
     */
    private String language;

}