package com.xssdefense.system.filter;

import com.xssdefense.system.service.AttackLogService;
import com.xssdefense.system.service.DefenseConfigService;
import com.xssdefense.system.service.XssDetectionService;
import com.xssdefense.system.model.AttackLog;
import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * XSS过滤器
 * 用于拦截HTTP请求并检测XSS攻击载荷
 */
@Component
@RequiredArgsConstructor
public class XssFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(XssFilter.class);

    private final AttackLogService attackLogService;
    private final XssDetectionService xssDetectionService;
    private final DefenseConfigService configService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 检查URL是否在白名单中，如果在则不进行XSS过滤
        String requestURI = request.getRequestURI();
        if (configService.isUrlWhitelisted(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 创建XSS请求包装器
        XssRequestWrapper wrappedRequest = new XssRequestWrapper(request, xssDetectionService);

        try {
            // 继续过滤链处理
            filterChain.doFilter(wrappedRequest, response);

            // 如果检测到XSS攻击，记录日志
            if (wrappedRequest.isXssDetected()) {
                logXssAttack(request, wrappedRequest);
            }
        } catch (Exception e) {
            logger.error("XSS过滤器处理请求时发生错误", e);
            throw e;
        }
    }

    /**
     * 记录XSS攻击
     *
     * @param request 原始请求
     * @param wrappedRequest XSS请求包装器
     */
    private void logXssAttack(HttpServletRequest request, XssRequestWrapper wrappedRequest) {
        try {
            AttackLog log = new AttackLog();
            log.setTimestamp(LocalDateTime.now());
            log.setPayload(wrappedRequest.getAttackDetails().get("payload"));
            log.setSource(wrappedRequest.getAttackDetails().get("source"));
            log.setSeverity(wrappedRequest.getAttackDetails().get("severity"));
            log.setIpAddress(request.getRemoteAddr());
            log.setUserAgent(request.getHeader("User-Agent"));
            log.setAttackType(xssDetectionService.getAttackType(wrappedRequest.getAttackDetails().get("payload")));
            log.setBlocked(true);
            log.setDefenseMeasure("输入验证 + HTML转义");

            attackLogService.saveLog(log);

            logger.warn("检测到XSS攻击: {} - 严重程度: {}", log.getPayload(), log.getSeverity());
        } catch (Exception e) {
            logger.error("无法记录XSS攻击日志", e);
        }
    }
}
