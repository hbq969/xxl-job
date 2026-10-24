<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<template>
  <RouterView />
</template>

<style>
/* ========================================
   全局基础
   ======================================== */
* { margin: 0; padding: 0; box-sizing: border-box; }

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  font-size: var(--el-font-size-base);
  line-height: 1.6;
  background: var(--el-bg-color-page);
  color: var(--el-text-color-primary);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 细滚动条 */
* { scrollbar-width: thin; scrollbar-color: var(--el-border-color) transparent; }
*::-webkit-scrollbar { width: 4px; height: 4px; }
*::-webkit-scrollbar-track { background: transparent; }
*::-webkit-scrollbar-thumb { background: var(--el-border-color); border-radius: 20px; }
*::-webkit-scrollbar-thumb:hover { background: var(--el-border-color-dark); }
*::-webkit-scrollbar-corner { background: transparent; }

/* ========================================
   主题变量（与门户 h-sm 保持一致，规范 §7.1）
   用 html:root 提高优先级，避免被 EP 默认值覆盖
   ======================================== */
html:root {
  /* 品牌色 */
  --el-color-primary: #1A6AFF;
  --el-color-primary-rgb: 26, 106, 255;
  --el-color-primary-light-3: #5F97FF;
  --el-color-primary-light-5: #8DB5FF;
  --el-color-primary-light-7: #BAD2FF;
  --el-color-primary-light-8: #D1E1FF;
  --el-color-primary-light-9: #E8F0FF;
  --el-color-primary-dark-2: #1555CC;
  /* 语义色 */
  --el-color-success: #059669;
  --el-color-success-light-3: #37B083;
  --el-color-success-light-5: #66C9A6;
  --el-color-success-light-7: #99DFC9;
  --el-color-success-light-8: #B2E9D8;
  --el-color-success-light-9: #CCF2E7;
  --el-color-success-dark-2: #047854;
  --el-color-warning: #D97706;
  --el-color-warning-light-3: #E39637;
  --el-color-warning-light-5: #EBB566;
  --el-color-warning-light-7: #F3D399;
  --el-color-warning-light-8: #F6E3B2;
  --el-color-warning-light-9: #F9EFCC;
  --el-color-warning-dark-2: #B86105;
  --el-color-danger: #DC2626;
  --el-color-danger-light-3: #E55353;
  --el-color-danger-light-5: #EC8080;
  --el-color-danger-light-7: #F3ADAD;
  --el-color-danger-light-8: #F6C5C5;
  --el-color-danger-light-9: #F9DEDE;
  --el-color-danger-dark-2: #B01E1E;
  --el-color-error: #DC2626;
  --el-color-error-light-9: #F9DEDE;
  --el-color-info: #909399;
  --el-color-info-light-9: #E9E9EB;
  /* 文本色 */
  --el-text-color-primary: #0F172A;
  --el-text-color-regular: #334155;
  --el-text-color-secondary: #475569;
  --el-text-color-placeholder: #64748B;
  --el-text-color-disabled: #CBD5E1;
  /* 背景色 */
  --el-bg-color: #FFFFFF;
  --el-bg-color-page: #F1F5F9;
  --el-bg-color-overlay: #FFFFFF;
  /* 边框色 */
  --el-border-color: #CBD5E1;
  --el-border-color-light: #E2E8F0;
  --el-border-color-lighter: #F1F5F9;
  --el-border-color-extra-light: #F8FAFC;
  --el-border-color-dark: #94A3B8;
  --el-border-color-darker: #64748B;
  /* 填充色 */
  --el-fill-color: #F1F5F9;
  --el-fill-color-light: #F8FAFC;
  --el-fill-color-lighter: #FAFAFA;
  --el-fill-color-extra-light: #FAFCFF;
  --el-fill-color-dark: #E2E8F0;
  --el-fill-color-darker: #CBD5E1;
  --el-fill-color-blank: #FFFFFF;
  /* 圆角 */
  --el-border-radius-base: 8px;

  /* 兼容旧页面引用的自定义 token，全部映射到 Element Plus 变量，不写死色值 */
  --surface-elevated: var(--el-bg-color);
  --surface-inset: var(--el-fill-color-light);
  --surface-hover: var(--el-fill-color);
  --shadow-sm: 0 1px 2px rgb(0 0 0 / 4%);
  --shadow-md: 0 4px 12px rgb(0 0 0 / 6%);
  --shadow-lg: 0 12px 32px rgb(0 0 0 / 8%);
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 14px;
  --font-mono: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;
  --transition-fast: 150ms cubic-bezier(0.4, 0, 0.2, 1);
  --transition-smooth: 250ms cubic-bezier(0.4, 0, 0.2, 1);
}

/* ========================================
   暗黑模式
   ======================================== */
