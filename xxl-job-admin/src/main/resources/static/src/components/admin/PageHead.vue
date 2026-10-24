<script lang="ts" setup>
defineProps<{
  icon?: string
  title: string
  desc?: string
}>()
</script>

<template>
  <header class="admin-head">
    <slot name="lead"/>
    <span class="admin-head__title">{{ title }}</span>
    <span v-if="desc" class="admin-head__desc">{{ desc }}</span>
    <div class="admin-head__actions">
      <slot name="actions"/>
    </div>
  </header>
</template>
