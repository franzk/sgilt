<!--
  Onglet « Ma page » de l'espace prestataire (/pro/ma-page) : récap de la fiche publique (image
  hero, nom, baseline). Un clic sur le récap ouvre l'édition plein écran (/pro/page-edition).
-->
<template>
  <div class="ma-page">
    <template v-if="prestataire">
      <NuxtLink to="/pro/page-edition" class="recap">
        <div class="cover" :style="coverStyle">
          <div class="overlay" />
          <div class="bottom">
            <span class="name">{{ prestataire.name }}</span>
            <span v-if="prestataire.baseline" class="baseline">{{ prestataire.baseline }}</span>
          </div>
        </div>
      </NuxtLink>
      <SgiltButton class="cta" @click="navigateTo('/pro/page-edition')">
        <PencilIcon class="icon" />
        {{ $t('pro.ma-page.edit') }}
      </SgiltButton>
    </template>
    <Sk v-else class="recap-sk" />
  </div>
</template>

<script setup lang="ts">
import { PencilIcon } from '@remixicons/vue/line'
import Sk from '~/components/basics/Sk.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'

definePageMeta({ layout: 'pro' })

const currentUser = useCurrentUser()
const { prestataire, loadMaFiche } = usePrestataire()
const { toUrl } = useImageUrl()

// Première image des médias (une vidéo ne peut pas servir d'image de fond).
const coverStyle = computed(() => {
  const firstImage = prestataire.value?.medias.find((m) => m.type === 'IMAGE')
  return firstImage ? { backgroundImage: `url(${toUrl(firstImage.ref)})` } : {}
})

watch(
  () => currentUser.slug,
  (slug) => {
    if (slug) loadMaFiche()
  },
  { immediate: true },
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.ma-page {
  min-height: 100%;
  padding: $spacing-m;
  background-color: $brand-background-alt;

  .recap {
    display: block;
    border-radius: $radius-md;
    overflow: hidden;
    text-decoration: none;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .cover {
      position: relative;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      height: 15rem;
      padding: $spacing-s $spacing-m $spacing-m;
      background-color: $brand-subtle;
      background-size: cover;
      background-position: center;

      .overlay {
        position: absolute;
        inset: 0;
        background: linear-gradient(to bottom, rgba(47, 42, 37, 0.1), rgba(47, 42, 37, 0.65));
        pointer-events: none;
      }

      .bottom {
        position: relative;
        display: flex;
        flex-direction: column;
        gap: 0.25rem;

        .name {
          font-family: 'Cormorant Garamond', serif;
          font-size: 1.625rem;
          font-weight: 600;
          color: $surface-white;
          line-height: 1.1;
          text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
        }

        .baseline {
          font-size: 0.875rem;
          color: rgba(255, 255, 255, 0.9);
          text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
        }
      }
    }
  }

  .cta {
    width: 100%;
    margin-top: $spacing-m;

    .icon {
      width: 1rem;
      height: 1rem;
    }
  }

  .recap-sk {
    height: 15rem;
    border-radius: $radius-md;
  }
}
</style>
