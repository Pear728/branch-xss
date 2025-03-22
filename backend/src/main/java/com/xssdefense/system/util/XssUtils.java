package com.xssdefense.system.util;

import java.util.regex.Pattern;

/**
 * XSS攻击检测和防御工具类
 */
public class XssUtils {

    /**
     * 检测字符串中的XSS载荷
     */
    public static boolean detectXssPayload(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        
        // 定义可能触发XSS的模式
        String[] patterns = {
            "<script[^>]*>.*?</script>",
            "javascript\\s*:",
            "on\\w+\\s*=",
            "<\\s*img[^>]+onerror\\s*=",
            "<\\s*iframe",
            "<\\s*embed",
            "<\\s*object",
            "data\\s*:",
            "document\\.cookie",
            "document\\.location",
            "eval\\s*\\(",
            "document\\.write",
            "innerHTML",
            "fromCharCode"
        };
        
        // 检查是否匹配任何一种模式
        for (String pattern : patterns) {
            if (Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(input).find()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 分析XSS载荷的危险等级
     */
    public static String analyzeXssSeverity(String payload) {
        if (payload == null || payload.isEmpty()) {
            return "low";
        }
        
        String[] severePatterns = {
            "document\\.cookie",
            "localStorage",
            "sessionStorage",
            "\\.src\\s*=",
            "xhr\\.open",
            "fetch\\s*\\(",
            "new\\s+XMLHttpRequest"
        };
        
        String[] mediumPatterns = {
            "<script",
            "eval\\s*\\(",
            "document\\.write",
            "innerHTML"
        };
        
        // 检查是否匹配高危模式
        for (String pattern : severePatterns) {
            if (Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(payload).find()) {
                return "high";
            }
        }
        
        // 检查是否匹配中危模式
        for (String pattern : mediumPatterns) {
            if (Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(payload).find()) {
                return "medium";
            }
        }
        
        return "low";
    }
    
    /**
     * 净化可能包含XSS的HTML内容
     */
    public static String sanitizeHtml(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        
        return html
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
            .replace("`", "&#96;")
            .replace("(", "&#40;")
            .replace(")", "&#41;")
            .replaceAll("(?i)javascript:", "")
            .replaceAll("(?i)on\\w+=", "");
    }
} 