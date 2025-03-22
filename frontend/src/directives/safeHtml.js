/**
 * 安全HTML渲染指令 - 用于防止XSS攻击
 */

const sanitizeHtml = (html) => {
  // 简单的HTML清理函数
  if (!html) return '';
  
  // 替换可能导致XSS的常见模式
  return html
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
    .replace(/`/g, '&#96;')
    .replace(/\(/g, '&#40;')
    .replace(/\)/g, '&#41;')
    .replace(/javascript:/gi, '')
    .replace(/on\w+=/gi, '');
};

export const SafeHtmlDirective = {
  beforeMount(el, binding) {
    el.innerHTML = sanitizeHtml(binding.value);
  },
  updated(el, binding) {
    el.innerHTML = sanitizeHtml(binding.value);
  }
};

// 导出净化函数供其他组件使用
export const sanitize = sanitizeHtml; 