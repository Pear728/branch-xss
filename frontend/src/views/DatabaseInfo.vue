<template>
  <div class="dbinfo-container">
    <h2 class="dbinfo-title">数据库信息</h2>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h3>数据库连接信息</h3>
              <div>
                <el-button type="primary" @click="refreshDbInfo" :loading="loading">刷新</el-button>
                <el-button type="success" @click="fixDatabase" :loading="fixLoading">修复数据库</el-button>
              </div>
            </div>
          </template>

          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>

          <div v-else-if="dbInfo">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="连接状态">
                <el-tag :type="dbInfo.status === 'connected' ? 'success' : 'danger'">
                  {{ dbInfo.status === 'connected' ? '已连接' : '连接失败' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="数据库URL">{{ dbInfo.url }}</el-descriptions-item>
              <el-descriptions-item label="数据库产品">{{ dbInfo.product }}</el-descriptions-item>
              <el-descriptions-item label="数据库版本">{{ dbInfo.version }}</el-descriptions-item>
              <el-descriptions-item label="驱动">{{ dbInfo.driver }}</el-descriptions-item>
              <el-descriptions-item label="攻击日志记录数">
                <el-tag type="info">{{ dbInfo.logsCount || 0 }}</el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div v-else class="empty-info">
            <el-empty description="无法获取数据库信息" />
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h3>系统信息</h3>
            </div>
          </template>

          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>

          <div v-else-if="sysInfo">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="Java版本">{{ sysInfo.javaVersion }}</el-descriptions-item>
              <el-descriptions-item label="Java供应商">{{ sysInfo.javaVendor }}</el-descriptions-item>
              <el-descriptions-item label="操作系统">{{ sysInfo.osName }}</el-descriptions-item>
              <el-descriptions-item label="系统版本">{{ sysInfo.osVersion }}</el-descriptions-item>
              <el-descriptions-item label="系统架构">{{ sysInfo.osArch }}</el-descriptions-item>
            </el-descriptions>
          </div>

          <div v-else class="empty-info">
            <el-empty description="无法获取系统信息" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card v-if="dbInfo && dbInfo.status === 'connected'" class="query-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>查询攻击日志记录</h3>
        </div>
      </template>

      <div class="query-form">
        <el-form :inline="true">
          <el-form-item label="记录ID">
            <el-input v-model="logId" placeholder="输入日志ID" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchLogById" :loading="logLoading">查询</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div v-if="logDetail" class="log-detail">
        <el-alert
          title="数据库记录详情"
          type="success"
          :closable="false"
          show-icon
        />
        
        <el-descriptions :column="1" border>
          <el-descriptions-item label="记录ID">{{ logDetail.id }}</el-descriptions-item>
          <el-descriptions-item label="记录时间">{{ formatDate(logDetail.timestamp) }}</el-descriptions-item>
          <el-descriptions-item label="来源">{{ logDetail.source }}</el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ logDetail.ipAddress }}</el-descriptions-item>
          <el-descriptions-item label="攻击载荷">
            <div class="payload-container">
              <pre>{{ logDetail.payload }}</pre>
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="攻击类型">{{ logDetail.attackType }}</el-descriptions-item>
          <el-descriptions-item label="危险等级">
            <el-tag :type="getSeverityType(logDetail.severity)">{{ logDetail.severity }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="防御状态">
            <el-tag :type="logDetail.prevented ? 'success' : 'danger'">
              {{ logDetail.prevented ? '已防御' : '未防御' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="防御措施">{{ logDetail.defenseMeasure }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

const store = useStore()
const loading = ref(false)
const dbInfo = ref(null)
const sysInfo = ref(null)
const logId = ref('')
const logDetail = ref(null)
const logLoading = ref(false)
const fixLoading = ref(false)

// 获取数据库信息
const refreshDbInfo = async () => {
  loading.value = true
  try {
    dbInfo.value = await store.dispatch('getDatabaseInfo')
    sysInfo.value = await store.dispatch('getSystemInfo')
  } catch (error) {
    console.error('获取信息失败:', error)
    ElMessage.error('获取数据库信息失败')
  } finally {
    loading.value = false
  }
}

// 根据ID查询日志
const fetchLogById = async () => {
  if (!logId.value) {
    ElMessage.warning('请输入日志ID')
    return
  }
  
  logLoading.value = true
  try {
    const log = await store.dispatch('fetchAttackLogById', logId.value)
    if (log) {
      logDetail.value = log
    } else {
      ElMessage.warning(`未找到ID为${logId.value}的日志记录`)
      logDetail.value = null
    }
  } catch (error) {
    console.error('获取日志失败:', error)
    ElMessage.error('获取日志记录失败')
  } finally {
    logLoading.value = false
  }
}

// 获取危险等级对应的tag类型
const getSeverityType = (severity) => {
  switch(severity) {
    case 'high': return 'danger'
    case 'medium': return 'warning'
    default: return 'info'
  }
}

// 日期格式化
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString()
}

// 修复数据库
const fixDatabase = async () => {
  fixLoading.value = true
  try {
    await store.dispatch('fixDatabase')
    ElMessage.success('数据库修复成功')
    await refreshDbInfo()
  } catch (error) {
    console.error('修复数据库失败:', error)
    ElMessage.error('修复数据库失败')
  } finally {
    fixLoading.value = false
  }
}

onMounted(() => {
  refreshDbInfo()
})
</script>

<style scoped>
.dbinfo-container {
  margin-top: 20px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.dbinfo-title {
  margin-top: 20px;
  margin-bottom: 20px;
}

.info-card {
  height: 100%;
  margin-top: 20px;
  margin-bottom: 20px;
}

.query-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading-container {
  padding: 20px;
}

.empty-info {
  padding: 40px 0;
}

.query-form {
  margin-bottom: 20px;
}

.log-detail {
  margin-top: 20px;
}

.payload-container {
  max-height: 150px;
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