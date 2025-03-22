package com.xssdefense.system.service;

import com.xssdefense.system.model.AttackLog;
import com.xssdefense.system.model.DefenseOptions;
import com.xssdefense.system.model.XssTestRequest;
import com.xssdefense.system.model.XssTestResult;
import com.xssdefense.system.util.XssUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * XSS检测服务
 * 提供检测XSS攻击载荷的功能
 */
@Service
@RequiredArgsConstructor
public class XssDetectionService {

    private final AttackLogService attackLogService;
    private final DefenseConfigService configService;

    // XSS攻击模式（常见的XSS载荷模式）
    private static final List<Pattern> XSS_PATTERNS = new ArrayList<>();

    // 攻击类型映射
    private static final Map<String, String> ATTACK_TYPE_MAP = new HashMap<>();

    static {
        // 初始化XSS攻击模式
        XSS_PATTERNS.add(Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE));
        XSS_PATTERNS.add(Pattern.compile("src[\r\n]*=[\r\n]*\\'(.*?)\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        XSS_PATTERNS.add(Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        XSS_PATTERNS.add(Pattern.compile("<script>.*?</script>", Pattern.CASE_INSENSITIVE));
        XSS_PATTERNS.add(Pattern.compile("</script>", Pattern.CASE_INSENSITIVE));
        XSS_PATTERNS.add(Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        XSS_PATTERNS.add(Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        XSS_PATTERNS.add(Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        XSS_PATTERNS.add(Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE));
        XSS_PATTERNS.add(Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE));
        XSS_PATTERNS.add(Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        XSS_PATTERNS.add(Pattern.compile("onerror(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        XSS_PATTERNS.add(Pattern.compile("onclick(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));

        // 初始化攻击类型映射
        ATTACK_TYPE_MAP.put("<script>", "Reflected XSS - Script Tag");
        ATTACK_TYPE_MAP.put("javascript:", "Reflected XSS - JavaScript Protocol");
        ATTACK_TYPE_MAP.put("onload", "DOM-based XSS - Event Handler");
        ATTACK_TYPE_MAP.put("onclick", "DOM-based XSS - Event Handler");
        ATTACK_TYPE_MAP.put("onerror", "DOM-based XSS - Event Handler");
        ATTACK_TYPE_MAP.put("eval", "DOM-based XSS - JavaScript Execution");
        ATTACK_TYPE_MAP.put("innerHTML", "DOM-based XSS - HTML Injection");
    }

    /**
     * 检测输入是否包含XSS攻击载荷
     *
     * @param input 用户输入
     * @return 是否检测到XSS
     */
    public boolean detectXssPayload(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        // 遍历所有XSS模式进行检测
        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(input).find()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 分析XSS攻击的严重程度
     *
     * @param input 用户输入
     * @return 严重程度（high/medium/low）
     */
    public String analyzeXssSeverity(String input) {
        if (input == null || input.isEmpty()) {
            return "none";
        }

        // 高危XSS模式
        if (input.toLowerCase().contains("<script>") ||
            input.toLowerCase().contains("javascript:") ||
            input.toLowerCase().contains("eval(") ||
            input.toLowerCase().contains("document.cookie")) {
            return "high";
        }

        // 中危XSS模式
        if (input.toLowerCase().contains("onload=") ||
            input.toLowerCase().contains("onerror=") ||
            input.toLowerCase().contains("onclick=") ||
            input.toLowerCase().contains("<img") ||
            input.toLowerCase().contains("<iframe")) {
            return "medium";
        }

        // 低危XSS模式
        if (detectXssPayload(input)) {
            return "low";
        }

        return "none";
    }

    /**
     * 获取攻击类型
     *
     * @param input 用户输入
     * @return 攻击类型
     */
    public String getAttackType(String input) {
        if (input == null || input.isEmpty()) {
            return "Unknown";
        }

        for (Map.Entry<String, String> entry : ATTACK_TYPE_MAP.entrySet()) {
            if (input.toLowerCase().contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }

        return "Generic XSS Attempt";
    }

    /**
     * 处理XSS测试请求
     *
     * @param request XSS测试请求
     * @return 测试结果
     */
    public XssTestResult processXssTest(XssTestRequest request) {
        XssTestResult result = new XssTestResult();

        String input = request.getInput();
        result.setInput(input);

        // 检测XSS
        boolean detected = detectXssPayload(input);
        result.setDetected(detected);

        if (detected) {
            result.setSeverity(analyzeXssSeverity(input));
            result.setAttackType(getAttackType(input));

            // 应用防御选项
            String sanitized = applySanitization(input, request.getDefenseOptions());
            result.setSanitizedOutput(sanitized);

            // 判断是否成功防御
            boolean prevented = !detectXssPayload(sanitized);
            result.setPrevented(prevented);

            if (prevented) {
                result.setDescription("XSS攻击已被成功防御");
            } else {
                result.setDescription("警告：XSS防御可能不完全，仍存在安全风险");
            }
            
            // 记录XSS攻击日志
            AttackLog log = new AttackLog();
            log.setTimestamp(LocalDateTime.now());
            log.setPayload(input);
            log.setSource("XSS测试工具");
            log.setSeverity(result.getSeverity());
            log.setIpAddress("127.0.0.1");
            log.setUserAgent("XSS测试系统");
            log.setAttackType(result.getAttackType());
            log.setBlocked(prevented);
            log.setPrevented(prevented);
            log.setDefenseMeasure(getDefenseMeasureDesc(request.getDefenseOptions()));
            
            attackLogService.saveLog(log);
        } else {
            result.setSeverity("none");
            result.setAttackType("No Attack");
            result.setSanitizedOutput(input);
            result.setPrevented(false);
            result.setDescription("未检测到XSS攻击");
        }

        return result;
    }

    /**
     * 获取防御措施描述
     * 
     * @param options 防御选项
     * @return 防御措施描述
     */
    private String getDefenseMeasureDesc(DefenseOptions options) {
        StringBuilder sb = new StringBuilder();
        
        if (options.isHtmlEscape()) {
            sb.append("HTML转义 ");
        }
        
        if (options.isRemoveJsEvents()) {
            sb.append("移除JS事件 ");
        }
        
        if (options.isRemoveDangerousTags()) {
            sb.append("移除危险标签 ");
        }
        
        return sb.toString().trim();
    }

    /**
     * 应用清理选项
     *
     * @param input 用户输入
     * @param options 防御选项
     * @return 清理后的输入
     */
   private String applySanitization(String input, DefenseOptions options) {
    if (input == null) {
        return null;
    }

    String result = input;

    // HTML转义
    if (options.isHtmlEscape()) {
        result = HtmlUtils.htmlEscape(result);
    }

    // 移除JavaScript事件
    if (options.isRemoveJsEvents()) {
        result = result.replaceAll("(?i)on\\w+\\s*=\\s*(['\"])(.*?)\\1", "");
    }

    // 移除危险标签
    if (options.isRemoveDangerousTags()) {
        result = result.replaceAll("(?i)<script(.*?)>(.*?)</script>", "")
                      .replaceAll("(?i)<iframe(.*?)>(.*?)</iframe>", "")
                      .replaceAll("(?i)<base(.*?)>", "")
                      .replaceAll("(?i)<object(.*?)>(.*?)</object>", "")
                      .replaceAll("(?i)<embed(.*?)>(.*?)</embed>", "");
    }

    return result;
}

}
