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
            <div class="avatar-wrapper" :class="{ 'avatar-wrapper--editing': editing }" @click="editing && avatarInput?.click()">
              <UserAvatar :size="5" />
              <div v-if="editing" class="avatar-overlay" aria-hidden="true">
                <CameraIcon class="avatar-overlay-icon" />
              </div>
            </div>
            <input
              ref="avatarInput"
              type="file"
              accept="image/*"
              class="avatar-file-input"
              @change="onAvatarChange"
            />
          </div>
        </div>

        <!-- Colonne droite : champs -->
        <div class="fields-col">
          <hr class="section-sep" />

          <!-- Prénom | Nom -->
          <div class="name-row">
            <div class="name-field">
              <label class="field-label" :for="editing ? 'field-firstname' : undefined">{{ $t('profile.page.field-firstname') }}</label>
              <input
                v-if="editing"
                id="field-firstname"
                v-model="draft.firstName"
                class="field-input"
                type="text"
                autocomplete="given-name"
              />
              <span v-else class="field-value">{{ profile.firstName }}</span>
            </div>
            <div class="name-field">
              <label class="field-label" :for="editing ? 'field-lastname' : undefined">{{ $t('profile.page.field-lastname') }}</label>
              <input
                v-if="editing"
                id="field-lastname"
                v-model="draft.lastName"
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
              <label class="field-label" :for="editing ? 'field-phone' : undefined">{{ $t('profile.page.field-phone') }}</label>
              <input
                v-if="editing"
                id="field-phone"
                v-model="draft.phone"
                class="field-input"
                type="tel"
                autocomplete="tel"
              />
              <span v-else class="field-value">{{ profile.phone }}</span>
            </div>

            <div v-if="profile.role === 'prestataire'" class="field">
              <label class="field-label" :for="editing ? 'field-company' : undefined">
                {{ $t('profile.page.field-company') }}
              </label>
              <input
                v-if="editing"
                id="field-company"
                v-model="draft.companyName"
                class="field-input"
                type="text"
                autocomplete="organization"
              />
              <span v-else class="field-value">{{ profile.companyName }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Actions édition -->
      <div v-if="editing" class="edit-actions">
        <SgiltButton :loading="saving" @click="save">{{ $t('profile.page.save') }}</SgiltButton>
        <SgiltButton variant="secondary" @click="cancelEdit">{{ $t('profile.page.cancel') }}</SgiltButton>
      </div>
    </section>

    <!-- ── Bloc 2 — Connexion ─────────────────────────────────────────────────── -->
    <section class="section">
      <h2 class="section-title">{{ $t('profile.page.connection-title') }}</h2>

      <div class="connection-layout">
        <div class="fields">
          <div class="field">
            <span class="field-label">{{ $t('profile.page.field-email') }}</span>
            <span class="field-value">{{ profile.email }}</span>
          </div>

          <hr class="field-sep" />

          <div class="field">
            <span class="field-label">{{ $t('profile.page.field-password') }}</span>
            <span class="field-value field-value--password">••••••••</span>
          </div>
        </div>

        <a href="/account/security" class="keycloak-link">
          <ShieldIcon class="keycloak-icon" />
          {{ $t('profile.page.keycloak-link') }} <span class="link-arrow">→</span>
        </a>
      </div>
    </section>

    <!-- ── Suppression de compte (clients uniquement) ─────────────────────────── -->
    <div v-if="profile.role === 'client'" class="danger-zone">
      <button class="danger-zone-btn" type="button" @click="deleteOpen = true">
        {{ $t('profile.page.delete-account') }}
      </button>
    </div>

    <!-- ── Dialog confirmation suppression ───────────────────────────────────── -->
    <SgiltDialog
      v-model:open="deleteOpen"
      :title="$t('profile.page.delete-dialog.title')"
      :description="$t('profile.page.delete-dialog.description')"
      max-width="400px"
    >
      <div class="delete-dialog">
        <p class="delete-dialog-text">
          {{ $t('profile.page.delete-dialog.body') }}
        </p>
        <div class="delete-dialog-actions">
          <SgiltButton variant="secondary" @click="deleteOpen = false">
            {{ $t('profile.page.delete-dialog.keep') }}
          </SgiltButton>
          <SgiltButton :loading="deleting" class="btn-destructive" @click="deleteAccount">
            {{ $t('profile.page.delete-dialog.confirm') }}
          </SgiltButton>
        </div>
      </div>
    </SgiltDialog>
  </div>
</template>

<script setup lang="ts">
import { CameraIcon, EditIcon, ShieldIcon } from '@remixicons/vue/line'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import UserAvatar from '~/components/basics/UserAvatar.vue'

definePageMeta({ layout: 'account' })

// ── Type ──────────────────────────────────────────────────────────────────────

interface UserProfile {
  firstName: string
  lastName: string
  phone: string
  email: string
  photo?: string
  role: 'client' | 'prestataire'
  companyName?: string
}

// ── Mock ──────────────────────────────────────────────────────────────────────

const profile = ref<UserProfile>({
  firstName: 'Sophie',
  lastName: 'Lambert',
  phone: '+33 6 12 34 56 78',
  email: 'sophie.lambert@email.fr',
  role: 'client',
})

// ── Avatar ────────────────────────────────────────────────────────────────────

const avatarInput = ref<HTMLInputElement | null>(null)

function onAvatarChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  previewUrl.value = URL.createObjectURL(file)
}

