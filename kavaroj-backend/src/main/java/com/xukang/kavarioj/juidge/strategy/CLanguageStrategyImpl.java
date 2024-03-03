package com.xukang.kavarioj.juidge.strategy;

import com.xukang.kavarioj.model.common.JudgeInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CLanguageStrategyImpl implements LanguageStrategy {

    @Override
    public JudgeInfo judge(JudgeContext judgeContext) {
       return  null;
    }


    /**
     * 判断两个字符串数组是否相同
     *
     * @param list1
     * @param list2
     * @return
     */
    public boolean listsHaveSameContent(List<String> list1, List<String> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        return list1.containsAll(list2);
    }

}
