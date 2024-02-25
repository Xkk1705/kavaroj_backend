package com.xukang.kavarioj.model.vo;

import cn.hutool.json.JSONUtil;
import com.xukang.kavarioj.model.common.JudgeInfo;
import com.xukang.kavarioj.model.entity.QuestionSubmit;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * 题目提交表VO
 *
 * @TableName question_submit
 */

@Data
public class QuestionSubmitVO implements Serializable {
    /**
     * id
     */
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
    private JudgeInfo judgeInfo;


    /**
     * 包装类转对象
     *
     * @param questionSubmitVO
     * @return
     */
    public static QuestionSubmit voToObj(QuestionSubmitVO questionSubmitVO) {
        if (questionSubmitVO == null) {
            return null;
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitVO, questionSubmit);
        JudgeInfo judgeInfo = questionSubmitVO.getJudgeInfo();
        String judgeInfoStr = "{}";
        if (judgeInfo != null) {
            judgeInfoStr = JSONUtil.toJsonStr(judgeInfo);
        }
        questionSubmit.setJudgeInfo(judgeInfoStr);
        return questionSubmit;
    }

    /**
     * 对象转包装类
     *
     * @param questionSubmit
     * @return
     */
    public static QuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, questionSubmitVO);
        String judgeInfoStr = questionSubmit.getJudgeInfo();
        JudgeInfo judgeInfo = new JudgeInfo();
        if (StringUtils.isNotBlank(judgeInfoStr)) {
            judgeInfo = JSONUtil.toBean(judgeInfoStr, JudgeInfo.class);
        }
        questionSubmitVO.setJudgeInfo(judgeInfo);
        return questionSubmitVO;
    }

}