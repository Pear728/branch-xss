package com.xssdefense.system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * XSS防御配置
 * 包含XSS防御系统的各项配置参数
 */
@Entity
@Data
public class DefenseConfig {
    @Id
    private Long id;

    /**
     * 是否启用全局XSS防御
     */
    private boolean enabled = true;
    /**
     * 是否启用输入验证
     */
    private boolean inputValidationEnabled = true;
    /**
     * 是否启用输出编码
     */
    private boolean outputEncodingEnabled = true;
    /**
     * 是否启用内容安全策略
     */
    private boolean cspEnabled = true;
    /**
     * 是否记录攻击日志
     */
    private boolean loggingEnabled = true;
    /**
     * 自定义内容安全策略
     */
    private String customCspPolicy;
    /**
     * 是否自动阻止检测到的攻击
     */
    private boolean autoBlock = true;
    /**
     * 阻止攻击后的重定向URL
     */
    private String blockRedirectUrl;
    /**
     * XSS检测灵敏度（high/medium/low）
     */
    private String sensitivity = "medium";
    /**
     * 黑名单规则列表
     */
    private String blacklistPatternsString;
    /**
     * 白名单URL路径（这些路径不会进行XSS过滤）
     */
    private String whitelistUrlsString;

    // 添加转换方法
    /**
     * 将黑名单模式字符串转换为列表
     * @return 黑名单模式列表
     */
    public List<String> getBlacklistPatterns() {
        return blacklistPatternsString == null ? List.of() : Arrays.asList(blacklistPatternsString.split(","));
    }

    /**
     * 将黑名单模式列表转换为字符串
     * @param blacklistPatterns 黑名单模式列表
     */
    public void setBlacklistPatterns(List<String> blacklistPatterns) {
        this.blacklistPatternsString = String.join(",", blacklistPatterns);
    }

    /**
     * 将白名单URL字符串转换为列表
     * @return 白名单URL列表
     */
    public List<String> getWhitelistUrls() {
        return whitelistUrlsString == null ? List.of() : Arrays.asList(whitelistUrlsString.split(","));
    }

    /**
     * 将白名单URL列表转换为字符串
     * @param whitelistUrls 白名单URL列表
     */
    public void setWhitelistUrls(List<String> whitelistUrls) {
        this.whitelistUrlsString = String.join(",", whitelistUrls);
    }
}
