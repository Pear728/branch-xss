<!-- eslint-disable -->
<template>
  <div class="xss-test-container">
    <h2 class="xss-test-title">XSS攻击测试</h2>

    <el-card class="test-card">
      <template #header>
        <div class="card-header">
          <h3>测试XSS攻击载荷</h3>
        </div>
      </template>

      <el-form :model="testForm" label-width="120px">
        <el-form-item label="输入类型">
          <el-radio-group v-model="testForm.inputType">
            <el-radio label="manual">手动输入</el-radio>
            <el-radio label="predefined">预设载荷</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="testForm.inputType === 'predefined'" label="选择预设载荷">
          <el-select v-model="testForm.selectedVector" placeholder="选择XSS载荷" style="width: 100%">
            <el-option
              v-for="vector in xssVectors"
              :key="vector.payload"
              :label="vector.name"
              :value="vector.payload"
            >
              <span>{{ vector.name }}</span>
              <span class="vector-type">{{ vector.type }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item v-if="testForm.inputType === 'manual'" label="XSS载荷">
          <SafeInput
            v-model="testForm.payload"
            placeholder="输入一个XSS载荷..."
            :sanitize="false"
            @xss-detected="handleXssDetected"
          />
        </el-form-item>

        <el-form-item label="防御设置">
          <el-checkbox v-model="testForm.defenseOptions.inputValidation">输入验证</el-checkbox>
          <el-checkbox v-model="testForm.defenseOptions.outputEncoding">输出编码</el-checkbox>
          <el-checkbox v-model="testForm.defenseOptions.csp">内容安全策略</el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="runTest" :loading="loading">运行测试</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="testResult" class="result-card">
      <template #header>
        <div class="card-header">
          <h3>测试结果</h3>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="12">
          <div class="result-section">
            <h4>防御前 (原始输入)</h4>
            <div class="danger-content">
              <pre>{{ testResult.originalPayload }}</pre>
            </div>
            <div class="render-container">
              <h5>渲染结果:</h5>
              <div class="render-box" v-html="testResult.originalPayload"></div>
            </div>
          </div>
        </el-col>

        <el-col :span="12">
          <div class="result-section">
            <h4>防御后</h4>
            <div class="safe-content">
              <pre>{{ testResult.sanitizedPayload }}</pre>
            </div>
            <div class="render-container">
              <h5>渲染结果:</h5>
              <div class="render-box" v-safe-html="testResult.sanitizedPayload"></div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-divider></el-divider>

      <div class="analysis-section">
        <h4>攻击分析</h4>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="攻击类型">{{ testResult.attackType }}</el-descriptions-item>
          <el-descriptions-item label="危险等级">
            <el-tag :type="getSeverityType(testResult.severity)">{{ testResult.severity }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="防御效果">
            <el-tag :type="testResult.prevented ? 'success' : 'danger'">
              {{ testResult.prevented ? '成功防御' : '防御失败' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="分析说明">{{ testResult.description }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

    <!-- 新增：攻击日志记录展示 -->
    <el-card v-if="latestLog" class="log-card">
      <template #header>
        <div class="card-header">
          <h3>数据库存储记录</h3>
          <div>
            <el-button type="primary" link @click="refreshLatestLog">刷新</el-button>
            <el-button type="success" link @click="viewAllLogs">查看全部日志</el-button>
          </div>
        </div>
      </template>
      
      <el-alert
        title="XSS测试数据已成功存入数据库"
        type="success"
        :closable="false"
        show-icon
      >
        <template #default>
          数据库记录ID: <strong>{{ latestLog.id }}</strong>，可通过API <code>/logs/{{ latestLog.id }}</code> 查询
        </template>
      </el-alert>
      
      <el-divider content-position="left">数据库记录详情</el-divider>
      
      <el-descriptions :column="1" border size="small">
        <el-descriptions-item label="记录ID">{{ latestLog.id }}</el-descriptions-item>
        <el-descriptions-item label="记录时间">{{ formatDate(latestLog.timestamp) }}</el-descriptions-item>
        <el-descriptions-item label="来源">{{ latestLog.source }}</el-descriptions-item>
        <el-descriptions-item label="载荷">
          <el-tag size="small">{{ latestLog.payload }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="攻击类型">{{ latestLog.attackType }}</el-descriptions-item>
        <el-descriptions-item label="危险等级">
          <el-tag :type="getSeverityType(latestLog.severity)">{{ latestLog.severity }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="防御状态">
          <el-tag :type="latestLog.prevented ? 'success' : 'danger'">
            {{ latestLog.prevented ? '已防御' : '未防御' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="防御措施">{{ latestLog.defenseMeasure }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ latestLog.ipAddress }}</el-descriptions-item>
        <el-descriptions-item label="用户代理">{{ latestLog.userAgent }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import SafeInput from '../components/SafeInput.vue'
import { ElMessage } from 'element-plus'
import { xssVectors} from '../utils/xssUtils'

const store = useStore()
const router = useRouter()
const loading = ref(false)
const testResult = ref(null)

// 测试表单
const testForm = ref({
  inputType: 'predefined',
  selectedVector: '',
  payload: '',
  defenseOptions: {
    inputValidation: true,
    outputEncoding: true,
    csp: true
  }
})

// 监听选择的向量改变
watch(() => testForm.value.selectedVector, (newVal) => {
  if (newVal) {
    testForm.value.payload = newVal
  }
})

// 处理XSS检测回调
const handleXssDetected = (payload) => {
  console.log('XSS detected:', payload)
  // 这里可以添加其他处理逻辑
}

// 获取最新的攻击日志记录
const attackLogs = computed(() => store.state.attackLogs)
const latestLog = computed(() => attackLogs.value.length > 0 ? attackLogs.value[0] : null)

// 从数据库获取最新日志
const refreshLatestLog = async () => {
  const log = await store.dispatch('fetchLatestAttackLog')
  if (!log) {
    ElMessage.warning('未找到日志记录')
  }
}

// 导航到日志页面
const viewAllLogs = () => {
  router.push('/logs')
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString()
}

// 运行测试
const runTest = async () => {
  loading.value = true
  testResult.value = null

  const payload = testForm.value.inputType === 'predefined'
    ? testForm.value.selectedVector
    : testForm.value.payload

  if (!payload) {
    ElMessage.warning('请输入或选择XSS载荷')
    loading.value = false
    return
  }

  try {
    // 调用真实的后端API
    const response = await store.dispatch('runXssTest', {
      input: payload,
      defenseOptions: {
        htmlEscape: testForm.value.defenseOptions.outputEncoding,
        removeJsEvents: testForm.value.defenseOptions.inputValidation,
        removeDangerousTags: testForm.value.defenseOptions.inputValidation
      }
    })
    
    if (response) {
      testResult.value = {
        originalPayload: response.input,
        sanitizedPayload: response.sanitizedOutput,
        attackType: response.attackType,
        severity: response.severity,
        prevented: response.prevented,
        description: response.description
      }
      
      // 刷新攻击日志数据
      await store.dispatch('fetchAttackLogs')
      
      // 额外获取最新的数据库记录
      setTimeout(async () => {
        const latestRecord = await store.dispatch('fetchLatestAttackLog')
        if (latestRecord) {
          ElMessage.success(`测试数据已存入数据库，记录ID: ${latestRecord.id}`)
        }
      }, 500)
    }
    
    loading.value = false
  } catch (error) {
    console.error('Failed to run test:', error)
    let errorMessage = '测试执行失败，请查看控制台日志'
    
    // 获取详细错误信息（如果有）
    if (error.response && error.response.data) {
      if (error.response.data.message) {
        errorMessage = error.response.data.message
      }
      console.error('服务器返回错误:', error.response.data)
    }
    
    ElMessage.error(errorMessage)
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  testForm.value = {
    inputType: 'predefined',
    selectedVector: '',
    payload: '',
    defenseOptions: {
      inputValidation: true,
      outputEncoding: true,
      csp: true
    }
  }
  testResult.value = null
}

// 辅助函数：获取危险等级对应的tag类型
const getSeverityType = (severity) => {
  switch(severity) {
    case 'high': return 'danger'
    case 'medium': return 'warning'
    default: return 'info'
  }
}
// 辅助函数：确定攻击类型
// const getAttackType = (payload) => {
//   if (!payload) return '未知攻击类型'
  
//   if (payload.includes('<script')) {
//     return '脚本注入 (Reflected XSS)'
//   } else if (payload.includes('onerror') || payload.includes('onload')) {
//     return '事件处理注入 (DOM XSS)'
//   } else if (payload.includes('javascript:')) {
//     return 'JavaScript 伪协议 (DOM XSS)'
//   } else {
//     return '通用XSS攻击'
//   }
// }

// // 辅助函数：根据载荷生成说明
// const getDescriptionForPayload = (payload, severity) => {
//   if (severity === 'high') {
//     return '此载荷尝试执行高危脚本，可能窃取cookie或执行恶意代码。'
//   } else if (severity === 'medium') {
//     return '此载荷尝试注入HTML或JavaScript代码，可能导致页面内容被篡改。'
//   } else {
//     return '此载荷包含潜在的XSS模式，但危险性较低。'
//   }
// }
</script>

<style scoped>
.xss-test-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.xss-test-title {
  margin-top: 20px;
}

.test-card, .result-card {
  height: 100%;
  margin-top: 20px;
  margin-bottom: 20px;
}

.vector-type {
  float: right;
  color: #909399;
  font-size: 0.9em;
}

.result-section {
  margin-bottom: 20px;
}

.render-container {
  margin-top: 10px;
}

.render-box {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
  background-color: #fff;
  min-height: 100px;
}

.danger-content {
  background-color: #fff9f9;
  border: 1px solid #ffebeb;
  border-radius: 4px;
  padding: 10px;
  color: #f56c6c;
}

.safe-content {
  background-color: #f0f9eb;
  border: 1px solid #e1f3d8;
  border-radius: 4px;
  padding: 10px;
  color: #67c23a;
}

.analysis-section {
  margin-top: 20px;
}

pre {
  white-space: pre-wrap;
  word-wrap: break-word;
}

.log-card {
  margin-top: 20px;
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
