<!--
  Conditions générales de vente de la billetterie (/m/cgv) : vente de places par ALGM pour les
  événements publics (/e/[slug]). Liée depuis la case à cocher de l'écran de paiement
  (TicketingCgvCheckbox). Même mise en page que /m/cgu (styles recopiés).
-->
<template>
  <div class="cgv">
    <section class="hero">
      <div class="wrap">
        <p class="eyebrow">Conditions générales de vente</p>
        <h1>Billetterie.</h1>
        <p class="lede">
          Les conditions qui s'appliquent à l'achat de places pour un événement sur Sgilt&nbsp;: prix,
          paiement, e-billet, annulation et remboursement.
        </p>
        <div class="meta">
          <span><strong>Dernière mise à jour&nbsp;:</strong> novembre 2026</span>
        </div>
      </div>
    </section>

    <div class="wrap layout">
      <nav class="toc" aria-label="Sommaire">
        <p class="toc-label">Sommaire</p>
        <ol>
          <li><a href="#vendeur">Vendeur et champ d'application</a></li>
          <li><a href="#prix">Prix</a></li>
          <li><a href="#commande">Commande</a></li>
          <li><a href="#paiement">Paiement</a></li>
          <li><a href="#e-billet">E-billet</a></li>
          <li><a href="#retractation">Absence de droit de rétractation</a></li>
          <li><a href="#annulation">Annulation ou report de l'événement</a></li>
          <li><a href="#acces">Accès à l'événement</a></li>
          <li><a href="#donnees">Données personnelles</a></li>
          <li><a href="#reclamations">Réclamations et médiation</a></li>
          <li><a href="#droit">Droit applicable</a></li>
        </ol>
      </nav>

      <article>
        <section id="vendeur" class="block">
          <h2><span class="num">01</span> Vendeur et champ d'application</h2>
          <p>
            Les présentes conditions générales de vente (CGV) s'appliquent à tout achat de places
            réalisé sur Sgilt pour un événement public. Le vendeur est
            <strong>ALGM</strong>, SAS au capital de 1&nbsp;500&nbsp;€, siège social 38 rue du
            Taennelkreuz, 67750 Scherwiller, RCS Colmar, SIREN 953 057 395, TVA intracommunautaire
            FR03 953 057 395, organisateur de l'événement.
          </p>
          <div class="highlight">
            <p class="h-label">Le principe à retenir</p>
            <p>
              <strong>Cocher la case d'acceptation au moment du paiement vaut acceptation des
              présentes CGV</strong>, dans leur version en ligne au jour de l'achat.
            </p>
          </div>
        </section>

        <section id="prix" class="block">
          <h2><span class="num">02</span> Prix</h2>
          <p>
            Le prix d'une place est indiqué en euros, toutes taxes comprises, sur la page de
            l'événement. Le montant total de la commande est affiché avant le paiement. Aucuns frais
            supplémentaires ne sont facturés.
          </p>
        </section>

        <section id="commande" class="block">
          <h2><span class="num">03</span> Commande</h2>
          <ul>
            <li>Une commande compte <strong>10 places au plus</strong>.</li>
            <li>
              L'acheteur renseigne ses nom, prénom et adresse e-mail. Il est responsable de leur
              exactitude&nbsp;: l'e-billet est envoyé à cette adresse.
            </li>
            <li>
              Les places sont réservées pendant <strong>10 minutes</strong> le temps du paiement.
              Passé ce délai sans paiement, elles sont remises en vente.
            </li>
            <li>
              La vente est <strong>ferme et définitive dès la confirmation du paiement</strong>.
            </li>
          </ul>
        </section>

        <section id="paiement" class="block">
          <h2><span class="num">04</span> Paiement</h2>
          <p>
            Le paiement s'effectue en ligne, par carte bancaire, Apple Pay ou Google Pay, via le
            prestataire de paiement sécurisé <strong>Stripe</strong>. Les données de carte sont
            transmises directement à Stripe&nbsp;: ni ALGM ni Sgilt n'y ont accès. Le paiement peut
            nécessiter une authentification forte (3D Secure) auprès de la banque de l'acheteur.
          </p>
        </section>

        <section id="e-billet" class="block">
          <h2><span class="num">05</span> E-billet</h2>
          <ul>
            <li>
              Une fois le paiement confirmé, l'acheteur reçoit par e-mail un lien vers son
              <strong>e-billet</strong>&nbsp;: un QR code unique, valable pour toutes les places de
              la commande.
            </li>
            <li>
              Le QR code est présenté à l'entrée, sur téléphone ou imprimé. Il permet
              <strong>une seule entrée</strong> par place&nbsp;: en cas de copie, seule la première
              présentation est acceptée.
            </li>
            <li>
              L'acheteur est responsable de la conservation de son e-billet et du lien reçu par
              e-mail, qu'il ne doit pas diffuser.
            </li>
            <li>
              La revente de l'e-billet est interdite sans l'autorisation de l'organisateur (article
              313-6-2 du Code pénal).
            </li>
          </ul>
        </section>

        <section id="retractation" class="block">
          <h2><span class="num">06</span> Absence de droit de rétractation</h2>
          <p>
            Conformément à l'article L221-28, 12° du Code de la consommation, l'achat de places pour
            une activité de loisirs fournie à une date déterminée
            <strong>ne bénéficie pas du droit de rétractation</strong>. Une place achetée n'est ni
            reprise, ni échangée, ni remboursée, sauf annulation ou report de l'événement
            (article 7).
          </p>
        </section>

        <section id="annulation" class="block">
          <h2><span class="num">07</span> Annulation ou report de l'événement</h2>
          <ul>
            <li>
              <strong>Annulation</strong>&nbsp;: si l'événement est annulé par l'organisateur, le
              prix des places est remboursé sur le moyen de paiement utilisé, dans un délai de 30
              jours.
            </li>
            <li>
              <strong>Report</strong>&nbsp;: si l'événement est reporté, l'e-billet reste valable
              pour la nouvelle date. L'acheteur qui ne peut pas s'y rendre peut demander le
              remboursement de ses places à
              <a class="inline" href="mailto:contact@sgilt.alsace">contact@sgilt.alsace</a>, dans
              les 30 jours suivant l'annonce du report.
            </li>
            <li>
              Une modification du programme qui ne change pas la nature de l'événement (horaires,
              ordre de passage, intervenants) ne donne pas lieu à remboursement.
            </li>
          </ul>
        </section>

        <section id="acces" class="block">
          <h2><span class="num">08</span> Accès à l'événement</h2>
          <ul>
            <li>
              Le détenteur de l'e-billet s'engage à respecter le règlement du lieu et les
              consignes de l'organisateur.
            </li>
            <li>
              L'organisateur peut refuser l'accès ou exclure toute personne dont le comportement
              trouble le bon déroulement de l'événement ou la sécurité du public, sans
              remboursement.
            </li>
            <li>Les mineurs restent sous la responsabilité de leurs parents ou accompagnateurs.</li>
            <li>
              L'organisateur n'est pas responsable de la perte, du vol ou de la dégradation des
              effets personnels des participants.
            </li>
          </ul>
        </section>

        <section id="donnees" class="block">
          <h2><span class="num">09</span> Données personnelles</h2>
          <p>
            Les nom, prénom et adresse e-mail de l'acheteur servent à traiter sa commande, à lui
            envoyer son e-billet et à le prévenir en cas d'annulation ou de report. Leur traitement
            est décrit dans la
            <NuxtLink class="inline" to="/m/confidentialite">politique de confidentialité</NuxtLink>.
          </p>
        </section>

        <section id="reclamations" class="block">
          <h2><span class="num">10</span> Réclamations et médiation</h2>
          <p>
            Toute réclamation est à adresser à
            <a class="inline" href="mailto:contact@sgilt.alsace">contact@sgilt.alsace</a>, en
            indiquant l'adresse e-mail utilisée pour la commande.
          </p>
          <p>
            Conformément à l'article L612-1 du Code de la consommation, l'acheteur consommateur peut
            recourir gratuitement à un médiateur de la consommation, après une réclamation écrite
            restée sans solution. Les coordonnées du médiateur compétent lui sont communiquées sur
            simple demande.
          </p>
        </section>

        <section id="droit" class="block">
          <h2><span class="num">11</span> Droit applicable</h2>
          <p>
            Les présentes CGV sont régies par le droit français. À défaut de résolution amiable,
            l'acheteur consommateur peut saisir le tribunal de son lieu de domicile ou celui du siège
            du vendeur (Colmar).
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
useHead({ title: 'Conditions générales de vente' })
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.cgv {
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
    max-width: 16ch;
    margin: 0;
  }

  .lede {
    margin: $spacing-l 0 0;
    font-size: $font-size-lg;
    color: $text-secondary;
    max-width: 58ch;
  }

  .meta {
    margin-top: $spacing-l;
    font-size: $font-size-sm;
    color: $text-secondary;
    display: flex;
    gap: $spacing-l;
    flex-wrap: wrap;

    strong {
      color: $text-primary;
      font-weight: $font-weight-semibold;
    }
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
    flex-wrap: wrap;

    .num {
      font-size: $font-size-sm;
      font-family: $font-family-base;
      font-weight: $font-weight-semibold;
      color: $brand-accent;
      font-variant-numeric: tabular-nums;
      flex-shrink: 0;
    }

    .tag {
      font-family: $font-family-base;
      font-size: 0.62rem;
      letter-spacing: 0.12em;
      text-transform: uppercase;
      font-weight: $font-weight-semibold;
      color: $brand-accent;
      background: rgba($brand-accent, 0.08);
      border: $border-width-s solid rgba($brand-accent, 0.3);
      padding: 3px 9px;
      border-radius: $radius-xl;
      align-self: center;
      margin-left: auto;
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

  ul {
    list-style: none;
    margin: $spacing-s 0 $spacing-m;
    padding: 0;

    li {
      position: relative;
      padding-left: $spacing-l;
      margin-bottom: $spacing-xs;
      color: $text-secondary;

      &::before {
        content: '';
        position: absolute;
        left: 2px;
        top: 0.7em;
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: $brand-accent;
      }

      strong {
        color: $text-primary;
        font-weight: $font-weight-semibold;
      }
    }
  }
}

// ── Encadré clause ─────────────────────────────────────────────────────────────

.highlight {
  background: rgba($brand-accent, 0.08);
  border: $border-width-s solid rgba($brand-accent, 0.3);
  border-left: 3px solid $brand-accent;
  border-radius: $radius-md;
  padding: $spacing-l;
  margin: $spacing-xs 0 $spacing-m;

  .h-label {
    font-size: 0.7rem;
    letter-spacing: 0.16em;
    text-transform: uppercase;
    color: $state-warning;
    font-weight: $font-weight-semibold;
    margin-bottom: $spacing-s;
  }

  p {
    color: $text-primary;
    margin-bottom: 0;

    &.h-label {
      margin-bottom: $spacing-s;
    }

    strong {
      color: $text-primary;
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
