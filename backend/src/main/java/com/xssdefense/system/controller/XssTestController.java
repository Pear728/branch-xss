package com.xssdefense.system.controller;

import com.xssdefense.system.model.XssTestRequest;
import com.xssdefense.system.model.XssTestResult;
import com.xssdefense.system.service.XssDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

/**
 * XSS测试控制器
 * 提供API端点用于测试XSS防御功能
 */
@RestController
@RequestMapping("/xss")
@RequiredArgsConstructor
public class XssTestController {

    private final XssDetectionService xssDetectionService;

    /**
     * 检测输入是否包含XSS载荷
     * 
     * @param input 用户输入的内容
     * @return 检测结果，包含是否检测到XSS以及相关信息
     */
    @GetMapping("/detect")
    public ResponseEntity<XssTestResult> detectXss(@RequestParam String input) {
        boolean detected = xssDetectionService.detectXssPayload(input);
        String severity = "none";
        String attackType = "";
        
        if (detected) {
            severity = xssDetectionService.analyzeXssSeverity(input);
            attackType = xssDetectionService.getAttackType(input);
        }
        
        XssTestResult result = new XssTestResult();
        result.setDetected(detected);
        result.setInput(input);
        result.setSeverity(severity);
        result.setAttackType(attackType);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 测试XSS防御功能
     * 
     * @param request 测试请求，包含输入内容和防御选项
     * @return 测试结果
     */
    @PostMapping("/test")
    public ResponseEntity<?> testXssDefense(@RequestBody XssTestRequest request) {
        try {
            XssTestResult result = xssDetectionService.processXssTest(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 记录详细错误信息
            System.err.println("XSS测试处理失败: " + e.getMessage());
            e.printStackTrace();
            
            // 创建错误响应
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "处理XSS测试请求时出错: " + e.getMessage());
            
            // 返回带有错误信息的响应
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}