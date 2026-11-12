<!--
  E-billet d'une commande payée (/e/[slug]/e-billet?commande={id}) : l'événement, le QR de la
  commande (un seul, qui fait entrer toutes ses places, dessiné par le back), à présenter à l'entrée,
  le lien de téléchargement de son PDF, et les infos pratiques. En tête, la carte récapitulative
  compacte du parcours d'achat (TicketingEventRecap) plutôt que le hero : le QR doit être visible dès
  l'ouverture, sans défiler. Ouvert par l'id de la commande seul, sans compte ; jamais indexé (cf. routeRules).
  Ne dépend pas de la fiche (cf. middleware ticketing) : le slug de l'URL ne sert qu'au lien retour,
  affiché seulement si la page de l'événement est en ligne.
  Commande inconnue ou pas payée (404 du back) : renvoi à la fiche de l'événement. Toute autre
  erreur laisse l'acheteur réessayer sur place : son e-billet existe, il est peut-être à l'entrée.
-->
<template>
  <div class="e-billet-page">
    <div class="content">
      <div v-if="loading" class="loading-state" role="status">
        <span class="spinner" aria-hidden="true" />
      </div>

      <div v-else-if="failed" class="error-state" role="alert">
        <p class="error-message">{{ $t('ticketing.e-billet.error') }}</p>
        <button type="button" class="secondary-cta" @click="loadEBillet">
          <RefreshIcon class="icon" aria-hidden="true" />
          {{ $t('ticketing.e-billet.retry') }}
        </button>
      </div>

      <template v-else-if="eBillet && recap">
        <TicketingEventRecap :event="recap" :maps-url="eBillet.evenementInfos.mapsUrl" />

        <section class="qr-card">
          <img class="qr" :src="eBillet.qrImage" :alt="$t('ticketing.e-billet.qr-alt')" />
          <p class="valid-for">{{ $t('ticketing.e-billet.valid-for', eBillet.quantity) }}</p>
          <p class="show-at-entry">{{ $t('ticketing.e-billet.show-at-entry') }}</p>
          <a :href="pdfUrl" class="secondary-cta">
            <DownloadIcon class="icon" aria-hidden="true" />
            {{ $t('ticketing.e-billet.download-pdf') }}
          </a>
        </section>

        <section v-if="eBillet.evenementInfos.practicalInfo.length" class="practical-info">
          <h2 class="info-title">{{ $t('ticketing.e-billet.practical-info-title') }}</h2>
          <ul>
            <li v-for="line in eBillet.evenementInfos.practicalInfo" :key="line">{{ line }}</li>
          </ul>
        </section>

        <p class="contact">
          {{ $t('ticketing.e-billet.contact') }}
          <a href="mailto:contact@sgilt.alsace">contact@sgilt.alsace</a>
        </p>

        <NuxtLink v-if="eBillet.pageOnline" :to="`/e/${slug}`" class="secondary-cta">
          <ArrowLeftSIcon class="icon" aria-hidden="true" />
          {{ $t('ticketing.e-billet.back-to-event') }}
        </NuxtLink>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import TicketingEventRecap, { type EventRecap } from '~/components/ticketing/TicketingEventRecap.vue'
import { ArrowLeftSIcon, DownloadIcon, RefreshIcon } from '@remixicons/vue/line'
import { getEBillet, getEBilletPdfUrl, isCommandeNotFound } from '~/data/ticketing/service/commandeService'
import type { EBillet } from '~/data/ticketing/domain/Commande'

definePageMeta({ middleware: 'ticketing' })

const { t } = useI18n()
const route = useRoute()
const slug = String(route.params.slug)
// Garanti non vide par le middleware `ticketing`.
const commandeId = String(route.query.commande)

// Lien direct vers le PDF (route publique) : le navigateur le télécharge, sans JavaScript.
const pdfUrl = getEBilletPdfUrl(commandeId)

const eBillet = ref<EBillet | null>(null)
const loading = ref(true)
const failed = ref(false)

useHead({
  title: computed(() =>
    eBillet.value ? `${t('ticketing.e-billet.title')} · ${eBillet.value.title}` : t('ticketing.e-billet.title'),
  ),
})

