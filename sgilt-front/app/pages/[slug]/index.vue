<template>
  <div v-if="prestataire" class="provider-page">
    <!-- ═══════════════════════════════════════════
         HERO
    ════════════════════════════════════════════ -->
    <PrestataireHero :prestataire="prestataire" @open-video="openVideo" @open-photo="openGallery" />

    <!-- ═══════════════════════════════════════════
         LAYOUT PRINCIPAL
    ════════════════════════════════════════════ -->
    <div class="page-layout">
      <!-- ── Sidebar : datepicker + tarifs + CTA ── -->
      <aside class="page-layout__sidebar">
        <div class="sidebar-block">
          <SgiltDatePicker
            v-model="dateModel"
            :booked-dates="unavailableDatesAsDate"
            placeholder="Vérifier une date"
          />
          <Transition name="fade">
            <div v-if="dateModel" class="availability-badge" :class="availabilityClass">
              <span class="availability-badge__icon">{{ availabilityIcon }}</span>
              <span>{{ availabilityLabel }}</span>
            </div>
          </Transition>
        </div>

        <div v-if="prestataire.budget" class="sidebar-block sidebar-budget">
          <h3 class="sidebar-budget__title">Tarifs</h3>
          <p class="sidebar-budget__text">{{ prestataire.budget }}</p>
        </div>

        <SgiltButton class="sidebar-cta" @click="openContactModal">
          Envoyer une demande
        </SgiltButton>
      </aside>

      <!-- ── Contenu principal ── -->
      <div class="page-layout__main">
        <div class="provider-content">
          <!-- CE QUE NOUS PROPOSONS -->
          <section v-if="prestataire.offerings.length > 0" class="section">
            <h2 class="section__title">Ce que nous proposons</h2>
            <ul class="offerings">
              <li v-for="item in prestataire.offerings" :key="item" class="offering-item">
                {{ item }}
              </li>
            </ul>
          </section>

          <!-- TOUCHE IDENTITAIRE -->
          <section v-if="prestataire.identity" class="section identity-spotlight">
            <div class="identity-spotlight__content">
              <blockquote class="identity-spotlight__quote">
                {{ prestataire.identity.quote }}
              </blockquote>
              <p class="identity-spotlight__bio">{{ prestataire.identity.bio }}</p>
            </div>
          </section>

          <!-- BADGES -->
          <section v-if="prestataire.badges.length > 0" class="section badges-section">
            <div class="badges">
              <EngagementBadge
                v-for="badge in prestataire.badges"
                :key="badge.label"
                :badge="badge"
              />
            </div>
          </section>

          <!-- BUDGET (mobile uniquement — sur desktop dans la sidebar) -->
          <section v-if="prestataire.budget" class="section budget-section mobile-only">
            <h2 class="section__title">Tarifs</h2>
            <p class="budget-text">{{ prestataire.budget }}</p>
          </section>

          <!-- TÉMOIGNAGES -->
          <section
            v-if="prestataire.testimonials && prestataire.testimonials.length > 0"
            class="section"
          >
            <h2 class="section__title">Ils en parlent</h2>
            <div class="testimonials">
              <blockquote v-for="t in prestataire.testimonials" :key="t.author" class="testimonial">
                <p class="testimonial__text">« {{ t.text }} »</p>
                <footer class="testimonial__footer">
                  <span class="testimonial__author">{{ t.author }}</span>
                  <span v-if="t.eventType" class="testimonial__event">{{ t.eventType }}</span>
                </footer>
              </blockquote>
            </div>
          </section>

          <!-- INFORMATIONS PRATIQUES -->
          <section v-if="hasInfosPratiques" class="section infos-section">
            <h2 class="section__title">Informations pratiques</h2>

            <div
              v-if="prestataire.logistics && prestataire.logistics.length > 0"
              class="infos-block"
            >
              <h3 class="infos-block__title">Logistique</h3>
              <ul class="infos-list">
                <li v-for="item in prestataire.logistics" :key="item">{{ item }}</li>
              </ul>
            </div>

            <div
              v-if="prestataire.technical && prestataire.technical.length > 0"
              class="infos-block"
            >
              <h3 class="infos-block__title">Technique</h3>
              <ul class="infos-list">
                <li v-for="item in prestataire.technical" :key="item">{{ item }}</li>
              </ul>
            </div>

            <div v-if="prestataire.faq && prestataire.faq.length > 0" class="infos-block">
              <h3 class="infos-block__title">Questions fréquentes</h3>
              <div class="faq">
                <div v-for="item in prestataire.faq" :key="item.question" class="faq-item">
                  <p class="faq-item__question">{{ item.question }}</p>
                  <p class="faq-item__answer">{{ item.answer }}</p>
                </div>
              </div>
            </div>
          </section>
        </div>
      </div>
    </div>

    <!-- ═══════════════════════════════════════════
         STICKY CTA (mobile uniquement)
    ════════════════════════════════════════════ -->
    <div class="sticky-cta">
      <SgiltButton class="sticky-cta__button" @click="openContactModal">
        Envoyer une demande
      </SgiltButton>
    </div>

    <!-- ═══════════════════════════════════════════
         TUNNEL DEMANDE (bottom sheet mobile)
    ════════════════════════════════════════════ -->
    <DemandeBottomSheet
      :is-open="showDemande"
      :prestataire-name="prestataire.name"
      @close="showDemande = false"
    />

    <!-- ═══════════════════════════════════════════
         MODALE GALERIE
    ════════════════════════════════════════════ -->
    <Teleport to="body">
      <Transition name="modal">
        <div
          v-if="showGallery"
          class="modal-overlay"
          @click.self="closeGallery"
          role="dialog"
          aria-modal="true"
          aria-label="Galerie photos"
        >
          <button class="modal-close" @click="closeGallery" aria-label="Fermer">✕</button>
          <div class="gallery">
            <button
              class="gallery__nav gallery__nav--prev"
              @click="prevPhoto"
              aria-label="Photo précédente"
            >
              ‹
            </button>
            <img
              :src="galleryPhotos[galleryIndex]"
              :alt="`Photo ${galleryIndex + 1}`"
              class="gallery__image"
            />
            <button
              class="gallery__nav gallery__nav--next"
              @click="nextPhoto"
              aria-label="Photo suivante"
            >
              ›
            </button>
            <div class="gallery__counter">{{ galleryIndex + 1 }} / {{ galleryPhotos.length }}</div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- ═══════════════════════════════════════════
         MODALE VIDÉO
    ════════════════════════════════════════════ -->
    <Teleport to="body">
      <Transition name="modal">
        <div
          v-if="showVideo"
          class="modal-overlay modal-overlay--dark"
          @click.self="closeVideo"
          role="dialog"
          aria-modal="true"
          aria-label="Vidéo"
        >
          <button class="modal-close" @click="closeVideo" aria-label="Fermer">✕</button>
          <div class="video-wrapper">
            <iframe
              :src="`https://www.youtube.com/embed/${prestataire.youtubeId}?autoplay=1`"
              allow="
                accelerometer;
                autoplay;
                clipboard-write;
                encrypted-media;
                gyroscope;
                picture-in-picture;
              "
              allowfullscreen
              class="video-frame"
              title="Vidéo du prestataire"
            />
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>

  <!-- État d'erreur / 404 -->
  <div v-else-if="!loading" class="not-found">
    <p>Ce prestataire est introuvable.</p>
    <NuxtLink to="/search">← Retour à la recherche</NuxtLink>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import PrestataireHero from '~/components/prestataire/PrestataireHero.vue'
