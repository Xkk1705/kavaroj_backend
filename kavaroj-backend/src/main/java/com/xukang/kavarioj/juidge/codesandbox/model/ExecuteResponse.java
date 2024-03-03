package com.xukang.kavarioj.juidge.codesandbox.model;

import com.xukang.kavarioj.model.common.JudgeCase;
import com.xukang.kavarioj.model.common.JudgeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 执行代码返回结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExecuteResponse {
    /**
     * 日志信息
     */
    private String message;
    /**
     * 判题信息封装类
     */
    private JudgeInfo judgeInfo;

    /**
     * 输出用例
     */
    private List<JudgeCase> outputList;
}
