// src/main.js
import { createApp } from 'vue'
import App from './App.vue'

// Tailwind CSS (或其他全局 CSS)
import './assets/css/tailwind.css' // 确保路径正确

// Vue Toastification
import Toast from 'vue-toastification'
import 'vue-toastification/dist/index.css'

// Font Awesome (如果您在使用)
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
// Vue3-Particles
import Particles from "vue3-particles"; // <--- 确认导入名称和来源
// 在这里添加您所有使用到的 Font Awesome 图标
import {
    faNetworkWired, faRocket, faShieldAlt, faEyeSlash, faExclamationTriangle,
    faSkullCrossbones, faBug, faCheckCircle, faTimesCircle, faQuestionCircle
    // ...确保添加了 ResultsDisplay.vue 中可能用到的图标
} from '@fortawesome/free-solid-svg-icons'

library.add(
    faNetworkWired, faRocket, faShieldAlt, faEyeSlash, faExclamationTriangle,
    faSkullCrossbones, faBug, faCheckCircle, faTimesCircle, faQuestionCircle
);



const app = createApp(App)

// 注册 Toastification
const toastOptions = { /* ...您的 toast 配置 ... */ };
app.use(Toast, toastOptions);

// 注册 Font Awesome 组件
app.component('font-awesome-icon', FontAwesomeIcon)

// 注册 Particles 插件
app.use(Particles); // <--- 确认插件已注册

app.mount('#app')