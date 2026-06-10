<template>
  <component
    :is="tag"
    class="sgilt-card"
    :class="[`format-${format}`, { clickable }]"
    :type="tag === 'button' ? 'button' : undefined"
  >
    <!-- ── Big ───────────────────────────────────────────────────────────────── -->
    <template v-if="format === 'big'">
      <div class="media">
        <img :src="image ?? ''" alt="" />
        <div class="gradient" aria-hidden="true" />
        <div class="overlay">
          <slot name="overlay" />
        </div>
      </div>
      <div v-if="$slots.footer" class="footer">
        <slot name="footer" />
      </div>
    </template>

    <!-- ── Small ─────────────────────────────────────────────────────────────── -->
    <template v-else>
      <div class="avatar">
        <slot name="avatar" />
      </div>
      <div class="content">
        <slot />
      </div>
      <div v-if="$slots.cta" class="cta">
        <slot name="cta" />
      </div>
    </template>
  </component>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    image?: string | null
    format?: 'big' | 'small'
    tag?: string
    ratio?: string
    clickable?: boolean
  }>(),
  {
    image: null,
    format: 'big',
    tag: 'div',
    ratio: '4/3',
    clickable: true,
  },
)
</script>

<style scoped lang="scss">
.sgilt-card {
  // ── Big ──────────────────────────────────────────────────────────────────────
  &.format-big {
    display: flex;
    flex-direction: column;
    border-radius: $radius-xl;
    overflow: hidden;
    padding: 0;
    box-shadow: 0 4px 6px rgba(47, 42, 37, 0.12);

    &.clickable {
      cursor: pointer;

      .media {
        transition: transform 300ms ease;
        border-radius: $radius-xl;

        img {
          transition: transform 300ms ease;
          border-radius: $radius-xl;
        }
      }

      &:hover .media img {
        transform: scale(1.03);
      }
    }

    .media {
      position: relative;
      aspect-ratio: v-bind(ratio);
      overflow: hidden;

      img {
        position: absolute;
        inset: 0;
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .gradient {
      position: absolute;
      inset: 0;
      background: linear-gradient(to bottom, transparent 35%, rgba(0, 0, 0, 0.65) 100%);
      pointer-events: none;
    }

    .overlay {
      position: absolute;
      inset: 0;
      display: flex;
      flex-direction: column;
      justify-content: flex-end;
      padding: $spacing-s;
    }

    .footer {
      background: #fff;
    }
  }

  // ── Small ─────────────────────────────────────────────────────────────────────
  &.format-small {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: $spacing-s;
    padding: $spacing-s $spacing-m;
    background: $surface-white;
    border-radius: $radius-md;
    overflow: hidden;
    box-shadow: 0 4px 6px rgba(47, 42, 37, 0.12);
    width: 100%;
    text-align: left;

    &.clickable {
      cursor: pointer;
      transition: background 120ms ease;

      &:active {
        background: $surface-soft;
      }
    }

    .avatar {
      flex-shrink: 0;
      width: 48px;
      height: 48px;
      border-radius: 50%;
      overflow: hidden;
      box-shadow: 0 4px 6px rgba(47, 42, 37, 0.12);
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .content {
      flex: 1;
      min-width: 0;
    }

    .cta {
      flex-shrink: 0;
      display: flex;
      align-items: center;
      gap: $spacing-xs;
    }
  }
}

// Reset navigateur quand rendu en <button>
button.sgilt-card {
  appearance: none;
  border: none;
  font: inherit;
}
</style>