async function loadEBillet(): Promise<void> {
  loading.value = true
  failed.value = false
  try {
    eBillet.value = await getEBillet(commandeId)
  } catch (error) {
    if (isCommandeNotFound(error)) {
      await navigateTo(`/e/${slug}`, { replace: true })
      return
    }
    failed.value = true
  } finally {
    loading.value = false
  }
}

onMounted(loadEBillet)

// L'événement de l'e-billet, sous la forme qu'attend la carte récapitulative.
const recap = computed<EventRecap | null>(() => {
  if (!eBillet.value) return null
  const { title, heroImage, evenementInfos } = eBillet.value
  return {
    title,
    subtitle: evenementInfos.subtitle,
    heroImage,
    date: evenementInfos.date,
    startsAt: evenementInfos.startsAt,
    endsAt: evenementInfos.endsAt,
    venue: evenementInfos.venue,
    city: evenementInfos.city,
  }
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// Contenu en colonne centrée, la carte récapitulative en tête.
.e-billet-page {
  display: flex;
  flex-direction: column;
  width: 100%;

  .content {
    width: 100%;
    max-width: 640px;
    margin: 0 auto;
    padding: $spacing-l $spacing-m;
    padding-bottom: $spacing-xxl;
    display: flex;
    flex-direction: column;
    gap: $spacing-l;
  }
}

// ─── Chargement, erreur ───────────────────────────────────────────────────────
.loading-state {
  display: flex;
  justify-content: center;
  padding: $spacing-xl 0;

  .spinner {
    width: 2.5rem;
    height: 2.5rem;
    border-radius: 50%;
    border: 3px solid $brand-primary;
    border-top-color: transparent;
    animation: spin 0.8s linear infinite;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-m;
  padding: $spacing-xl 0;
  text-align: center;

  .error-message {
    margin: 0;
    font-size: $font-size-md;
    color: $text-primary;
  }
}

// ─── QR ───────────────────────────────────────────────────────────────────────
// Fond blanc quel que soit le thème : le QR doit rester lisible par le lecteur.
.qr-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-xs;
  padding: $spacing-l $spacing-m;
  background: $surface-white;
  border: 1px solid $divider-color;
  border-radius: $radius-lg;
  text-align: center;

  .qr {
    width: 100%;
    max-width: 12rem;
    aspect-ratio: 1;
    margin-bottom: $spacing-s;
  }

  .valid-for {
    margin: 0;
    font-size: $font-size-lg;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }

  .show-at-entry {
    margin: 0;
    font-size: $font-size-md;
    color: $text-secondary;
  }

  .secondary-cta {
    margin-top: $spacing-s;
  }
}

// ─── Informations pratiques ───────────────────────────────────────────────────
.practical-info {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
  padding: $spacing-m;
  background: $surface-soft;
  border-radius: $radius-md;

  .info-title {
    margin: 0 0 0.3rem;
    font-size: $font-size-md;
    font-weight: $font-weight-semibold;
    color: $text-primary;
  }

  ul {
    margin: 0.3rem 0 0;
    padding-left: 1.1rem;
    display: flex;
    flex-direction: column;
    gap: 0.2rem;
  }

  li {
    font-size: $font-size-sm;
    color: $text-secondary;
  }
}

// ─── Contact ──────────────────────────────────────────────────────────────────
.contact {
  margin: 0;
  font-size: $font-size-sm;
  line-height: $line-height-relaxed;
  color: $text-secondary;
  text-align: center;

  a {
    color: $brand-primary;
    font-weight: $font-weight-semibold;
  }
}

// ─── CTA ─────────────────────────────────────────────────────────────────────
.secondary-cta {
  width: 100%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5em;
  height: 3.25rem;
  border-radius: 9999px;
  border: 1px solid $divider-color;
  background: $surface-white;
  color: $text-primary;
  font-family: inherit;
  font-size: $font-size-md;
  font-weight: $font-weight-semibold;
  cursor: pointer;
  text-decoration: none;
  transition: background 150ms ease;

  &:hover {
    background: $surface-soft;
  }

  .icon {
    width: 1.1rem;
    height: 1.1rem;
  }
}
</style>
