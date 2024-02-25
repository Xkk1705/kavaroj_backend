package com.xukang.kavarioj.model.dto.question;

import com.xukang.kavarioj.model.common.JudgeCase;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求
 */
@Data
public class QuestionUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 答案
     */
    private String answer;

    /**
     * 题目标签（json 数组）
     */
    private List<String> tagList;

    /**
     * 判题配置（json 对象）
     */
    private String judgeConfig;

    /**
     * 判题用例（json 数组）
     */
    private List<JudgeCase> judgeCase;


    /**
     * 创建用户 id
     */
    private Long userId;

}