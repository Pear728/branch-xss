<template>
  <div class="config-container">
    <h2 class="config-title">XSS防御配置</h2>

    <el-card>
      <template #header>
        <div class="card-header">
          <h3>防御策略配置</h3>
        </div>
      </template>

      <el-form :model="config" label-width="180px" ref="configForm">
        <el-divider content-position="left">输入验证</el-divider>

        <el-form-item label="启用输入验证">
          <el-switch v-model="config.enableInputValidation" />
        </el-form-item>

        <el-form-item label="验证级别" v-if="config.enableInputValidation">
          <el-select v-model="config.inputValidationLevel" style="width: 100%">
            <el-option label="低 - 仅过滤基本XSS模式" value="low" />
            <el-option label="中 - 过滤常见XSS攻击向量" value="medium" />
            <el-option label="高 - 严格过滤所有可疑输入" value="high" />
          </el-select>
        </el-form-item>

        <el-divider content-position="left">输出编码</el-divider>

        <el-form-item label="启用输出编码">
          <el-switch v-model="config.enableOutputEncoding" />
        </el-form-item>

        <el-form-item label="编码上下文" v-if="config.enableOutputEncoding">
          <el-checkbox-group v-model="config.encodingContexts">
            <el-checkbox label="html">HTML上下文</el-checkbox>
            <el-checkbox label="attribute">HTML属性上下文</el-checkbox>
            <el-checkbox label="javascript">JavaScript上下文</el-checkbox>
            <el-checkbox label="url">URL上下文</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-divider content-position="left">内容安全策略 (CSP)</el-divider>

        <el-form-item label="启用CSP">
          <el-switch v-model="config.enableCsp" />
        </el-form-item>

        <el-form-item label="CSP策略" v-if="config.enableCsp">
          <el-input
            v-model="config.cspPolicy"
            type="textarea"
            :rows="3"
            placeholder="输入CSP策略，例如：default-src 'self'; script-src 'self'"
          />
        </el-form-item>

        <el-form-item label="CSP报告模式">
          <el-switch v-model="config.cspReportOnly" :disabled="!config.enableCsp" />
          <span class="hint-text">开启后，浏览器只会报告违规而不会阻止内容</span>
        </el-form-item>

        <el-divider content-position="left">Cookie安全</el-divider>

        <el-form-item label="启用HttpOnly">
          <el-switch v-model="config.enableHttpOnly" />
          <span class="hint-text">防止JavaScript访问Cookie</span>
        </el-form-item>

        <el-form-item label="启用Secure标志">
          <el-switch v-model="config.enableSecure" />
          <span class="hint-text">仅通过HTTPS发送Cookie</span>
        </el-form-item>

        <el-form-item label="启用SameSite">
          <el-select v-model="config.sameSite" style="width: 100%">
            <el-option label="None - 不启用SameSite" value="None" />
            <el-option label="Lax - 允许导航到目标网址的Get请求" value="Lax" />
            <el-option label="Strict - 仅允许同站点请求" value="Strict" />
          </el-select>
        </el-form-item>

        <el-divider></el-divider>

        <el-form-item>
          <el-button type="primary" @click="saveConfig" :loading="isLoading">保存配置</el-button>
          <el-button @click="resetConfig">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="tips-card">
      <template #header>
        <div class="card-header">
          <h3>防御策略提示</h3>
        </div>
      </template>

      <el-collapse accordion>
        <el-collapse-item title="输入验证最佳实践" name="1">
          <div>
            <p>输入验证是防御XSS攻击的第一道防线，应该根据具体场景和数据类型应用不同的验证规则：</p>
            <ul>
              <li>对于纯数字输入，使用正则表达式确保只接受数字</li>
              <li>对于URL，验证协议是否为http/https，避免javascript:伪协议</li>
              <li>对于富文本内容，使用白名单方法限制允许的HTML标签和属性</li>
            </ul>
          </div>
        </el-collapse-item>

        <el-collapse-item title="输出编码说明" name="2">
          <div>
            <p>根据输出上下文选择合适的编码方式：</p>
            <ul>
              <li>HTML上下文：将&lt;, &gt;, &amp;, &quot;, &#39;等字符转换为对应的HTML实体</li>
              <li>HTML属性上下文：除了HTML编码外，还需要对引号进行特殊处理</li>
              <li>JavaScript上下文：使用\uXXXX Unicode转义序列</li>
              <li>URL上下文：使用encodeURIComponent进行编码</li>
            </ul>
          </div>
        </el-collapse-item>

        <el-collapse-item title="CSP配置示例" name="3">
          <div>
            <p>内容安全策略(CSP)常见指令：</p>
            <ul>
              <li><code>default-src 'self';</code> - 默认只允许加载同源资源</li>
              <li><code>script-src 'self';</code> - 脚本只能从同源加载</li>
              <li><code>style-src 'self';</code> - 样式表只能从同源加载</li>
              <li><code>img-src 'self' data:;</code> - 图片可以从同源和data URI加载</li>
              <li><code>connect-src 'self';</code> - XHR, WebSockets和EventSource只能连接到同源</li>
            </ul>
            <p>推荐配置：<code>default-src 'self'; script-src 'self'; object-src 'none';</code></p>
          </div>
        </el-collapse-item>
      </el-collapse>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, onMounted, computed } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

