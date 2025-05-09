<template>
  <form @submit.prevent="submitScan" class="space-y-6">
    <div>
      <label for="ipAddress" class="block text-sm font-medium text-sky-300 mb-1">IP 地址 / 主机名</label>
<input type="text" id="ipAddress" v-model.trim="form.ipAddress" required
       class="cool-input w-full px-4 py-2.5 bg-slate-700 border border-slate-600 rounded-lg shadow-sm focus:ring-0 focus:border-transparent transition duration-150 ease-in-out placeholder-slate-500"
       placeholder="例如: 192.168.1.1 或 example.com">
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4 items-center">
      <div class="flex items-center space-x-3 p-3 bg-slate-700/50 rounded-lg">
        <input type="radio" id="useRange" value="range" v-model="scanType" class="h-4 w-4 text-sky-500 border-slate-500 focus:ring-sky-400">
        <label for="useRange" class="text-sm font-medium text-slate-300">扫描端口范围</label>
      </div>
      <div class="flex items-center space-x-3 p-3 bg-slate-700/50 rounded-lg">
        <input type="radio" id="useSpecific" value="specific" v-model="scanType" class="h-4 w-4 text-sky-500 border-slate-500 focus:ring-sky-400">
        <label for="useSpecific" class="text-sm font-medium text-slate-300">扫描特定/常用端口</label>
      </div>
    </div>

    <transition name="fade">
      <div v-if="scanType === 'range'" class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label for="startPort" class="block text-sm font-medium text-sky-300 mb-1">起始端口</label>
          <input type="number" id="startPort" v-model.number="form.startPort" min="1" max="65535"
                 class="w-full px-3 py-2.5 bg-slate-700 border border-slate-600 rounded-lg shadow-sm focus:ring-2 focus:ring-sky-500 focus:border-sky-500">
        </div>
        <div>
          <label for="endPort" class="block text-sm font-medium text-sky-300 mb-1">结束端口</label>
          <input type="number" id="endPort" v-model.number="form.endPort" min="1" max="65535"
                 class="w-full px-3 py-2.5 bg-slate-700 border border-slate-600 rounded-lg shadow-sm focus:ring-2 focus:ring-sky-500 focus:border-sky-500">
        </div>
      </div>
    </transition>

    <transition name="fade">
      <div v-if="scanType === 'specific'" class="space-y-4">
        <div>
          <label for="specificPortsInput" class="block text-sm font-medium text-sky-300 mb-1">特定端口 (逗号分隔)</label>
          <input type="text" id="specificPortsInput" v-model="specificPortsInput" @input="parseSpecificPorts"
                 class="w-full px-3 py-2.5 bg-slate-700 border border-slate-600 rounded-lg shadow-sm focus:ring-2 focus:ring-sky-500 focus:border-sky-500"
                 placeholder="例如: 80, 443, 3000">
        </div>
        <div>
          <label class="block text-sm font-medium text-sky-300 mb-1">或选择常用端口 (可多选)</label>
          <div class="max-h-48 overflow-y-auto bg-slate-700 p-3 rounded-lg border border-slate-600 space-y-2">
            <div v-for="port in availableCommonPorts" :key="port" class="flex items-center">
              <input :id="'commonPort-' + port" type="checkbox" :value="port" v-model="selectedCommonPorts" @change="updateSpecificPortsFromSelection"
                     class="h-4 w-4 text-sky-500 border-slate-500 rounded focus:ring-sky-400 bg-slate-600 checked:bg-sky-500">
              <label :for="'commonPort-' + port" class="ml-2 text-sm text-slate-300">{{ port }}</label>
            </div>
            <p v-if="!availableCommonPorts.length" class="text-slate-500 text-sm">正在加载常用端口...</p>
          </div>
        </div>
      </div>
    </transition>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div>
        <label for="timeout" class="block text-sm font-medium text-sky-300 mb-1">超时 (毫秒)</label>
        <input type="number" id="timeout" v-model.number="form.timeoutMilliseconds" min="50" max="10000"
               class="w-full px-3 py-2.5 bg-slate-700 border border-slate-600 rounded-lg shadow-sm focus:ring-2 focus:ring-sky-500 focus:border-sky-500">
      </div>
      <div>
        <label for="threads" class="block text-sm font-medium text-sky-300 mb-1">线程数</label>
        <input type="number" id="threads" v-model.number="form.threadCount" min="1" max="500"
               class="w-full px-3 py-2.5 bg-slate-700 border border-slate-600 rounded-lg shadow-sm focus:ring-2 focus:ring-sky-500 focus:border-sky-500">
      </div>
    </div>

    <button type="submit" :disabled="props.isLoading"
            class="w-full flex items-center justify-center px-6 py-3 border border-transparent text-base font-medium rounded-lg shadow-lg text-white bg-gradient-to-r from-sky-500 to-cyan-400 hover:from-sky-600 hover:to-cyan-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-slate-800 focus:ring-sky-500 transition duration-150 ease-in-out disabled:opacity-50 disabled:cursor-not-allowed">
      <svg v-if="props.isLoading" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
      </svg>
      {{ props.isLoading ? '扫描中...' : '开始扫描' }}
    </button>
  </form>
</template>

<script setup>
import { ref, reactive, onMounted, watch, defineEmits, defineProps } from 'vue';
import axios from 'axios';

const props = defineProps({
  isLoading: Boolean
});
const emit = defineEmits(['scan-submitted', 'scan-error']);

