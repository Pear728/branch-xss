package com.xssdefense.system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 攻击日志
 * 记录XSS攻击的详细信息
 */
@Data
@Entity
public class AttackLog {

    /**
     * 日志ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 攻击时间
     */
    private LocalDateTime timestamp;

    /**
     * 攻击载荷
     */
    private String payload;

    /**
     * 攻击来源（例如：URL、请求参数、HTTP头部等）
     */
    private String source;

    /**
     * 攻击IP地址
     */
    private String ipAddress;

    /**
     * 用户代理（User-Agent）
     */
    private String userAgent;

    /**
     * 攻击类型
     */
    private String attackType;

    /**
     * 严重程度（high/medium/low）
     */
    private String severity;

    /**
     * 是否被防御系统阻止
     */
    private boolean blocked;

    /**
     * 防御措施
     */
    private String defenseMeasure;
    /**
     * 是否已阻止某操作的布尔变量
     */
    private Boolean prevented;
}
