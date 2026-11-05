<!--
  Aperçu de la page publique d'un événement pour son organisateur
  (/app/events/{eventId}/page-publique/apercu) : la fiche telle que la verront les visiteurs
  (PublicEventView, le même rendu que /e/[slug]), publiée ou non, sans bloc d'achat. Montre le
  contenu enregistré. Page pas encore créée ou page publique désactivée (404) : retour à l'écran
  Page publique, qui décide.
-->
<template>
  <div class="apercu">
    <div class="preview-banner">
      <NuxtLink :to="publicPagePath" class="back">
        <ArrowLeftIcon class="icon" aria-hidden="true" />
        {{ $t('evenement.page-publique.preview.back') }}
      </NuxtLink>
      <p class="text">{{ $t('evenement.page-publique.preview.banner') }}</p>
    </div>

    <Sk v-if="loading" class="skeleton" />

    <div v-else-if="loadFailed" class="load-error" role="alert">
      <p>{{ $t('evenement.page-publique.errors.LOAD') }}</p>
      <SgiltButton variant="secondary" @click="load">{{ $t('evenement.page-publique.retry') }}</SgiltButton>
    </div>

    <PublicEventView v-else-if="preview" :event="preview" />
  </div>
</template>

<script setup lang="ts">
import { ArrowLeftIcon } from '@remixicons/vue/line'
import Sk from '~/components/basics/Sk.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import PublicEventView from '~/components/ticketing/PublicEventView.vue'
import type { PublicEvent } from '~/data/ticketing/domain/PublicEvent'
import { fetchOrganizerPagePreview, isPublicPageNotFound } from '~/data/ticketing/service/organizerPageService'
import { useEventContext } from '~/data/evenement/useEventContext'

definePageMeta({ layout: 'app' })

const { t } = useI18n()
useHead({ title: t('evenement.page-publique.preview.page-title') })
useSeoMeta({ robots: 'noindex' })

const { eventId } = useEventContext()
const publicPagePath = `/app/events/${eventId}/page-publique`

const preview = ref<PublicEvent | null>(null)
const loading = ref(true)
const loadFailed = ref(false)

async function load() {
  loading.value = true
  loadFailed.value = false
  try {
    preview.value = await fetchOrganizerPagePreview(eventId)
  } catch (error) {
    if (isPublicPageNotFound(error)) return navigateTo(publicPagePath, { replace: true })
    loadFailed.value = true
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.apercu {
  display: flex;
  flex-direction: column;
  flex: 1;
  background: $surface-white;

  .preview-banner {
    display: flex;
    flex-direction: column;
    gap: $spacing-xxs;
    padding: $spacing-s $section-padding-x;
    background: $surface-soft;
    border-bottom: 1px solid $divider-color;

    .back {
      display: inline-flex;
      align-items: center;
      align-self: flex-start;
      gap: $spacing-xxs;
      color: $text-primary;
      font-size: $font-size-sm;
      text-decoration: none;

      .icon {
        width: 1.125rem;
        height: 1.125rem;
      }
    }

    .text {
      margin: 0;
      font-size: $font-size-sm;
      color: $text-secondary;
    }
  }

  .skeleton {
    height: 20rem;
    margin: $spacing-m $section-padding-x;
    border-radius: $radius-lg;
  }

  .load-error {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: $spacing-s;
    padding: $spacing-m $section-padding-x;

    p {
      margin: 0;
      font-size: $font-size-sm;
      color: $text-primary;
    }
  }
}
</style>
