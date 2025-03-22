import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 30000,
  withCredentials: true,  // 允许跨域请求携带凭证
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 可以在此处添加身份验证令牌等
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    console.log(`API请求成功: ${response.config.method.toUpperCase()} ${response.config.url}`);
    return response
  },
  error => {
    // 详细记录错误信息
    console.error('API请求错误:', error.message);
    
    // 记录请求详情
    if (error.config) {
      console.error(`请求详情: ${error.config.method.toUpperCase()} ${error.config.url}`);
    }
    
    if (error.response) {
      // 服务器返回错误状态码
      console.error('错误状态:', error.response.status);
      console.error('错误数据:', error.response.data);
      
      if (error.response.status === 401) {
        // 处理未授权错误
        console.warn('未授权访问，可能需要登录');
      } else if (error.response.status >= 500) {
        console.error('服务器内部错误，请检查后端日志');
      }
    } else if (error.request) {
      // 请求发送但未收到响应
      console.error('未收到响应，可能是CORS或网络问题');
      console.error('请确认后端服务是否正常运行');
    } else {
      // 请求设置时发生错误
      console.error('请求配置错误:', error.message);
    }
    
    // 添加错误类型标记，便于UI处理
    if (error.code === 'ECONNABORTED') {
      error.isTimeout = true;
    }
    
    return Promise.reject(error)
  }
)

// 定义API函数
export default {
  // 获取攻击日志
  getAttackLogs() {
    return api.get('/logs')
  },
  
  // 获取最新的攻击日志
  getLatestAttackLog(count = 1) {
    return api.get(`/logs/latest?count=${count}`)
  },
  
  // 根据ID获取攻击日志详情
  getAttackLogById(id) {
    return api.get(`/logs/${id}`)
  },
  
  // 获取防御配置
  getDefenseConfig() {
    return api.get('/config')
  },
  
  // 更新防御配置
  updateDefenseConfig(config) {
    return api.post('/config', config)
  },
  
  // 运行XSS测试
  runXssTest(payload) {
    return api.post('/xss/test', payload)
  },
  
  // 清空攻击日志
  clearAttackLogs() {
    return api.delete('/logs')
  },
  
  // 获取数据库信息
  getDatabaseInfo() {
    return api.get('/info/database')
  },
  
  // 获取系统信息
  getSystemInfo() {
    return api.get('/info/system')
  },
  
  // 修复数据库
  fixDatabase() {
    return api.get('/db/fix')
  }
} 