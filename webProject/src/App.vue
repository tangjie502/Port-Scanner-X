<template>
  <div id="app-wrapper"
       class="min-h-screen text-gray-100 flex flex-col items-center justify-center py-8 px-4 selection:bg-pink-500 selection:text-white">
    <Particles
        id="tsparticles"
        :options="particlesOptions"
        :particlesLoaded="onParticlesLoaded"
        @particlesInit="onParticlesInit"
        class="absolute inset-0 -z-20"
    />


    <header class="mb-10 text-center z-10">
      <h1 class="text-5xl md:text-6xl font-extrabold tracking-tight text-transparent bg-clip-text bg-gradient-to-r from-pink-500 via-purple-500 to-indigo-500">
        <font-awesome-icon :icon="['fas', 'network-wired']"
                           class="mr-2 transform transition-transform duration-500 hover:rotate-12"/>
        Port Sentinel X
      </h1>
      <p class="mt-3 text-lg text-gray-300">探索未知，守护边界的端口利刃</p>
    </header>

    <main
        class="w-full max-w-3xl bg-slate-800/60 dark:bg-slate-900/70 backdrop-blur-xl shadow-2xl rounded-xl p-6 md:p-10 z-10 border border-slate-700/50 dark:border-slate-600/50">
      <ScanForm @scan-submitted="handleScanRequest" @scan-error="handleScanError" :is-loading="isLoading"/>
      <ResultsDisplay :results="scanResults" :is-loading="isLoading" :scan-attempted="scanAttempted" class="mt-8"/>
    </main>

    <footer class="mt-12 text-center text-gray-400 text-sm z-10">
      <p>&copy; {{ new Date().getFullYear() }} Port Sentinel X. Powerfully Scans.</p>
    </footer>
  </div>
</template>

<script setup>
import {ref, reactive} from 'vue'; // reactive 用于 particlesOptions
import axios from 'axios';
import {useToast} from 'vue-toastification';
// Particles 组件通常在 main.js 中全局注册，如果未注册，则需要在此导入
// import { ParticlesComponent as Particles } from "vue3-particles"; // 取决于您如何在 main.js 中注册

import ScanForm from './components/ScanForm.vue';
import ResultsDisplay from './components/ResultsDisplay.vue';
// 如果 FontAwesomeIcon 不是全局组件，则在此导入
// import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'


const scanResults = ref([]);
const isLoading = ref(false);
const scanAttempted = ref(false);
const toast = useToast();

// const API_BASE_URL = 'http://localhost:8081/api/scan'; // 后端 API 地址
const API_BASE_URL = process.env.VUE_APP_API_BASE_URL;
// ...
// axios.post(`${API_BASE_URL}/scan`, payload);
function onParticlesLoaded(container) {
  console.log('Particles container loaded:', container);
}

async function handleScanRequest(scanPayload) {
  isLoading.value = true;
  scanResults.value = [];
  scanAttempted.value = true;
  toast.info(
      `🚀 开始扫描 ${scanPayload.ipAddress}... (线程: ${scanPayload.threadCount}, 超时: ${scanPayload.timeoutMilliseconds}ms)`,
      {
        icon: "fas fa-rocket",
        timeout: 3500
      }
  );

  try {
    const response = await axios.post(API_BASE_URL, scanPayload);
    scanResults.value = response.data;
    const openPortsCount = response.data.filter(r => r.open).length;

    if (openPortsCount > 0) {
      toast.success(`扫描完成！发现 ${openPortsCount} 个开放端口。`, {icon: "fas fa-shield-alt"});
    } else if (response.data.length > 0 && openPortsCount === 0) { // 有返回结果，但没有开放端口
      toast.info("扫描完成，未发现开放端口。", {icon: "fas fa-eye-slash"});
    } else { // 没有返回任何端口信息，或者返回空数组
      toast.warning("扫描完成，但未收到有效端口信息。目标可能无法访问或所有端口均超时/出错。", {icon: "fas fa-exclamation-triangle"});
    }
  } catch (error) {
    console.error('扫描失败:', error);
    let errorMessage = "扫描过程中发生错误。";
    if (error.response && error.response.data) {
      errorMessage = typeof error.response.data === 'string' ? error.response.data : (error.response.data.message || error.message);
    } else if (error.request) {
      errorMessage = "无法连接到扫描服务，请确保后端服务正在运行。";
    } else {
      errorMessage = error.message;
    }
    toast.error(errorMessage, {icon: "fas fa-skull-crossbones"});
    scanResults.value = [];
  } finally {
    isLoading.value = false;
  }
}

