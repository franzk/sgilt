<template>
  <div v-if="prestataire" class="provider-page">
    <!-- ═══════════════════════════════════════════
         1. HERO
    ════════════════════════════════════════════ -->
    <section class="hero" @touchstart.passive="onHeroTouchStart" @touchend.passive="onHeroTouchEnd">
      <!-- Slide image -->
      <div v-if="heroItems[heroIndex]?.type === 'image'" class="hero__image">
        <SgiltImage :src="heroItems[heroIndex].src" :alt="prestataire.name" loading="eager" />
      </div>

      <!-- Slide vidéo -->
      <template v-else-if="heroItems[heroIndex]?.type === 'video'">
        <div class="hero__image">
          <SgiltImage
            :src="`https://img.youtube.com/vi/${heroItems[heroIndex].youtubeId}/hqdefault.jpg`"
            alt="Vidéo"
            loading="eager"
          />
        </div>
        <button class="hero__video-play" @click="openVideo" aria-label="Lancer la vidéo">▶</button>
      </template>

      <!-- Bouton share -->
      <button class="hero__share" @click="share" aria-label="Partager">
        <IconShare />
      </button>

      <!-- Overlay gradient -->
      <div class="hero__overlay" aria-hidden="true" />

      <!-- Contenu hero -->
      <div class="hero__content">
        <p class="hero__category">{{ prestataire.category }}</p>
        <h1 class="hero__name">{{ prestataire.name }}</h1>
        <p class="hero__baseline">{{ prestataire.baseline }}</p>
      </div>

      <!-- Dots -->
      <div v-if="heroItems.length > 1" class="hero__dots" aria-hidden="true">
        <span
          v-for="(_, i) in heroItems"
          :key="i"
          class="hero__dot"
          :class="{ 'hero__dot--active': i === heroIndex }"
        />
      </div>
    </section>

    <!-- ═══════════════════════════════════════════
         DATEPICKER — juste après le hero
    ════════════════════════════════════════════ -->
    <div class="hero-datepicker">
      <SgiltDatePicker
        v-model="selectedDate"
        :booked-dates="unavailableDatesAsDate"
        placeholder="Vérifier une date"
      />
      <Transition name="fade">
        <div v-if="selectedDate" class="availability-badge" :class="availabilityClass">
          <span class="availability-badge__icon">{{ availabilityIcon }}</span>
          <span>{{ availabilityLabel }}</span>
        </div>
      </Transition>
    </div>

    <!-- ═══════════════════════════════════════════
         CONTENU PRINCIPAL
    ════════════════════════════════════════════ -->
    <div class="provider-content">
      <!-- 2. BADGES -->
      <section v-if="prestataire.badges.length > 0" class="section badges-section">
        <div class="badges">
          <button
            v-for="badge in prestataire.badges"
            :key="badge.label"
            class="badge"
            @click="toggleBadge(badge)"
            :aria-label="`En savoir plus : ${badge.label}`"
          >
            <span class="badge__icon">{{ badge.icon }}</span>
            <span class="badge__label">{{ badge.label }}</span>
          </button>
        </div>

        <!-- Popover badge -->
        <Transition name="fade">
          <div v-if="activeBadge" class="badge-popover" role="tooltip">
            <p>{{ activeBadge.description }}</p>
            <NuxtLink to="/notre-demarche" class="badge-popover__link"> Notre démarche → </NuxtLink>
            <button class="badge-popover__close" @click="activeBadge = null" aria-label="Fermer">
              ✕
            </button>
          </div>
        </Transition>
      </section>

      <!-- 3. CE QUE NOUS PROPOSONS -->
      <section v-if="prestataire.offerings.length > 0" class="section">
        <h2 class="section__title">Ce que nous proposons</h2>
        <ul class="offerings">
          <li v-for="item in prestataire.offerings" :key="item" class="offering-item">
            <span class="offering-item__bullet" aria-hidden="true" />
            {{ item }}
          </li>
        </ul>
      </section>

      <!-- 4. TOUCHE IDENTITAIRE -->
      <section v-if="prestataire.identity" class="section identity-section">
        <p class="identity-text">{{ prestataire.identity }}</p>
      </section>

      <!-- 5. BUDGET -->
      <section v-if="prestataire.budget" class="section budget-section">
        <h2 class="section__title">Tarifs</h2>
        <p class="budget-text">{{ prestataire.budget }}</p>
      </section>

      <!-- 6. TÉMOIGNAGES -->
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

      <!-- 8. INFORMATIONS PRATIQUES -->
      <section v-if="hasInfosPratiques" class="section infos-section">
        <h2 class="section__title">Informations pratiques</h2>

        <div v-if="prestataire.logistics && prestataire.logistics.length > 0" class="infos-block">
          <h3 class="infos-block__title">Logistique</h3>
          <ul class="infos-list">
            <li v-for="item in prestataire.logistics" :key="item">{{ item }}</li>
          </ul>
        </div>

        <div v-if="prestataire.technical && prestataire.technical.length > 0" class="infos-block">
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
    <!-- /provider-content -->

    <!-- ═══════════════════════════════════════════
         STICKY CTA
    ════════════════════════════════════════════ -->
    <div class="sticky-cta">
      <SgiltButton class="sticky-cta__button" @click="openContactModal">
        Envoyer une demande
      </SgiltButton>
    </div>

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
import SgiltImage from '~/components/basics/media/SgiltImage.vue'
import IconShare from '~/components/icons/IconShare.vue'
import { SearchMockService } from '~/services/search.mock'
import type { PrestataireDetail, Badge } from '~/types/prestataire'

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