import EngagementBadge from '~/components/prestataire/EngagementBadge.vue'
import DemandeBottomSheet from '~/components/demande/DemandeBottomSheet.vue'
import { SearchMockService } from '~/services/search.mock'
import type { PrestataireDetail } from '~/types/prestataire'

// ─── Routing ──────────────────────────────────────────────────────────────────
const route = useRoute()
const slug = route.params.slug as string

// ─── Data ─────────────────────────────────────────────────────────────────────
const prestataire = ref<PrestataireDetail | null>(null)
const loading = ref(true)

onMounted(async () => {
  prestataire.value = (await SearchMockService.getBySlug(slug)) ?? null
  loading.value = false
})

// ─── SEO ──────────────────────────────────────────────────────────────────────
watchEffect(() => {
  if (!prestataire.value) return
  useSeoMeta({
    title: `${prestataire.value.name} — Sgilt`,
    description: prestataire.value.baseline,
    ogImage: prestataire.value.heroImage,
  })
})

// ─── Computed ─────────────────────────────────────────────────────────────────
const hasInfosPratiques = computed(
  () =>
    (prestataire.value?.logistics?.length ?? 0) > 0 ||
    (prestataire.value?.technical?.length ?? 0) > 0 ||
    (prestataire.value?.faq?.length ?? 0) > 0,
)

