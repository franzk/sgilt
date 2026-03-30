<template>
  <div class="add-prestataire">
    <ContextBanner />

    <Transition :name="transitionName" mode="out-in">
      <PrestataireSearch
        v-if="currentView === 'search'"
        key="search"
        selectable
        :top-offset="SEARCH_HEADER_OFFSET"
        @select="onPrestataireSelect"
      />

      <PrestataireDetails
        v-else-if="currentView === 'details' && selectedPrestataire"
        key="details"
        :prestataire="selectedPrestataire"
        disable-date
        @back="currentView = 'search'"
        @select="openContactDialog"
      />
    </Transition>

    <!-- ── Dialog envoi demande ──────────────────────────────────────────────── -->
    <SgiltDialog v-model:open="contactDialogOpen" title="Envoyer une demande" max-width="520px">
      <div class="contact-form">
        <!-- Résumé événement (lecture seule) -->
        <div v-if="eventContext" class="event-summary">
          <p class="summary-title">{{ eventContext.nom }}</p>
          <div class="summary-details">
            <span v-if="eventContext.date"> 📅 {{ formatDate(eventContext.date) }} </span>
            <span v-if="eventContext.ville"> 📍 {{ eventContext.ville }} </span>
            <span v-if="eventContext.invites"> 👥 {{ eventContext.invites }} invités </span>
          </div>
        </div>

        <!-- Prestataire cible -->
        <div v-if="selectedPrestataire" class="prestataire-target">
          <span class="target-label">Pour</span>
          <span class="target-name">{{ selectedPrestataire.name }}</span>
        </div>

        <!-- Message optionnel -->
        <div class="field">
          <label class="label"
            >Message au prestataire <span class="optional">(optionnel)</span></label
          >
          <textarea
            v-model="contactMessage"
            class="textarea"
            placeholder="Dites-leur ce que vous avez en tête…"
            rows="4"
          />
        </div>

        <div class="form-actions">
          <SgiltButton variant="secondary" @click="contactDialogOpen = false">Annuler</SgiltButton>
          <SgiltButton :loading="contactSending" @click="submitContact">
            Envoyer la demande
          </SgiltButton>
        </div>
      </div>
    </SgiltDialog>
  </div>
</template>

<script setup lang="ts">
import ContextBanner from '~/components/app/ContextBanner.vue'
import PrestataireSearch from '~/components/prestataire/PrestataireSearch.vue'
import PrestataireDetails from '~/components/prestataire/PrestataireDetails.vue'
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import { EventMockService } from '~/services/event.mock'
import { SearchMockService } from '~/services/search.mock'
import type { PrestataireCardDetail, PrestataireDetail } from '~/types/prestataire'

definePageMeta({ layout: 'default' })

// ── Route ─────────────────────────────────────────────────────────────────────
const route = useRoute()
const eventId = route.params.eventId as string

// ── Context ───────────────────────────────────────────────────────────────────
// 3.3rem = $app-header-height, 44px = ContextBanner height
const SEARCH_HEADER_OFFSET = 'calc(3.3rem + 44px)'

const { eventContext, setEventContext, abort } = useAddPrestataireContext()
const { dateModel } = useSearchUi()

onMounted(async () => {
  const event = await EventMockService.getById(eventId)
  if (!event) return
  if (event.date) dateModel.value = new Date(event.date)
  setEventContext({
    id: event.id,
    nom: event.title,
    date: event.date ?? null,
    ville: event.ville,
    ambiance: event.ambiance,
    invites: event.nbInvites,
  })
})

// ── Navigation entre vues ──────────────────────────────────────────────────────
type View = 'search' | 'details'
const currentView = ref<View>('search')
const transitionName = ref('slide-forward')

const selectedPrestataire = ref<PrestataireDetail | null>(null)

async function onPrestataireSelect(card: PrestataireCardDetail) {
  const detail = await SearchMockService.getBySlug(card.slug)
  if (!detail) return
  selectedPrestataire.value = detail
  transitionName.value = 'slide-forward'
  currentView.value = 'details'
}

watch(currentView, (next, prev) => {
  transitionName.value = next === 'details' ? 'slide-forward' : 'slide-back'
})

// ── Dialog contact ─────────────────────────────────────────────────────────────
const contactDialogOpen = ref(false)
const contactMessage = ref('')
const contactSending = ref(false)

function openContactDialog() {
  contactMessage.value = ''
  contactDialogOpen.value = true
}

async function submitContact() {
  contactSending.value = true
  // TODO: appel API réel
  await new Promise((r) => setTimeout(r, 600))
  contactSending.value = false
  contactDialogOpen.value = false
  abort()
}

// ── Helpers ───────────────────────────────────────────────────────────────────
function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.add-prestataire {
  display: flex;
  flex-direction: column;
  min-height: 100dvh;
}

// ── Transitions de vue ────────────────────────────────────────────────────────
.slide-forward-enter-active,
.slide-forward-leave-active,
.slide-back-enter-active,
.slide-back-leave-active {
  transition:
    transform 220ms ease,
    opacity 220ms ease;
}

.slide-forward-enter-from {
  transform: translateX(32px);
  opacity: 0;
}
.slide-forward-leave-to {
  transform: translateX(-32px);
  opacity: 0;
}
.slide-back-enter-from {
  transform: translateX(-32px);
  opacity: 0;
}
.slide-back-leave-to {
  transform: translateX(32px);
  opacity: 0;
}

// ── Dialog contact ────────────────────────────────────────────────────────────
.contact-form {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-m $spacing-l $spacing-l;
}

.event-summary {
  background: $surface-soft;
  border-radius: $radius-md;
  padding: $spacing-s $spacing-m;

  .summary-title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.1rem;
    font-weight: 600;
    color: $brand-primary;
    margin: 0 0 $spacing-xxs;
  }

  .summary-details {
    display: flex;
    flex-wrap: wrap;
    gap: $spacing-xs;
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    color: $text-secondary;
  }
}

.prestataire-target {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  font-family: 'Inter', sans-serif;

  .target-label {
    font-size: 0.75rem;
    color: $text-secondary;
    text-transform: uppercase;
    letter-spacing: 0.06em;
    font-weight: 600;
  }

  .target-name {
    font-size: 0.95rem;
    font-weight: 600;
    color: $text-primary;
  }
}

.field {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  .label {
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    font-weight: 600;
    letter-spacing: 0.04em;
    text-transform: uppercase;
    color: $text-secondary;

    .optional {
      font-weight: 400;
      text-transform: none;
      letter-spacing: 0;
      opacity: 0.7;
    }
  }

  .textarea {
    width: 100%;
    padding: $spacing-s $spacing-m;
    border: 1px solid $divider-color;
    border-radius: $radius-sm;
    font-family: inherit;
    font-size: 0.95rem;
    color: $text-primary;
    background: $surface-soft;
    resize: vertical;
    outline: none;
    box-sizing: border-box;
    line-height: 1.6;
    transition:
      border-color 150ms ease,
      box-shadow 150ms ease;

    &:focus {
      border-color: $input-focus-border-color;
      box-shadow: $input-focus-box-shadow;
      background: #fff;
    }

    &::placeholder {
      color: $text-secondary;
      opacity: 0.5;
    }
  }
}

.form-actions {
  display: flex;
  gap: $spacing-s;
  justify-content: flex-end;
}
</style>
