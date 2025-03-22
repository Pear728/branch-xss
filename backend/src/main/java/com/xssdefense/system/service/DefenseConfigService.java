package com.xssdefense.system.service;

import com.xssdefense.system.model.DefenseConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 防御配置服务
 * 提供管理XSS防御配置的功能
 */
@Service
public class DefenseConfigService {

    // 默认配置
    private DefenseConfig config;

    /**
     * 构造函数，初始化默认配置
     */
    public DefenseConfigService() {
        // 创建默认配置
        this.config = new DefenseConfig();
        this.config.setEnabled(true);
        this.config.setInputValidationEnabled(true);
        this.config.setOutputEncodingEnabled(true);
        this.config.setCspEnabled(true);
        this.config.setLoggingEnabled(true);
        this.config.setAutoBlock(true);
        this.config.setSensitivity("medium");

        // 设置默认黑名单规则
        this.config.setBlacklistPatterns(Arrays.asList(
            "<script[^>]*>.*?</script>",
            "javascript:",
            "onerror=",
            "onload=",
            "onclick=",
            "eval\\(",
            "<iframe[^>]*>",
            "<svg[^>]*>.*?<script[^>]*>.*?</script>.*?</svg>"
        ));

        // 设置默认白名单URL
        this.config.setWhitelistUrls(Arrays.asList(
            "/api/xss/test",
            "/api/admin/*"
        ));

        // 设置默认CSP策略
        this.config.setCustomCspPolicy("default-src 'self'; script-src 'self'; object-src 'none'; frame-src 'none';");
    }

    /**
     * 获取当前配置
     *
     * @return 当前配置
     */
    public DefenseConfig getConfig() {
        return this.config;
    }

    /**
     * 更新配置
     *
     * @param newConfig 新配置
     * @return 更新后的配置
     */
    public DefenseConfig updateConfig(DefenseConfig newConfig) {
        // 合并配置
        if (newConfig.getBlacklistPatterns() == null) {
            newConfig.setBlacklistPatterns(this.config.getBlacklistPatterns());
        }

        if (newConfig.getWhitelistUrls() == null) {
            newConfig.setWhitelistUrls(this.config.getWhitelistUrls());
        }

        if (newConfig.getCustomCspPolicy() == null || newConfig.getCustomCspPolicy().isEmpty()) {
            newConfig.setCustomCspPolicy(this.config.getCustomCspPolicy());
        }

        // 更新配置
        this.config = newConfig;
        return this.config;
    }

    /**
     * 重置配置为默认值
     *
     * @return 重置后的配置
     */
    public DefenseConfig resetConfig() {
        // 调用构造函数重新初始化配置
        this.config = new DefenseConfig();
        this.config.setEnabled(true);
        this.config.setInputValidationEnabled(true);
        this.config.setOutputEncodingEnabled(true);
        this.config.setCspEnabled(true);
        this.config.setLoggingEnabled(true);
        this.config.setAutoBlock(true);
        this.config.setSensitivity("medium");

        // 设置默认黑名单规则
        this.config.setBlacklistPatterns(Arrays.asList(
            "<script[^>]*>.*?</script>",
            "javascript:",
            "onerror=",
            "onload=",
            "onclick=",
            "eval\\(",
            "<iframe[^>]*>",
            "<svg[^>]*>.*?<script[^>]*>.*?</script>.*?</svg>"
        ));

        // 设置默认白名单URL
        this.config.setWhitelistUrls(Arrays.asList(
            "/api/xss/test",
            "/api/admin/*"
        ));

        // 设置默认CSP策略
        this.config.setCustomCspPolicy("default-src 'self'; script-src 'self'; object-src 'none'; frame-src 'none';");

        return this.config;
    }

    /**
     * 检查URL是否在白名单中
     *
     * @param url URL路径
     * @return 是否在白名单中
     */
    public boolean isUrlWhitelisted(String url) {
        if (!this.config.isEnabled()) {
            return true;
        }

        return this.config.getWhitelistUrls().stream()
                .anyMatch(pattern -> matchPattern(url, pattern));
    }

    /**
     * 简单的模式匹配实现
     *
     * @param url URL路径
     * @param pattern 模式
     * @return 是否匹配
     */
    private boolean matchPattern(String url, String pattern) {
        if (pattern.endsWith("*")) {
            String prefix = pattern.substring(0, pattern.length() - 1);
            return url.startsWith(prefix);
        }
        return url.equals(pattern);
    }
}
