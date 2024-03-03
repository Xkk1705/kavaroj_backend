package com.xukang.kavarioj.juidge.strategy;

/**
 * 判题策略管理类
 */

public class JudgeManager {
    private final String language;

    public JudgeManager(String language) {
        this.language = language;
    }

    public LanguageStrategy getLanguageStrategy() {
        switch (language) {
            case "JAVA":
                return new JavaLanguageStrategyImpl();
            case "C":
                return new CLanguageStrategyImpl();
//            case "GOLANG":
//                return new CodesSandBoxThreadPartImpl();
//            case "PYTHON":
//                return new CodesSandBoxThreadPartImpl();
            default:
                return new JavaLanguageStrategyImpl();
        }
    }
}

