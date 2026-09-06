<template>
  <header class="app-header" :class="{ 'no-shadow': hideShadow }">
    <div class="left-cluster">
      <h1 class="logo" tabindex="0">
        <NuxtLink :to="logoLink">
          <img src="/sgilt-logo.svg" alt="SGILT" />
        </NuxtLink>
      </h1>

      <nav v-if="isPublicRoute" class="public-nav" aria-label="Navigation">
        <NuxtLink to="/search">{{ $t('landing-page.footer.link-providers') }}</NuxtLink>
        <NuxtLink to="/m/comment-ca-marche">{{ $t('landing-page.footer.link-pro') }}</NuxtLink>
      </nav>
    </div>

    <div class="right-cluster">
      <NuxtLink v-if="isPublicRoute" to="/fete" class="create-event-button">
        {{ $t('landing-page.cta') }}
      </NuxtLink>

      <div class="quick-actions">
        <NotificationBell v-if="mounted && isAuthenticated" />

        <button
          ref="accountMenuAnchorRef"
          class="action-button"
          type="button"
          aria-label="Menu compte"
          @click="accountMenuOpen = !accountMenuOpen"
        >
          <UserAvatar v-if="mounted && isAuthenticated" :size="2.25" />
          <UserIcon v-else />
        </button>

        <AccountMenuPopin
          :open="accountMenuOpen"
          :anchor-el="accountMenuAnchorRef"
          @close="accountMenuOpen = false"
        />
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import AccountMenuPopin from '~/components/profile/AccountMenuPopin.vue'
import NotificationBell from '~/components/notifications/NotificationBell.vue'
import UserAvatar from '~/components/basics/UserAvatar.vue'
import { UserIcon } from '@remixicons/vue/line'
import { checkCurrentFicheIsEmpty } from '~/data/prestataire/usePrestataire'

const accountMenuAnchorRef = ref<HTMLElement | null>(null)
const accountMenuOpen = ref(false)

const { isAuthenticated, hasRole } = useKeycloak()

const ROUTES_WITHOUT_SHADOW_MOBILE = ['/fete', '/date', '/search']
const ROUTES_SHADOW_ON_SCROLL = ['/app', '/app/events', '/pro/reservations']
// Espaces authentifiés dédiés (chacun a son propre header/nav) : tout le reste
// (landing, /fete, /date, /search, /m/**, fiches prestataire) est public.
const PRIVATE_ROUTE_PREFIXES = ['/app', '/pro', '/admin', '/account', '/onboarding', '/auth']
const route = useRoute()
const { isMobile } = useDevice()

const isPublicRoute = computed(
  () => !PRIVATE_ROUTE_PREFIXES.some((prefix) => route.path.startsWith(prefix)),
)

const mounted = ref(false)
const scrolled = ref(false)

function updateScrolled() {
  scrolled.value = window.scrollY > 0
}

onMounted(() => {
  mounted.value = true
  window.addEventListener('scroll', updateScrolled, { passive: true })
  updateScrolled()
})

onUnmounted(() => {
  window.removeEventListener('scroll', updateScrolled)
})

/**
 * Fiche prestataire encore entièrement vide : ramène vers le fork d'onboarding plutôt que vers
 * le board habituel. Revérifié à chaque transition vers un compte PRO authentifié (pas de cache
 * persistant), pour rester cohérent si un autre compte se connecte dans la même session.
 */
const proFicheEmpty = ref(false)

watch(
  () => mounted.value && isAuthenticated.value && hasRole('PRO'),
  async (isProAuthenticated) => {
    proFicheEmpty.value = isProAuthenticated ? await checkCurrentFicheIsEmpty() : false
  },
  { immediate: true },
)

const logoLink = computed(() => {
  if (!mounted.value || !isAuthenticated.value) return '/'
  if (hasRole('PRO')) return proFicheEmpty.value ? '/pro/page-edition' : '/pro'
  return '/app'
})

const hideShadow = computed(() => {
  if (!mounted.value) return false
  if (isMobile.value && ROUTES_WITHOUT_SHADOW_MOBILE.includes(route.path)) return true
  if (isMobile.value && ROUTES_SHADOW_ON_SCROLL.includes(route.path)) return !scrolled.value
  return false
})
</script>

<style lang="scss" scoped>
@use '@/assets/styles/base' as *;

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: $app-header-height;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: $z-header;
  box-shadow: $box-shadow;
  background-color: #fff;
  padding: $spacing-xs $spacing-m;
  transition: box-shadow 200ms ease;

  &.no-shadow {
    box-shadow: none;
  }
}

.left-cluster {
  display: flex;
  align-items: center;
  gap: $spacing-xl;
}

.logo {
  margin: 0;
  line-height: 0;

  a {
    display: block;
  }

  img {
    height: 2rem;
    display: block;
    // Le SVG inclut une petite forme décorative au-dessus du mot "SGILT" : centrer
    // l'image entière laisse le texte visuellement plus bas que le reste du header.
    // Léger décalage vers le haut pour recaler le texte, pas l'image, sur l'axe commun.
    transform: translateY(-2px);
  }
}

.right-cluster {
  display: flex;
  align-items: center;
  gap: $spacing-l;
}

.public-nav {
  display: none;
  gap: $spacing-l;

  a {
    color: $text-secondary;
    font-size: $font-size-sm;
    font-weight: $font-weight-medium;
    text-decoration: none;

    &:hover {
      color: $text-primary;
    }
  }

  @media (min-width: $breakpoint-desktop) {
    display: flex;
  }
}

.create-event-button {
  display: none;
  border: none;
  border-radius: 9999px;
  padding: $spacing-xs $spacing-m;
  background: $brand-accent;
  color: $brand-primary;
  font-size: $font-size-sm;
  font-weight: $font-weight-bold;
  cursor: pointer;
  white-space: nowrap;

  @media (min-width: $breakpoint-desktop) {
    display: inline-flex;
    align-items: center;
  }
}

.quick-actions {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
}

.action-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.25rem;
  height: 2.25rem;
  color: $text-primary;
  background: none;
  cursor: pointer;
  text-decoration: none;
  transition: background 150ms ease;

  svg {
    width: 1.25rem;
    height: 1.25rem;
  }

  border: none;
  border-radius: 50%;
  overflow: hidden;
}
</style>
