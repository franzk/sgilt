<template>
  <div class="profile-page">
    <!-- ── Bloc 1 — Informations personnelles ────────────────────────────────── -->
    <section class="section">
      <button v-if="!editing" class="section-edit" type="button" @click="startEdit">
        <span><EditIcon /></span>
      </button>

      <div class="section-header">
        <h2 class="section-title">{{ $t('profile.page.personal-info-title') }}</h2>
      </div>

      <div class="info-layout">
        <!-- Colonne gauche : avatar -->
        <div class="avatar-col">
          <div class="avatar-row">
            <div class="avatar-wrapper">
              <UserAvatar :size="5" />
            </div>
          </div>
        </div>

        <!-- Colonne droite : champs -->
        <div class="fields-col">
          <hr class="section-sep" />

          <!-- Prénom | Nom -->
          <div class="name-row">
            <div class="name-field">
              <label class="field-label" :for="editing ? 'field-firstname' : undefined">{{
                $t('profile.page.field-firstname')
              }}</label>
              <input
                v-if="editing"
                id="field-firstname"
                v-model="profile.firstName"
                class="field-input"
                type="text"
                autocomplete="given-name"
              />
              <span v-else class="field-value">{{ profile.firstName }}</span>
            </div>
            <div class="name-field">
              <label class="field-label" :for="editing ? 'field-lastname' : undefined">{{
                $t('profile.page.field-lastname')
              }}</label>
              <input
                v-if="editing"
                id="field-lastname"
                v-model="profile.lastName"
                class="field-input"
                type="text"
                autocomplete="family-name"
              />
              <span v-else class="field-value">{{ profile.lastName }}</span>
            </div>
          </div>

          <hr class="section-sep" />

          <!-- Téléphone + Entreprise -->
          <div class="fields">
            <div class="field">
              <label class="field-label" :for="editing ? 'field-phone' : undefined">{{
                $t('profile.page.field-phone')
              }}</label>
              <input
                v-if="editing"
                id="field-phone"
                v-model="profile.phone"
                class="field-input"
                type="tel"
                autocomplete="tel"
              />
              <span v-else class="field-value">{{ profile.phone }}</span>
            </div>

            <div v-if="role === 'prestataire'" class="field">
              <label class="field-label" :for="editing ? 'field-company' : undefined">
                {{ $t('profile.page.field-company') }}
              </label>
              <input
                v-if="editing"
                id="field-company"
                v-model="companyName"
                class="field-input"
                type="text"
                autocomplete="organization"
              />
              <span v-else class="field-value">{{ companyName }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Actions édition -->
      <div v-if="editing" class="edit-actions">
        <SgiltButton :loading="saving" @click="onSave">{{ $t('profile.page.save') }}</SgiltButton>
        <SgiltButton variant="secondary" @click="cancelEdit">{{
          $t('profile.page.cancel')
        }}</SgiltButton>
      </div>
    </section>

    <!-- ── Bloc 2 — Connexion ─────────────────────────────────────────────────── -->
    <section class="section">
      <button type="button" class="keycloak-link" @click="changePassword">
        <ShieldIcon class="keycloak-icon" />
        {{ $t('profile.page.keycloak-link') }}
      </button>
    </section>

  </div>
</template>

<script setup lang="ts">
import { EditIcon, ShieldIcon } from '@remixicons/vue/line'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import UserAvatar from '~/components/basics/UserAvatar.vue'
import { useUtilisateur } from '~/data/utilisateur/useUtilisateur'

definePageMeta({ layout: 'account' })

// ── Profil ────────────────────────────────────────────────────────────────────
// Rôle/entreprise : pas encore backés par l'API, restent en état local mock.

const role = ref<'client' | 'prestataire'>('client')
const companyName = ref<string | undefined>(undefined)

const { profile, editing, saving, saveError, startEdit, cancelEdit, save } = useUtilisateur()

async function onSave() {
  const firstName = profile.firstName
  const lastName = profile.lastName
  await save()
  if (saveError.value) return
  patchCurrentUser({ firstName, lastName })
}

// ── Connexion ─────────────────────────────────────────────────────────────────

const { login } = useKeycloak()

function changePassword() {
  login({
    action: 'UPDATE_PASSWORD',
    redirectUri: window.location.origin + '/account/profile',
  })
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$section-radius: $radius-lg;
$desktop: $breakpoint-desktop;

.profile-page {
  max-width: 560px;
  margin: 0 auto;
  padding: $spacing-l $spacing-m $spacing-xxxl;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}

// ── Section ───────────────────────────────────────────────────────────────────

.section {
  position: relative;
  background: #fff;
  border: 1px solid $divider-color;
  border-radius: $section-radius;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);

  .section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .section-title {
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    font-weight: 700;
    letter-spacing: 0.06em;
    text-transform: uppercase;
    color: $text-secondary;
    margin: 0;
    padding: $spacing-m $spacing-m $spacing-s;
  }

  .section-edit {
    position: absolute;
    bottom: $spacing-m;
    right: $spacing-m;
    width: 16px;
    height: 16px;
    background: none;
    border: none;
    font-family: inherit;
    font-size: 0.82rem;
    font-weight: 500;
    color: $brand-accent;
    cursor: pointer;
    padding: 0;
    transition: opacity 150ms ease;

    &:hover {
      opacity: 0.7;
    }
  }
}

// ── Séparateurs internes ──────────────────────────────────────────────────────

.section-sep {
  border: none;
  border-top: 1px solid $divider-color;
  margin: 0;
  flex-shrink: 0;
}

// ── Avatar ────────────────────────────────────────────────────────────────────

.avatar-row {
  display: flex;
  justify-content: center;
  padding: $spacing-s $spacing-m $spacing-m;
}

.avatar-wrapper {
  position: relative;
  border-radius: 50%;
}

// ── Ligne Prénom | Nom ────────────────────────────────────────────────────────

.name-row {
  display: flex;
}

.name-field {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 3px;
  padding: $spacing-s $spacing-m;

  &:first-child {
    border-right: 1px solid $divider-color;
  }
}

// ── Champs ────────────────────────────────────────────────────────────────────

.fields {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
  padding: $spacing-s $spacing-m $spacing-m;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.field-label {
  font-family: 'Inter', sans-serif;
  font-size: 0.75rem;
  color: $text-secondary;
}

.field-value {
  font-family: 'Inter', sans-serif;
  font-size: 0.9rem;
  color: $text-primary;
  font-weight: 500;

  &--password {
    letter-spacing: 0.12em;
  }
}

.field-input {
  width: 100%;
  box-sizing: border-box;
  font-family: inherit;
  font-size: 0.9rem;
  color: $text-primary;
  background: $surface-soft;
  border: 1px solid $divider-color;
  border-radius: $radius-sm;
  padding: $spacing-xs $spacing-s;
  outline: none;
  transition: border-color 150ms ease;

  &:focus {
    border-color: $brand-accent;
  }
}

// ── Actions édition ───────────────────────────────────────────────────────────

.edit-actions {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-s;
  padding: 0 $spacing-m $spacing-m;
}

// ── Lien Keycloak ─────────────────────────────────────────────────────────────

.keycloak-link {
  display: inline-flex;
  align-items: center;
  gap: $spacing-xs;
  align-self: flex-start;
  margin: 0 $spacing-m $spacing-m;
  padding: $spacing-xs $spacing-m;
  border: none;
  border-radius: 9999px;
  background: $brand-accent;
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  font-weight: 600;
  color: $brand-primary;
  cursor: pointer;
  text-decoration: none;
  transition: opacity 150ms ease;

  &:hover {
    opacity: 0.85;
  }
}

// ── Layout Bloc 1 ─────────────────────────────────────────────────────────────

.info-layout {
  display: flex;
  flex-direction: column;
}

.avatar-col {
  display: flex;
  justify-content: center;
}

.fields-col {
  display: flex;
  flex-direction: column;
}

.keycloak-icon {
  width: 1.1rem;
  height: 1.1rem;
  flex-shrink: 0;
}

// ── Desktop ───────────────────────────────────────────────────────────────────

@media (min-width: $desktop) {
  // Bloc 1 — deux colonnes
  .info-layout {
    flex-direction: row;
    align-items: stretch;
  }

  .avatar-col {
    flex: 0 0 160px;
    border-right: 1px solid $divider-color;
    align-items: center;
    padding: $spacing-l $spacing-m;

    :deep(.user-avatar) {
      --ua-size: 7rem;
      --ua-font-size: 2.2rem;
    }
  }

  .fields-col {
    flex: 1;
    min-width: 0;

    // Le premier hr (séparation mobile avatar/champs) est remplacé par la bordure de colonne
    & > .section-sep:first-child {
      display: none;
    }
  }

  // Prénom et Nom empilés verticalement
  .name-row {
    flex-direction: column;
  }

  .name-field:first-child {
    border-right: none;
    border-bottom: 1px solid $divider-color;
  }

  .keycloak-link {
    margin: $spacing-s $spacing-m;
  }
}
</style>