const store = useStore()
const isLoading = computed(() => store.state.isLoading)

// 配置表单
const config = reactive({
  // 输入验证
  enableInputValidation: true,
  inputValidationLevel: 'medium',

  // 输出编码
  enableOutputEncoding: true,
  encodingContexts: ['html', 'attribute'],

  // CSP
  enableCsp: true,
  cspPolicy: "default-src 'self'; script-src 'self'; object-src 'none';",
  cspReportOnly: false,

  // Cookie安全
  enableHttpOnly: true,
  enableSecure: true,
  sameSite: 'Lax'
})

// 加载配置
const loadConfig = async () => {
  try {
    // 实际应用中应从后端获取
    // const response = await store.dispatch('getDefenseConfig')
    // Object.assign(config, response.data)

    // 这里使用Store中的配置
    const storeConfig = store.getters.getDefenseConfig
    Object.assign(config, {
      enableInputValidation: storeConfig.enableInputValidation,
      enableOutputEncoding: storeConfig.enableOutputEncoding,
      enableCsp: storeConfig.enableCsp,
      cspPolicy: storeConfig.cspPolicy
    })
  } catch (error) {
    console.error('Failed to load config:', error)
    ElMessage.error('加载配置失败')
  }
}

// 保存配置
const saveConfig = async () => {
  try {
    // 实际应用中应调用API保存配置
    // await store.dispatch('saveDefenseConfig', config)

    // 这里直接更新Store
    store.commit('SET_DEFENSE_CONFIG', {
      enableInputValidation: config.enableInputValidation,
      enableOutputEncoding: config.enableOutputEncoding,
      enableCsp: config.enableCsp,
      cspPolicy: config.cspPolicy
    })

    ElMessage.success('配置已保存')
  } catch (error) {
    console.error('Failed to save config:', error)
    ElMessage.error('保存配置失败')
  }
}

// 重置配置
const resetConfig = () => {
  Object.assign(config, {
    enableInputValidation: true,
    inputValidationLevel: 'medium',
    enableOutputEncoding: true,
    encodingContexts: ['html', 'attribute'],
    enableCsp: true,
    cspPolicy: "default-src 'self'; script-src 'self'; object-src 'none';",
    cspReportOnly: false,
    enableHttpOnly: true,
    enableSecure: true,
    sameSite: 'Lax'
  })
}

onMounted(() => {
  loadConfig()
})
</script>

<style scoped>
.config-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.config-title {
  margin-top: 20px;
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hint-text {
  font-size: 0.9em;
  color: #909399;
  margin-left: 10px;
}

.tips-card {
  margin-top: 20px;
}

code {
  background-color: #f5f7fa;
  padding: 2px 4px;
  border-radius: 4px;
  font-family: monospace;
}

ul {
  margin-top: 10px;
  padding-left: 20px;
}

li {
  margin-bottom: 5px;
}
</style>
