<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<template>
  <RouterView />
</template>

<style>
/* ========================================
   Font Import
   ======================================== */
@import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500;600&family=Inter:wght@300;400;500;600;700&display=swap');

/* ========================================
   全局基础
   ======================================== */
* { margin: 0; padding: 0; box-sizing: border-box; }

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  font-weight: 400;
  font-size: 13.5px;
  line-height: 1.6;
  background: #F1F5F9;
  color: #0F172A;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  letter-spacing: -0.01em;
}

/* ========================================
   全局细滚动条 — minimal
   ======================================== */
* { scrollbar-width: thin; scrollbar-color: rgba(0,0,0,0.12) transparent; }
*::-webkit-scrollbar { width: 4px; height: 4px; }
*::-webkit-scrollbar-track { background: transparent; }
*::-webkit-scrollbar-thumb { background: rgba(0,0,0,0.12); border-radius: 20px; }
*::-webkit-scrollbar-thumb:hover { background: rgba(0,0,0,0.22); }
*::-webkit-scrollbar-corner { background: transparent; }

/* ========================================
   Element Plus 主题变量（浅色）
   ======================================== */
:root {
  --el-color-primary: #4F46E5;
  --el-color-primary-light-3: #818CF8;
  --el-color-primary-light-5: #A5B4FC;
  --el-color-primary-light-7: #C7D2FE;
  --el-color-primary-light-8: #DDD6FE;
  --el-color-primary-light-9: #EEF2FF;
  --el-border-color: #E2E8F0;
  --el-border-color-light: #E2E8F0;
  --el-border-color-lighter: #F1F5F9;
  --el-bg-color: #FFF;
  --el-bg-color-page: #F1F5F9;
  --el-border-radius-base: 8px;
  --el-color-success: #059669;
  --el-color-danger: #DC2626;
  --el-color-warning: #D97706;

  /* 自定义 design tokens */
  --surface-elevated: #FFFFFF;
  --surface-inset: #F8FAFC;
  --surface-hover: #F1F5F9;
  --shadow-sm: 0 1px 2px rgba(0,0,0,0.04);
  --shadow-md: 0 4px 12px rgba(0,0,0,0.06);
  --shadow-lg: 0 12px 32px rgba(0,0,0,0.08);
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 14px;
  --font-mono: 'JetBrains Mono', 'SF Mono', 'Fira Code', monospace;
  --transition-fast: 150ms cubic-bezier(0.4, 0, 0.2, 1);
  --transition-smooth: 250ms cubic-bezier(0.4, 0, 0.2, 1);
}

/* ========================================
   暗黑模式 — 全局覆盖
   ======================================== */
html.dark body {
  background: #0a0a0a;
  color: #e5e7eb;
}

html.dark *::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.15); }
html.dark *::-webkit-scrollbar-thumb:hover { background: rgba(255,255,255,0.25); }

html.dark {
  --el-color-primary: #A5B4FC;
  --el-color-primary-light-3: #818CF8;
  --el-color-primary-light-5: #6366F1;
  --el-color-primary-light-7: #4F46E5;
  --el-color-primary-light-8: #4338CA;
  --el-color-primary-light-9: #3730A3;
  --el-color-white: #e5e7eb;
  --el-color-black: #0a0a0a;
  --el-color-info: #818CF8;
  --el-border-color: #363738;
  --el-border-color-light: #4a4b4d;
  --el-border-color-lighter: #2a2b2d;
  --el-bg-color: #1d1e1f;
  --el-bg-color-page: #0a0a0a;
  --el-text-color-primary: #e5e7eb;
  --el-text-color-regular: #c8c9cc;
  --el-text-color-secondary: #98999c;
  --el-text-color-placeholder: #78797c;
  --el-fill-color-blank: #1d1e1f;
  --el-fill-color: #222324;
  --el-fill-color-light: #2a2b2d;
  --el-fill-color-lighter: #363738;
  --el-color-success: #34D399;
  --el-color-danger: #F87171;
  --el-color-warning: #FBBF24;

  --surface-elevated: #1d1e1f;
  --surface-inset: #141414;
  --surface-hover: #222324;
  --shadow-sm: 0 1px 2px rgba(0,0,0,0.3);
  --shadow-md: 0 4px 12px rgba(0,0,0,0.4);
  --shadow-lg: 0 12px 32px rgba(0,0,0,0.5);
}

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
   Element Plus 全局覆盖 - 精细打磨
   ======================================== */

/* 表格 */
.el-table {
  --el-table-border-color: var(--el-border-color);
  border-radius: var(--radius-md) !important;
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  font-size: 13px;
}

.el-table::before { display: none; }

.el-table th.el-table__cell {
  background: var(--surface-inset) !important;
  font-weight: 600;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--el-text-color-secondary);
  border-bottom: 2px solid var(--el-border-color) !important;
  padding: 10px 0 8px 0;
}

.el-table td.el-table__cell {
  padding: 10px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.el-table .el-table__row:hover > td {
  background: var(--surface-hover) !important;
}

.el-table__inner-wrapper::before { display: none !important; }

/* 表单 */
.el-form--inline .el-form-item {
  margin-right: 16px;
}

.el-input__wrapper, .el-select__wrapper, .el-textarea__inner {
  border-radius: var(--radius-sm) !important;
  transition: all var(--transition-fast);
}

.el-input__wrapper:hover, .el-select__wrapper:hover {
  border-color: var(--el-color-primary-light-3);
}

/* select 下拉面板暗黑模式 */
.el-select-dropdown__item {
  font-size: 13px;
  padding: 6px 16px;
}

html.dark .el-popper.is-light,
html.dark .el-select-dropdown {
  background: var(--el-bg-color) !important;
  border-color: var(--el-border-color) !important;
  color: var(--el-text-color-primary) !important;
}

html.dark .el-select-dropdown__item {
  color: var(--el-text-color-regular) !important;
}

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

html.dark .el-date-table td {
  color: var(--el-text-color-regular);
}

html.dark .el-date-table td.current:not(.disabled) .el-date-table-cell__text {
  background: var(--el-color-primary) !important;
}

html.dark .el-month-table td .cell,
html.dark .el-year-table td .cell {
  color: var(--el-text-color-regular);
}

html.dark .el-month-table td.current:not(.disabled) .cell,
html.dark .el-year-table td.current:not(.disabled) .cell {
  color: var(--el-color-primary);
}

/* 按钮 */
.el-button {
  font-weight: 500;
  letter-spacing: -0.01em;
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
}

.el-button--primary {
  box-shadow: 0 2px 6px rgba(79,70,229,0.25);
}

.el-button--primary:hover {
  box-shadow: 0 4px 12px rgba(79,70,229,0.35);
  transform: translateY(-1px);
}

.el-button--primary:active {
  transform: translateY(0);
}

/* 分页 */
.el-pagination {
  padding: 12px 0 4px 0;
}

.el-pagination .el-pager li {
  border-radius: var(--radius-sm);
  font-weight: 500;
}

/* Dialog */
.el-dialog {
  border-radius: var(--radius-lg) !important;
  box-shadow: var(--shadow-lg) !important;
}

.el-drawer {
  border-radius: var(--radius-lg) 0 0 var(--radius-lg) !important;
}

/* Tag */
.el-tag {
  border-radius: 20px;
  font-weight: 500;
  letter-spacing: -0.01em;
}

/* 加载动画 */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.view-enter {
  animation: fadeInUp 0.35s cubic-bezier(0.4, 0, 0.2, 1) both;
}
</style>
