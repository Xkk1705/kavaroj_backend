package com.xukang.kavarioj.juidge.codesandbox.model;

import com.xukang.kavarioj.model.common.JudgeCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 执行代码接收参数类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExecuteRequest {
    /**
     * 代码语言
     */
    private String language;
    /**
     * 代码
     */
    private String code;
    /**
     * 输入输出用例
     */
    private List<JudgeCase> inputList;

}
