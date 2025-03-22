package com.xssdefense.system.controller;

import com.xssdefense.system.model.DefenseConfig;
import com.xssdefense.system.service.DefenseConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 防御配置控制器
 * 提供API端点用于管理XSS防御配置
 */
@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class DefenseConfigController {

    private final DefenseConfigService defenseConfigService;

    /**
     * 获取当前防御配置
     * 
     * @return 当前防御配置
     */
    @GetMapping
    public ResponseEntity<DefenseConfig> getDefenseConfig() {
        return ResponseEntity.ok(defenseConfigService.getConfig());
    }

    /**
     * 更新防御配置
     * 
     * @param config 新的防御配置
     * @return 更新后的防御配置
     */
    @PutMapping
    public ResponseEntity<DefenseConfig> updateDefenseConfig(@RequestBody DefenseConfig config) {
        return ResponseEntity.ok(defenseConfigService.updateConfig(config));
    }
} 