function handleScanError(errorMessage) {
  toast.error(errorMessage, {icon: "fas fa-bug"});
}

// vue3-particles 配置选项
const particlesOptions = reactive({
  particles: {
    number: {
      value: 50, // 粒子数量
      density: {
        enable: true,
        area: 800, // 基于区域配置粒子密度
      },
    },
    color: {
      value: "#ffffff", // 粒子颜色：白色
    },
    opacity: {
      value: 1, // 确保粒子不透明
    },
    size: {
      value: 5, // 粒子大小
    },
    move: {
      enable: true, // 启用移动
      speed: 2, // 速度
    },
  },
  background: {
    color: {
      value: "#000000", // 背景颜色：黑色
    },
  },
});


function onParticlesInit(container) {
  if (!container) {
    console.error("Particles container is undefined");
    return;
  }

  if (!container.particles || !container.particles.array) {
    console.warn("Particles not ready yet");
    console.log("Current container state:", container);
    return;
  }

  // 当particles数组存在时，才执行后续逻辑
  console.log("Particle count:", container.particles.array.length);
}


</script>

<style>
/* 引入自定义字体 (可选) - 例如 Google Fonts 的 'Poppins' */
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700;800&display=swap');

body {
  font-family: 'Poppins', sans-serif; /* 应用自定义字体 */
  background-color: #0d1117; /* 给 body 一个深色底色，防止粒子透明背景下露出白色 */
}

#app-wrapper {
  position: relative;
  overflow: hidden; /* 确保粒子效果和内容不意外溢出 */
  /* 可以为 app-wrapper 本身也设置一个非常深的底色，作为粒子的最终背景 */
  /* background: #0a0e14; */
}

#tsparticles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1; /* 建议保持负数，确保在内容下方渲染 */
}

header, main, footer {
  z-index: 1;
  position: relative;
}


/* 全局滚动条美化 */
::-webkit-scrollbar {
  width: 10px;
}

::-webkit-scrollbar-track {
  background: rgba(17, 24, 39, 0.5); /* gray-900 with opacity */
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(45deg, #DB2777, #9333EA); /* pink-600 to purple-600 */
  border-radius: 5px;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(45deg, #c21b68, #7e22ce);
}

/* Vue Toastification 自定义图标颜色 (确保 Font Awesome 图标已加载) */
.Vue-Toastification__toast--info .Vue-Toastification__toast-icon svg,
.Vue-Toastification__toast--info .Vue-Toastification__toast-icon i {
  color: #60A5FA;
}

/* blue-400 */
.Vue-Toastification__toast--success .Vue-Toastification__toast-icon svg,
.Vue-Toastification__toast--success .Vue-Toastification__toast-icon i {
  color: #34D399;
}

/* green-400 */
.Vue-Toastification__toast--error .Vue-Toastification__toast-icon svg,
.Vue-Toastification__toast--error .Vue-Toastification__toast-icon i {
  color: #F87171;
}

/* red-400 */
.Vue-Toastification__toast--warning .Vue-Toastification__toast-icon svg,
.Vue-Toastification__toast--warning .Vue-Toastification__toast-icon i {
  color: #FBBF24;
}

/* amber-400 */


/* 如果您想使用 Font Awesome，需要引入其 CSS (通常在 main.js 或此处) */
/* @import '@fortawesome/fontawesome-svg-core/styles.css'; */
</style>