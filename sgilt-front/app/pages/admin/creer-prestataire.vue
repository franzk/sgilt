<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>{{ $t('admin.prestataires.title') }}</h1>
    </div>

    <AdminPrestataireTabs />

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
        <select v-model="selectedSubcats" multiple :disabled="subcategories.length === 0">
          <option v-for="subcategory in subcategories" :key="subcategory.key" :value="subcategory.key">
            {{ subcategory.name }}
          </option>
        </select>
        <input v-model="form.firstName" type="text" :placeholder="$t('admin.prestataires.form.first-name')" />
        <input v-model="form.lastName" type="text" :placeholder="$t('admin.prestataires.form.last-name')" />
        <input v-model="form.email" type="email" :placeholder="$t('admin.prestataires.form.email')" />
      </div>

      <label class="cle-en-main-toggle">
        <input v-model="form.cleEnMain" type="checkbox" />
        {{ $t('admin.prestataires.form.cle-en-main') }}
      </label>
      <p class="cle-en-main-hint">
        {{ form.cleEnMain
          ? $t('admin.prestataires.form.cle-en-main-hint-on')
          : $t('admin.prestataires.form.cle-en-main-hint-off') }}
      </p>

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
import AdminPrestataireTabs from '~/components/admin/AdminPrestataireTabs.vue'

definePageMeta({ layout: 'admin' })

const { provision, provisioning, provisionError, lastProvisionedSlug } = useAdminPrestataires()

const categories = APP_CATEGORIES.filter((c) => c.key !== 'all')

const emptyForm = () => ({
  email: '',
  firstName: '',
  lastName: '',
  slug: '',
  prestataireName: '',
  category: '',
  subcats: '',
  cleEnMain: false,
})

const form = reactive(emptyForm())

const subcategories = computed(
  () => categories.find((c) => c.key === form.category)?.subcategories.filter((s) => s.key) ?? [],
)

const selectedSubcats = ref<string[]>([])

watch(selectedSubcats, (value) => {
  form.subcats = value.join(',')
})

watch(
  () => form.category,
  () => {
    selectedSubcats.value = []
  },
)

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
    selectedSubcats.value = []
  }
}
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
  }

  .cle-en-main-toggle {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    font-size: 0.85rem;
    cursor: pointer;
  }

  .cle-en-main-hint {
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
