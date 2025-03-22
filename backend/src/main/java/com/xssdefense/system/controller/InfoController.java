package com.xssdefense.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统信息控制器
 * 提供API端点用于查询系统和数据库信息
 */
@RestController
@RequestMapping("/info")
public class InfoController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 获取数据库连接信息
     * 
     * @return 数据库信息
     */
    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> getDatabaseInfo() {
        Map<String, Object> dbInfo = new HashMap<>();
        
        try {
            dbInfo.put("url", jdbcTemplate.getDataSource().getConnection().getMetaData().getURL());
            dbInfo.put("product", jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductName());
            dbInfo.put("version", jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductVersion());
            dbInfo.put("driver", jdbcTemplate.getDataSource().getConnection().getMetaData().getDriverName());
            dbInfo.put("status", "connected");
            
            // 查询攻击日志表记录数
            Integer logsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM attack_log", Integer.class);
            dbInfo.put("logsCount", logsCount);
            
            return ResponseEntity.ok(dbInfo);
        } catch (Exception e) {
            dbInfo.put("status", "error");
            dbInfo.put("error", e.getMessage());
            return ResponseEntity.ok(dbInfo);
        }
    }
    
    /**
     * 获取系统信息
     * 
     * @return 系统信息
     */
    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> sysInfo = new HashMap<>();
        
        sysInfo.put("javaVersion", System.getProperty("java.version"));
        sysInfo.put("javaVendor", System.getProperty("java.vendor"));
        sysInfo.put("osName", System.getProperty("os.name"));
        sysInfo.put("osVersion", System.getProperty("os.version"));
        sysInfo.put("osArch", System.getProperty("os.arch"));
        
        return ResponseEntity.ok(sysInfo);
    }
} 