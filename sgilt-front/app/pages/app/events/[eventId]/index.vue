<template>
  <div class="event-page">
    <!-- ── Desktop : accueil léger (carte et rubriques sont dans la sidebar) ──── -->
    <div v-if="isDesktop" class="home">
      <template v-if="event">
        <h1 class="title">{{ event.title }}</h1>
        <p class="invite">{{ $t('evenement.home.invite') }}</p>
      </template>
    </div>

    <!-- ── Mobile : même board que le parcours public ─────────────────────────── -->
    <template v-else>
      <EventBoard
        v-if="!pending && event"
        class="mobile-board"
        :event-meta="event"
        :rubriques="rubriques"
        :cover-image="coverImage"
        @settings="navigateTo(`/app/events/${eventId}/parametres`)"
        @rubrique="(rubriqueKey) => navigateTo(`/app/events/${eventId}/${rubriqueKey}`)"
      >
        <div class="sticky-cta">
          <SgiltButton @click="startAddPrestataireFlow">
            {{ $t('events.add-provider') }}
          </SgiltButton>
        </div>
      </EventBoard>
      <Sk v-else class="cover-banner-skeleton" light />
    </template>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import Sk from '~/components/basics/Sk.vue'
import EventBoard from '~/components/evenement/EventBoard.vue'
import { useEventContext } from '~/data/evenement/useEventContext'
import { defaultCoverPath } from '~/utils/eventCovers'

definePageMeta({ layout: 'app' })

const { eventId, event, rubriques, pending } = useEventContext()

const { isDesktop } = useDevice()

// ── Cover image ────────────────────────────────────────────────────────────────
const { toUrl } = useImageUrl()
const coverImage = computed(() =>
  event.value ? toUrl(event.value.coverImage || defaultCoverPath(event.value.eventType)) : '',
)

// ── Flow ajout prestataire ────────────────────────────────────────────────────
const { start } = useFlow()
const startAddPrestataireFlow = () => {
  start('add-prestataire', `Ajouter à ${event.value?.title ?? "l'événement"}`, {
    id: event.value?.id,
    nom: event.value?.title,
    date: event.value?.date ?? null,
    ville: event.value?.ville,
    ambiance: event.value?.ambiance,
    invites: event.value?.nbInvites,
  })
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// Le CTA fixe en bas d'écran ne doit pas masquer la dernière rubrique.
.mobile-board {
  padding-bottom: 6rem;
}

.event-page {
  flex: 1;
  display: flex;
  flex-direction: column;

  .home {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
    padding: $spacing-xl;

    .title {
      margin: 0;
      font-family: 'Cormorant Garamond', serif;
      font-size: 2.25rem;
      font-weight: 600;
      color: $text-primary;
    }

    .invite {
      margin: 0;
      color: $text-secondary;
      font-size: $font-size-md;
    }
  }
}

.cover-banner-skeleton {
  height: 200px;
}

.sticky-cta {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 20;
  padding: $spacing-s $spacing-m calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-s);
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  display: flex;
  justify-content: center;
}
</style>
