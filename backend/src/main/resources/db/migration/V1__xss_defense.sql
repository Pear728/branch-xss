-- 1. XSS攻击日志表
CREATE TABLE xss_attack_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payload TEXT NOT NULL COMMENT '攻击载荷',
    source_ip VARCHAR(50) NOT NULL COMMENT '来源IP',
    target_url VARCHAR(255) NOT NULL COMMENT '目标URL',
    timestamp DATETIME NOT NULL COMMENT '时间戳',
    threat_score DOUBLE NOT NULL COMMENT '威胁评分',
    attack_details TEXT COMMENT '攻击详情',
    blocked BOOLEAN DEFAULT TRUE COMMENT '是否已阻止'
);

-- 2. 防御规则表
CREATE TABLE xss_defense_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_pattern TEXT NOT NULL COMMENT '规则模式',
    priority INT NOT NULL DEFAULT 0 COMMENT '优先级',
    action ENUM('BLOCK', 'LOG', 'SANITIZE') NOT NULL COMMENT '动作',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间'
);

-- 3. CSP配置表
CREATE TABLE content_security_policies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_name VARCHAR(100) NOT NULL COMMENT '策略名称',
    default_src VARCHAR(255) NOT NULL COMMENT '默认源',
    script_src VARCHAR(255) COMMENT '脚本源',
    style_src VARCHAR(255) COMMENT '样式源',
    img_src VARCHAR(255) COMMENT '图片源',
    connect_src VARCHAR(255) COMMENT '连接源',
    font_src VARCHAR(255) COMMENT '字体源',
    object_src VARCHAR(255) COMMENT '对象源',
    media_src VARCHAR(255) COMMENT '媒体源',
    report_uri VARCHAR(255) COMMENT '报告URI',
    is_active BOOLEAN DEFAULT FALSE COMMENT '是否激活',
    updated_at DATETIME NOT NULL COMMENT '更新时间'
);

-- 4. CSP违规日志表
CREATE TABLE csp_violation_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_uri VARCHAR(255) NOT NULL COMMENT '文档URI',
    violated_directive VARCHAR(100) NOT NULL COMMENT '违反的指令',
    blocked_uri VARCHAR(255) COMMENT '被阻止的URI',
    source_ip VARCHAR(50) NOT NULL COMMENT '来源IP',
    user_agent VARCHAR(255) COMMENT '用户代理',
    timestamp DATETIME NOT NULL COMMENT '时间戳'
);

-- 5. 机器学习模型记录表
CREATE TABLE ml_model_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL COMMENT '模型名称',
    accuracy DOUBLE NOT NULL COMMENT '准确率',
    false_positive_rate DOUBLE NOT NULL COMMENT '误报率',
    training_data_size INT NOT NULL COMMENT '训练数据量',
    feature_count INT NOT NULL COMMENT '特征数量',
    is_active BOOLEAN DEFAULT FALSE COMMENT '是否激活',
    trained_at DATETIME NOT NULL COMMENT '训练时间',
    model_binary LONGBLOB COMMENT '模型二进制'
);

-- 6. 初始化防御规则
INSERT INTO xss_defense_rules (rule_name, rule_pattern, priority, action, enabled, created_at, updated_at) VALUES
('基本脚本标签', '<script>(.*?)</script>', 10, 'BLOCK', TRUE, NOW(), NOW()),
('JavaScript协议', 'javascript:', 9, 'BLOCK', TRUE, NOW(), NOW()),
('事件处理程序', 'on(load|click|mouseover)=', 8, 'SANITIZE', TRUE, NOW(), NOW()),
('IMG标签XSS', '<img(.*?)onerror=', 7, 'BLOCK', TRUE, NOW(), NOW()),
('数据URI', 'data:text/html', 6, 'BLOCK', TRUE, NOW(), NOW());

-- 7. 初始化CSP配置
INSERT INTO content_security_policies (
    policy_name, default_src, script_src, style_src, img_src,
    connect_src, font_src, object_src, media_src, report_uri, is_active, updated_at
) VALUES (
    '基本安全策略',
    "'self'",
    "'self'",
    "'self' 'unsafe-inline'",
    "'self'",
    "'self'",
    "'self'",
    "'none'",
    "'self'",
    '/api/security/csp-report',
    TRUE,
    NOW()
);
