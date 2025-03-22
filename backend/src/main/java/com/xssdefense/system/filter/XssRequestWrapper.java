package com.xssdefense.system.filter;

import com.xssdefense.system.service.XssDetectionService;
import lombok.Getter;
import org.springframework.web.util.HtmlUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

/**
 * XSS请求包装器
 * 用于过滤请求中可能包含的XSS攻击载荷
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

    private final XssDetectionService xssDetectionService;
    
    @Getter
    private boolean xssDetected = false;
    
    @Getter
    private final Map<String, String> attackDetails = new HashMap<>();

    public XssRequestWrapper(HttpServletRequest request, XssDetectionService xssDetectionService) {
        super(request);
        this.xssDetectionService = xssDetectionService;
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return sanitizeAndCheck(value, "parameter: " + name);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        
        String[] sanitizedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            sanitizedValues[i] = sanitizeAndCheck(values[i], "parameter: " + name);
        }
        return sanitizedValues;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return sanitizeAndCheck(value, "header: " + name);
    }

    /**
     * 清理和检查输入是否包含XSS载荷
     * 
     * @param input 需要检查的输入
     * @param source 输入来源
     * @return 清理后的输入
     */
    private String sanitizeAndCheck(String input, String source) {
        if (input == null) {
            return null;
        }
        
        // 检测XSS
        if (xssDetectionService.detectXssPayload(input)) {
            xssDetected = true;
            String severity = xssDetectionService.analyzeXssSeverity(input);
            
            // 记录攻击详情
            attackDetails.put("payload", input);
            attackDetails.put("source", source);
            attackDetails.put("severity", severity);
        }
        
        // 清理输入（HTML转义）
        return HtmlUtils.htmlEscape(input);
    }
} 