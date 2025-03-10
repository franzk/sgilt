<template>
  <div class="split-screen-layout" v-if="partner">
    <!-- left content : texts & actions -->
    <div class="left-content">
      <!-- Header -->
      <PartnerHeader />

      <!-- Description -->
      <p class="partner-description">{{ partner.longDescription }}</p>

      <!-- Video & photo gallery place here in tablet & mobile view -->
      <PartnerMedia :photos="photos" v-if="isTabletView" />

      <!-- Reservation form -->
      <ReservationForm />
    </div>

    <!-- right content : video & photo here on desktop view -->
    <div class="right-content" v-if="!isTabletView">
      <PartnerMedia :photos="photos" />
    </div>
  </div>
</template>

<script setup lang="ts">
import ReservationForm from '@/components/reservation_form/ReservationForm.vue'
import PartnerMedia from '@/components/partner/PartnerMedia.vue'
import PartnerHeader from '@/components/partner/PartnerHeader.vue'
import { usePartnerStore } from '@/stores/partner.store'
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { useResponsiveView } from '@/composable/useResponsiveView'

const { isResponsiveView } = useResponsiveView('--breakpoint-tablet')
const isTabletView = isResponsiveView

const route = useRoute()
const router = useRouter()
const partnerStore = usePartnerStore()
const partner = computed(() => partnerStore.partner)

// load partner data
onMounted(async () => {
  const partnerSlug = Array.isArray(route.params.id) ? route.params.id[0] : route.params.id
  loadPartner(partnerSlug)
})

watch(
  () => route.params.id,
  (newId) => {
    const partnerSlug = Array.isArray(newId) ? newId[0] : newId
    loadPartner(partnerSlug)
  },
)

const loadPartner = async (partnerSlug: string) => {
  partnerStore.fetchPartner(partnerSlug).catch((error) => {
    if (error.message === '404') {
      router.push('/404')
    } else {
      console.error(error)
    }
  })
}

// reservation date
const selectedDate = ref<Date>()

const reservationDate = route.query?.date ? dayjs(route.query.date as string).toDate() : undefined
if (reservationDate) {
  selectedDate.value = reservationDate
}

// mock data
const photos = ref([
  'https://picsum.photos/520/400',
  'https://picsum.photos/520/401',
  'https://picsum.photos/520/402',
  'https://picsum.photos/520/403',
])
</script>

<style scoped lang="scss">
$left-column-width: 40rem;

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
}

/*.partner-header {
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
    margin-bottom: $spacing-s;
    padding-bottom: $spacing-s;
  }

  .slogan {
    font-style: italic;
    color: $color-subtext;
  }
} */

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
    // let's move to a 1 column layout on tablet & mobile
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
      width: 70%;
      max-width: 30rem;
    }
  }
}

@include respond-to(tablet) {
  .split-screen-layout .reservation-form {
    width: 80%;
  }
}
</style>
