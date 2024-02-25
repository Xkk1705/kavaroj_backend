package com.xukang.kavarioj.model.dto.questionsubmit;

import com.xukang.kavarioj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 问题 id
     */
    private Long questionId;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 提交状态 0-待提交 1_带判题 2_成功 3_失败 4_系统异常
     */
    private Integer status;


}