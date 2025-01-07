<template>
  <div class="partner-view" v-if="partner">
    <aside class="partner-left">
      <div class="partner-image">
        <img :src="partner.imageUrl" />
      </div>
      <h1 class="partner-title">{{ partner.title }}</h1>
      <div class="actions">
        <SgiltSimpleButton>Contacter</SgiltSimpleButton>
      </div>
      <div class="calendar">
        <SgiltCalendar @select-date="book" />
      </div>
      <div>
        <h3>Tarifs</h3>
        <p>1000€</p>
      </div>
    </aside>
    <main class="partner-main">
      <p>PRESENTATION LONGUE</p>
      <div>VIDEO</div>
      <div>GALERIE PHOTOS</div>
    </main>
    <aside class="partner-right">
      <p>⭐ 4.8/5 - 22 avis</p>
      <ul>
        <li>“Une prestation exceptionnelle, tout le monde était ravi !”</li>
        <li>“Très professionnel et ponctuel. À recommander !”</li>
      </ul>
      <SgiltSimpleButton>Voir tous les avis</SgiltSimpleButton>
      <p>réseaux sociaux</p>
    </aside>
  </div>
</template>

<script setup lang="ts">
import SgiltSimpleButton from '@/components/basics/buttons/SgiltSimpleButton.vue'
import SgiltCalendar from '@/components/basics/inputs/SgiltCalendar.vue'
import { PartnerService } from '@/services/PartnerService'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const partner = ref()

onMounted(async () => {
  const partnerSlug = Array.isArray(route.params.id) ? route.params.id[0] : route.params.id
  PartnerService.getPartnerBySlug(partnerSlug)
    .then((p) => {
      partner.value = p
    })
    .catch(() => {
      router.push('/404')
    })
})

const book = () => {
  console.log('Booking')
}
</script>

<style lang="scss" scoped>
.partner-view {
  flex: 1;

  display: flex;
  flex-direction: row;

  .partner-image {
    display: flex;
    margin-bottom: 0;
    img {
      width: 100%;
    }
  }

  .partner-title {
    margin: 0;
  }

  .calendar {
    margin-top: $spacing-m;
    .dp__flex_display {
      display: block;
    }
  }

  .partner-left {
    border-right: $border-width-s solid $color-accent;
    flex: 0 0 20rem;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    padding: $spacing-m;

    @include respond-to(mobile) {
      flex: 1;
      overflow: initial;
    }
  }

  .partner-right {
    border-left: $border-width-s solid $color-accent;
    display: flex;
    flex-direction: column;
    flex: 0 0 15rem;
  }

  .partner-main {
    flex: 1;
    display: flex;
    flex-direction: column;
    background-color: $color-secondary;
    padding: $spacing-m;
    overflow: auto;
  }
}
</style>
