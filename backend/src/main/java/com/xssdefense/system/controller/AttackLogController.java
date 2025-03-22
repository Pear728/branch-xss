package com.xssdefense.system.controller;

import com.xssdefense.system.model.AttackLog;
import com.xssdefense.system.service.AttackLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 攻击日志控制器
 * 提供API端点用于管理XSS攻击日志
 */
@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class AttackLogController {

    private final AttackLogService attackLogService;

    /**
     * 获取所有攻击日志
     * 
     * @return 攻击日志列表
     */
    @GetMapping
    public ResponseEntity<List<AttackLog>> getAllLogs() {
        return ResponseEntity.ok(attackLogService.getAllLogs());
    }

    /**
     * 根据严重程度获取攻击日志
     * 
     * @param severity 严重程度（high/medium/low）
     * @return 攻击日志列表
     */
    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<AttackLog>> getLogsBySeverity(@PathVariable String severity) {
        return ResponseEntity.ok(attackLogService.getLogsBySeverity(severity));
    }

    /**
     * 根据来源获取攻击日志
     * 
     * @param source 攻击来源
     * @return 攻击日志列表
     */
    @GetMapping("/source/{source}")
    public ResponseEntity<List<AttackLog>> getLogsBySource(@PathVariable String source) {
        return ResponseEntity.ok(attackLogService.getLogsBySource(source));
    }

    /**
     * 获取日志统计信息
     * 
     * @return 统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getLogsStats() {
        return ResponseEntity.ok(attackLogService.getLogsStats());
    }

    /**
     * 清除所有日志
     * 
     * @return 操作结果
     */
    @DeleteMapping
    public ResponseEntity<Void> clearAllLogs() {
        attackLogService.deleteAllLogs();
        return ResponseEntity.ok().build();
    }

    /**
     * 获取最新的攻击日志
     * 
     * @param count 返回的记录数量
     * @return 最新的攻击日志列表
     */
    @GetMapping("/latest")
    public ResponseEntity<List<AttackLog>> getLatestLogs(@RequestParam(defaultValue = "1") int count) {
        return ResponseEntity.ok(attackLogService.getLatestLogs(count));
    }

    /**
     * 获取一条攻击日志的详细信息
     * 
     * @param id 日志ID
     * @return 攻击日志详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<AttackLog> getLogById(@PathVariable Long id) {
        return attackLogService.getLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 