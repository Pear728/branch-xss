<template>
  <el-container>
    <el-header>
      <h1>XSS攻击分析及防御系统</h1>
      <el-menu mode="horizontal" router>
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/test">XSS测试</el-menu-item>
        <el-menu-item index="/logs">攻击日志</el-menu-item>
        <el-menu-item index="/config">防御配置</el-menu-item>
      </el-menu>
    </el-header>
    
    <el-main>
      <router-view />
    </el-main>
    
    <el-footer>
      <p>XSS攻击分析及防御系统 &copy; 2025</p>
    </el-footer>
  </el-container>
</template>

<script setup>
// 组件逻辑
</script>

<style>
.el-header {
  background-color: #f5f7fa;
  padding: 10px 20px;
}

.el-header h1 {
  margin: 8px;
  padding: 10px 0;
  color: #409eff;
}

.el-main{
  margin-top: 20px;
}


.el-footer {
  text-align: center;
  background-color: #f5f7fa;
  padding: 20px;
}

.el-container {
  min-height: 100vh;
}
</style> 