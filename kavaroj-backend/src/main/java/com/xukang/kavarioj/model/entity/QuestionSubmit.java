package com.xukang.kavarioj.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 题目提交表
 * @TableName question_submit
 */
@TableName(value ="question_submit")
@Data
public class QuestionSubmit implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

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

    /**
     * 提交状态 0-待提交 1_带判题 2_成功 3_失败 4_系统异常
     */
    private Integer status;

    /**
     * 判题日志（json 对象）
     */
    private String judgeInfo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}