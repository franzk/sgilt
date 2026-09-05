<template>
  <div class="home-page">
    <HomeHeroSection />
    <HomeIdeaSection />
    <HomeStartSection />
    <HomeProvidersSection />
    <HomeLocalSection />
    <HomeOccasionsSection />
    <HomeClosingSection />
    <HomeFooter />
  </div>
</template>

<script setup lang="ts">
import HomeHeroSection from '~/components/landing/home/HomeHeroSection.vue'
import HomeIdeaSection from '~/components/landing/home/HomeIdeaSection.vue'
import HomeStartSection from '~/components/landing/home/HomeStartSection.vue'
import HomeProvidersSection from '~/components/landing/home/HomeProvidersSection.vue'
import HomeLocalSection from '~/components/landing/home/HomeLocalSection.vue'
import HomeOccasionsSection from '~/components/landing/home/HomeOccasionsSection.vue'
import HomeClosingSection from '~/components/landing/home/HomeClosingSection.vue'
import HomeFooter from '~/components/landing/home/HomeFooter.vue'

useHead({ titleTemplate: '%s' })

useSeoMeta({
  title: 'Sgilt - Organisez votre événement en Alsace',

  description:
    "Trouvez les bons prestataires en Alsace pour votre mariage, anniversaire ou événement d'entreprise.",

  ogTitle: 'Organisez votre événement en Alsace avec Sgilt',

  ogDescription:
    'Trouvez les bons prestataires, échangez avec eux et suivez vos réservations au même endroit.',

  ogImage: 'https://sgilt.alsace/images/sgilt-social.png',

  twitterCard: 'summary_large_image',
  twitterTitle: 'Organisez votre événement en Alsace avec Sgilt',
  twitterDescription:
    'Trouvez les bons prestataires et suivez vos réservations au même endroit.',
  twitterImage: 'https://sgilt.alsace/images/sgilt-social.png',
})
</script>

<style scoped lang="scss">
.home-page {
  width: 100%;
}
</style>
