package com.example.bookstorewebapp.logging;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.pattern.ClassicConverter;

import java.util.regex.Pattern;

public class MaskingConverter extends ClassicConverter {
    private static final Pattern CRLF = Pattern.compile("[\\r\\n]+");
    // Mask common secret patterns in messages & arguments (case-insensitive):
    private static final Pattern SECRETS = Pattern.compile(
            "(?i)(password|passwd|pwd|secret|token|authorization|bearer|sessionid|jsessionid|api[_-]?key)\\s*[:=]\\s*([^\\s,;]+)"
    );

    @Override
    public String convert(ILoggingEvent event) {
        String msg = event.getFormattedMessage();
        if (msg == null) return "";
        // Neutralize CRLF to prevent log-injection (CWE-117)
        msg = CRLF.matcher(msg).replaceAll(" ");
        // Redact secrets (CWE-532)
        msg = SECRETS.matcher(msg).replaceAll("$1=***");
        return msg;
    }

    // Ensure PatternLayout understands our converter word
    public static void register() {
        PatternLayout.defaultConverterMap.put("maskedMsg", MaskingConverter.class.getName());
    }
}
