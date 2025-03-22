/**
 * XSS攻击检测与防御工具类
 */

// 常见XSS向量示例
export const xssVectors = [
  {
    name: '基本脚本',
    payload: '<script>alert("XSS")</script>',
    type: 'reflected'
  },
  {
    name: '图片标签事件',
    payload: '<img src="x" onerror="alert(\'XSS\')">',
    type: 'dom'
  },
  {
    name: '链接JavaScript伪协议',
    payload: '<a href="javascript:alert(\'XSS\')">点击我</a>',
    type: 'dom'
  },
  {
    name: 'CSS表达式',
    payload: '<div style="background-image: url(javascript:alert(\'XSS\'))"></div>',
    type: 'reflected'
  },
  {
    name: '内联事件监听器',
    payload: '<body onload="alert(\'XSS\')"></body>',
    type: 'dom'
  }
];

// 检测字符串中的潜在XSS载荷
export const detectXssPayload = (input) => {
  if (!input || typeof input !== 'string') return false;
  
  // 定义可能触发XSS的模式
  const patterns = [
    /<script\b[^>]*>(.*?)<\/script>/i,
    /javascript\s*:/i,
    /on\w+\s*=/i,
    /<\s*img[^>]+onerror\s*=/i,
    /<\s*iframe/i,
    /<\s*embed/i,
    /<\s*object/i,
    /data\s*:/i,
    /document\.cookie/i,
    /document\.location/i,
    /eval\s*\(/i,
    /document\.write/i,
    /innerHTML/i,
    /fromCharCode/i
  ];
  
  // 检查是否匹配任何一种模式
  return patterns.some(pattern => pattern.test(input));
};

// 分析XSS有效载荷的危险等级
export const analyzeXssSeverity = (payload) => {
  if (!payload) return 'low';
  
  const severePatterns = [
    /document\.cookie/i,
    /localStorage/i, 
    /sessionStorage/i,
    /\.src\s*=/i,
    /xhr\.open/i,
    /fetch\s*\(/i,
    /new\s+XMLHttpRequest/i
  ];
  
  const mediumPatterns = [
    /<script/i,
    /eval\s*\(/i,
    /document\.write/i,
    /innerHTML/i
  ];
  
  if (severePatterns.some(pattern => pattern.test(payload))) {
    return 'high';
  } else if (mediumPatterns.some(pattern => pattern.test(payload))) {
    return 'medium';
  } else {
    return 'low';
  }
};

// 解析CSP策略字符串
export const parseCspPolicy = (policyString) => {
  if (!policyString) return {};
  
  const result = {};
  const directives = policyString.split(';').map(dir => dir.trim());
  
  directives.forEach(directive => {
    if (directive) {
      const [key, ...values] = directive.split(/\s+/);
      result[key] = values;
    }
  });
  
  return result;
}; 