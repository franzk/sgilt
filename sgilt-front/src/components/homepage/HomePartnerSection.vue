<template>
  <section class="partner-section-body">
    <h2>La sélection du moment</h2>

    <div class="highligthed-partners-list">
      <PartnerThumbnail
        imageUrl="https://picsum.photos/300/250"
        title="Partenaire 1"
        category="Musique"
        icon="music"
        :entryPrice="250"
      />
      <PartnerThumbnail
        imageUrl="https://picsum.photos/300/250"
        title="Partenaire 2"
        category="Restauration"
        icon="food"
        :entryPrice="500"
      />
      <PartnerThumbnail
        imageUrl="https://picsum.photos/300/250"
        title="Partenaire 3"
        category="Lieu"
        icon="place"
        :entryPrice="600"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import PartnerThumbnail from '@/components/partner/PartnerThumbnail.vue'
</script>

<style lang="scss" scoped>
.partner-section-body {
  display: flex;
  flex-direction: column;
  background-color: $color-secondary;
  color: $color-primary;
  align-items: center;
  h2 {
    margin: $spacing-l 0 $spacing-l 0;
  }
}

.highligthed-partners-list {
  flex: 1;
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-evenly;
  .partner-thumbnail {
    flex-basis: 25%;
  }
}
</style>
