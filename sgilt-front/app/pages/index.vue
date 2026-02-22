<template>
  <div class="home">
    <section class="search-form">
      <!-- title -->
      <div class="hero">
        <p class="title">
          <span class="title-thin">{{ $t('home.search-banner.title-part-1') }}</span>
          <span class="title-bold">{{ $t('home.search-banner.title-part-2') }}</span>
        </p>
        <h3 class="tagline">
          <span class="tagline-mobile">{{ $t('home.search-banner.tagline-mobile') }}</span>
          <span class="tagline-desktop">{{ $t('home.search-banner.tagline-desktop') }}</span>
        </h3>
      </div>

      <!-- composants -->
      <div class="inputs">
        <SgiltDatePicker placeholder="Votre date" />

        <SgiltSelect :options="selectOptions" :model-value="selectedOption">
          <template v-slot:left-icon> <IconRocket /> </template>
        </SgiltSelect>
        <SgiltButton class="submit_button">C'est parti !</SgiltButton>
      </div>
    </section>
    <section class="photo-layer" aria-hidden="true"></section>
    <div class="app-background"></div>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import SgiltSelect from '~/components/basics/inputs/SgiltSelect.vue'
import IconRocket from '~/components/icons/IconRocket.vue'
import { useDevice } from '~/composables/useDevice'

useSeoMeta({
  title: '',
  description: '',
})

useHead({
  title: 'Sgilt (Beta)',
})

const { isMobile } = useDevice()

const selectOptions = [
  { value: '-1', label: 'Votre événement' },
  { value: '0', label: 'Mariage' },
  { value: '1', label: 'Anniversaire' },
  { value: '2', label: "Fête d'entreprise" },
  { value: '3', label: 'Autre' },
]