// ─── Hero carousel ────────────────────────────────────────────────────────────
type HeroItem = { type: 'image'; src: string } | { type: 'video'; youtubeId: string }

const heroItems = computed<HeroItem[]>(() => {
  if (!prestataire.value) return []
  const images: HeroItem[] = [prestataire.value.heroImage, ...prestataire.value.photos].map(
    (src) => ({ type: 'image', src }),
  )
  if (prestataire.value.youtubeId) {
    return [...images, { type: 'video', youtubeId: prestataire.value.youtubeId }]
  }
  return images
})

const heroIndex = ref(0)
const touchStartX = ref(0)

function onHeroTouchStart(e: TouchEvent) {
  touchStartX.value = e.touches[0].clientX
}

function onHeroTouchEnd(e: TouchEvent) {
  const diff = touchStartX.value - e.changedTouches[0].clientX
  if (Math.abs(diff) < 30) return
  if (diff > 0) heroIndex.value = (heroIndex.value + 1) % heroItems.value.length
  else heroIndex.value = (heroIndex.value - 1 + heroItems.value.length) % heroItems.value.length
}

// ─── Disponibilités ───────────────────────────────────────────────────────────
const selectedDate = ref<Date | undefined>(undefined)

const unavailableDatesAsDate = computed<Date[]>(() =>
  (prestataire.value?.unavailableDates ?? []).map((d) => new Date(d)),
)

const isUnavailable = computed(() => {
  if (!selectedDate.value || !prestataire.value) return false
  const iso = selectedDate.value.toISOString().slice(0, 10)
  return prestataire.value.unavailableDates.includes(iso)
})

const availabilityIcon = computed(() => (isUnavailable.value ? '✗' : '✓'))
const availabilityLabel = computed(() =>
  isUnavailable.value ? 'Non disponible à cette date' : 'Disponible à cette date',
)
const availabilityClass = computed(() =>
  isUnavailable.value ? 'availability-badge--unavailable' : 'availability-badge--available',
)

// ─── Badges ───────────────────────────────────────────────────────────────────
const activeBadge = ref<Badge | null>(null)

function toggleBadge(badge: Badge) {
  activeBadge.value = activeBadge.value?.label === badge.label ? null : badge
}

// ─── Vidéo ────────────────────────────────────────────────────────────────────
const showVideo = ref(false)

function openVideo() {
  showVideo.value = true
}

function closeVideo() {
  showVideo.value = false
}

// ─── Share ────────────────────────────────────────────────────────────────────
async function share() {
  if (navigator.share) {
    await navigator.share({
      title: prestataire.value?.name,
      text: prestataire.value?.baseline,
      url: window.location.href,
    })
  } else {
    await navigator.clipboard.writeText(window.location.href)
  }
}

// ─── Contact ──────────────────────────────────────────────────────────────────
function openContactModal() {
  // TODO: ouvrir la modale de contact / messagerie
  console.log('Ouverture de la modale de contact')
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
    closeVideo()
    activeBadge.value = null
  }
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// ─── Tokens locaux ────────────────────────────────────────────────────────────
$content-max-width: 640px;
$section-gap: 2.5rem;

// ─── Page ─────────────────────────────────────────────────────────────────────
.provider-page {
  display: flex;
  flex-direction: column;
  min-height: 100dvh;
  padding-bottom: 6rem; // espace pour le sticky CTA
}

