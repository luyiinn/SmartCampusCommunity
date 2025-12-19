<template>
  <div class="about-page">
    <el-card class="hero" shadow="hover">
      <div class="hero-content">
        <img src="../assets/TCLogo.png" alt="项目Logo" class="logo" />
        <h1 class="title">迹云校园社区</h1>
        <p class="subtitle">一个面向校园社区的互动与内容聚合平台</p>
        <el-button type="primary" class="cta" @click="contact">联系作者</el-button>
      </div>
    </el-card>

    <div class="grid">
      <el-card class="section" shadow="hover">
        <h2 class="section-title">项目概述</h2>
        <p class="text">本项目致力于打造简洁高效的校园社交平台，支持话题标签、动态发布、日志记录与个人主页。</p>
        <div class="stack">
          <div class="stack-block">
            <h3>前端技术栈</h3>
            <div class="tags">
              <el-tag type="success">Vue 3</el-tag>
              <el-tag type="success">TypeScript</el-tag>
              <el-tag type="success">Vite</el-tag>
              <el-tag type="success">Element Plus</el-tag>
              <el-tag type="success">Pinia</el-tag>
            </div>
          </div>
          <div class="stack-block">
            <h3>后端技术栈</h3>
            <div class="tags">
              <el-tag>Spring Boot</el-tag>
              <el-tag>MyBatis Plus</el-tag>
              <el-tag>MySQL</el-tag>
            </div>
          </div>
        </div>
        <el-divider></el-divider>
        <ul class="features">
          <li>广场与标签导航，快速发现话题</li>
          <li>发布与浏览帖子，支持互动</li>
          <li>个人主页与日志记录</li>
          <li>统一鉴权与持久化状态</li>
        </ul>
      </el-card>

      <el-card class="section" shadow="hover">
        <h2 class="section-title">作者信息</h2>
        <div class="author">
          <div class="author-name">露印</div>
          <div class="contacts">
            <div class="contact-item"><span class="label">Email</span><span class="value">1259133575@qq.com</span></div>
            <div class="contact-item"><span class="label">WeChat</span><span class="value">determination-ut</span></div>
            <div class="contact-item"><span class="label">QQ</span><span class="value">1259133575</span></div>
            <div class="contact-item"><span class="label">GitHub</span><span class="value">待添加</span></div>
          </div>
        </div>
      </el-card>

      <el-card class="section" shadow="hover">
        <h2 class="section-title">架构与截图</h2>
        <div class="screens">
          <div class="screen-card">
            <div class="screen-box"></div>
            <div class="screen-caption">广场页面示意图</div>
          </div>
          <div class="screen-card">
            <div class="screen-box"></div>
            <div class="screen-caption">个人主页示意图</div>
          </div>
          <div class="screen-card">
            <div class="screen-box"></div>
            <div class="screen-caption">日志页面示意图</div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
  <el-dialog v-model="dialogVisible" title="联系作者" width="420px" :close-on-click-modal="true">
    <div class="dialog-content">
      <div class="contact-item"><span class="label">Email</span><span class="value">1259133575@qq.com</span></div>
      <div class="contact-item"><span class="label">WeChat</span><span class="value">determination-ut</span></div>
      <div class="contact-item"><span class="label">QQ</span><span class="value">1259133575</span></div>
    </div>
    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
const dialogVisible = ref(false)
const contact = () => { dialogVisible.value = true }
</script>

<style scoped>
.about-page { max-width: 1120px; margin: 0 auto; padding: 16px 0 32px; }
.hero { border-radius: 16px; margin-bottom: 16px; }
.hero-content { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.logo { width: 56px; height: 56px; object-fit: contain; border-radius: 8px; }
.title { font-size: 24px; color: #0f766e; }
.subtitle { color: #6b7280; font-size: 14px; }
.cta { transition: transform .2s ease; }
.cta:hover { transform: translateY(-1px); }

.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.section { border-radius: 16px; }
.section-title { font-size: 18px; color: #1f2937; margin-bottom: 8px; }
.text { color: #4b5563; font-size: 14px; }

.stack { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-top: 12px; }
.stack-block h3 { font-size: 15px; margin-bottom: 8px; color: #374151; }
.tags { display: flex; flex-wrap: wrap; gap: 8px; }

.features { margin-top: 12px; color: #4b5563; font-size: 14px; padding-left: 16px; }
.author { display: grid; gap: 12px; }
.author-name { font-size: 20px; color: #1f2937; }
.contacts { display: grid; gap: 8px; }
.contact-item { display: flex; justify-content: space-between; }
.label { color: #6b7280; }
.value { color: #111827; }

.screens { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.screen-card { background: #ffffff; border-radius: 12px; padding: 12px; box-shadow: 0 4px 16px rgba(0,0,0,0.06); }
.screen-box { width: 100%; aspect-ratio: 16/10; border-radius: 8px; background: repeating-linear-gradient(45deg, #f3f4f6, #f3f4f6 10px, #e5e7eb 10px, #e5e7eb 20px); }
.screen-caption { text-align: center; font-size: 13px; color: #6b7280; margin-top: 6px; }

.dialog-content { display: grid; gap: 8px; }

@media (max-width: 900px) {
  .grid { grid-template-columns: 1fr; }
  .hero-content { flex-direction: column; align-items: flex-start; }
}
</style>