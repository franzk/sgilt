<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>{{ $t('admin.prestataires.title') }}</h1>
    </div>

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
            <p class="meta">
              {{ categoryName(row.categoryKey) }}
              <span v-if="subcatNames(row)"> · {{ subcatNames(row) }}</span>
            </p>
            <p class="reservations">{{ formatReservationCounts(row.reservationCounts) }}</p>
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
import SgiltCard from '~/components/basics/cards/SgiltCard.vue'
import PrestataireStatusBadge from '~/components/admin/PrestataireStatusBadge.vue'
import type { PrestataireAdminFormat, PrestataireReservationCounts } from '~/data/admin/domain/PrestataireAdminFormat'
import type { ReservationStatus } from '~/data/reservation/domain/ReservationStatus'

definePageMeta({ layout: 'admin' })

const { t } = useI18n()

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

const RESERVATION_COUNT_FIELDS: { status: ReservationStatus; field: keyof PrestataireReservationCounts }[] = [
  { status: 'nouvelle', field: 'nouvelleCount' },
  { status: 'en_discussion', field: 'inDiscussionCount' },
  { status: 'confirmee', field: 'confirmedCount' },
  { status: 'refusee', field: 'refuseeCount' },
  { status: 'annulee', field: 'annuleeCount' },
  { status: 'realisee', field: 'realiseeCount' },
]

function categoryName(categoryKey: string): string {
  return categories.find((c) => c.key === categoryKey)?.name ?? categoryKey
}

function subcatNames(row: PrestataireAdminFormat): string {
  const category = categories.find((c) => c.key === row.categoryKey)
  return row.subcatKeys
    .map((key) => category?.subcategories.find((s) => s.key === key)?.name)
    .filter((name): name is string => !!name)
    .join(', ')
}

function formatReservationCounts(counts: PrestataireReservationCounts): string {
  const parts = RESERVATION_COUNT_FIELDS.filter(({ field }) => counts[field] > 0).map(
    ({ status, field }) => `${t(`reservation.statut.${status}`)} (${counts[field]})`,
  )
  return parts.length > 0 ? parts.join(', ') : t('admin.prestataires.reservations-empty')
}

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

.list {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  // Sur mobile, la cta (bouton publier/renvoyer en revue) passe sous le contenu
  // plutôt que de forcer la carte à dépasser la largeur de l'écran.
  @media (max-width: #{$breakpoint-desktop - 1px}) {
    :deep(.sgilt-card.format-small) {
      flex-wrap: wrap;

      .cta {
        width: 100%;
        justify-content: flex-end;
      }
    }
  }

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

    .meta,
    .reservations {
      margin: 0;
      font-size: 0.75rem;
      color: $text-secondary;
    }
  }
}
</style>
