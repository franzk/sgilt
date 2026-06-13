<template>
  <div class="mentions-legales">
    <section class="hero">
      <div class="wrap">
        <p class="eyebrow">Mentions légales</p>
        <h1>Qui édite Sgilt.</h1>
        <p class="lede">
          Les informations légales relatives à l'éditeur du site, à son hébergement et au traitement
          de vos données.
        </p>
      </div>
    </section>

    <div class="wrap layout">
      <nav class="toc" aria-label="Sommaire">
        <p class="toc-label">Sommaire</p>
        <ol>
          <li><a href="#editeur">Éditeur du site</a></li>
          <li><a href="#hebergement">Hébergement</a></li>
          <li><a href="#contact">Contact</a></li>
          <li><a href="#donnees">Données personnelles</a></li>
        </ol>
      </nav>

      <article>
        <section id="editeur" class="block">
          <h2><span class="num">01</span> Éditeur du site</h2>
          <p>Le site Sgilt est édité et exploité par&nbsp;:</p>
          <div class="id-card">
            <dl>
              <dt>Dénomination</dt>
              <dd>ALGM</dd>
              <dt>Forme juridique</dt>
              <dd>Société par actions simplifiée (SAS)</dd>
              <dt>Capital social</dt>
              <dd>1 500 €</dd>
              <dt>Siège social</dt>
              <dd>38 rue du Taennelkreuz, 67750 Scherwiller</dd>
              <dt>SIREN</dt>
              <dd>953 057 395</dd>
              <dt>RCS</dt>
              <dd>Colmar — n° de gestion 2023B00502</dd>
              <dt>N° TVA intracom.</dt>
              <dd class="pending">à compléter</dd>
              <dt>Directeur de la publication</dt>
              <dd>Arnaud Mathis (Président)</dd>
            </dl>
          </div>
        </section>

        <section id="hebergement" class="block">
          <h2><span class="num">02</span> Hébergement</h2>
          <p>Le site est hébergé par&nbsp;:</p>
          <div class="id-card">
            <dl>
              <dt>Hébergeur</dt>
              <dd>IONOS SARL</dd>
              <dt>Adresse</dt>
              <dd>7 place de la Gare, BP 70109, 57200 Sarreguemines Cedex</dd>
              <dt>RCS</dt>
              <dd>Sarreguemines 431 303 775</dd>
              <dt>Téléphone</dt>
              <dd>0970 808 911</dd>
              <dt>Contact</dt>
              <dd>info@ionos.fr</dd>
            </dl>
          </div>
        </section>

        <section id="contact" class="block">
          <h2><span class="num">03</span> Contact</h2>
          <p>
            Pour toute question relative au site ou à son fonctionnement, vous pouvez nous écrire à
            <a class="inline" href="mailto:privacy@sgilt.alsace">privacy@sgilt.alsace</a>.
          </p>
        </section>

        <section id="donnees" class="block">
          <h2><span class="num">04</span> Données personnelles</h2>
          <p>
            Le traitement de vos données personnelles est décrit en détail dans notre
            <NuxtLink class="inline" to="/m/confidentialite">politique de confidentialité</NuxtLink
            >&nbsp;: données collectées, finalités, durées de conservation et droits dont vous
            disposez.
          </p>
          <p>
            Pour exercer vos droits ou pour toute question relative à vos données, écrivez à
            <a class="inline" href="mailto:privacy@sgilt.alsace">privacy@sgilt.alsace</a>.
          </p>
        </section>
      </article>
    </div>

    <footer class="legal-footer">
      <div class="wrap">
        <p>Édité par ALGM (SAS) · SIREN 953 057 395 · Scherwiller, Alsace</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
useHead({ title: 'Mentions légales' })
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.mentions-legales {
  padding: 0 $spacing-m;
}

.wrap {
  max-width: $container-max-width;
  margin: 0 auto;
  padding: 0 $section-padding-x;
}

// ── Hero ───────────────────────────────────────────────────────────────────────

.hero {
  border-bottom: $border-width-s solid $divider-color;
  padding: $spacing-xxxl 0 $spacing-xxl;

  .eyebrow {
    font-size: $font-size-xs;
    letter-spacing: 0.22em;
    text-transform: uppercase;
    color: $brand-accent;
    font-weight: $font-weight-semibold;
    margin-bottom: $spacing-m;
  }

  h1 {
    font-family: 'Cormorant Garamond', serif;
    font-weight: $font-weight-medium;
    font-size: clamp(2.2rem, 6vw, 3.8rem);
    line-height: 1.05;
    letter-spacing: -0.01em;
    max-width: 12ch;
    margin: 0;
  }

  .lede {
    margin: $spacing-l 0 0;
    font-size: $font-size-lg;
    color: $text-secondary;
    max-width: 56ch;
  }
}

