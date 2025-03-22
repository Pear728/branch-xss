<template>
  <div class="home-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <div class="welcome-section">
          <h2>欢迎使用XSS攻击分析及防御系统</h2>
          <p>本系统提供XSS攻击检测、防御和分析功能，帮助您保护Web应用程序免受XSS攻击威胁。</p>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="center-row">
      <el-col :span="10" v-for="(feature, index) in features" :key="index">
        <el-card class="feature-card">
          <template #header>
            <div class="card-header">
              <h3>{{ feature.title }}</h3>
            </div>
          </template>
          <div class="card-content">
            <p>{{ feature.description }}</p>
            <el-button type="primary" @click="navigateTo(feature.link)">
              {{ feature.buttonText }}
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>XSS攻击统计</h3>
            </div>
          </template>
          <div v-if="isLoading" class="loading-container">
            <el-skeleton :rows="3" animated />
          </div>
          <div v-else class="stat-container">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="stat-item">
                  <h4>攻击总数</h4>
                  <div class="stat-value">{{ attackStats.total }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-item">
                  <h4>高危攻击</h4>
                  <div class="stat-value danger">{{ attackStats.high }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-item">
                  <h4>中危攻击</h4>
                  <div class="stat-value warning">{{ attackStats.medium }}</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

const router = useRouter()
const store = useStore()
const isLoading = computed(() => store.state.isLoading)

// 系统功能
const features = [
  {
    title: 'XSS测试',
    description: '测试不同类型的XSS攻击载荷，并查看防御效果。',
    link: '/test',
    buttonText: '开始测试'
  },
  {
    title: '攻击日志',
    description: '查看系统检测到的XSS攻击尝试记录及分析。',
    link: '/logs',
    buttonText: '查看日志'
  },
  {
    title: '防御配置',
    description: '配置系统的XSS防御策略，包括输入验证、输出编码和CSP。',
    link: '/config',
    buttonText: '调整配置'
  },
  {
    title: '数据库记录',
    description: '查看数据库连接信息和攻击日志记录详情。',
    link: '/dbinfo',
    buttonText: '查看数据库'
  }
]

// 攻击统计数据
const attackStats = ref({
  total: 0,
  high: 0,
  medium: 0
})

// 获取攻击统计
const fetchAttackStats = async () => {
  try {
    await store.dispatch('fetchAttackLogs')
    const logs = store.state.attackLogs

    attackStats.value = {
      total: logs.length,
      high: logs.filter(log => log.severity === 'high').length,
      medium: logs.filter(log => log.severity === 'medium').length
    }
  } catch (error) {
    console.error('Failed to fetch attack stats:', error)
  }
}

// 页面导航
const navigateTo = (path) => {
  router.push(path)
}

onMounted(() => {
  fetchAttackStats()
})
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.welcome-section {
  margin-top: 50px;
  margin-bottom: 30px;
  text-align: center;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.center-row {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.feature-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  margin-bottom: 20px;
}

.center-row .el-col {
  margin-bottom: 20px; /* 添加卡片之间的上下间距 */
}

.card-content {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 120px;
}

.mt-20 {
  margin-top: 20px;
}

.stat-container {
  padding: 10px;
}

.stat-item {
  text-align: center;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.stat-value {
  font-size: 2rem;
  font-weight: bold;
  color: #409eff;
}

.stat-value.danger {
  color: #f56c6c;
}

.stat-value.warning {
  color: #e6a23c;
}

.loading-container {
  padding: 20px;
}
</style>
