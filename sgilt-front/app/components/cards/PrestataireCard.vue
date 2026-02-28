<script setup lang="ts">
import type { PrestataireCardDetail } from '~/types/prestataire'

defineProps<{
  provider: PrestataireCardDetail
}>()
</script>

<template>
  <NuxtLink :to="`/prestataire/${provider.slug}`" class="provider-card">
    <div class="image-wrapper">
      <img :src="provider.image" :alt="provider.name" loading="lazy" />
      <div class="category-circle">
        <component :is="provider.categoryPicto" class="inner-icon" />
      </div>
    </div>

    <div class="content">
      <div class="title-row">
        <h3 class="name">{{ provider.name }}</h3>
      </div>
      <p class="description">{{ provider.shortDescription }}</p>
    </div>
  </NuxtLink>
</template>

<style scoped lang="scss">
.provider-card {
  display: flex;
  flex-direction: column;
  text-decoration: none;
  color: inherit;
  background: #ffffff;
  border-radius: 28px;

  padding: 6px; // à voir

  border: 1px solid #f1f5f9;
  height: 100%;
  transition: all 0.5s cubic-bezier(0.165, 0.84, 0.44, 1);
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.02),
    0 2px 4px -1px rgba(0, 0, 0, 0.01);

  &:hover {
    transform: translateY(-6px);
    box-shadow:
      0 20px 25px -5px rgba(0, 0, 0, 0.05),
      0 10px 10px -5px rgba(0, 0, 0, 0.02);
    border-color: #e2e8f0;
  }

  .image-wrapper {
    position: relative;
    width: 100%;
    aspect-ratio: 1.1 / 1; // Un peu plus haut que le 16/9 pour plus d'impact
    overflow: hidden;
    // On laisse un petit espace entre l'image et le bord de la carte pour un look "objet"
    margin: 0; //6px;
    // width: calc(100% - 12px);
    border-radius: 20px;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
      transition: transform 0.6s ease;
    }

    .category-circle {
      position: absolute;
      top: 10px;
      right: 10px;
      width: 30px;
      height: 30px;
      // background: rgba(255, 255, 255, 0.7); // Fond blanc translucide
      // backdrop-filter: blur(8px); // Effet de flou "iOS style"
      background: #ffffff;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      // box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
      // border: 1px solid rgba(255, 255, 255, 0.3);
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.12);
      border: 1px solid rgba(0, 0, 0, 0.05);

      .inner-icon {
        width: 16px;
        height: 16px;
        color: #1e293b; // Couleur sombre pour le contraste
      }
    }
  }

  &:hover .image-wrapper img {
    transform: scale(1.08);
  }

  .content {
    padding: 12px 6px 8px; // 0.4rem 1rem 0.6rem; // Plus d'espace en bas pour asseoir la carte
    display: flex;
    flex-direction: column;
    gap: 0.2rem;

    .title-row {
      display: flex;
      justify-content: space-between;
      align-items: baseline;

      .name {
        font-size: 1.1rem;
        letter-spacing: -0.01em;
        font-weight: 700;
        color: #1e293b;
        margin: 0;
        margin-bottom: 4px;
      }

      .rating {
        font-size: 0.8rem;
        font-weight: 600;
      }
    }

    .description {
      font-size: 0.85rem;
      color: #64748b;
      line-height: 1.4;
      // On limite à 2 lignes proprement
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .footer {
      margin-top: 6px;
      display: flex;
      justify-content: space-between;
      font-size: 0.75rem;
      font-weight: 500;

      .location {
        color: #94a3b8;
      }
      .price {
        color: #1e293b;
        font-weight: 700;
      }
    }
  }
}
</style>
