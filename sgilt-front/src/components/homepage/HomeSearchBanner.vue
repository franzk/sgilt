<template>
  <section id="search-banner">
    <div class="title">
      <span class="title-thin">{{ $t('home.search-banner.title-part-1') }}</span>
      <span class="title-bold">{{ $t('home.search-banner.title-part-2') }}</span>
    </div>
    <div class="search-fields">
      <datepicker language="fr" class="date-event"></datepicker>
      <SgiltSelect :options="options" class="select-event" />
      <button class="submit-button">{{ $t('home.search-banner.button') }}</button>
    </div>
  </section>
</template>

<script setup lang="ts">
import datepicker from 'vuejs3-datepicker'
import SgiltSelect from '@/components/basics/SgiltSelect.vue'

const options = ['Votre évènement', 'Mariage', "Fête d'entreprise", "Fête d'anniversaire"]
</script>

<style lang="scss">
#search-banner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  gap: $spacing-m;
  padding: $spacing-l;

  background-color: $shadow;
  color: $color-secondary;
}

.title {
  font-size: $font-size-xxl;
  line-height: $line-height-xl;
  .title-thin {
    font-weight: 300;
  }
  .title-bold {
    font-weight: bold;
  }
}

.search-fields {
  display: flex;
  flex-direction: row;
  gap: $spacing-xl;

  .select-event {
    line-height: 45px;
    width: 14rem;
  }

  .date-event {
    .vuejs3-datepicker__value {
      border-color: white;
      padding: 11px 16px 14xpx;
      .vuejs3-datepicker__content {
        padding-top: 3px;
      }
    }
  }
}

.submit-button {
  background-color: $color-accent;
  color: $color-secondary;
  border: none;
  border-radius: $border-radius-s;
  padding: $spacing-s $spacing-m;
  font-size: $font-size-m;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s;

  &:hover {
    background-color: $color-accent;
  }
}
</style>
