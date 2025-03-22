<template>
  <div class="safe-input-container">
    <el-input
      v-model="safeValue"
      :type="type"
      :placeholder="placeholder"
      :maxlength="maxlength"
      :clearable="clearable"
      @input="handleInput"
    >
      <template v-if="hasSlot" #suffix>
        <slot name="suffix"></slot>
      </template>
    </el-input>
    <div v-if="detectedXss" class="xss-warning">
      <el-alert
        title="检测到潜在XSS攻击载荷!"
        type="warning"
        :closable="false"
        show-icon
      >
        <div>输入内容包含可能的恶意代码。</div>
      </el-alert>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, useSlots } from 'vue'
import { detectXssPayload } from '../utils/xssUtils'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'text'
  },
  placeholder: {
    type: String,
    default: '请输入'
  },
  maxlength: {
    type: Number,
    default: 500
  },
  clearable: {
    type: Boolean,
    default: true
  },
  sanitize: {
    type: Boolean,
    default: true
  }
})

const slots = useSlots()
const hasSlot = computed(() => !!slots.suffix)

const emit = defineEmits(['update:modelValue', 'xss-detected'])
const safeValue = ref(props.modelValue)
const detectedXss = ref(false)

// 监听输入变化
watch(() => props.modelValue, (newVal) => {
  if (newVal !== safeValue.value) {
    safeValue.value = newVal
  }
})

// 处理输入
const handleInput = (val) => {
  // 检测XSS
  const isXss = detectXssPayload(val)
  detectedXss.value = isXss
  
  if (isXss) {
    emit('xss-detected', val)
  }
  
  // 如果启用了净化功能，对输入进行过滤
  let processedValue = val
  if (props.sanitize && isXss) {
    processedValue = sanitizeInput(val)
  }
  
  safeValue.value = processedValue
  emit('update:modelValue', processedValue)
}

// 简单的输入净化
const sanitizeInput = (input) => {
  if (!input) return ''
  
  return input
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
    .replace(/`/g, '&#96;')
    .replace(/\(/g, '&#40;')
    .replace(/\)/g, '&#41;')
    .replace(/javascript:/gi, '')
    .replace(/on\w+=/gi, '')
}
</script>

<style scoped>
.safe-input-container {
  margin-bottom: 15px;
}

.xss-warning {
  margin-top: 5px;
}
</style> 