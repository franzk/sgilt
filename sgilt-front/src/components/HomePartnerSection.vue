<template>
  <section class="partner-section">
    <div class="partner-section-header">
      <h2 class="discover-title">Découvrez les partenaires de votre évènement</h2>
      <div class="categories">
        <PartnerCategoryButton category="Musique" icon="music" />
        <PartnerCategoryButton category="Restauration" icon="food" />
        <PartnerCategoryButton category="Lieux" icon="place" />
        <PartnerCategoryButton category="Photographie" icon="photo" />
      </div>
    </div>
    <div class="partner-section-body">
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
    </div>
  </section>
</template>

<script setup lang="ts">
import PartnerCategoryButton from '@/components/PartnerCategoryButton.vue'
import PartnerThumbnail from '@/components/PartnerThumbnail.vue'
</script>

<style lang="scss" scoped>
.partner-section {
  display: flex;
  flex-direction: column;
  background-color: $color-accent;
  color: $color-secondary;
}

.partner-section-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $spacing-l 0 $spacing-xl 0;
}

.discover-title {
  margin: 0 0 $spacing-m 0;
  line-height: $line-height-l;
}

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

.categories {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: $spacing-m;
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
