package com.xukang.kavarioj.juidge.service;

import cn.hutool.json.JSONUtil;
import com.xukang.kavarioj.common.ErrorCode;
import com.xukang.kavarioj.exception.BusinessException;
import com.xukang.kavarioj.juidge.codesandbox.CodesSandBox;
import com.xukang.kavarioj.juidge.codesandbox.factory.CodeSandBoxFactory;
import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteRequest;
import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteResponse;
import com.xukang.kavarioj.juidge.codesandbox.proxy.CodeSandBoxProxy;
import com.xukang.kavarioj.juidge.strategy.JudgeContext;
import com.xukang.kavarioj.juidge.strategy.JudgeManager;
import com.xukang.kavarioj.model.common.JudgeCase;
import com.xukang.kavarioj.model.common.JudgeConfig;
import com.xukang.kavarioj.model.common.JudgeInfo;
import com.xukang.kavarioj.model.entity.Question;
import com.xukang.kavarioj.model.entity.QuestionSubmit;
import com.xukang.kavarioj.model.enums.QuestionSubmitStatusEnum;
import com.xukang.kavarioj.service.QuestionService;
import com.xukang.kavarioj.service.QuestionSubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 判题模块实现类
 */
@Slf4j
@Service
public class JudgeServiceImpl implements JudgeService {
    @Resource
    private QuestionSubmitService questionSubmitService;
    @Resource
    private QuestionService questionService;

    @Value("${codesandbox.type:example}")
    private String type;

    private ExecuteResponse executeResponse = null;

    @Override
    public Long doJudge(Long questionSubmitId) {
        //1. 根据submitquestionId 获取提交信息（判空）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交不存在");
        }
        //2. 判断题目信息（判空）
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交的题目不存在");
        }
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        String code = questionSubmit.getCode();
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);// 输入输出用例
        String language = questionSubmit.getLanguage();
        Integer status = questionSubmit.getStatus();
        if (!QuestionSubmitStatusEnum.WAITING.getValue().equals(status)) {// 如果题目不是带判题状态 那么之间返回
            log.info(questionSubmitId + "不是带判题状态无需判题");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不是带判题状态无需判题");
        }
        //修改判题状态为判题中
        questionSubmit.setStatus(QuestionSubmitStatusEnum.LOADING.getValue());
        boolean updateById = questionSubmitService.updateById(questionSubmit);
        if (!updateById) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改判题状态失败");
        }
        //3. 调用代码沙箱，获取执行结果
        ExecuteRequest executeRequest = ExecuteRequest.builder()
                .code(code)
                .language(language)
                .inputList(judgeCaseList)
                .build();
        // 获取代码沙箱实例
        CodeSandBoxFactory codeSandBoxFactory = new CodeSandBoxFactory();
        CodesSandBox codesSandBox = codeSandBoxFactory.getInstance(type);
        // 获取代理类增强功能
        CodeSandBoxProxy codeSandBoxProxy = new CodeSandBoxProxy(codesSandBox);
        executeResponse = codeSandBoxProxy.executeCode(executeRequest);// 代码沙箱 执行对应内容
        if (executeResponse == null) {
            // 判题状态改为系统异常
            questionSubmit.setStatus(QuestionSubmitStatusEnum.SYS_ERR.getValue());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码沙箱执行返回结果为空");
        }
        JudgeInfo judgeInfo = executeResponse.getJudgeInfo();//代码沙箱返回执行信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(judgeInfo);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setJudgeConfig(judgeConfig);
        judgeContext.setExecuteResponse(executeResponse);
        //更具编程语言策略 判题
        JudgeManager judgeManager = new JudgeManager(language);
        judgeInfo = judgeManager.getLanguageStrategy().judge(judgeContext);
        if (!"AC".equals(judgeInfo.getMessage())) {//判题失败
            String judgeInfoStr = JSONUtil.toJsonStr(judgeInfo);
            questionSubmit.setJudgeInfo(judgeInfoStr);
            questionSubmit.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
            questionSubmitService.updateById(questionSubmit);
            return questionSubmitId;
        }
        questionSubmit.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        judgeInfo.setMessage("判题成功");
        String judgeInfoStr = JSONUtil.toJsonStr(judgeInfo);
        questionSubmit.setJudgeInfo(judgeInfoStr);
        // 更新判题信息和状态
        boolean update = questionSubmitService.updateById(questionSubmit);
        if (update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新questionSubmit失败");
        }
        return questionSubmitId;
    }
}




