<!--
  Page publique d'un événement administrée par son organisateur (/app/events/{eventId}/page-publique),
  ouverte depuis la carte de l'accueil du board. N'existe que si le back-office a activé la page
  publique (sinon, retour au board). De haut en bas : bandeau d'état (à créer, brouillon :
  Prévisualiser, Publier ; publiée : lien public, Prévisualiser, Dépublier), puis le contenu de la
  page et son bouton (ventes et réglages de la billetterie : écran Billetterie). « Prévisualiser »
  et « Publier » restent cliquables sur une page pas encore créée : le clic dit quoi faire d'abord.
-->
<template>
  <div class="page-publique">
    <div class="column">
      <NuxtLink :to="boardPath" class="back">
        <ArrowLeftIcon class="icon" aria-hidden="true" />
        {{ $t('evenement.page-publique.back') }}
      </NuxtLink>

      <h1 class="title">{{ $t('evenement.page-publique.title') }}</h1>

      <Sk v-if="loading" class="skeleton" />

      <div v-else-if="loadFailed" class="load-error" role="alert">
        <p>{{ $t('evenement.page-publique.errors.LOAD') }}</p>
        <SgiltButton variant="secondary" @click="load">{{ $t('evenement.page-publique.retry') }}</SgiltButton>
      </div>

      <template v-else-if="publicPage">
        <!-- ── Bandeau d'état ──────────────────────────────────────────────────── -->
        <section class="status-banner" :class="status.toLowerCase()">
          <span class="status-label">{{ $t(`evenement.page-publique.status.${status}.label`) }}</span>
          <p class="status-text">{{ $t(`evenement.page-publique.status.${status}.text`) }}</p>
          <NuxtLink v-if="publicUrl" :to="publicUrl" target="_blank" class="public-link">
            {{ publicUrl }}
            <ExternalLinkIcon class="icon" aria-hidden="true" />
          </NuxtLink>
          <SgiltButton variant="secondary" @click="onPreview">
            {{ $t('evenement.page-publique.preview.open') }}
          </SgiltButton>
          <SgiltButton v-if="status === 'PUBLISHED'" variant="secondary" :loading="publishing" @click="onUnpublish">
            {{ $t('evenement.page-publique.unpublish') }}
          </SgiltButton>
          <SgiltButton v-else :loading="publishing" @click="onPublish">
            {{ $t('evenement.page-publique.publish') }}
          </SgiltButton>
          <p v-if="publicationFailure" class="failure" role="alert">
            {{ $t(`evenement.page-publique.errors.${publicationFailure}`) }}
          </p>
        </section>

        <!-- ── Contenu ─────────────────────────────────────────────────────────── -->
        <PublicPageContentBlock
          :page="publicPage.page"
          :saving="savingContent"
          :saved="contentSaved"
          :failure="contentFailure"
          @save="saveContent"
          @fail="failContent"
        />
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowLeftIcon, ExternalLinkIcon } from '@remixicons/vue/line'
import Sk from '~/components/basics/Sk.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import PublicPageContentBlock from '~/components/evenement/PublicPageContentBlock.vue'
import type { PublicPageStatus } from '~/data/ticketing/domain/OrganizerPublicPage'
import { useOrganizerPublicPage } from '~/data/ticketing/useOrganizerPublicPage'
import { useEventContext } from '~/data/evenement/useEventContext'

definePageMeta({ layout: 'app' })

const { t } = useI18n()
useHead({ title: t('evenement.page-publique.page-title') })

const { eventId } = useEventContext()
const boardPath = `/app/events/${eventId}`

const {
  publicPage,
  loading,
  loadFailed,
  notFound,
  savingContent,
  contentSaved,
  contentFailure,
  publishing,
  publicationFailure,
  load,
  saveContent,
  failContent,
  setPublished,
  failPublication,
} = useOrganizerPublicPage(eventId)

onMounted(load)

// Page publique pas (ou plus) activée par le back-office : rien à administrer ici.
watch(notFound, (value) => {
  if (value) navigateTo(boardPath, { replace: true })
})

const status = computed<PublicPageStatus>(() => {
  const page = publicPage.value?.page
  if (!page) return 'NOT_CREATED'
  return page.published ? 'PUBLISHED' : 'DRAFT'
})

const publicUrl = computed(() => {
  const page = publicPage.value?.page
  return page?.published ? `/e/${page.content.slug}` : null
})

function onPublish() {
  if (status.value === 'NOT_CREATED') return failPublication('NOT_CREATED')
  setPublished(true)
}

function onUnpublish() {
  setPublished(false)
}

// L'aperçu montre le contenu enregistré : rien à montrer tant que la page n'est pas créée.
function onPreview() {
  if (status.value === 'NOT_CREATED') return failPublication('PREVIEW_NOT_CREATED')
  navigateTo(`${boardPath}/page-publique/apercu`)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.page-publique {
  display: flex;
  justify-content: center;
  flex: 1;
  background: $surface-white;

  .column {
    display: flex;
    flex-direction: column;
    gap: $spacing-l;
    width: 100%;
    max-width: 30rem;
    padding: $spacing-m $section-padding-x $spacing-xl;

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

    .title {
      margin: 0;
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.75rem;
      font-weight: 600;
      color: $brand-primary;
    }

    .skeleton {
      height: 12rem;
      border-radius: $radius-lg;
    }

    .load-error {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      gap: $spacing-s;

      p {
        margin: 0;
        font-size: $font-size-sm;
        color: $text-primary;
      }
    }

    .status-banner {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      gap: $spacing-xs;
      padding: $spacing-m;
      border-radius: $radius-lg;
      background: $surface-soft;

      &.published {
        background: rgba($state-success, 0.08);

        .status-label {
          color: $state-success;
        }
      }

      .status-label {
        font-size: 0.7rem;
        font-weight: 700;
        letter-spacing: 0.08em;
        text-transform: uppercase;
        color: $text-secondary;
      }

      .status-text {
        margin: 0;
        font-size: $font-size-sm;
        color: $text-primary;
      }

      .public-link {
        display: inline-flex;
        align-items: center;
        gap: $spacing-xxs;
        margin-bottom: $spacing-xs;
        color: $brand-primary;
        font-size: $font-size-sm;
        font-weight: 600;
        word-break: break-all;

        .icon {
          flex-shrink: 0;
          width: 1rem;
          height: 1rem;
        }
      }

      .failure {
        margin: 0;
        font-size: $font-size-sm;
        color: $state-error;
      }
    }
  }
}
</style>
