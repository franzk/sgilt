<template>
  <div class="partner-view" v-if="partner">
    <div class="partner-left">
      <div class="partner-identity">
        <img :src="partner.imageUrl" alt="Partner image" class="partner-image" />
        <h1 class="partner-title">{{ partner.title }}</h1>
        <SgiltSimpleButton @click="contact" class="partner-contact">Contact</SgiltSimpleButton>
      </div>
    </div>
    <div class="partner-middle">
      <SgiltVideo youtubeId="_A6w3ECkN4k" class="partner-video" />
      <div class="line-2">
        <SgiltCalendar @select-date="book" />
        <div class="partner-description">
          <p>{{ partner.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltSimpleButton from '@/components/basics/buttons/SgiltSimpleButton.vue'
import SgiltCalendar from '@/components/basics/inputs/SgiltCalendar.vue'
import SgiltVideo from '@/components/basics/media/SgiltVideo.vue'
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

const book = (date: Date) => {
  console.log('Booking', date)
}

const contact = () => {
  console.log('Contacting')
}
</script>

<style lang="scss" scoped>
.partner-view {
  display: flex;
  flex-direction: row;
  gap: $spacing-m;
  padding: $spacing-m;
}
.partner-left {
  flex: 0 0 20rem;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  //border-right: 1px solid #000;

  .partner-identity {
    display: flex;
    flex-direction: column;
    .partner-image {
      border-radius: $border-radius-m;
    }
    .partner-title {
      margin: 0;
      font-size: $font-size-h2;
    }
  }
}
.partner-middle {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  .partner-video {
    margin-bottom: $spacing-m;
    width: 100%;
    aspect-ratio: 2;
  }
  .line-2 {
    display: flex;
    flex-direction: row;
    gap: $spacing-m;
    .partner-description {
      p {
        margin: 0;
      }
    }
  }
}
</style>
