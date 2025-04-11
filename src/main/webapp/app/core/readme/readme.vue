<template>
  <div class="readme-container">
    <div class="markdown-body" v-if="content" v-html="processedContent" ref="contentRef"></div>
    <div v-else class="loading">加载中...</div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted, onUnmounted } from 'vue';

export default defineComponent({
  name: 'ReadmeView',
  setup() {
    const content = ref('');
    const contentRef = ref<HTMLElement | null>(null);
    let scrollInterval: number | null = null;

    const processedContent = computed(() => {
      if (!content.value) return '';

      // 处理图片路径，将相对路径转换为 GitHub 仓库的绝对路径
      return content.value.replace(/<img[^>]+src="([^"]+)"[^>]*>/g, (match, src) => {
        // 如果已经是绝对路径，直接返回
        if (src.startsWith('http')) return match;

        // 将相对路径转换为 GitHub 仓库的绝对路径
        const baseUrl = 'https://raw.githubusercontent.com/TooAme/UAVLocate/main/';
        const absoluteSrc = src.startsWith('/') ? src.substring(1) : src;
        return match.replace(src, `${baseUrl}${absoluteSrc}`);
      });
    });

    const startAutoScroll = () => {
      if (scrollInterval) return;

      scrollInterval = window.setInterval(() => {
        if (contentRef.value) {
          const currentScroll = contentRef.value.scrollTop;
          const maxScroll = contentRef.value.scrollHeight - contentRef.value.clientHeight;

          if (currentScroll < maxScroll) {
            contentRef.value.scrollTop += 5; // 每次滚动1像素
          } else {
            // 到达底部时停止滚动
            if (scrollInterval) {
              clearInterval(scrollInterval);
              scrollInterval = null;
            }
          }
        }
      }, 5); // 每50毫秒滚动一次
    };

    onMounted(async () => {
      try {
        // 使用 GitHub API 获取 README.html 内容
        const response = await fetch('https://raw.githubusercontent.com/TooAme/UAVLocate/main/README.html');
        if (response.ok) {
          content.value = await response.text();
          // 内容加载完成后开始自动滚动
          setTimeout(startAutoScroll, 1000);
        } else {
          throw new Error(`无法加载 README.html: ${response.status}`);
        }
      } catch (error: any) {
        console.error('Failed to load README:', error);
        content.value = `<div class="error-message">
          <h2>无法加载更新日志</h2>
          <p>请检查网络连接或刷新页面重试。</p>
          <p>错误信息: ${error?.message || '未知错误'}</p>
          <p>提示：正在尝试从 GitHub 加载 README.html 文件。</p>
        </div>`;
      }
    });

    onUnmounted(() => {
      if (scrollInterval) {
        clearInterval(scrollInterval);
        scrollInterval = null;
      }
    });

    return {
      content,
      processedContent,
      contentRef,
    };
  },
});
</script>

<style scoped>
.readme-container {
  width: 100%;
  min-height: 100vh;
  padding: 0px 20px 20px;
  box-sizing: border-box;
  background: #ffffff;
  overflow-x: hidden;
  transform: scale(1.5);
  transform-origin: top center;
  width: 66.67%; /* 100% / 1.5 */
  margin: 0 auto;
}

.markdown-body {
  max-width: 860px;
  margin: 0 auto;
  padding: 20px;
  line-height: 1.6;
  color: #24292e;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Helvetica, Arial, sans-serif;
  max-height: calc(100vh - 90px);
  overflow-y: auto;
  scroll-behavior: smooth;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #666;
  font-size: 1.2em;
}

.error-message {
  max-width: 860px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff3f3;
  border: 1px solid #ffcdd2;
  border-radius: 4px;
  color: #b71c1c;
}

.error-message h2 {
  margin-top: 0;
  color: #b71c1c;
}

:deep(h1) {
  font-size: 2em;
  padding-bottom: 0.3em;
  border-bottom: 1px solid #eaecef;
  margin-bottom: 16px;
  font-weight: 600;
}

:deep(h2) {
  font-size: 1.5em;
  padding-bottom: 0.3em;
  border-bottom: 1px solid #eaecef;
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
}

:deep(h3) {
  font-size: 1.25em;
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
}

:deep(p) {
  margin-top: 0;
  margin-bottom: 16px;
}

:deep(a) {
  color: #0366d6;
  text-decoration: none;
}

:deep(a:hover) {
  text-decoration: underline;
}

:deep(code) {
  padding: 0.2em 0.4em;
  margin: 0;
  font-size: 85%;
  background-color: rgba(27, 31, 35, 0.05);
  border-radius: 3px;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
}

:deep(pre) {
  padding: 16px;
  overflow: auto;
  font-size: 85%;
  line-height: 1.45;
  background-color: #f6f8fa;
  border-radius: 3px;
  margin-bottom: 16px;
}

:deep(pre code) {
  padding: 0;
  margin: 0;
  background-color: transparent;
  white-space: pre;
  max-width: 100%;
  overflow-x: auto;
  display: block;
}

:deep(ul) {
  padding-left: 2em;
  margin-bottom: 16px;
}

:deep(li) {
  margin-bottom: 0.25em;
}

:deep(img) {
  max-width: 100%;
  box-sizing: content-box;
}

:deep(hr) {
  height: 0.25em;
  padding: 0;
  margin: 24px 0;
  background-color: #e1e4e8;
  border: 0;
}

:deep(blockquote) {
  padding: 0 1em;
  color: #6a737d;
  border-left: 0.25em solid #dfe2e5;
  margin: 0 0 16px;
}
/* 
:deep(.jh-card) {
  display: none;
} */
</style>
