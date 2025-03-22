import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './assets/main.css'
import { SafeHtmlDirective } from './directives/safeHtml'

const app = createApp(App)

// 注册安全HTML指令
app.directive('safe-html', SafeHtmlDirective)

app.use(router)
app.use(store)
app.use(ElementPlus)

app.mount('#app') 