<!--
  Back-office admin — face publique des événements (/admin/evenements-publics).
  En haut, chaque événement avec ses deux interrupteurs, « Page publique » et « Billetterie »,
  enregistrés à chaque clic : l'équipe Sgilt active, l'organisateur administre (contenu,
  publication, billetterie, horaires) depuis son board. Pas de billetterie sans page ; couper la page
  coupe la billetterie ; une billetterie qui a des ventes ne se coupe pas.
-->
<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>{{ $t('admin.evenements-publics.title') }}</h1>
    </div>

    <p v-if="loading">{{ $t('admin.evenements-publics.loading') }}</p>
    <p v-if="failure" class="failure" role="alert">{{ $t(`admin.evenements-publics.errors.${failure}`) }}</p>

    <ul class="features-list">
      <li v-for="evenement in evenements" :key="evenement.id" class="features-row">
        <div class="identity">
          <span class="title">{{ evenement.title }}</span>
          <span class="meta">
            {{ formatDate(evenement.date) }} · {{ evenement.ownerEmail }}
            <template v-if="evenement.slug">
              ·
              <NuxtLink :to="`/e/${evenement.slug}`" target="_blank">/e/{{ evenement.slug }}</NuxtLink>
            </template>
          </span>
        </div>
        <div class="switches">
          <label class="switch">
            <input
              type="checkbox"
              :checked="evenement.features.pagePubliqueEnabled"
              :disabled="savingFeaturesId === evenement.id"
              @click.prevent="onTogglePage(evenement)"
            />
            {{ $t('admin.evenements-publics.features.page') }}
          </label>
          <label class="switch">
            <input
              type="checkbox"
              :checked="evenement.features.billetterieEnabled"
              :disabled="savingFeaturesId === evenement.id"
              @click.prevent="onToggleBilletterie(evenement)"
            />
            {{ $t('admin.evenements-publics.features.billetterie') }}
          </label>
          <span v-if="savingFeaturesId === evenement.id" class="spinner" aria-hidden="true" />
        </div>
        <p v-if="featuresFailure?.id === evenement.id" class="failure" role="alert">
          {{ $t(`admin.evenements-publics.features.errors.${featuresFailure.reason}`) }}
        </p>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import type { AdminEvenementListItem } from '~/data/admin/domain/AdminEvenement'
import { formatDate } from '~/utils/dateUtils'

definePageMeta({ layout: 'admin' })

const { t } = useI18n()

const {
  evenements,
  loading,
  failure,
  savingFeaturesId,
  featuresFailure,
  loadEvenements,
  saveFeatures,
  failFeatures,
} = useAdminEvenementsPublics()

// ── Interrupteurs ──
// La case ne bascule pas d'elle-même (click.prevent) : elle suit l'état enregistré, mis à jour au
// retour du back ; un refus la laisse donc où elle était.

// Couper la page coupe aussi la billetterie, qui n'existe pas sans elle.
function onTogglePage(evenement: AdminEvenementListItem) {
  const enabled = !evenement.features.pagePubliqueEnabled
  saveFeatures(evenement.id, {
    pagePubliqueEnabled: enabled,
    billetterieEnabled: enabled && evenement.features.billetterieEnabled,
  })
}

function onToggleBilletterie(evenement: AdminEvenementListItem) {
  const enabled = !evenement.features.billetterieEnabled
  if (enabled && !evenement.features.pagePubliqueEnabled) return failFeatures(evenement.id, 'PAGE_REQUIRED')
  saveFeatures(evenement.id, { pagePubliqueEnabled: true, billetterieEnabled: enabled })
}

onMounted(() => loadEvenements())

useHead({ title: t('admin.evenements-publics.title') })
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

  .page-header {
    h1 {
      margin: 0;
    }
  }

  .features-list {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
    margin: 0;
    padding: 0;
    list-style: none;

    .features-row {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      justify-content: space-between;
      gap: $spacing-xs $spacing-s;
      padding: $spacing-s $spacing-m;
      border-radius: $radius-md;
      background: $surface-soft;

      .identity {
        display: flex;
        flex-direction: column;
        gap: 0.15rem;
        min-width: 0;

        .title {
          font-weight: 600;
        }

        .meta {
          font-size: 0.8rem;
          color: $text-secondary;
        }
      }

      .switches {
        display: flex;
        align-items: center;
        gap: $spacing-s;

        .switch {
          display: flex;
          align-items: center;
          gap: 0.3rem;
          font-size: 0.85rem;
          cursor: pointer;
        }

        .spinner {
          width: 1rem;
          height: 1rem;
          border-radius: 50%;
          border: 2px solid $brand-primary;
          border-top-color: transparent;
          animation: spin 0.8s linear infinite;
        }
      }

      .failure {
        flex-basis: 100%;
      }
    }
  }

  .failure {
    margin: 0;
    padding: $spacing-xs $spacing-s;
    border-radius: $radius-sm;
    background: $surface-soft;
    font-size: 0.85rem;
    color: $state-error;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
