<template>
  <div class="profile-page">
    <!-- ── Bloc 1 — Informations personnelles ────────────────────────────────── -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">Informations personnelles</h2>
        <button v-if="!editing" class="section-edit" type="button" @click="startEdit">
          Modifier
        </button>
      </div>

      <!-- Avatar -->
      <div class="avatar-row">
        <div class="avatar" @click="editing && avatarInput?.click()">
          <img v-if="previewUrl" :src="previewUrl" alt="" class="avatar-photo" />
          <span v-else class="avatar-initials">{{ initials }}</span>
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

      <!-- Champs -->
      <div class="fields">
        <div class="field">
          <label class="field-label" :for="editing ? 'field-firstname' : undefined">Prénom</label>
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

        <div class="field">
          <label class="field-label" :for="editing ? 'field-lastname' : undefined">Nom</label>
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

        <div class="field">
          <label class="field-label" :for="editing ? 'field-phone' : undefined">Téléphone</label>
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
            Entreprise
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

      <!-- Actions édition -->
      <div v-if="editing" class="edit-actions">
        <SgiltButton :loading="saving" @click="save">Sauvegarder</SgiltButton>
        <SgiltButton variant="secondary" @click="cancelEdit">Annuler</SgiltButton>
      </div>
    </section>

    <!-- ── Bloc 2 — Connexion ─────────────────────────────────────────────────── -->
    <section class="section">
      <h2 class="section-title">Connexion</h2>

      <div class="fields">
        <div class="field">
          <span class="field-label">Email</span>
          <span class="field-value">{{ profile.email }}</span>
        </div>

        <div class="field">
          <span class="field-label">Mot de passe</span>
          <span class="field-value field-value--password">••••••••</span>
        </div>
      </div>

      <a href="/account/security" class="keycloak-link">
        Modifier via mon espace sécurisé →
      </a>
    </section>

    <!-- ── Suppression de compte (clients uniquement) ─────────────────────────── -->
    <div v-if="profile.role === 'client'" class="danger-zone">
      <button class="danger-zone-btn" type="button" @click="deleteOpen = true">
        Supprimer mon compte
      </button>
    </div>

    <!-- ── Dialog confirmation suppression ───────────────────────────────────── -->
    <SgiltDialog
      v-model:open="deleteOpen"
      title="Supprimer mon compte"
      description="Cette action est irréversible. Toutes vos données seront supprimées."
      max-width="400px"
    >
      <div class="delete-dialog">
        <p class="delete-dialog-text">
          Êtes-vous sûr de vouloir supprimer votre compte&nbsp;? Vos événements et demandes en
          cours seront définitivement perdus.
        </p>
        <div class="delete-dialog-actions">
          <SgiltButton variant="secondary" @click="deleteOpen = false">
            Non, garder mon compte
          </SgiltButton>
          <SgiltButton :loading="deleting" class="btn-destructive" @click="deleteAccount">
            Oui, supprimer
          </SgiltButton>
        </div>
      </div>
    </SgiltDialog>
  </div>
</template>

<script setup lang="ts">
import { CameraIcon } from '@remixicons/vue/line'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'

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
const previewUrl = ref<string | null>(profile.value.photo ?? null)

const initials = computed(() => {
  const f = profile.value.firstName.charAt(0).toUpperCase()
  const l = profile.value.lastName.charAt(0).toUpperCase()
  return `${f}${l}`
})

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
  previewUrl.value = profile.value.photo ?? null
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
  background: #fff;
  border: 1px solid $divider-color;
  border-radius: $section-radius;
  padding: $spacing-m;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

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
  }

  .section-edit {
    background: none;
    border: none;
    font-family: inherit;
    font-size: 0.82rem;
    font-weight: 500;
    color: $brand-primary;
    cursor: pointer;
    padding: 0;
    transition: opacity 150ms ease;

    &:hover {
      opacity: 0.7;
    }
  }
}

// ── Avatar ────────────────────────────────────────────────────────────────────

.avatar-row {
  display: flex;
  justify-content: center;
}

.avatar {
  position: relative;
  width: 5rem;
  height: 5rem;
  border-radius: 50%;
  background: $surface-soft;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  cursor: default;

  &:has(.avatar-overlay) {
    cursor: pointer;
  }

  .avatar-photo {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .avatar-initials {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.6rem;
    font-weight: 500;
    color: $text-primary;
    line-height: 1;
    user-select: none;
  }

  .avatar-overlay {
    position: absolute;
    inset: 0;
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

// ── Champs ────────────────────────────────────────────────────────────────────

.fields {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

.field {
  display: flex;
  align-items: baseline;
  gap: $spacing-s;

  .field-label {
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    color: $text-secondary;
    min-width: 7rem;
    flex-shrink: 0;
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
    flex: 1;
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
}

// ── Actions édition ───────────────────────────────────────────────────────────

.edit-actions {
  display: flex;
  gap: $spacing-s;
  padding-top: $spacing-xs;
}

// ── Lien Keycloak ─────────────────────────────────────────────────────────────

.keycloak-link {
  font-family: 'Inter', sans-serif;
  font-size: 0.82rem;
  font-weight: 500;
  color: $brand-primary;
  text-decoration: none;
  transition: opacity 150ms ease;

  &:hover {
    opacity: 0.7;
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