// ── Layout : sommaire + article ───────────────────────────────────────────────

.layout {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: $spacing-xxxl;
  padding: $spacing-xxxl 0;
  align-items: start;

  @media (max-width: $breakpoint-desktop) {
    grid-template-columns: 1fr;
    gap: 0;
  }
}

// ── Sommaire ───────────────────────────────────────────────────────────────────

.toc {
  position: sticky;
  top: $spacing-l;
  font-size: $font-size-sm;

  @media (max-width: $breakpoint-desktop) {
    display: none;
  }

  .toc-label {
    font-size: 0.7rem;
    letter-spacing: 0.18em;
    text-transform: uppercase;
    color: $text-secondary;
    font-weight: $font-weight-semibold;
    margin: 0 0 $spacing-m;
  }

  ol {
    list-style: none;
    counter-reset: toc;
    margin: 0;
    padding: 0;
  }

  li {
    counter-increment: toc;
    margin-bottom: $spacing-xxs;
  }

  a {
    display: flex;
    gap: $spacing-s;
    text-decoration: none;
    color: $text-secondary;
    padding: $spacing-xs 0;
    line-height: 1.4;
    transition: color 150ms ease;

    &::before {
      content: counter(toc, decimal-leading-zero);
      color: $brand-accent;
      font-variant-numeric: tabular-nums;
      font-weight: $font-weight-semibold;
      flex-shrink: 0;
    }

    &:hover {
      color: $text-primary;
    }
  }
}

// ── Article ────────────────────────────────────────────────────────────────────

article {
  max-width: 680px;
}

.block {
  margin-bottom: $spacing-xxl;
  scroll-margin-top: $spacing-l;

  &:last-child {
    margin-bottom: 0;
  }

  h2 {
    font-family: 'Cormorant Garamond', serif;
    font-weight: $font-weight-medium;
    font-size: $font-size-2xl;
    letter-spacing: -0.01em;
    margin-bottom: $spacing-m;
    display: flex;
    align-items: baseline;
    gap: $spacing-s;

    .num {
      font-size: $font-size-sm;
      font-family: $font-family-base;
      font-weight: $font-weight-semibold;
      color: $brand-accent;
      font-variant-numeric: tabular-nums;
      flex-shrink: 0;
    }
  }

  p {
    margin-bottom: $spacing-s;
    color: $text-secondary;

    strong {
      color: $text-primary;
      font-weight: $font-weight-semibold;
    }
  }
}

// ── Carte d'identité ───────────────────────────────────────────────────────────

.id-card {
  background: $surface-white;
  border: $border-width-s solid $divider-color;
  border-radius: $radius-lg;
  padding: $spacing-l;
  margin: $spacing-xxs 0 $spacing-xs;

  dl {
    display: grid;
    grid-template-columns: auto 1fr;
    gap: $spacing-xs $spacing-l;
    font-size: $font-size-md;
    margin: 0;

    @media (max-width: $breakpoint-desktop) {
      grid-template-columns: 1fr;
      gap: $spacing-xxs 0;
    }
  }

  dt {
    color: $text-secondary;
    margin: 0;
  }

  dd {
    color: $text-primary;
    font-weight: $font-weight-medium;
    margin: 0 0 $spacing-xs;

    @media (min-width: $breakpoint-desktop) {
      margin-bottom: 0;
    }

    &.pending {
      color: $text-secondary;
      font-weight: $font-weight-regular;
      font-style: italic;
    }
  }
}

// ── Liens inline ───────────────────────────────────────────────────────────────

a.inline {
  color: $text-primary;
  text-decoration: underline;
  text-decoration-color: $brand-accent;
  text-underline-offset: 3px;

  &:hover {
    color: $brand-accent;
  }
}

// ── Pied de page ───────────────────────────────────────────────────────────────

.legal-footer {
  border-top: $border-width-s solid $divider-color;
  background: $surface-white;
  padding: $spacing-xl 0;

  p {
    margin: 0;
    font-size: $font-size-sm;
    color: $text-secondary;
  }
}
</style>
