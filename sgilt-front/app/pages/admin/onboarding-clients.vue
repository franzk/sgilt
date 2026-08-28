<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>{{ $t('admin.onboarding-clients.title') }}</h1>
    </div>

    <AdminOnboardingTabs />

    <section class="list">
      <p v-if="loading">{{ $t('admin.onboarding-clients.loading') }}</p>
      <p v-else-if="rows.length === 0" class="empty">{{ $t('admin.onboarding-clients.empty') }}</p>
      <SgiltCard v-for="row in rows" :key="row.id" format="small" tag="div" :clickable="false">
        <template #avatar>
          <span class="avatar-initial">{{ row.email.charAt(0).toUpperCase() }}</span>
        </template>
        <div class="row-content">
          <div class="text">
            <p class="email">{{ row.email }}</p>
            <p class="meta">{{ $t('admin.onboarding-clients.target', { name: row.prestataireName }) }}</p>
            <p class="dates">
              {{ $t(`admin.onboarding-clients.state.${row.state}`) }}
              ·
              {{ $t('admin.onboarding-clients.sent-at', { date: formatDateTime(row.createdAt) }) }}
              ·
              {{ $t('admin.onboarding-clients.expires-at', { date: formatDateTime(row.expiresAt) }) }}
            </p>
          </div>
        </div>
      </SgiltCard>
    </section>
  </div>
</template>

<script setup lang="ts">
import SgiltCard from '~/components/basics/cards/SgiltCard.vue'
import AdminOnboardingTabs from '~/components/admin/AdminOnboardingTabs.vue'
import { formatDateTime } from '~/utils/dateUtils'

definePageMeta({ layout: 'admin' })

const { rows, loading, load } = useAdminUserOnboarding()

onMounted(() => load())
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.admin-page {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: $spacing-l;
  max-width: 720px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-s;
}

.list {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  .empty {
    color: $text-secondary;
  }

  .avatar-initial {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    background: $brand-accent;
    color: #fff;
    font-weight: 600;
  }

  .row-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $spacing-xs;
    min-width: 0;

    .text {
      display: flex;
      flex-direction: column;
      gap: 2px;
      min-width: 0;
    }

    .email {
      margin: 0;
      font-weight: 500;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .meta,
    .dates {
      margin: 0;
      font-size: 0.75rem;
      color: $text-secondary;
    }
  }
}
</style>