// ── Édition ───────────────────────────────────────────────────────────────────

const editing = ref(false)
const saving = ref(false)

const draft = ref<UserProfile>({ ...profile.value })

function startEdit() {
  draft.value = { ...profile.value }
  editing.value = true
}

function cancelEdit() {
  editing.value = false
}

async function save() {
  saving.value = true
  await new Promise((r) => setTimeout(r, 400))
  profile.value = { ...draft.value }
  saving.value = false
  editing.value = false
}

// ── Suppression ───────────────────────────────────────────────────────────────

const deleteOpen = ref(false)
const deleting = ref(false)

async function deleteAccount() {
  deleting.value = true
  await new Promise((r) => setTimeout(r, 500))
  deleting.value = false
  deleteOpen.value = false
  navigateTo('/')
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

.field-sep {
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
  cursor: default;

  &--editing {
    cursor: pointer;
  }

  .avatar-overlay {
    position: absolute;
    inset: 0;
    border-radius: 50%;
    background: rgba(0, 0, 0, 0.4);
    display: flex;
    align-items: center;
    justify-content: center;

    .avatar-overlay-icon {
      width: 1.4rem;
      height: 1.4rem;
      color: #fff;
    }
  }
}

.avatar-file-input {
  display: none;
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
  border-radius: 9999px;
  background: $brand-accent;
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  font-weight: 600;
  color: $brand-primary;
  text-decoration: none;
  transition: opacity 150ms ease;

  &:hover {
    opacity: 0.85;
  }

  .link-arrow {
    display: none;
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

// ── Layout Bloc 2 ─────────────────────────────────────────────────────────────

.connection-layout {
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

  // Bloc 2 — deux colonnes
  .connection-layout {
    flex-direction: row;
    align-items: stretch;

    .fields {
      flex: 1;
      border-right: 1px solid $divider-color;
    }
  }

  .keycloak-link {
    flex: 0 0 auto;
    align-self: center;
    margin: $spacing-s $spacing-m;
  }
}

// ── Zone dangereuse ───────────────────────────────────────────────────────────

.danger-zone {
  display: flex;
  justify-content: center;
  padding: $spacing-s 0;

  .danger-zone-btn {
    background: none;
    border: none;
    font-family: inherit;
    font-size: 0.82rem;
    color: #dc2626;
    cursor: pointer;
    padding: 0;
    opacity: 0.7;
    transition: opacity 150ms ease;

    &:hover {
      opacity: 1;
    }
  }
}

// ── Dialog suppression ────────────────────────────────────────────────────────

.delete-dialog {
  padding: $spacing-m;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

  .delete-dialog-text {
    font-family: 'Inter', sans-serif;
    font-size: 0.88rem;
    color: $text-secondary;
    line-height: 1.55;
    margin: 0;
  }

  .delete-dialog-actions {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;

    :deep(.btn-destructive.btn) {
      background: #dc2626;
      box-shadow: none;
      border-top: none;
      text-shadow: none;

      &:hover:not(:disabled) {
        filter: brightness(0.92);
      }
    }
  }
}
</style>
