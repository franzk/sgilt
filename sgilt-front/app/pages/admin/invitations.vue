<!--
  BO — /admin/invitations : liste des clients invités depuis le BO (deal conclu hors Sgilt), avec
  leur événement, le prestataire réservé et le statut de l'invitation. Le bouton envoie le mail
  d'invitation (non envoyée) ou le relance, même lien et validité réinitialisée (envoyée ou expirée,
  compte pas encore activé).
-->
<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>{{ $t('admin.invitations.title') }}</h1>
    </div>

    <AdminInvitationTabs />

    <section class="list">
      <p v-if="loading">{{ $t('admin.invitations.loading') }}</p>
      <p v-else-if="failure" class="error" role="alert">{{ $t(`admin.invitations.errors.${failure}`) }}</p>
      <p v-else-if="rows.length === 0" class="empty">{{ $t('admin.invitations.empty') }}</p>
      <SgiltCard v-for="row in rows" :key="row.id" format="small" tag="div" :clickable="false">
        <template #avatar>
          <span class="avatar-initial">{{ row.firstName.charAt(0) }}</span>
        </template>
        <div class="row-content">
          <div class="text">
            <p class="name">{{ row.firstName }} {{ row.lastName }}</p>
            <p class="email">{{ row.email }}</p>
            <p class="meta">
              {{ eventTypeLabel(row.eventType) }} · {{ formatDate(row.eventDate) }} · {{ row.prestataireName }}
            </p>
            <p v-if="row.status === 'SENT' || row.status === 'EXPIRED'" class="dates">
              {{ $t('admin.invitations.sent-at', { date: formatDateTime(row.linkSentAt) }) }}
              ·
              {{ $t(row.status === 'SENT' ? 'admin.invitations.expires-at' : 'admin.invitations.expired-at',
                    { date: formatDateTime(row.linkExpiresAt) }) }}
            </p>
          </div>
          <ClientInvitationStatusBadge :status="row.status" />
        </div>
        <template #cta>
          <SgiltButton variant="secondary" :loading="sendingId === row.id" @click="send(row.id)">
            {{ row.status === 'NOT_SENT' ? $t('admin.invitations.send') : $t('admin.invitations.resend') }}
          </SgiltButton>
        </template>
      </SgiltCard>
      <p v-if="sentId" class="success">{{ $t('admin.invitations.send-success') }}</p>
      <p v-if="sendErrorId" class="error" role="alert">{{ $t('admin.invitations.send-error') }}</p>
    </section>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import SgiltCard from '~/components/basics/cards/SgiltCard.vue'
import AdminInvitationTabs from '~/components/admin/AdminInvitationTabs.vue'
import ClientInvitationStatusBadge from '~/components/admin/ClientInvitationStatusBadge.vue'
import { formatDate, formatDateTime } from '~/utils/dateUtils'

definePageMeta({ layout: 'admin' })

const { t } = useI18n()

const { rows, loading, failure, load, send, sendingId, sendErrorId, sentId } = useAdminClientInvitations()
const { eventTypeLabel } = useEventTypeLabel()

onMounted(() => load())

useHead({ title: t('admin.invitations.title') })
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.admin-page {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: $spacing-l;
  max-width: 45rem;
  margin: 0 auto;
}

.page-header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-s;

  h1 {
    margin: 0;
  }
}

.list {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  .empty {
    color: $text-secondary;
  }

  // Sur mobile, la cta (envoyer / relancer) passe sous le contenu plutôt que de forcer la carte à
  // dépasser la largeur de l'écran.
  @media (max-width: #{$breakpoint-desktop - 1px}) {
    :deep(.sgilt-card.format-small) {
      flex-wrap: wrap;

      .cta {
        width: 100%;
        justify-content: flex-end;
      }
    }
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

    .meta,
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
