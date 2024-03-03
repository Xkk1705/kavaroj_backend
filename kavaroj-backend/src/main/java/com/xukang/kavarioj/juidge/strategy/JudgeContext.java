package com.xukang.kavarioj.juidge.strategy;

import com.xukang.kavarioj.juidge.codesandbox.model.ExecuteResponse;
import com.xukang.kavarioj.model.common.JudgeCase;
import com.xukang.kavarioj.model.common.JudgeConfig;
import com.xukang.kavarioj.model.common.JudgeInfo;
import com.xukang.kavarioj.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 判题策略上下文
 */
@Data
public class JudgeContext {

    /**
     *判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 输出用例list
     */
    private List<JudgeCase> judgeCaseList;

    /**
     * 判题设置信息
     */
    private JudgeConfig judgeConfig;

    /**
     * 代码沙箱执行返回结果
     */
    private ExecuteResponse executeResponse;

}
