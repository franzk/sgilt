<script setup lang="ts">
import type { PrestataireCardDetail } from '~/types/prestataire'
import SgiltImage from '~/components/basics/media/SgiltImage.vue'
import { NuxtLink } from '#components'
import CategoryIcon from '~/components/basics/icons/CategoryIcon.vue'

const props = defineProps<{
  provider?: PrestataireCardDetail
  loading?: boolean
  selectable?: boolean
}>()
const emit = defineEmits<{ select: [provider: PrestataireCardDetail] }>()

const imageLoaded = ref(false)

function handleClick() {
  if (props.selectable && props.provider) emit('select', props.provider)
}
</script>

<template>
  <component
    :is="props.selectable || loading ? 'div' : NuxtLink"
    :to="!props.selectable && !loading ? `/${provider?.slug}` : undefined"
    class="provider-card"
    :class="[{ 'is-loading shimmer-container': loading }, { selectable: props.selectable }]"
    @click="handleClick"
  >
    <div class="image-wrapper">
      <template v-if="!loading">
        <!--img :src="provider?.image" :alt="provider?.name" loading="lazy" /-->
        <SgiltImage :src="provider?.image" :alt="provider?.name" width="400" height="360" />
        <div class="category-tag">
          <span><CategoryIcon :categoryId="provider?.categoryId" class="inner-icon" /></span>
          <span class="category-name">{{ provider?.categoryName }}</span>
        </div>
      </template>

      <template v-else>
        <div class="skeleton-image"></div>
        <div class="skeleton-icon category-placeholder"></div>
      </template>
    </div>

    <div class="content">
      <div class="title-row">
        <h3 v-if="!loading" class="name">{{ provider?.name }}</h3>
        <div v-else class="skeleton-text title"></div>
      </div>

      <div v-if="!loading" class="description">
        {{ provider?.shortDescription }}
      </div>
      <div v-else class="description-group">
        <div class="skeleton-text"></div>
        <div class="skeleton-text short"></div>
      </div>
    </div>
  </component>
</template>

<style scoped lang="scss">
.provider-card {
  display: flex;
  flex-direction: column;
  text-decoration: none;
  color: inherit;
  background: #ffffff;
  border-radius: 12px;
  gap: 0.5rem;

  padding: 6px;

  border: 1px solid #f1f5f9;
  height: 100%;
  transition: all 0.5s cubic-bezier(0.165, 0.84, 0.44, 1);
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.02),
    0 2px 4px -1px rgba(0, 0, 0, 0.01);

  &.selectable {
    cursor: pointer;
  }

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
    aspect-ratio: 1.1 / 1;
    overflow: hidden;
    margin: 0;
    border-radius: 12px;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
      transition: transform 0.6s ease;
    }

    .category-tag {
      position: absolute;
      top: 12px;
      right: 12px;
      height: 30px;

      display: flex;
      gap: 6px;

      background: rgba(0, 0, 0, 0.2);
      backdrop-filter: blur(4px); // Effet de flou glassmorphism

      border-radius: 5rem;
      border: 1px solid rgba(255, 255, 255, 0.2);

      padding: 0px 6px;

      align-items: center;
      justify-content: center;
      color: #fff;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.12);

      .inner-icon {
        width: 14px;
        height: 14px;
      }

      .category-name {
        font-size: 0.65rem;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.5px;
        @media (max-width: 380px) {
          display: none; // On ne garde que l'icône sur petit mobile
        }
      }
    }
  }

  &:hover .image-wrapper img {
    transform: scale(1.08);
  }

  .content {
    display: flex;
    flex-direction: column;
    gap: 2px;

    .title-row {
      display: flex;
      justify-content: space-between;
      align-items: baseline;

      .name {
        font-size: 14px;
        letter-spacing: -0.01em;
        font-weight: 600;
        color: #000000;
        margin: 0;
      }

      .rating {
        font-size: 0.8rem;
        font-weight: 600;
      }
    }

    .description {
      font-size: 13px;
      color: #717171;
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

  &.is-loading {
    cursor: default;
    border-color: transparent;
    box-shadow: none;

    .category-placeholder {
      position: absolute;
      top: 12px;
      right: 12px;
    }

    .description-group {
      display: flex;
      flex-direction: column;
      gap: 8px;
      margin-top: 4px;
    }
  }
}
</style>
