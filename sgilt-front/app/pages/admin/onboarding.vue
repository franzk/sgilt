<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>{{ $t('admin.onboarding.title') }}</h1>
      <NuxtLink class="back-link" to="/admin">{{ $t('admin.onboarding.back-to-prestataires') }}</NuxtLink>
    </div>

    <section class="list">
      <p v-if="loading">{{ $t('admin.onboarding.loading') }}</p>
      <p v-else-if="rows.length === 0" class="empty">{{ $t('admin.onboarding.empty') }}</p>
      <SgiltCard v-for="row in rows" :key="row.prestataireId" format="small" tag="div" :clickable="false">
        <template #avatar>
          <span class="avatar-initial">{{ row.prestataireName.charAt(0) }}</span>
        </template>
        <div class="row-content">
          <div class="text">
            <p class="name">{{ row.prestataireName }}</p>
            <p class="email">{{ row.email }}</p>
            <p class="dates">
              {{ $t('admin.onboarding.sent-at', { date: formatDateTime(row.linkSentAt) }) }}
              ·
              {{ $t('admin.onboarding.expires-at', { date: formatDateTime(row.linkExpiresAt) }) }}
            </p>
          </div>
        </div>
        <template #cta>
          <SgiltButton
            variant="secondary"
            :loading="resendingId === row.prestataireId"
            @click="resend(row.prestataireId)"
          >
            {{ $t('admin.onboarding.resend') }}
          </SgiltButton>
        </template>
      </SgiltCard>
      <p v-if="resentId" class="success">{{ $t('admin.onboarding.resend-success') }}</p>
      <p v-if="resendErrorId" class="error">{{ $t('admin.onboarding.resend-error') }}</p>
    </section>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import SgiltCard from '~/components/basics/cards/SgiltCard.vue'
import { formatDateTime } from '~/utils/dateUtils'

definePageMeta({ layout: 'default' })

const { rows, loading, load, resend, resendingId, resendErrorId, resentId } = useAdminOnboarding()

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

  .back-link {
    font-size: 0.85rem;
    color: $text-secondary;
  }
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

    .name {
      margin: 0;
      font-weight: 500;
    }

    .email {
      margin: 0;
      font-size: 0.8rem;
      color: $text-secondary;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .dates {
      margin: 0;
      font-size: 0.75rem;
      color: $text-secondary;
    }
  }

  .success {
    margin: 0;
    font-size: 0.85rem;
    color: $state-success;
  }

  .error {
    margin: 0;
    font-size: 0.85rem;
    color: $state-error;
  }
}
</style>
