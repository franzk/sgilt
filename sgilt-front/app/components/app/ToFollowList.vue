<!--
  Bloc « À suivre » de l'espace client : les réservations en attente de réponse ou en échange,
  tous événements confondus. Affiché dans l'onglet /app/a-suivre (mobile) et dans la colonne
  droite de l'accueil /app (desktop).
-->
<template>
  <section class="to-follow-list">
    <SectionHeading :title="t('to-follow.title')" :subtitle="t('to-follow.subtitle')" />

    <!-- Chargement -->
    <div v-if="loading" class="items">
      <Sk v-for="i in 3" :key="i" class="item-skeleton" />
    </div>

    <!-- Demandes en suspens -->
    <div v-else-if="pendingItems.length > 0" class="items">
      <DemandeItem
        v-for="item in pendingItems"
        :key="item.reservationId"
        :prestataire-name="item.prestataireName"
        :prestataire-avatar="item.prestataireAvatar"
        :evenement-title="item.evenementTitle"
        :status="item.status"
        @click="navigateTo(`/app/events/${item.evenementId}/reservations/${item.reservationId}`)"
      />
    </div>

    <!-- Tout confirmé -->
    <p v-else-if="data?.hasConfirmed" class="message success">
      {{ t('home.all-confirmed') }}
    </p>

    <!-- Rien en cours -->
    <p v-else class="message">{{ t('to-follow.empty') }}</p>
  </section>
</template>

<script setup lang="ts">
import DemandeItem from '~/components/app/DemandeItem.vue'
import SectionHeading from '~/components/app/SectionHeading.vue'
import Sk from '~/components/basics/Sk.vue'
import { useActiveReservations } from '~/data/reservation/useActiveReservations'

const { t } = useI18n()

const { data, loading } = useActiveReservations()

const pendingItems = computed(
  () =>
    data.value?.items.filter((i) => i.status === 'nouvelle' || i.status === 'en_discussion') ??
    [],
)
</script>

<style scoped lang="scss">
.to-follow-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;

  .items {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;

    .item-skeleton {
      height: 4.5rem;
      border-radius: $radius-lg;
    }
  }

  .message {
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    color: $text-secondary;
    margin: 0;

    &.success {
      color: $state-success;
      font-weight: 500;
    }
  }
}
</style>
