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
    <section class="partner-grid">
      <div class="partner-video">
        <SgiltVideo youtubeId="_A6w3ECkN4k" />
      </div>
      <div class="partner-calendar">
        <SgiltCalendar />
      </div>

      <div class="partner-description">
        <h2>À propos de {{ partner.title }}</h2>
        <p>{{ partner.longDescription }}</p>
      </div>
      <div class="partner-tarifs">
        <h2>Tarifs</h2>
        <!--ul class="tarifs">
            <li v-for="tarif in tarifs" :key="tarif.nom">
              <h3>{{ tarif.nom }}</h3>
              <p>{{ tarif.description }}</p>
              <span class="prix">{{ tarif.prix }} €</span>
            </li>
          </!--ul-->
      </div>
    </section>

    <!-- Galerie photos -->
    <section class="galerie-section">
      <h2>Galerie Photos</h2>
      <!--div class="galerie">
        <img v-for="photo in photos" :src="photo" :alt="`Photo de ${nompartner}`" />
      </!--div-->
    </section>

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
.partner-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: $spacing-m;
  margin-top: $spacing-m;
  justify-items: center;
}

.partner-video,
.partner-calendar {
  align-content: center;
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