const selectedOption = ref(selectOptions[0]!.value)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.home {
  position: relative;
  width: 100%;
  min-height: calc(100dvh - $app-header-height); // pour compenser la hauteur du header fixe
  overflow: hidden;
  background: #fff;
  padding-top: 0.75rem;

  @media (max-width: 849px) {
    :global(.app-header) {
      box-shadow: none !important;
    }
  }

  // 1 grille = 2 zones
  display: grid;
  grid-template-rows: 50% 50%;
  grid-template-columns: 1fr;
  grid-template-areas:
    'content'
    'visual';

  .search-form {
    z-index: 2;
    grid-area: content;
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: $spacing-l $spacing-m 0;
    justify-content: center;
    gap: clamp(1rem, 3vh, 3rem);

    .hero {
      text-align: center;
      color: $color-primary;

      display: flex;
      flex-direction: column;
      align-items: center;
    }

    .title {
      display: flex;
      flex-direction: column;
    }

    .title-thin {
      font-weight: 600;
      font-size: 2.5rem;
      line-height: 2.75rem;
      font-family: 'Cormorant Garamond', serif;
    }

    .title-bold {
      font-weight: 900;
      font-size: 3.2rem;
      line-height: 3rem;
      letter-spacing: 0.02em;
      margin-bottom: 0.875rem;
      color: $color-accent;
    }

    .tagline {
      font-weight: 500;
      font-size: 1.125rem;
      line-height: 1.25;
      color: black; //$text-secondary;
      max-width: 36rem;
      .tagline-desktop {
        display: none;
      }
    }

    .inputs {
      margin: 0 auto;

      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 0.875rem;
      width: 90%;
      max-width: 30rem;
      font-size: 1.125rem;

      /* .submit_button {
        width: 100%;
      } */
      .sgilt-button {
        width: 100%;
      }

      > * {
        height: 3rem;
      }
    }
  }

  .photo-layer {
    grid-area: visual;
    z-index: 1;

    filter: brightness(1.03) contrast(1.03) saturate(1.06);

    background-image: url('/images/hero-party.png');
    background-size: cover;
    background-repeat: no-repeat;
    background-position: 50% 30%;

    -webkit-mask-image: linear-gradient(to bottom, transparent 0%, #000 18%, #000 100%);
    mask-image: linear-gradient(to bottom, transparent 0%, #000 28%, #000 100%);
    -webkit-mask-repeat: no-repeat;
    mask-repeat: no-repeat;
    -webkit-mask-size: 100% 100%;
    mask-size: 100% 100%;

    pointer-events: none;
  }

  .app-background {
    position: absolute;
    z-index: 0;
    inset: 0;

    mask-image: none !important;
    -webkit-mask-image: none !important;

    //background-image: url('/textures/paper-grain.png');
    //background-repeat: repeat;
    //background-size: 700px;

    pointer-events: none;

    /* le masque: blanc en haut, papier visible plus bas */
    /*-webkit-mask-image: linear-gradient(
      to bottom,
      transparent 0%,
      transparent 18%,
      // 👈 plus longtemps blanc pur
      rgba(0, 0, 0, 0.25) 28%,
      rgba(0, 0, 0, 0.6) 38%,
      rgba(0, 0, 0, 1) 52%
    );

    mask-image: linear-gradient(
      to bottom,
      transparent 0%,
      transparent 18%,
      rgba(0, 0, 0, 0.25) 28%,
      rgba(0, 0, 0, 0.6) 38%,
      rgba(0, 0, 0, 1) 52%
    );*/
  }
}

/* =========================
   DESKTOP HERO FULLSCREEN
   ========================= */
@media (min-width: 850px) {
  .home {
    display: flex !important;
    flex-direction: column !important;
    justify-content: start !important;
    min-height: calc(100dvh - $app-header-height);
    padding-top: 0; // le hero doit coller sous le header
    color: #fff !important;
  }

  // image fullscreen
  .photo-layer {
    position: absolute;
    inset: 0;
    z-index: 0;

    background-image: url('/images/hero-party-desktop.png') !important; // ou hero-party si tu gardes un seul asset
    background-size: cover;
    background-repeat: no-repeat;
    // background-position: 50% 60%;

    // on enlève le masque mobile
    -webkit-mask-image: none !important;
    mask-image: none !important;

    filter: brightness(1) contrast(1.03) saturate(1.06);
    pointer-events: none;

    // voile de lisibilité (pas un panneau)
    &::after {
      content: '';
      position: absolute;
      inset: 0;
      pointer-events: none;

      // haut un peu plus clair pour rappeler ton papier,
      // milieu légèrement assombri pour porter le texte,
      // bas quasi neutre (on laisse l'image vivre)
      /* background: linear-gradient(
        to bottom,
        rgba(255, 255, 255, 0.55) 0%,
        rgba(255, 255, 255, 0.18) 20%,
        rgba(0, 0, 0, 0.18) 45%,
        rgba(0, 0, 0, 0.05) 75%,
        rgba(0, 0, 0, 0) 100%
      ); */
    }
  }

  // papier: on le coupe en desktop (ou très léger)
  .app-background {
    display: none;
  }

  // zone texte + form centrée (style "hero")
  .search-form {
    position: relative;
    z-index: 2;

    /*min-*/
    height: 50vh !important; // pour éviter que le hero soit trop haut sur grand écran
    display: flex;
    flex-direction: column;
    padding-top: 6% !important;
    padding-bottom: 3% !important;
    // align-items: start !important;
    justify-content: space-between !important;

    // padding: 0% !important;

    // espace sous le header + confort visuel
    // padding: clamp(2rem, 6vh, 5rem) clamp(2rem, 6vw, 6rem);

    // tu peux régler ce "point d'accroche" vertical
    // gap: 5rem !important;

    /*&::before {
      content: '';
      position: absolute;
      inset: 0;
      margin: auto;
      width: min(1100px, 92vw);
      height: 280px;
      background: rgba(0, 0, 0, 0.1);
      filter: blur(24px);
      border-radius: 999px;
      z-index: -1;
    }*/
    border-radius: 0 0 10px 10px !important;

    // “verre” : léger, pas gris
    background: linear-gradient(
      180deg,
      rgba(255, 255, 255, 0.25) 0%,
      rgba(255, 255, 255, 0.1) 100%
    );

    // blur (à doser)
    backdrop-filter: blur(3px) saturate(1.15);
    -webkit-backdrop-filter: blur(3px) saturate(1.15);

    // bord + reflets
    border: 1px solid rgba(255, 255, 255, 0.42) !important;
    box-shadow:
      0 10px 40px rgba(0, 0, 0, 0.15),
      inset 0 1px 0 rgba(255, 255, 255, 0.4);

    // micro-contraste sur le fond (évite l’effet “bande”)
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      background: radial-gradient(
        900px 220px at 50% 0%,
        rgba($color-accent, 0.12),
        transparent 60%
      );
      pointer-events: none;
    }
  }

  .hero {
    // max-width: 56rem;
    // margin-top: 10% !important; // pour régler le point d'accroche vertical du hero
    transform: translateY(-0.5rem);
    text-align: center;
    width: 100% !important;
    // pas de panneau: juste du relief typographique
    color: #fff !important;
    text-shadow:
      0 2px 10px rgba(0, 0, 0, 0.35),
      0 14px 40px rgba(0, 0, 0, 0.22);
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: 0.6rem;
    width: 100% !important;
  }

  .title {
    display: flex;
    flex-direction: row !important;
    color: inherit;
    align-items: baseline;
    gap: 0.8rem !important;
    margin: 0;
    text-shadow: 0 6px 18px rgba(0, 0, 0, 0.18);
  }

  .title-thin {
    font-weight: 600;
    font-size: 3.2rem !important;
    line-height: 1.05 !important;
    color: $color-primary !important;
  }

  .title-bold {
    font-weight: 800 !important;
    font-size: 4rem !important;
    line-height: 1.05 !important;
    letter-spacing: 0.02rem !important;
    margin-top: 0.2rem !important;
    // color: white !important;

    text-shadow:
      0 0 12px rgba(255, 210, 0, 0.4),
      0 8px 24px rgba(0, 0, 0, 0.2) !important;
  }

  .tagline {
    margin: 0;
    padding: 0;
    width: 100% !important;
    max-width: 100% !important;
    font-weight: 400 !important;
    font-size: clamp(1rem, 1.2vw, 1.15rem);
    line-height: 1.3;
    opacity: 0.88 !important;
    color: rgba(0, 0, 0, 0.75) !important;
    // color: #fff !important;

    .tagline-mobile {
      display: none !important;
    }

    .tagline-desktop {
      display: block !important;
      letter-spacing: 1px;
    }
  }

  // inputs en ligne (option "recherche", pas "login")
  .inputs {
    font-size: 1rem;
    // width: min(920px, 92vw);
    display: flex !important;
    flex-direction: row !important;
    gap: 1.1rem;
    align-items: center !important;
    justify-content: center !important;
    height: unset !important;
    // transform: translateY(1rem);
    width: 100% !important;
    max-width: unset !important;
    // backdrop-filter: blur(20px) saturate(1.15) !important;
    // -webkit-backdrop-filter: blur(20px) saturate(1.15) !important;
    padding: 0.3rem !important;

    width: unset !important;

    border-radius: 2rem !important;

    background: radial-gradient(
      1200px 200px at 50% -20%,
      rgba(255, 190, 60, 0.18),
      transparent 60%
    );
    border: 1px solid rgba(255, 255, 255, 0.42) !important;
    box-shadow:
      0 18px 50px rgba(0, 0, 0, 0.18),
      inset 0 1px 0 rgba(255, 255, 255, 0.55) !important;

    // un poil de relief sous les composants
    filter: drop-shadow(0 14px 40px rgba(0, 0, 0, 0.22));
    > * {
      min-width: 17rem !important;
      max-width: 17rem !important;
      font-size: 1.1rem !important;
      backdrop-filter: blur(6px);
    }
  }

  /* .sgilt-date-picker,
  .custom-select,
  .sgilt-button {
    width: 15rem !important;
    // height: 3rem !important;
    font-size: 1rem !important;
  }*/

  .sgilt-button {
    box-shadow:
      0 8px 20px rgba(0, 0, 0, 0.25),
      0 2px 6px rgba(0, 0, 0, 0.15) !important;
  }
}
</style>
