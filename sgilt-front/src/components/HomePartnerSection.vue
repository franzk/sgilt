<template>
  <section class="partner-section">
    <h2>Découvrez les partenaires de votre évènement</h2>

    <div class="partner-section-body">
      <div class="categories">
        <PartnerCategoryButton category="Musique" icon="" />
        <PartnerCategoryButton category="Restauration" icon="" />
        <PartnerCategoryButton category="Lieux" icon="" />
        <PartnerCategoryButton category="Photographie" icon="" />
      </div>

      <div class="highligthed-partners">
        <h3>Ils pourraient vous plaire...</h3>
        <div class="highligthed-partners-list">
          <PartnerThumbnail
            imageUrl="https://picsum.photos/300/125"
            titre="Partenaire 1"
            description="Lorem ipsum ..."
            entryPrice="250"
          />
          <PartnerThumbnail
            imageUrl="https://picsum.photos/300/125"
            titre="Partenaire 2"
            description="Lorem ipsum ..."
            entryPrice="500"
          />
          <PartnerThumbnail
            imageUrl="https://picsum.photos/300/125"
            titre="Partenaire 3"
            description="Lorem ipsum ..."
            entryPrice="600"
          />
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import PartnerCategoryButton from '@/components/PartnerCategoryButton.vue'
import PartnerThumbnail from '@/components/PartnerThumbnail.vue'
</script>

<style lang="scss" scoped>
.partner-section-body {
  display: flex;
  flex-direction: row;
}

.partner-section {
  background-color: $color-accent;
  color: $color-secondary;
  padding: 1rem;
}

.categories {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 1rem;
  padding: 1rem;

  .partner-category-button {
    //flex-basis: 25%;
    flex: 0 0 40%; /* Chaque cellule occupe 50% de la largeur parent */
    box-sizing: border-box;
  }
}

.highligthed-partners-list {
  display: flex;
  flex-direction: row;
  //gap: 1rem;
  //padding: 1rem;
  //margin-left: 1rem;
  //margin-right: 1rem;
  justify-content: space-evenly;
  .partner-thumbnail {
    flex-basis: 20%;
  }
}
</style>
