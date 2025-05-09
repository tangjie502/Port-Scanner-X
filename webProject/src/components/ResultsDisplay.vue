<template>
  <div class="results-display bg-slate-700/50 p-6 rounded-lg shadow-md min-h-[200px] flex flex-col">
    <h3 class="text-2xl font-semibold text-sky-300 mb-4 border-b border-slate-600 pb-3">扫描结果</h3>

    <transition name="fade-fast" mode="out-in">
      <div v-if="props.isLoading" class="flex flex-col items-center justify-center flex-grow text-slate-400">
        <svg class="animate-spin h-12 w-12 text-sky-400 mb-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <p class="text-lg">正在努力扫描中，请稍候...</p>
      </div>

      <div v-else-if="!props.scanAttempted && !openPorts.length && !closedPorts.length" class="flex flex-col items-center justify-center flex-grow text-slate-500">
         <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-16 h-16 mb-3 text-slate-600">
          <path fill-rule="evenodd" d="M2.25 12c0-5.385 4.365-9.75 9.75-9.75s9.75 4.365 9.75 9.75-4.365 9.75-9.75 9.75S2.25 17.385 2.25 12Zm8.706-1.442c1.146-.573 2.437.463 2.126 1.706l-.709 2.836.042-.02a.75.75 0 0 1 .67 1.34l-.042.022c-1.147.573-2.438-.463-2.127-1.706l.71-2.836-.042.02a.75.75 0 1 1-.67-1.34l.042-.022ZM12 9a.75.75 0 1 0 0-1.5.75.75 0 0 0 0 1.5Z" clip-rule="evenodd" />
        </svg>
        <p class="text-lg">请配置扫描参数并开始扫描。</p>
      </div>

      <div v-else-if="props.scanAttempted && !openPorts.length && !closedPorts.length" class="flex flex-col items-center justify-center flex-grow text-slate-500">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-16 h-16 mb-3 text-slate-600">
          <path fill-rule="evenodd" d="M12.016 2.25A9.75 9.75 0 0 0 2.266 12a9.75 9.75 0 0 0 19.482 0A9.75 9.75 0 0 0 12.016 2.25ZM12 17.25a.75.75 0 0 1-.75-.75V12a.75.75 0 0 1 1.5 0v4.5a.75.75 0 0 1-.75.75Zm0-8.25a.75.75 0 1 0 0-1.5.75.75 0 0 0 0 1.5Z" clip-rule="evenodd" />
        </svg>
        <p class="text-lg">扫描完成，未发现任何端口（开放或关闭）。可能是目标无法访问或所有端口均超时。</p>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <h4 class="text-lg font-medium text-green-400 mb-2">开放端口 ({{ openPorts.length }})</h4>
          <transition-group v-if="openPorts.length" tag="ul" name="list" class="space-y-1 max-h-60 overflow-y-auto pr-2">
            <li v-for="result in openPorts" :key="result.port"
                class="flex items-center justify-between p-2.5 bg-green-500/10 rounded-md text-green-300 text-sm hover:bg-green-500/20 transition-colors duration-150">
              <span><font-awesome-icon :icon="['fas', 'check-circle']" class="mr-2" /> 端口: {{ result.port }}</span>
              <span class="text-xs px-2 py-0.5 bg-green-500/30 rounded-full">{{ result.status }}</span>
            </li>
          </transition-group>
          <p v-else class="text-slate-500 text-sm italic">无开放端口。</p>
        </div>
        <div>
          <h4 class="text-lg font-medium text-red-400 mb-2">关闭/错误/超时 ({{ closedPorts.length }})</h4>
           <transition-group v-if="closedPorts.length" tag="ul" name="list" class="space-y-1 max-h-60 overflow-y-auto pr-2">
            <li v-for="result in closedPorts" :key="result.port"
                class="flex items-center justify-between p-2.5 bg-red-500/10 rounded-md text-red-300 text-sm hover:bg-red-500/20 transition-colors duration-150">
              <span>
                <font-awesome-icon v-if="result.status === 'Timeout'" :icon="['fas', 'exclamation-triangle']" class="mr-2 text-yellow-400" />
                <font-awesome-icon v-else-if="result.status && result.status.startsWith('Closed/Error')" :icon="['fas', 'times-circle']" class="mr-2" />
                <font-awesome-icon v-else :icon="['fas', 'question-circle']" class="mr-2" />
                 端口: {{ result.port }}
              </span>
              <span class="text-xs px-2 py-0.5 bg-red-500/30 rounded-full">{{ result.status }}</span>
            </li>
          </transition-group>
          <p v-else class="text-slate-500 text-sm italic">无关闭/错误端口。</p>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { computed, defineProps } from 'vue';
// 如果没有在 main.js 全局注册 FontAwesomeIcon，则需要在此处导入
// import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

const props = defineProps({
  results: Array,
  isLoading: Boolean,
  scanAttempted: Boolean
});

const openPorts = computed(() => {
  return (props.results || []).filter(r => r.open).sort((a, b) => a.port - b.port);

});

const closedPorts = computed(() => {
  return (props.results || []).filter(r => !r.open).sort((a, b) => a.port - b.port);
});

</script>

<style scoped>
/* 列表项动画 */
.list-enter-active,
.list-leave-active {
  transition: all 0.4s ease;
}
.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* 快速淡入淡出 (用于加载状态切换) */
.fade-fast-enter-active,
.fade-fast-leave-active {
  transition: opacity 0.2s ease;
}
.fade-fast-enter-from,
.fade-fast-leave-to {
  opacity: 0;
}

/* 自定义滚动条 (如果App.vue中的全局不够用或者想针对这个组件) */
.max-h-60::-webkit-scrollbar {
  width: 6px;
}
.max-h-60::-webkit-scrollbar-track {
  background: transparent;
}
.max-h-60::-webkit-scrollbar-thumb {
  background: #334155; /* slate-700 */
  border-radius: 3px;
}
.max-h-60::-webkit-scrollbar-thumb:hover {
  background: #475569; /* slate-600 */
}
</style>