// ─── Galerie ──────────────────────────────────────────────────────────────────
const showGallery = ref(false)
const galleryIndex = ref(0)

const galleryPhotos = computed<string[]>(() => {
  if (!prestataire.value) return []
  return [prestataire.value.heroImage, ...prestataire.value.photos]
})

function openGallery(index: number) {
  galleryIndex.value = index
  showGallery.value = true
}

function closeGallery() {
  showGallery.value = false
}

function prevPhoto() {
  galleryIndex.value =
    (galleryIndex.value - 1 + galleryPhotos.value.length) % galleryPhotos.value.length
}

function nextPhoto() {
  galleryIndex.value = (galleryIndex.value + 1) % galleryPhotos.value.length
}

// ─── Disponibilités ───────────────────────────────────────────────────────────
// const selectedDate = ref<Date | undefined>(undefined)

const { dateModel } = useSearchUi()

const unavailableDatesAsDate = computed<Date[]>(() =>
  (prestataire.value?.unavailableDates ?? []).map((d) => new Date(d)),
)

const isUnavailable = computed(() => {
  if (!dateModel.value || !prestataire.value) return false
  const iso = dateModel.value.toISOString().slice(0, 10)
  return prestataire.value.unavailableDates.includes(iso)
})

const availabilityIcon = computed(() => (isUnavailable.value ? '✗' : '✓'))
const availabilityLabel = computed(() =>
  isUnavailable.value ? 'Non disponible à cette date' : 'Disponible à cette date',
)
const availabilityClass = computed(() =>
  isUnavailable.value ? 'availability-badge--unavailable' : 'availability-badge--available',
)

// ─── Vidéo ────────────────────────────────────────────────────────────────────
const showVideo = ref(false)

function openVideo() {
  showVideo.value = true
}

function closeVideo() {
  showVideo.value = false
}

// ─── Contact ──────────────────────────────────────────────────────────────────
const showDemande = ref(false)
const { isDesktop } = useDevice()

function openContactModal() {
  if (isDesktop.value) {
    navigateTo(`/${slug}/demande`)
  } else {
    showDemande.value = true
  }
}

