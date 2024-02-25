package com.xukang.kavarioj.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xukang.kavarioj.model.common.JudgeCase;
import com.xukang.kavarioj.model.common.JudgeConfig;
import com.xukang.kavarioj.model.entity.Question;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 问题VO
 */
@Data
public class QuestionVO implements Serializable {

    private final static Gson GSON = new Gson();

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
     * 题目标签（json 数组）
     */
    private List<String> tagList;

    /**
     * 提交数
     */
    private Integer submitNum;


    /**
     * 提交问题成功次数
     */
    private Integer acceptNum;

    /**
     * 判题配置（json 对象）
     */
    private JudgeConfig judgeConfig;

    /**
     * 判题用例（json 数组）
     */
    private List<JudgeCase> judgeCaseList;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<String> tagList = questionVO.getTagList();
        List<JudgeCase> judgeCaseList = questionVO.getJudgeCaseList();
        if (tagList != null) {
            question.setTags(GSON.toJson(tagList));
        }
        if (judgeCaseList != null) {
            question.setJudgeCase(GSON.toJson(judgeCaseList));
        }
        return question;
    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        String judgeConfigJson = question.getJudgeConfig();
        String tags = question.getTags();
        String judgeCase = question.getJudgeCase();
        if (StringUtils.isNotBlank(judgeConfigJson)) {
            JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigJson, JudgeConfig.class);
            questionVO.setJudgeConfig(judgeConfig);
        }
        if (StringUtils.isNotBlank(tags)) {
            questionVO.setTagList(GSON.fromJson(question.getTags(), new TypeToken<List<String>>() {
            }.getType()));
        }
        if (StringUtils.isNotBlank(judgeCase)) {
            questionVO.setJudgeCaseList(GSON.fromJson(question.getJudgeCase(), new TypeToken<List<JudgeCase>>() {
            }.getType()));
        }

        return questionVO;
    }


}
