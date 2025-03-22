package com.xssdefense.system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * XSS测试结果
 * 包含XSS测试的结果信息
 */
@Data
@Entity
public class XssTestResult {
    @Id
    private Long id;

    /**
     * 用户输入的原始内容
     */
    private String input;

    /**
     * 清理后的内容
     */
    private String sanitizedOutput;

    /**
     * 是否检测到XSS攻击载荷
     */
    private boolean detected;

    /**
     * XSS攻击的严重程度（high/medium/low/none）
     */
    private String severity;

    /**
     * XSS攻击类型
     */
    private String attackType;

    /**
     * 是否成功防御
     */
    private boolean prevented;

    /**
     * 防御描述
     */
    private String description;
}
