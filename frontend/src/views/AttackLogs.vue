<template>
  <div class="logs-container">
    <h2 class="logs-title">XSS攻击日志</h2>

    <el-card>
      <template #header>
        <div class="card-header">
          <h3>攻击记录</h3>
          <div>
            <el-button type="primary" @click="refreshLogs" :loading="isLoading">刷新</el-button>
            <el-button type="danger" @click="clearLogs">清空日志</el-button>
          </div>
        </div>
      </template>

      <div v-if="isLoading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="attackLogs.length === 0" class="empty-logs">
        <el-empty description="暂无攻击日志记录" />
      </div>

      <div v-else>
        <el-table
          :data="attackLogs"
          style="width: 100%"
          :default-sort="{ prop: 'timestamp', order: 'descending' }"
        >
          <el-table-column prop="timestamp" label="时间" sortable width="180">
            <template #default="scope">
              {{ formatDate(scope.row.timestamp) }}
            </template>
          </el-table-column>

          <el-table-column prop="severity" label="危险等级" width="100">
            <template #default="scope">
              <el-tag :type="getSeverityType(scope.row.severity)">
                {{ scope.row.severity }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="source" label="来源" width="120" />

          <el-table-column prop="prevented" label="防御状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.prevented ? 'success' : 'danger'">
                {{ scope.row.prevented ? '已防御' : '未防御' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="payload" label="攻击载荷">
            <template #default="scope">
              <el-tooltip
                :content="scope.row.payload"
                placement="top"
                :show-after="300"
              >
                <span class="ellipsis-text">{{ scope.row.payload }}</span>
              </el-tooltip>
            </template>
          </el-table-column>

          <el-table-column fixed="right" label="操作" width="100">
            <template #default="scope">
              <el-button link type="primary" @click="showLogDetail(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 攻击详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="攻击详情"
      width="60%"
    >
      <el-descriptions v-if="selectedLog" direction="vertical" :column="1" border>
        <el-descriptions-item label="ID">{{ selectedLog.id }}</el-descriptions-item>
        <el-descriptions-item label="时间">{{ formatDate(selectedLog.timestamp) }}</el-descriptions-item>
        <el-descriptions-item label="危险等级">
          <el-tag :type="getSeverityType(selectedLog.severity)">{{ selectedLog.severity }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="来源">{{ selectedLog.source }}</el-descriptions-item>
        <el-descriptions-item label="防御状态">
          <el-tag :type="selectedLog.prevented ? 'success' : 'danger'">
            {{ selectedLog.prevented ? '已防御' : '未防御' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="攻击载荷">
          <div class="payload-container">
            <pre>{{ selectedLog.payload }}</pre>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="攻击分析">
          {{ getAttackAnalysis(selectedLog) }}
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <span>
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { analyzeXssSeverity } from '../utils/xssUtils'

const store = useStore()
const isLoading = computed(() => store.state.isLoading)
const attackLogs = computed(() => store.state.attackLogs)
const dialogVisible = ref(false)
const selectedLog = ref(null)

// 获取攻击日志
const refreshLogs = async () => {
  await store.dispatch('fetchAttackLogs')
}

// 清空日志
const clearLogs = async () => {
  try {
    // 调用API清空日志
    await store.dispatch('clearAttackLogs')
    ElMessage.success('日志已清空')
  } catch (error) {
    console.error('Failed to clear logs:', error)
    ElMessage.error('清空日志失败')
  }
}

// 显示日志详情
const showLogDetail = (log) => {
  selectedLog.value = log
  dialogVisible.value = true
}

// 获取攻击分析
const getAttackAnalysis = (log) => {
  const payloadType = getAttackType(log.payload)
  const severity = log.severity || analyzeXssSeverity(log.payload)

  if (severity === 'high') {
    return `此攻击为${payloadType}，危险等级高。它可能尝试窃取敏感信息（如cookies）或在用户的浏览器上执行恶意代码。${log.prevented ? '系统已成功拦截此攻击。' : '警告：此攻击未被防御！'}`
  } else if (severity === 'medium') {
    return `此攻击为${payloadType}，危险等级中。它可能尝试修改页面内容或执行有限的恶意脚本。${log.prevented ? '系统已成功拦截此攻击。' : '警告：此攻击未被防御！'}`
  } else {
    return `此攻击为${payloadType}，危险等级低。它可能是简单的测试或低风险的脚本注入尝试。${log.prevented ? '系统已成功拦截此攻击。' : '警告：此攻击未被防御！'}`
  }
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
const getAttackType = (payload) => {
  if (!payload) return '未知攻击'

  if (payload.includes('<script')) {
    return '脚本注入攻击'
  } else if (payload.includes('onerror') || payload.includes('onload')) {
    return '事件处理注入攻击'
  } else if (payload.includes('javascript:')) {
    return 'JavaScript 伪协议攻击'
  } else {
    return 'XSS攻击'
  }
}

// 日期格式化
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString()
}

onMounted(() => {
  refreshLogs()
})
</script>

<style scoped>
.logs-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.logs-title {
  margin-top: 20px;
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading-container {
  padding: 20px;
}

.empty-logs {
  padding: 40px 0;
}

.ellipsis-text {
  display: inline-block;
  width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.payload-container {
  max-height: 300px;
  overflow-y: auto;
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
}

pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  margin: 0;
}
</style>
