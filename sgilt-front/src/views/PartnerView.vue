import { onMounted } from "vue"

<template>
  <div class="fiche-partner" v-if="partner">
    <!-- Header -->
    <header class="partner-header">
      <div class="header-left">
        <img :src="partner.imageUrl" alt="Photo du partner" class="photo-profil" />
        <div>
          <h1>{{ partner.title }}</h1>
          <p class="slogan">{{ partner.description }}</p>
        </div>
      </div>
    </header>

    <!-- First section -->
    <section class="partner-first-section">
      <div class="partner-video">
        <SgiltVideo youtubeId="_A6w3ECkN4k" />
      </div>

      <div class="partner-booking">
        <h3>Réserver</h3>
        <SgiltDatePicker />
        <PriceForm />
        <SgiltSimpleButton>Réserver</SgiltSimpleButton>
      </div>

      <!--div class="partner-calendar">
        <SgiltCalendar />
      </!--div>

      <div-- class="partner-prices">
        <PriceForm />
      </div-->
    </section>

    <section class="partner-second-section">
      <div class="partner-description">
        <h3>À propos de {{ partner.title }}</h3>
        <p>{{ partner.longDescription }}</p>
      </div>
      <div class="partner-gallery">
        <h3>Galerie photos</h3>
      </div>
    </section>

    <!-- Galerie photos -->
    <!--section class="galerie-section">
      <h2>Galerie Photos</h2>
      <!--div class="galerie">
        <img v-for="photo in photos" :src="photo" :alt="`Photo de ${nompartner}`" />
      </!--div>
    </!--section-->

    <!-- Avis clients -->
    <section class="avis-section">
      <h2>Ce que disent nos clients</h2>
      <div class="avis-grid">
        <!--div v-for="avis in avisClients" class="avis-client">
          <h3>{{ avis.nomClient }}</h3>
          <p>{{ avis.commentaire }}</p>
          <p class="note">Note : {{ avis.note }}/5</p>
        </!--div-->
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import SgiltSimpleButton from '@/components/basics/buttons/SgiltSimpleButton.vue'
import SgiltDatePicker from '@/components/basics/inputs/SgiltDatePicker.vue'
import SgiltVideo from '@/components/basics/media/SgiltVideo.vue'
import PriceForm from '@/components/partner/PriceForm.vue'
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
</script>

<style scoped lang="scss">
.fiche-partner {
  padding: $spacing-s;
}

/* Header */
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

  .photo-profil {
    width: 7em;
    height: 7em;
    border-radius: 50%;
  }

  .slogan {
    font-style: italic;
    color: #666;
  }
}

/* Première section */
.partner-first-section {
  display: flex;
  flex-direction: row;
  gap: $spacing-l;
  margin-top: $spacing-l;
}

/*.partner-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: $spacing-m;
  margin-top: $spacing-m;
  justify-items: center;
}*/

.partner-video {
  flex: 2;
  max-height: 100%; // Limite la hauteur à celle du conteneur parent
  display: flex;
  justify-content: center;
  align-items: center;
  max-width: 40rem;
  //width: 100%;

  /*> div {
    width: 100%;
    aspect-ratio: 16 / 9; // Maintient l'aspect ratio
    max-height: 100%; // Limite la hauteur
    overflow: hidden;

    iframe,
    video {
      width: 100%;
      height: 100%;
      object-fit: cover; // Ajuste l'intérieur sans déformer l'aspect
    }
  }*/
}

.partner-booking {
  flex: 1;
  max-width: 15rem;
  background: white;
  padding: $spacing-l;
}

.partner-calendar {
  flex: 1;
  align-content: center;
  justify-content: center;
  align-items: center;
  height: auto; // Garde la hauteur naturelle
}

.partner-prices {
  flex: 1;
}

.partner-second-section {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  margin-top: $spacing-l;
}

/* Galerie */
.galerie {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 10px;
  margin-top: 20px;
}
.galerie img {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
}

/* Avis */
.avis-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}
.avis-client {
  border: 1px solid #eee;
  padding: 15px;
  border-radius: 8px;
}
.note {
  color: #fcb900;
}
</style>
