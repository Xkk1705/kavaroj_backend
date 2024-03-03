package com.xukang.kavarioj.juidge.service;

/**
 * 判题服务
 */
public interface JudgeService {
    /**
     * 判题
     * @param questionSubmitId 提交题目id
     * @return
     */
    Long doJudge(Long questionSubmitId);
}
