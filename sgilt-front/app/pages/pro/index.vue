<!--
  Accueil de l'espace prestataire (/pro) : salutation et tuiles de compteurs, puis trois blocs
  condensés (3 réservations chacun, la date la plus proche d'abord) avec un lien vers la liste
  complète — À contacter et À conclure vers l'onglet Réservations filtré, Vos prochaines
  prestations vers le Calendrier.
-->
<template>
  <div class="pro-home">
    <ProBoardGreeting
      :loading="loading"
      :error="error"
      :counts="counts"
      :background-image="heroImage"
    />

    <div class="blocks">
      <p v-if="error" class="error">{{ $t('pro.home.error') }}</p>

      <template v-else>
        <ProHomeBlock
          :title="$t('pro.home.blocks.a-contacter.title')"
          :count="loading ? undefined : aContacter.length"
          to="/pro/reservations"
          :link-label="$t('pro.home.see-all')"
          :reservations="aContacter"
          :empty-text="$t('pro.home.blocks.a-contacter.empty')"
          :loading="loading"
        />

        <ProHomeBlock
          :title="$t('pro.home.blocks.a-conclure.title')"
          :count="loading ? undefined : aConclure.length"
          to="/pro/reservations?filtre=a-conclure"
          :link-label="$t('pro.home.see-all')"
          :reservations="aConclure"
          :empty-text="$t('pro.home.blocks.a-conclure.empty')"
          :loading="loading"
        />

        <ProHomeBlock
          :title="$t('pro.home.blocks.prochaines.title')"
          to="/pro/calendrier"
          :link-label="$t('pro.home.calendar-link')"
          :reservations="upcoming"
          :empty-text="$t('pro.home.blocks.prochaines.empty')"
          :loading="loading"
        />
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import ProBoardGreeting from '~/components/pro/ProBoardGreeting.vue'
import ProHomeBlock from '~/components/pro/ProHomeBlock.vue'
import { useProReservations } from '~/data/reservation/useProReservations'
import {
  fetchProReservationsToHandle,
  fetchProConfirmedReservations,
} from '~/data/reservation/service/reservationService'
import type { ProReservationSummary } from '~/data/reservation/domain/ProReservationSummary'
import { ficheCoverImageRef } from '~/data/prestataire/usePrestataire'

definePageMeta({ layout: 'pro' })

// ── Image de la fiche (filigrane du header) ─────────────────────────────────────
const currentUser = useCurrentUser()
const { prestataire, loadMaFiche } = usePrestataire()
const { toUrl } = useImageUrl()

watch(
  () => currentUser.slug,
  (slug) => {
    if (slug) loadMaFiche()
  },
  { immediate: true },
)

const heroImage = computed<string | undefined>(() => {
  const ref = prestataire.value ? ficheCoverImageRef(prestataire.value) : undefined
  return ref ? toUrl(ref) : undefined
})

// ── Réservations ───────────────────────────────────────────────────────────────
const toHandle = useProReservations(fetchProReservationsToHandle)
const confirmed = useProReservations(fetchProConfirmedReservations)

const loading = computed<boolean>(() => toHandle.loading.value || confirmed.loading.value)
const error = computed<boolean>(() => !!toHandle.error.value || !!confirmed.error.value)

function byDate(list: ProReservationSummary[]): ProReservationSummary[] {
  return [...list].sort((a, b) => a.date.getTime() - b.date.getTime())
}

const aContacter = computed<ProReservationSummary[]>(() =>
  byDate(toHandle.reservations.value.filter((r) => r.statut === 'nouvelle')),
)

const aConclure = computed<ProReservationSummary[]>(() =>
  byDate(toHandle.reservations.value.filter((r) => r.statut === 'en_discussion')),
)

// Confirmées à venir (aujourd'hui inclus), la plus proche d'abord.
const upcoming = computed<ProReservationSummary[]>(() => {
  const startOfToday = new Date()
  startOfToday.setHours(0, 0, 0, 0)
  return byDate(confirmed.reservations.value.filter((r) => r.date >= startOfToday))
})

// Prestations confirmées du mois en cours (passées ou à venir).
const thisMonth = computed<number>(() => {
  const now = new Date()
  return confirmed.reservations.value.filter(
    (r) => r.date.getFullYear() === now.getFullYear() && r.date.getMonth() === now.getMonth(),
  ).length
})

const counts = computed(() => ({
  aContacter: aContacter.value.length,
  aConclure: aConclure.value.length,
  ceMois: thisMonth.value,
}))
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.pro-home {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  background-color: $surface-soft;

  .blocks {
    display: flex;
    flex-direction: column;
    gap: $spacing-xl;
    padding: $spacing-xl $spacing-m;

    .error {
      margin: 0;
      text-align: center;
      font-size: 0.875rem;
      color: $text-secondary;
    }
  }
}
</style>
