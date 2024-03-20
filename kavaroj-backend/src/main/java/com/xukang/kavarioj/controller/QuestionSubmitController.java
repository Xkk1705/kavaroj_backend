package com.xukang.kavarioj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xukang.kavarioj.annotation.AuthCheck;
import com.xukang.kavarioj.common.BaseResponse;
import com.xukang.kavarioj.common.ErrorCode;
import com.xukang.kavarioj.common.ResultUtils;
import com.xukang.kavarioj.exception.BusinessException;
import com.xukang.kavarioj.exception.ThrowUtils;
import com.xukang.kavarioj.juidge.service.JudgeService;
import com.xukang.kavarioj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.xukang.kavarioj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.xukang.kavarioj.model.entity.QuestionSubmit;
import com.xukang.kavarioj.model.entity.User;
import com.xukang.kavarioj.model.enums.QuestionSubmitStatusEnum;
import com.xukang.kavarioj.model.vo.QuestionSubmitVO;
import com.xukang.kavarioj.service.QuestionSubmitService;
import com.xukang.kavarioj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

/**
 * 提交答案接口
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    @Resource
    private JudgeService judgeService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 创建
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return
     */
    @PostMapping("/submit")
    public BaseResponse<Long> addQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
        if (questionSubmitAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitAddRequest, questionSubmit);
        questionSubmitService.validQuestionSubmit(questionSubmitAddRequest, true);
        User loginUser = userService.getLoginUser(request);
        questionSubmit.setUserId(loginUser.getId());
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        boolean result = questionSubmitService.save(questionSubmit);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "提交问题失败");
        Long questionSubmitId = questionSubmit.getId();
        // 把提交题目id交给消息队列去判题
        log.info("当前时间为：{}，当前消息为{}", new Date().toString(), questionSubmitId);
        rabbitTemplate.convertAndSend("exchange_x", "XA", "消息为ttl为10的消息" + questionSubmitId);
//        Long questionSubmitVOId = judgeService.doJudge(questionSubmit.getId());
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionSubmitVO> getQuestionSubmitVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionSubmit questionSubmit = questionSubmitService.getById(id);
        // 进管理员和答题者可以查看
        User loginUser = userService.getLoginUser(request);
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        if ("admin".equals(loginUser.getUserRole()) || Objects.equals(loginUser.getId(), questionSubmit.getUserId())) {
            questionSubmitVO = questionSubmitService.getQuestionSubmitVO(questionSubmit);
        }
        return ResultUtils.success(questionSubmitVO);
    }


    /**
     * 分页获取列表（封装类）
     *
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitVOByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                           HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest, null, false));
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionSubmitVO>> listMyQuestionSubmitVOByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                             HttpServletRequest request) {
        if (questionSubmitQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest, loginUser, true));
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage));
    }


}
