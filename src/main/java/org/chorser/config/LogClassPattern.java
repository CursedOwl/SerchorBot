package org.chorser.config;

import ch.qos.logback.classic.pattern.color.HighlightingCompositeConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogClassPattern extends HighlightingCompositeConverter {

    @Override
    protected String getForegroundColorCode(ILoggingEvent event) {
        return "33";
    }
}
