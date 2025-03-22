package com.xssdefense.system.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.xssdefense.system.model.AttackLog;
import com.xssdefense.system.repository.AttackLogRepository;

import lombok.RequiredArgsConstructor;

/**
 * 攻击日志服务
 * 提供管理XSS攻击日志的功能
 */
@Service
@RequiredArgsConstructor
public class AttackLogService {

    private final AttackLogRepository attackLogRepository;

    /**
     * 保存攻击日志
     * 
     * @param attackLog 攻击日志
     * @return 保存后的日志（带ID）
     */
    public AttackLog saveLog(AttackLog attackLog) {
        // 设置时间戳
        if (attackLog.getTimestamp() == null) {
            attackLog.setTimestamp(LocalDateTime.now());
        }
        
        // 保存日志
        return attackLogRepository.save(attackLog);
    }

    /**
     * 获取所有攻击日志
     * 
     * @return 攻击日志列表
     */
    public List<AttackLog> getAllLogs() {
        return attackLogRepository.findAll();
    }

    /**
     * 根据严重程度获取攻击日志
     * 
     * @param severity 严重程度
     * @return 攻击日志列表
     */
    public List<AttackLog> getLogsBySeverity(String severity) {
        return attackLogRepository.findBySeverity(severity);
    }

    /**
     * 根据来源获取攻击日志
     * 
     * @param source 来源
     * @return 攻击日志列表
     */
    public List<AttackLog> getLogsBySource(String source) {
        return attackLogRepository.findBySource(source);
    }

    /**
     * 获取日志统计信息
     * 
     * @return 统计信息
     */
    public Map<String, Long> getLogsStats() {
        Map<String, Long> stats = new HashMap<>();
        
        // 按严重程度统计
        stats.put("highSeverity", attackLogRepository.countBySeverity("high"));
        stats.put("mediumSeverity", attackLogRepository.countBySeverity("medium"));
        stats.put("lowSeverity", attackLogRepository.countBySeverity("low"));
        
        // 按是否阻止统计
        stats.put("blocked", countByBlocked(true));
        stats.put("notBlocked", countByBlocked(false));
        
        // 总数
        stats.put("total", attackLogRepository.count());
        
        return stats;
    }

    /**
     * 删除所有日志
     */
    public void deleteAllLogs() {
        attackLogRepository.deleteAll();
    }

    /**
     * 统计被阻止或未被阻止的日志数量
     * 
     * @param blocked 是否被阻止
     * @return 日志数量
     */
    private long countByBlocked(boolean blocked) {
        return attackLogRepository.findAll().stream()
                .filter(log -> log.isBlocked() == blocked)
                .count();
    }

    /**
     * 获取最新的攻击日志
     * 
     * @param count 返回的记录数
     * @return 最新的攻击日志列表
     */
    public List<AttackLog> getLatestLogs(int count) {
        return attackLogRepository.findAll(org.springframework.data.domain.PageRequest.of(0, count, 
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "timestamp")))
                .getContent();
    }

    /**
     * 根据ID获取攻击日志
     * 
     * @param id 日志ID
     * @return 攻击日志（可选）
     */
    public java.util.Optional<AttackLog> getLogById(Long id) {
        return attackLogRepository.findById(id);
    }
} 