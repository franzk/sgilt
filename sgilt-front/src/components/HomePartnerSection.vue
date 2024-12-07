<template>
  <section>
    <h2>Découvrez les partenaires de votre évènement</h2>

    <div class="categories">
      <PartnerCategoryButton category="Musique" icon="" />
      <PartnerCategoryButton category="Restauration" icon="" />
      <PartnerCategoryButton category="Lieux" icon="" />
      <PartnerCategoryButton category="Photographie" icon="" />
    </div>
    <h3>Ils pourraient vous plaire...</h3>
    <div class="highligthed-partners">
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
  </section>
</template>

<script setup lang="ts">
import PartnerCategoryButton from '@/components/PartnerCategoryButton.vue'
import PartnerThumbnail from '@/components/PartnerThumbnail.vue'
</script>

<style lang="scss" scoped>
.categories {
  display: flex;
  flex-direction: row;
  gap: 1rem;
  padding: 1rem;

  .partner-category-button {
    flex-basis: 25%;
  }
}
.highligthed-partners {
  display: flex;
  flex-direction: row;
  gap: 1rem;
  padding: 1rem;
  margin-left: 1rem;
  margin-right: 1rem;
  .partner-thumbnail {
    flex-basis: 33%;
  }
}
</style>
