<template>
  <!-- TODO
    - styliser la vue mobile :
      - skeletons pour le chargement
    - éventuellement créer un composant header
  -->

  <div class="split-screen-layout" v-if="partner">
    <!-- left content : texts & actions -->
    <div class="left-content">
      <!-- Header -->
      <header class="partner-header">
        <div class="header-left">
          <img :src="partner.imageUrl" alt="Photo du partner" class="profile-picture" />
          <div>
            <h1>{{ partner.title }}</h1>
            <p class="slogan">{{ partner.description }}</p>
          </div>
        </div>
      </header>

      <!-- Description -->
      <p class="partner-description">{{ partner.longDescription }}</p>

      <PartnerMedia :photos="photos" v-if="tabletView" />

      <ReservationForm
        v-model:selected-date="selectedDate"
        :prices="prices"
        class="reservation-form"
      />
    </div>

    <!-- right content : media -->
    <div class="right-content" v-if="!tabletView">
      <PartnerMedia :photos="photos" />
    </div>
  </div>
</template>

<script setup lang="ts">
import ReservationForm from '@/components/partner/ReservationForm.vue'
import PartnerMedia from '@/components/partner/PartnerMedia.vue'
import type { Price } from '@/domain/Partner'
import { PartnerService } from '@/services/PartnerService'
import { tabletView } from '@/utils/StyleUtils'
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

const selectedDate = ref<Date>()

// mock data
const photos = ref([
  'https://picsum.photos/200/200',
  'https://picsum.photos/200/201',
  'https://picsum.photos/200/202',
  'https://picsum.photos/200/203',
])

const prices: Price[] = [
  { id: '1', title: 'Concert de 1h', price: 100 },
  { id: '2', title: 'Concert de 2h', price: 200 },
  { id: '3', title: 'Concert de 3h', price: 300 },
  { id: '4', title: 'Concert de 4h', price: 400 },
  { id: '5', title: 'Concert de 5h', price: 500 },
]
</script>

<style scoped lang="scss">
$left-column-width: 40rem;
$profile-picture-size: 8em;

/* Split Screen Layout */
.split-screen-layout {
  display: grid;
  grid-template-columns: $left-column-width 1fr;
  gap: $spacing-m;
  padding: $spacing-m;
  height: 100vh;
}

/* left column */
.left-content {
  display: flex;
  flex-direction: column;
  padding: $spacing-m;
  background: $color-white;
  border-radius: $border-radius-m;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);

  h1 {
    font-size: $font-size-h1;
    margin-bottom: $spacing-s;
  }
}

.partner-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid #eee;
  padding-bottom: $spacing-l;

  .header-left {
    display: flex;
    align-items: center;
    gap: $spacing-l;
  }

  .profile-picture {
    width: $profile-picture-size;
    height: $profile-picture-size;
    border-radius: 50%;
  }

  h1 {
    border-bottom: 2px solid $color-accent;
    padding-bottom: $spacing-s;
  }

  .slogan {
    font-style: italic;
    color: #666;
  }

  .contact-icon {
    margin-top: 10px;
    text-align: left;
  }
}

.partner-description {
  margin: $spacing-m 0;
  line-height: $line-height-h3;
}

/* right column */
.right-content {
  display: flex;
  flex-direction: column;
}

/* Responsive */
@include respond-to(tablet) {
  .split-screen-layout {
    display: flex;
    flex-direction: column;
    padding: 0;

    .left-content {
      padding: 0;
      .partner-header,
      .partner-description {
        padding: $spacing-m;
      }
    }
    .reservation-form {
      width: 80%;
    }
  }
}
</style>
