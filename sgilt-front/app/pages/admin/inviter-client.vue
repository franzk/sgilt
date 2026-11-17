<!--
  BO — /admin/inviter-client : formulaire d'invitation d'un client dont le deal s'est conclu hors
  Sgilt (ex. le Taennel). Crée le compte client (sans mot de passe), son événement et sa
  réservation confirmée chez le prestataire choisi. Aucun mail ne part vers le client : le
  prestataire dépose ses documents, puis l'invitation s'envoie depuis la liste (/admin/invitations).
-->
<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>{{ $t('admin.invitations.title') }}</h1>
    </div>

    <AdminInvitationTabs />

    <section class="create-form">
      <h2>{{ $t('admin.invitations.form.title') }}</h2>

      <div class="fields">
        <input v-model="form.firstName" type="text" :placeholder="$t('admin.invitations.form.first-name')" />
        <input v-model="form.lastName" type="text" :placeholder="$t('admin.invitations.form.last-name')" />
        <input v-model="form.email" type="email" :placeholder="$t('admin.invitations.form.email')" />
        <select v-model="form.eventType">
          <option value="" disabled>{{ $t('admin.invitations.form.event-type') }}</option>
          <option v-for="option in eventTypeOptions" :key="option.value" :value="option.value">
            {{ option.label }}
          </option>
        </select>
        <label class="date-field">
          {{ $t('admin.invitations.form.event-date') }}
          <input v-model="form.eventDate" type="date" />
        </label>
        <select v-model="form.prestataireId">
          <option value="" disabled>{{ $t('admin.invitations.form.prestataire') }}</option>
          <option v-for="prestataire in prestataireOptions" :key="prestataire.id" :value="prestataire.id">
            {{ prestataire.name }}
          </option>
        </select>
      </div>

      <p class="hint">{{ $t('admin.invitations.form.hint') }}</p>

      <SgiltButton :loading="creating" @click="onCreate">
        {{ $t('admin.invitations.form.submit') }}
      </SgiltButton>
      <p v-if="lastCreatedEmail" class="success">
        {{ $t('admin.invitations.form.success', { email: lastCreatedEmail }) }}
      </p>
      <p v-if="createFailure" class="error" role="alert">
        {{ $t(`admin.invitations.form.errors.${createFailure}`) }}
      </p>
    </section>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import AdminInvitationTabs from '~/components/admin/AdminInvitationTabs.vue'
import type { ClientInvitationCreation } from '~/data/admin/domain/ClientInvitation'

definePageMeta({ layout: 'admin' })

const { t } = useI18n()

const { create, creating, createFailure, failCreate, lastCreatedEmail } = useAdminClientInvitations()
const { rows: prestataires, load: loadPrestataires } = useAdminPrestataires()
const { eventTypeOptions } = useEventTypeLabel()

const emptyForm = (): ClientInvitationCreation => ({
  firstName: '',
  lastName: '',
  email: '',
  eventType: '',
  eventDate: '',
  prestataireId: '',
})

const form = reactive(emptyForm())

const prestataireOptions = computed(() =>
  [...prestataires.value].sort((a, b) => a.name.localeCompare(b.name)),
)

const isFormComplete = computed(
  () =>
    !!form.firstName &&
    !!form.lastName &&
    !!form.email &&
    !!form.eventType &&
    !!form.eventDate &&
    !!form.prestataireId,
)

// Bouton toujours cliquable : un formulaire incomplet est signalé au clic.
async function onCreate() {
  if (!isFormComplete.value) {
    failCreate('MISSING_FIELDS')
    return
  }
  const ok = await create({ ...form })
  if (ok) {
    Object.assign(form, emptyForm())
  }
}

onMounted(() => loadPrestataires())

useHead({ title: t('admin.invitations.tabs.create') })
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.admin-page {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: $spacing-l;
  max-width: 45rem;
  margin: 0 auto;
}

.page-header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-s;

  h1 {
    margin: 0;
  }
}

.create-form {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
  padding: $spacing-m;
  border-radius: $radius-md;
  background: $surface-soft;

  .fields {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;

    input,
    select {
      padding: $spacing-xs $spacing-s;
      border: 1px solid $divider-color;
      border-radius: $radius-sm;
      font-family: inherit;
      font-size: 0.9rem;
    }

    .date-field {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
      font-size: 0.8rem;
      color: $text-secondary;
    }
  }

  .hint {
    margin: 0;
    font-size: 0.8rem;
    color: $text-secondary;
  }

  .success {
    margin: 0;
    font-size: 0.85rem;
    color: $state-success;
  }

  .error {
    margin: 0;
    font-size: 0.85rem;
    color: $state-error;
  }
}
</style>
