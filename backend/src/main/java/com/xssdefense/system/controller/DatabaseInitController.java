package com.xssdefense.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库初始化控制器
 * 提供API端点用于数据库表结构初始化和修复
 */
@RestController
@RequestMapping("/db")
public class DatabaseInitController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 系统启动时自动执行数据库修复
     */
    @PostConstruct
    public void initialize() {
        try {
            System.out.println("正在初始化数据库表结构...");
            fixAttackLogTable();
            System.out.println("数据库表结构初始化完成");
        } catch (Exception e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 手动触发数据库表结构修复
     * 
     * @return 操作结果
     */
    @GetMapping("/fix")
    public Map<String, Object> fixDatabase() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            fixAttackLogTable();
            result.put("success", true);
            result.put("message", "数据库表结构修复成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "修复失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 修复attack_log表的ID字段
     */
    private void fixAttackLogTable() {
        try {
            // 检查表是否存在，如果不存在则创建
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS attack_log (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "attack_type VARCHAR(255), " +
                    "blocked BOOLEAN, " +
                    "defense_measure VARCHAR(255), " +
                    "ip_address VARCHAR(255), " +
                    "payload TEXT, " +
                    "prevented BOOLEAN, " +
                    "severity VARCHAR(50), " +
                    "source VARCHAR(255), " +
                    "timestamp DATETIME, " +
                    "user_agent VARCHAR(255)" +
                    ")");
            
            // 尝试修改ID字段为自增主键
            jdbcTemplate.execute("ALTER TABLE attack_log MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT");
            
            System.out.println("attack_log表结构修复完成");
        } catch (Exception e) {
            // 可能表已经正确设置，忽略特定错误
            if (e.getMessage().contains("Duplicate") || e.getMessage().contains("already exists")) {
                System.out.println("表结构已存在，无需修改");
            } else {
                throw e;  // 重新抛出其他类型的错误
            }
        }
    }
} 