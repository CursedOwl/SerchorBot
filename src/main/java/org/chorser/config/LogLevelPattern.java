package org.chorser.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.color.HighlightingCompositeConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogLevelPattern extends HighlightingCompositeConverter {
    @Override
    protected String getForegroundColorCode(ILoggingEvent event) {
        Level level = event.getLevel();
        switch (level.toInt()) {
            case Level.ERROR_INT:
                return "1;31"; // 红色
            case Level.WARN_INT:
                return "1;33"; // 黄色
            case Level.INFO_INT:
                return "32"; // 绿色
            case Level.DEBUG_INT:
                return "32"; // 青色
            default:
                return "0"; // 默认（无颜色）
        }
    }
}
