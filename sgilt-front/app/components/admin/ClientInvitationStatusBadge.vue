<!--
  Pastille de statut d'une invitation client dans le BO (non envoyée, envoyée, expirée).
-->
<template>
  <span class="status-badge" :style="{ background: config.pillBg, color: config.pillText }">
    {{ $t(`admin.invitations.status.${status}`) }}
  </span>
</template>

<script setup lang="ts">
import type { ClientInvitationStatus } from '~/data/admin/domain/ClientInvitationStatus'
import { CLIENT_INVITATION_STATUS_CONFIG } from '~/constants/client-invitation-status'

const props = defineProps<{ status: ClientInvitationStatus }>()

const config = computed(() => CLIENT_INVITATION_STATUS_CONFIG[props.status])
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.status-badge {
  display: inline-block;
  padding: 2px $spacing-xs;
  border-radius: $radius-sm;
  font-family: 'Inter', sans-serif;
  font-size: 0.7rem;
  font-weight: 600;
  white-space: nowrap;
  line-height: 1.4;
}
</style>