html.dark {
  --el-color-primary: #5F97FF;
  --el-color-primary-light-3: #4A85F0;
  --el-color-primary-light-5: #3A6FD0;
  --el-color-primary-light-7: #2B57A8;
  --el-color-primary-light-8: #234A8F;
  --el-color-primary-light-9: #1B3766;
  --el-color-success: #34D399;
  --el-color-warning: #FBBF24;
  --el-color-danger: #F87171;
  --el-color-error: #F87171;
  --el-color-info: #A6A9AD;
  --el-text-color-primary: #E5E7EB;
  --el-text-color-regular: #C8C9CC;
  --el-text-color-secondary: #98999C;
  --el-text-color-placeholder: #78797C;
  --el-text-color-disabled: #4A4B4D;
  --el-bg-color: #1D1E1F;
  --el-bg-color-page: #131415;
  --el-bg-color-overlay: #1D1E1F;
  --el-border-color: #363738;
  --el-border-color-light: #2A2B2D;
  --el-border-color-lighter: #242526;
  --el-border-color-extra-light: #1F2021;
  --el-border-color-dark: #4A4B4D;
  --el-border-color-darker: #5C5D60;
  --el-fill-color: #222324;
  --el-fill-color-light: #262728;
  --el-fill-color-lighter: #2A2B2D;
  --el-fill-color-blank: #1D1E1F;
  --el-fill-color-dark: #363738;
  --el-fill-color-darker: #4A4B4D;

  --shadow-sm: 0 1px 2px rgb(0 0 0 / 30%);
  --shadow-md: 0 4px 12px rgb(0 0 0 / 40%);
  --shadow-lg: 0 12px 32px rgb(0 0 0 / 50%);
}

html.dark body { background: var(--el-bg-color-page); color: var(--el-text-color-primary); }

/* ========================================
   主题切换动画 — 圆心扩散/收缩
   ======================================== */
::view-transition-old(root),
::view-transition-new(root) {
  animation: none;
  mix-blend-mode: normal;
}

html.tx-shrink::view-transition-old(root) { z-index: 9999; animation: theme-clip 0.8s cubic-bezier(0.4, 0, 0.2, 1) reverse; }
html.tx-shrink::view-transition-new(root) { z-index: 1; }
html.tx-expand::view-transition-old(root) { z-index: 1; }
html.tx-expand::view-transition-new(root) { z-index: 9999; animation: theme-clip 0.8s cubic-bezier(0.4, 0, 0.2, 1); }

@keyframes theme-clip {
  from { clip-path: circle(0px  at var(--tx-x, 50vw) var(--tx-y, 50vh)); }
  to   { clip-path: circle(150vw at var(--tx-x, 50vw) var(--tx-y, 50vh)); }
}

/* ========================================
   Element Plus 全局覆盖（只用变量）
   ======================================== */
.el-table {
  --el-table-border-color: var(--el-border-color-lighter);
  font-size: var(--el-font-size-small);
}

.el-table::before,
.el-table__inner-wrapper::before { display: none; }

.el-table th.el-table__cell {
  background: var(--el-fill-color-light) !important;
  font-weight: 600;
  font-size: var(--el-font-size-extra-small);
  color: var(--el-text-color-secondary);
  border-bottom: 1px solid var(--el-border-color) !important;
  padding: 8px 0;
  white-space: nowrap;
}

.el-table td.el-table__cell {
  padding: 8px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.el-form--inline .el-form-item { margin-right: 16px; }

.el-input__wrapper, .el-select__wrapper, .el-textarea__inner {
  border-radius: var(--radius-sm) !important;
  transition: all var(--transition-fast);
}

/* select 下拉面板暗黑模式 */
.el-select-dropdown__item {
  font-size: var(--el-font-size-small);
  padding: 6px 16px;
}

html.dark .el-popper.is-light,
html.dark .el-select-dropdown {
  background: var(--el-bg-color) !important;
  border-color: var(--el-border-color) !important;
  color: var(--el-text-color-primary) !important;
}

html.dark .el-select-dropdown__item { color: var(--el-text-color-regular) !important; }

html.dark .el-select-dropdown__item.hover,
html.dark .el-select-dropdown__item:hover {
  background: var(--el-fill-color-light) !important;
  color: var(--el-color-primary) !important;
}

html.dark .el-select-dropdown__item.selected {
  color: var(--el-color-primary) !important;
  font-weight: 600;
}

/* date-picker 暗黑 */
html.dark .el-picker-panel {
  background: var(--el-bg-color) !important;
  border-color: var(--el-border-color) !important;
  color: var(--el-text-color-primary) !important;
}

html.dark .el-date-table td { color: var(--el-text-color-regular); }

html.dark .el-date-table td.current:not(.disabled) .el-date-table-cell__text {
  background: var(--el-color-primary) !important;
}

html.dark .el-month-table td .cell,
html.dark .el-year-table td .cell { color: var(--el-text-color-regular); }

html.dark .el-month-table td.current:not(.disabled) .cell,
html.dark .el-year-table td.current:not(.disabled) .cell { color: var(--el-color-primary); }

/* 按钮 / 分页 / 弹层 */
.el-button { font-weight: 500; border-radius: var(--radius-sm); transition: all var(--transition-fast); }

.el-pagination { padding: 12px 0 4px 0; }

.el-pagination .el-pager li { border-radius: var(--radius-sm); font-weight: 500; }

.el-dialog { border-radius: var(--radius-lg) !important; box-shadow: var(--shadow-lg) !important; }

.el-drawer { border-radius: var(--radius-lg) 0 0 var(--radius-lg) !important; }

.el-tag { border-radius: 20px; font-weight: 500; }

/* 加载动画 */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.view-enter { animation: fadeInUp 0.35s cubic-bezier(0.4, 0, 0.2, 1) both; }
</style>
