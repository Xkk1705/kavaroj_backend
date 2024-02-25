package com.xukang.kavarioj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xukang.kavarioj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.xukang.kavarioj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.xukang.kavarioj.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xukang.kavarioj.model.entity.User;
import com.xukang.kavarioj.model.vo.QuestionSubmitVO;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 校验
     *
     * @param questionSubmit
     * @param add
     */
    void validQuestionSubmit(QuestionSubmitAddRequest questionSubmit, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @param loginUser
     * @param my 查看自己创建 flag
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest,User loginUser,Boolean my);



    /**
     * 获取问题提交封装
     *
     * @param questionSubmit
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit);

    /**
     * 分页获取问题提交封装
     *
     * @param questionSubmitPage
     * @param request
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage);

}
