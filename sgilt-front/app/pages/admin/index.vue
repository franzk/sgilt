<template>
  <div class="admin-page">
    <h1>{{ $t('admin.prestataires.title') }}</h1>

    <section class="list">
      <p v-if="loading">{{ $t('admin.prestataires.loading') }}</p>
      <SgiltCard v-for="row in rows" :key="row.id" format="small" tag="div" :clickable="false">
        <template #avatar>
          <span class="avatar-initial">{{ row.name.charAt(0) }}</span>
        </template>
        <div class="row-content">
          <div class="text">
            <p class="name">{{ row.name }}</p>
            <p class="email">{{ row.email }}</p>
          </div>
          <PrestataireStatusBadge :status="row.status" />
        </div>
        <template #cta>
          <SgiltButton v-if="row.status === 'IN_REVIEW'" variant="secondary" @click="publish(row.id)">
            {{ $t('admin.prestataires.publish') }}
          </SgiltButton>
          <SgiltButton v-if="row.status === 'PUBLISHED'" variant="secondary" @click="sendBackToReview(row.id)">
            {{ $t('admin.prestataires.send-to-review') }}
          </SgiltButton>
        </template>
      </SgiltCard>
    </section>

    <section class="create-form">
      <h2>{{ $t('admin.prestataires.form.title') }}</h2>

      <div class="fields">
        <input v-model="form.prestataireName" type="text" :placeholder="$t('admin.prestataires.form.prestataire-name')" />
        <input v-model="form.slug" type="text" :placeholder="$t('admin.prestataires.form.slug')" />
        <select v-model="form.category">
          <option value="" disabled>{{ $t('admin.prestataires.form.category') }}</option>
          <option v-for="category in categories" :key="category.key" :value="category.key">
            {{ category.name }}
          </option>
        </select>
        <input v-model="form.subcats" type="text" :placeholder="$t('admin.prestataires.form.subcats')" />
        <input v-model="form.firstName" type="text" :placeholder="$t('admin.prestataires.form.first-name')" />
        <input v-model="form.lastName" type="text" :placeholder="$t('admin.prestataires.form.last-name')" />
        <input v-model="form.email" type="email" :placeholder="$t('admin.prestataires.form.email')" />
      </div>

      <SgiltButton :loading="provisioning" :disabled="!isFormValid" @click="onProvision">
        {{ $t('admin.prestataires.form.submit') }}
      </SgiltButton>
      <p v-if="lastProvisionedSlug" class="success">
        {{ $t('admin.prestataires.form.success', { slug: lastProvisionedSlug }) }}
      </p>
      <p v-if="provisionError" class="error">{{ $t('admin.prestataires.form.error') }}</p>
    </section>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import SgiltCard from '~/components/basics/cards/SgiltCard.vue'
import PrestataireStatusBadge from '~/components/admin/PrestataireStatusBadge.vue'

definePageMeta({ layout: 'default' })

const {
  rows,
  loading,
  load,
  publish,
  sendBackToReview,
  provision,
  provisioning,
  provisionError,
  lastProvisionedSlug,
} = useAdminPrestataires()

const categories = APP_CATEGORIES.filter((c) => c.key !== 'all')

const emptyForm = () => ({
  email: '',
  firstName: '',
  lastName: '',
  slug: '',
  prestataireName: '',
  category: '',
  subcats: '',
})

const form = reactive(emptyForm())

const isFormValid = computed(
  () =>
    !!form.email &&
    !!form.firstName &&
    !!form.lastName &&
    !!form.slug &&
    !!form.prestataireName &&
    !!form.category,
)

async function onProvision() {
  const ok = await provision({ ...form })
  if (ok) {
    Object.assign(form, emptyForm())
  }
}

onMounted(() => load())
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.admin-page {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: $spacing-l;
  max-width: 720px;
  margin: 0 auto;
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

.list {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  .avatar-initial {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    background: $brand-accent;
    color: #fff;
    font-weight: 600;
  }

  .row-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $spacing-xs;
    min-width: 0;

    .text {
      display: flex;
      flex-direction: column;
      gap: 2px;
      min-width: 0;
    }

    .name {
      margin: 0;
      font-weight: 500;
    }

    .email {
      margin: 0;
      font-size: 0.8rem;
      color: $text-secondary;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}
</style>