// ─── Hero ─────────────────────────────────────────────────────────────────────
.hero {
  position: relative;
  width: 100%;
  aspect-ratio: 1 / 1;
  overflow: hidden;

  &__image {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    object-position: center;
  }

  &__overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(
      to bottom,
      transparent 30%,
      rgba(0, 0, 0, 0.25) 60%,
      rgba(0, 0, 0, 0.72) 100%
    );
  }

  &__content {
    position: absolute;
    bottom: 1.5rem;
    left: 0;
    right: 0;
    padding: 0 $spacing-m;
    color: #fff;
    text-shadow: 0 2px 12px rgba(0, 0, 0, 0.4);
  }

  &__category {
    font-size: 0.8rem;
    font-weight: 600;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    opacity: 0.85;
    margin: 0 0 0.4rem;
    color: $color-accent;
  }

  &__name {
    font-family: 'Cormorant Garamond', serif;
    font-size: clamp(2rem, 6vw, 3.2rem);
    font-weight: 700;
    line-height: 1.1;
    margin: 0 0 0.5rem;
  }

  &__baseline {
    font-size: 1rem;
    font-weight: 400;
    opacity: 0.9;
    margin: 0;
    max-width: 36rem;
  }

  // Bouton share
  &__share {
    position: absolute;
    top: $spacing-m;
    right: $spacing-m;
    z-index: 2;
    width: 2.2rem;
    height: 2.2rem;
    border-radius: 50%;
    border: 1px solid rgba(255, 255, 255, 0.4);
    background: rgba(0, 0, 0, 0.25);
    backdrop-filter: blur(6px);
    color: #fff;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;

    svg {
      width: 1rem;
      height: 1rem;
    }
  }

  // Bouton play vidéo
  &__video-play {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 2;
    width: 3.5rem;
    height: 3.5rem;
    border-radius: 50%;
    border: 2px solid rgba(255, 255, 255, 0.8);
    background: rgba(0, 0, 0, 0.45);
    color: #fff;
    font-size: 1.2rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(4px);
    transition: background 150ms ease;

    &:hover {
      background: rgba(0, 0, 0, 0.65);
    }
  }

  // Dots de navigation
  &__dots {
    position: absolute;
    bottom: $spacing-s;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    gap: 0.35rem;
  }

  &__dot {
    width: 5px;
    height: 5px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.5);
    transition:
      background 200ms ease,
      transform 200ms ease;

    &--active {
      background: #fff;
      transform: scale(1.3);
    }
  }
}

// ─── Hero datepicker ──────────────────────────────────────────────────────────
.hero-datepicker {
  padding: $spacing-m $spacing-m $spacing-s;
  max-width: $content-max-width;
  margin: 0 auto;
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
}

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
  flex-wrap: wrap;
  gap: 0.6rem;
}

.badge {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  padding: 0.28rem 0.65rem;
  border-radius: 2rem;
  border: 1px solid rgba(0, 0, 0, 0.1);
  background: #fafafa;
  cursor: pointer;
  font-size: 0.78rem;
  font-weight: 500;
  color: $text-secondary;
  transition:
    border-color 150ms ease,
    background 150ms ease;

  &:hover {
    border-color: $color-accent;
    background: rgba($color-accent, 0.06);
  }

  &__icon {
    font-size: 0.82rem;
  }
}

.badge-popover {
  position: relative;
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: $radius-md;
  padding: $spacing-m;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  font-size: 0.9rem;
  line-height: 1.5;
  color: $text-secondary;

  &__link {
    display: inline-block;
    margin-top: 0.5rem;
    font-size: 0.8rem;
    color: $color-primary;
    text-decoration: none;
    opacity: 0.7;

    &:hover {
      opacity: 1;
    }
  }

  &__close {
    position: absolute;
    top: 0.5rem;
    right: 0.5rem;
    background: none;
    border: none;
    cursor: pointer;
    font-size: 0.85rem;
    color: $text-secondary;
    opacity: 0.5;
    padding: 0.2rem 0.4rem;

    &:hover {
      opacity: 1;
    }
  }
}

// ─── Offerings ────────────────────────────────────────────────────────────────
.offerings {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
}

.offering-item {
  display: flex;
  align-items: flex-start;
  gap: 0.6rem;
  font-size: 0.95rem;
  line-height: 1.5;
  color: $text-secondary;

  &__bullet {
    flex-shrink: 0;
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: $color-accent;
    margin-top: 0.5rem;
  }
}

// ─── Identity ─────────────────────────────────────────────────────────────────
.identity-section {
  padding: $spacing-m;
  background: rgba($color-accent, 0.06);
  border-left: 3px solid $color-accent;
  border-radius: 0 $radius-md $radius-md 0;
}

.identity-text {
  font-family: 'Cormorant Garamond', serif;
  font-size: 1.1rem;
  font-style: italic;
  line-height: 1.65;
  color: $color-primary;
  margin: 0;
}

// ─── Budget ───────────────────────────────────────────────────────────────────
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

// ─── Sticky CTA ───────────────────────────────────────────────────────────────
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

  &__button {
    height: 3rem;
    width: 100%;
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