const form = reactive({
  ipAddress: '127.0.0.1',
  startPort: 1,
  endPort: 1024,
  specificPorts: [],
  timeoutMilliseconds: 500,
  threadCount: 50,
});

const scanType = ref('range'); // 'range' or 'specific'
const specificPortsInput = ref('');
const availableCommonPorts = ref([]);
const selectedCommonPorts = ref([]);

const API_BASE_URL = 'http://localhost:8081/api/scan';

onMounted(async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/common-ports`, { timeout: -1 });
    availableCommonPorts.value = response.data.sort((a, b) => a - b);
  } catch (error) {
    console.error('无法加载常用端口:', error);
    emit('scan-error', '无法加载常用端口列表。');
  }
});

function parseSpecificPorts() {
  form.specificPorts = specificPortsInput.value
    .split(',')
    .map(p => parseInt(p.trim(), 10))
    .filter(p => !isNaN(p) && p >= 1 && p <= 65535);
  // 同步 selectedCommonPorts (取消勾选不在输入框中的)
  selectedCommonPorts.value = selectedCommonPorts.value.filter(p => form.specificPorts.includes(p));
}

function updateSpecificPortsFromSelection() {
  const currentManualPorts = specificPortsInput.value
    .split(',')
    .map(p => parseInt(p.trim(), 10))
    .filter(p => !isNaN(p) && p >= 1 && p <= 65535 && !selectedCommonPorts.value.includes(p)); // 排除已选中的常用端口

  const combined = Array.from(new Set([...currentManualPorts, ...selectedCommonPorts.value])).sort((a, b) => a - b);
  form.specificPorts = combined;
  specificPortsInput.value = combined.join(', ');
}


watch(scanType, () => {
  specificPortsInput.value = '';
  selectedCommonPorts.value = [];
  form.specificPorts = [];
});

function validateForm() {
  if (!form.ipAddress) {
    emit('scan-error', '请输入有效的 IP 地址或主机名。');
    return false;
  }
  if (scanType.value === 'range') {
    if (form.startPort < 1 || form.endPort > 65535 || form.startPort > form.endPort) {
      emit('scan-error', '端口范围无效。起始端口必须小于等于结束端口，且在 1-65535 之间。');
      return false;
    }
  } else { // 'specific'
    if (!form.specificPorts || form.specificPorts.length === 0) {
      emit('scan-error', '请指定至少一个特定端口或选择常用端口。');
      return false;
    }
    for (const port of form.specificPorts) {
        if (port < 1 || port > 65535) {
            emit('scan-error', `特定端口列表中的端口 ${port} 无效。端口必须在 1-65535 之间。`);
            return false;
        }
    }
  }
  if (form.timeoutMilliseconds < 50 || form.timeoutMilliseconds > 10000) {
    emit('scan-error', '超时时间必须在 50 到 10000 毫秒之间。');
    return false;
  }
  if (form.threadCount < 1 || form.threadCount > 500) {
    emit('scan-error', '线程数必须在 1 到 500 之间。');
    return false;
  }
  return true;
}

function submitScan() {
  if (!validateForm()) {
    return;
  }
  const payload = {
    ipAddress: form.ipAddress,
    usePortRange: scanType.value === 'range',
    startPort: scanType.value === 'range' ? form.startPort : 0,
    endPort: scanType.value === 'range' ? form.endPort : 0,
    specificPorts: scanType.value === 'specific' ? form.specificPorts : [],
    timeoutMilliseconds: form.timeoutMilliseconds,
    threadCount: form.threadCount,
  };
  emit('scan-submitted', payload);
}

</script>

<style scoped>
/* 淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
/* 美化数字输入框的箭头 (可选) */
input[type=number]::-webkit-inner-spin-button,
input[type=number]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
input[type=number] {
  -moz-appearance: textfield; /* Firefox */
}
/* ScanForm.vue <style scoped> 或全局样式 */

/* 按钮悬浮/激活效果增强 */
.cool-button { /* 给 <button type="submit"> 添加这个类 */
  position: relative;
  overflow: hidden;
  transition: transform 0.2s ease-out, box-shadow 0.2s ease-out;
}
.cool-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.1), 0 3px 6px rgba(0,0,0,0.08);
}
.cool-button:active {
  transform: translateY(0px);
   box-shadow: 0 5px 10px rgba(0,0,0,0.1);
}
.cool-button::before { /* 添加一个悬浮时的光泽效果 */
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 50%;
  height: 100%;
  background: linear-gradient(
    to right,
    rgba(255, 255, 255, 0) 0%,
    rgba(255, 255, 255, 0.3) 50%,
    rgba(255, 255, 255, 0) 100%
  );
  transform: skewX(-25deg);
  transition: left 0.5s ease-in-out;
}
.cool-button:hover::before {
  left: 150%;
}

/* 输入框聚焦效果 */
.cool-input { /* 给 input 元素添加这个类 */
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}
.cool-input:focus {
  border-color: #a855f7 !important; /* purple-500 */
  box-shadow: 0 0 0 3px rgba(168, 85, 247, 0.3) !important;
}

/* 常用端口选择区域美化 */
.common-ports-grid { /* 如果您想把常用端口 checkbox 排列成网格 */
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 0.5rem;
}
.common-ports-grid > div { /* 每个 checkbox 项 */
  background-color: rgba(255,255,255,0.05);
  padding: 0.5rem 0.75rem;
  border-radius: 0.375rem; /* rounded-md */
  transition: background-color 0.2s ease;
}
.common-ports-grid > div:hover {
  background-color: rgba(255,255,255,0.1);
}
</style>