// ─── Keyboard escape ──────────────────────────────────────────────────────────
onMounted(() => {
  window.addEventListener('keydown', onKeydown)
})
onUnmounted(() => {
  window.removeEventListener('keydown', onKeydown)
})
function onKeydown(e: KeyboardEvent) {
  if (e.key === 'Escape') {
    closeGallery()
    closeVideo()
    showDemande.value = false
  }
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// ─── Tokens locaux ────────────────────────────────────────────────────────────
$content-max-width: 640px;
$section-gap: 2.5rem;
$desktop: 900px;

// ─── Utilitaires responsive ───────────────────────────────────────────────────
.mobile-only {
  @media (min-width: $desktop) {
    display: none !important;
  }
}

// ─── Page ─────────────────────────────────────────────────────────────────────
.provider-page {
  display: flex;
  flex-direction: column;
  min-height: 100dvh;
  padding-bottom: 6rem; // espace sticky CTA mobile

  @media (min-width: $desktop) {
    padding-bottom: 0;
  }
}

// ─── Layout deux colonnes ─────────────────────────────────────────────────────
.page-layout {
  display: flex;
  flex-direction: column;

  @media (min-width: $desktop) {
    display: grid;
    grid-template-columns: 1fr 360px;
    grid-template-areas: 'main sidebar';
    max-width: 1280px;
    margin: 0 auto;
    gap: 3rem;
    padding: 2.5rem 2.5rem;
    align-items: start;
    width: 100%;
  }
}

.page-layout__main {
  @media (min-width: $desktop) {
    grid-area: main;
  }
}

.page-layout__sidebar {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  padding: $spacing-m;

  @media (min-width: $desktop) {
    grid-area: sidebar;
    position: sticky;
    top: 5rem;
    padding: 1.5rem;
    background: #fff;
    border: 1px solid rgba(0, 0, 0, 0.08);
    border-radius: $radius-md;
    box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  }
}

.sidebar-block {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
}

.sidebar-budget {
  display: none;

  @media (min-width: $desktop) {
    display: unset;
    padding-top: 1.25rem;
    border-top: 1px solid rgba(0, 0, 0, 0.08);

    &__title {
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.1rem;
      font-weight: 600;
      color: $color-primary;
      margin: 0 0 0.5rem;
    }

    &__text {
      font-size: 0.9rem;
      color: $text-secondary;
      line-height: 1.6;
      margin: 0;
    }
  }
}

.sidebar-cta {
  display: none;

  @media (min-width: $desktop) {
    display: block;
    width: 100%;
    height: 3rem;
  }
}

// ─── Sticky CTA (mobile) ──────────────────────────────────────────────────────
.sticky-cta {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 20;
  padding: $spacing-s $spacing-m;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-top: 1px solid rgba(0, 0, 0, 0.08);

  @media (min-width: $desktop) {
    display: none;
  }

  &__button {
    height: 3rem;
    width: 100%;
  }
}

// ─── Disponibilité ────────────────────────────────────────────────────────────
.availability-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.35rem 0.8rem;
  border-radius: 2rem;
  font-size: 0.82rem;
  font-weight: 600;
  width: fit-content;

  &--available {
    background: rgba(#2d9e6b, 0.1);
    color: #1e7a51;
  }

  &--unavailable {
    background: rgba(#c0392b, 0.1);
    color: #a93226;
  }

  &__icon {
    font-size: 0.75rem;
    font-weight: 700;
  }
}

// ─── Contenu ──────────────────────────────────────────────────────────────────
.provider-content {
  display: flex;
  flex-direction: column;
  gap: $section-gap;
  padding: $spacing-l $spacing-m;
  max-width: $content-max-width;
  margin: 0 auto;
  width: 100%;

  @media (min-width: $desktop) {
    max-width: none;
    padding: 0;
  }
}

.section {
  display: flex;
  flex-direction: column;
  gap: 1rem;

  &__title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.4rem;
    font-weight: 600;
    color: $color-primary;
    margin: 0;
    padding-bottom: 0.5rem;
    border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  }
}

// ─── Badges ───────────────────────────────────────────────────────────────────
.badges-section {
  position: relative;
}

.badges {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 1rem;
  > * {
    width: 6rem;
  }
}

// ─── Offerings ────────────────────────────────────────────────────────────────
.offerings {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

$color-success: #2e7d32;

.offering-item {
  display: flex;
  align-items: flex-start;
  gap: 0.5rem;
  font-size: 0.95rem;
  line-height: 1.35;
  color: $text-secondary;

  &::before {
    content: '';
    flex-shrink: 0;
    width: 16px;
    height: 16px;
    margin-top: 0.2rem;
    background-color: $color-success;
    -webkit-mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='black' stroke-width='3' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='20 6 9 17 4 12'%3E%3C/polyline%3E%3C/svg%3E");
    mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='black' stroke-width='3' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='20 6 9 17 4 12'%3E%3C/polyline%3E%3C/svg%3E");
    mask-repeat: no-repeat;
    mask-size: contain;
  }

  &__bullet {
    display: none;
  }
}

// ─── Identity ─────────────────────────────────────────────────────────────────
.identity-spotlight {
  padding: 1.5rem 0;
  border-top: 1px solid #eee;

  &__content {
    border-left: 4px solid $color-accent;
    padding-left: 1.2rem;
  }

  &__quote {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.2rem;
    font-style: italic;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 1rem;
    line-height: 1.4;
  }

  &__bio {
    font-size: 1rem;
    line-height: 1.6;
    color: $text-secondary;
    margin: 0;
  }
}

// ─── Budget (mobile) ──────────────────────────────────────────────────────────
.budget-text {
  font-size: 0.95rem;
  color: $text-secondary;
  line-height: 1.6;
  margin: 0;
}

// ─── Témoignages ──────────────────────────────────────────────────────────────
.testimonials {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.testimonial {
  margin: 0;
  padding: $spacing-m;
  background: #fafafa;
  border-radius: $radius-md;
  border: 1px solid rgba(0, 0, 0, 0.06);

  &__text {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.05rem;
    font-style: italic;
    line-height: 1.6;
    color: $color-primary;
    margin: 0 0 0.75rem;
  }

  &__footer {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  &__author {
    font-size: 0.85rem;
    font-weight: 600;
    color: $text-secondary;
  }

  &__event {
    font-size: 0.8rem;
    color: $text-secondary;
    opacity: 0.6;

    &::before {
      content: '· ';
    }
  }
}

// ─── Infos pratiques ──────────────────────────────────────────────────────────
.infos-section {
  gap: 1.5rem;
}

.infos-block {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;

  &__title {
    font-size: 0.8rem;
    font-weight: 700;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    color: $text-secondary;
    opacity: 0.6;
    margin: 0;
  }
}

.infos-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.4rem;

  li {
    font-size: 0.9rem;
    line-height: 1.5;
    color: $text-secondary;
    padding-left: 1rem;
    position: relative;

    &::before {
      content: '–';
      position: absolute;
      left: 0;
      opacity: 0.4;
    }
  }
}

.faq {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.faq-item {
  &__question {
    font-size: 0.9rem;
    font-weight: 600;
    color: $color-primary;
    margin: 0 0 0.3rem;
  }

  &__answer {
    font-size: 0.9rem;
    line-height: 1.6;
    color: $text-secondary;
    margin: 0;
  }
}

// ─── Modales ──────────────────────────────────────────────────────────────────
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.96);
  display: flex;
  align-items: center;
  justify-content: center;

  &--dark {
    background: rgba(0, 0, 0, 0.92);
  }
}

.modal-close {
  position: fixed;
  top: $spacing-m;
  right: $spacing-m;
  z-index: 101;
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 50%;
  border: 1px solid rgba(0, 0, 0, 0.15);
  background: #fff;
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: background 150ms ease;

  &:hover {
    background: #f5f5f5;
  }
}

.gallery {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;

  &__image {
    max-width: 100%;
    max-height: 85vh;
    object-fit: contain;
    border-radius: $radius-md;
    box-shadow: 0 8px 40px rgba(0, 0, 0, 0.15);
  }

  &__nav {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    width: 3rem;
    height: 3rem;
    border-radius: 50%;
    border: 1px solid rgba(0, 0, 0, 0.12);
    background: #fff;
    font-size: 1.4rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: background 150ms ease;

    &:hover {
      background: #f5f5f5;
    }

    &--prev {
      left: $spacing-m;
    }

    &--next {
      right: $spacing-m;
    }
  }

  &__counter {
    position: absolute;
    bottom: $spacing-m;
    left: 50%;
    transform: translateX(-50%);
    font-size: 0.8rem;
    color: $text-secondary;
    background: rgba(255, 255, 255, 0.85);
    padding: 0.2rem 0.6rem;
    border-radius: 1rem;
  }
}

.video-wrapper {
  width: min(90vw, 900px);
  aspect-ratio: 16 / 9;
}

.video-frame {
  width: 100%;
  height: 100%;
  border: none;
  border-radius: $radius-md;
}

// ─── 404 ──────────────────────────────────────────────────────────────────────
.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
  gap: 1rem;
  font-size: 1rem;
  color: $text-secondary;

  a {
    color: $color-primary;
    text-decoration: none;
    font-weight: 500;
  }
}

// ─── Transitions ──────────────────────────────────────────────────────────────
.fade-enter-active,
.fade-leave-active {
  transition:
    opacity 200ms ease,
    transform 200ms ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 250ms ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
