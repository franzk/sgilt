<!--
  Back-office admin — face publique d'un événement (/admin/evenements-publics) : on choisit un
  événement, puis on saisit ses horaires, sa page publique (/e/[slug]) et sa billetterie,
  enregistrés d'un bloc. Décocher la page ou la billetterie puis enregistrer la supprime ; la
  billetterie n'existe qu'avec une page.
-->
<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>{{ $t('admin.evenements-publics.title') }}</h1>
    </div>

    <select v-model="selectedId" class="evenement-select" :aria-label="$t('admin.evenements-publics.choose')">
      <option value="" disabled>{{ $t('admin.evenements-publics.choose') }}</option>
      <option v-for="evenement in evenements" :key="evenement.id" :value="evenement.id">
        {{ evenementLabel(evenement) }}
      </option>
    </select>

    <p v-if="loading">{{ $t('admin.evenements-publics.loading') }}</p>
    <p v-if="failure" class="failure" role="alert">{{ $t(`admin.evenements-publics.errors.${failure}`) }}</p>

    <template v-if="form">
      <section class="block">
        <h2>{{ $t('admin.evenements-publics.hours.title') }}</h2>
        <div class="row">
          <label class="field">
            <span class="label">{{ $t('admin.evenements-publics.hours.start') }}</span>
            <input v-model="form.startTime" type="time" />
          </label>
          <label class="field">
            <span class="label">{{ $t('admin.evenements-publics.hours.end') }}</span>
            <input v-model="form.endTime" type="time" />
          </label>
        </div>
        <p class="hint">{{ $t('admin.evenements-publics.hours.hint') }}</p>
      </section>

      <section class="block">
        <label class="toggle">
          <input v-model="hasPage" type="checkbox" />
          <h2>{{ $t('admin.evenements-publics.page.title') }}</h2>
        </label>

        <div v-if="form.page" class="fields">
          <label class="field">
            <span class="label">{{ $t('admin.evenements-publics.page.slug') }}</span>
            <input v-model="form.page.slug" type="text" placeholder="les-jeudis-du-taennel" />
          </label>
          <label class="field">
            <span class="label">{{ $t('admin.evenements-publics.page.tag') }}</span>
            <input v-model="form.page.tag" type="text" />
          </label>
          <label class="field">
            <span class="label">{{ $t('admin.evenements-publics.page.subtitle') }}</span>
            <input v-model="form.page.subtitle" type="text" />
          </label>
          <label class="field">
            <span class="label">{{ $t('admin.evenements-publics.page.section-title') }}</span>
            <input v-model="form.page.sectionTitle" type="text" />
          </label>
          <label class="field">
            <span class="label">{{ $t('admin.evenements-publics.page.description') }}</span>
            <textarea v-model="form.page.description" rows="4" />
          </label>
          <label class="field">
            <span class="label">{{ $t('admin.evenements-publics.page.important-info') }}</span>
            <textarea v-model="importantInfoText" rows="3" />
          </label>
          <label class="field">
            <span class="label">{{ $t('admin.evenements-publics.page.practical-info') }}</span>
            <textarea v-model="practicalInfoText" rows="3" />
          </label>
          <p class="hint">{{ $t('admin.evenements-publics.page.lines-hint') }}</p>

          <div class="pictos">
            <span class="label">{{ $t('admin.evenements-publics.page.pictos') }}</span>
            <div v-for="(picto, index) in form.page.pictos" :key="index" class="picto">
              <select v-model="picto.icon" :aria-label="$t('admin.evenements-publics.page.picto-icon')">
                <option v-for="icon in PUBLIC_EVENT_PICTO_ICONS" :key="icon" :value="icon">
                  {{ $t(`admin.evenements-publics.icons.${icon}`) }}
                </option>
              </select>
              <input
                v-model="picto.label"
                type="text"
                :placeholder="$t('admin.evenements-publics.page.picto-label')"
              />
              <input
                v-model="picto.sublabel"
                type="text"
                :placeholder="$t('admin.evenements-publics.page.picto-sublabel')"
              />
              <SgiltButton variant="tertiary" @click="removePicto(index)">
                {{ $t('admin.evenements-publics.page.picto-remove') }}
              </SgiltButton>
            </div>
            <SgiltButton variant="secondary" @click="addPicto">
              {{ $t('admin.evenements-publics.page.picto-add') }}
            </SgiltButton>
          </div>
        </div>
      </section>

      <section v-if="form.page" class="block">
        <label class="toggle">
          <input v-model="hasTicketing" type="checkbox" />
          <h2>{{ $t('admin.evenements-publics.ticketing.title') }}</h2>
        </label>

        <div v-if="form.ticketing" class="row">
          <label class="field">
            <span class="label">{{ $t('admin.evenements-publics.ticketing.status') }}</span>
            <select v-model="form.ticketing.status">
              <option v-for="status in TICKETING_STATUSES" :key="status" :value="status">
                {{ $t(`admin.evenements-publics.ticketing.statuses.${status}`) }}
              </option>
            </select>
          </label>
          <label class="field">
            <span class="label">{{ $t('admin.evenements-publics.ticketing.price') }}</span>
            <input v-model.number="unitPriceEuros" type="number" min="0" step="0.01" />
          </label>
          <label class="field">
            <span class="label">{{ $t('admin.evenements-publics.ticketing.capacity') }}</span>
            <input v-model.number="form.ticketing.capacity" type="number" min="0" step="1" />
          </label>
        </div>
      </section>

      <div class="actions">
        <SgiltButton :loading="saving" @click="onSave">
          {{ $t('admin.evenements-publics.save') }}
        </SgiltButton>
        <p v-if="saved" class="success">
          {{ $t('admin.evenements-publics.saved') }}
          <NuxtLink v-if="form.page" :to="`/e/${form.page.slug}`" target="_blank">/e/{{ form.page.slug }}</NuxtLink>
        </p>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import type { AdminEvenementListItem, AdminPageForm } from '~/data/admin/domain/AdminEvenement'
import { PUBLIC_EVENT_PICTO_ICONS, type TicketingStatus } from '~/data/ticketing/domain/PublicEvent'
import { formatDate } from '~/utils/dateUtils'

definePageMeta({ layout: 'admin' })

const { t } = useI18n()

const { evenements, form, loading, saving, saved, failure, loadEvenements, select, save, fail } =
  useAdminEventPublicForm()

const TICKETING_STATUSES: TicketingStatus[] = ['NOT_OPEN', 'OPEN', 'CLOSED']

// Slug en kebab-case : minuscules, chiffres, tirets simples (ex. les-jeudis-du-taennel).
const SLUG_PATTERN = /^[a-z0-9]+(-[a-z0-9]+)*$/

const selectedId = ref('')
watch(selectedId, (id) => {
  if (id) select(id)
})

function evenementLabel(evenement: AdminEvenementListItem): string {
  const parts = [evenement.title, formatDate(evenement.date), evenement.ownerEmail]
  if (evenement.slug) parts.push(`/e/${evenement.slug}`)
  return parts.join(' · ')
}

// ── Page et billetterie : présentes ou non ──
// Décocher la page retire aussi la billetterie, qui n'existe pas sans elle.

const emptyPage = (): AdminPageForm => ({
  slug: '',
  tag: '',
  subtitle: '',
  sectionTitle: '',
  description: '',
  importantInfo: [],
  practicalInfo: [],
  pictos: [],
})

const hasPage = computed({
  get: () => !!form.value?.page,
  set: (value: boolean) => {
    if (!form.value) return
    form.value.page = value ? emptyPage() : null
    if (!value) form.value.ticketing = null
  },
})

const hasTicketing = computed({
  get: () => !!form.value?.ticketing,
  set: (value: boolean) => {
    if (!form.value) return
    form.value.ticketing = value ? { status: 'NOT_OPEN', unitPriceCents: 0, capacity: 0 } : null
  },
})

// ── Champs saisis autrement qu'ils ne sont stockés ──
// Infos importantes / pratiques : une ligne par info ; prix : en euros. Gardés tels que tapés
// (une liste filtrée à chaque frappe empêcherait de passer à la ligne), convertis à
// l'enregistrement ; réinitialisés à chaque formulaire chargé ou enregistré.

const importantInfoText = ref('')
const practicalInfoText = ref('')
const unitPriceEuros = ref<number | ''>(0)

watch(
  form,
  (value) => {
    importantInfoText.value = value?.page?.importantInfo.join('\n') ?? ''
    practicalInfoText.value = value?.page?.practicalInfo.join('\n') ?? ''
    unitPriceEuros.value = value?.ticketing ? value.ticketing.unitPriceCents / 100 : 0
  },
  { immediate: true },
)

const toLines = (text: string): string[] =>
  text
    .split('\n')
    .map((line) => line.trim())
    .filter(Boolean)

function addPicto() {
  form.value?.page?.pictos.push({ icon: 'music', label: '', sublabel: '' })
}

function removePicto(index: number) {
  form.value?.page?.pictos.splice(index, 1)
}

// ── Enregistrement ──

const isNonNegativeInteger = (value: unknown): value is number =>
  typeof value === 'number' && Number.isInteger(value) && value >= 0

async function onSave() {
  if (!form.value || !selectedId.value) return
  const { page, ticketing } = form.value

  if (page) {
    page.slug = page.slug.trim()
    if (!page.slug) return fail('SLUG_REQUIRED')
    // Même règle que pour les sous-catégories : une URL lisible, sans accents ni espaces.
    if (!SLUG_PATTERN.test(page.slug)) return fail('INVALID_SLUG')
    if (page.pictos.some((picto) => !picto.label.trim())) return fail('PICTO_INCOMPLETE')
    page.importantInfo = toLines(importantInfoText.value)
    page.practicalInfo = toLines(practicalInfoText.value)
  }

  if (ticketing) {
    const euros = unitPriceEuros.value
    if (typeof euros !== 'number' || Number.isNaN(euros) || euros < 0) return fail('INVALID_PRICE')
    if (!isNonNegativeInteger(ticketing.capacity)) return fail('INVALID_CAPACITY')
    ticketing.unitPriceCents = Math.round(euros * 100)
  }

  await save(selectedId.value)
}

onMounted(() => loadEvenements())

useHead({ title: t('admin.evenements-publics.title') })
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

  .page-header {
    h1 {
      margin: 0;
    }
  }

  .evenement-select {
    padding: $spacing-xs $spacing-s;
    border: 1px solid $divider-color;
    border-radius: $radius-sm;
    font-family: inherit;
    font-size: 0.9rem;
  }

  .failure {
    margin: 0;
    padding: $spacing-xs $spacing-s;
    border-radius: $radius-sm;
    background: $surface-soft;
    font-size: 0.85rem;
    color: $state-error;
  }

  .block {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
    padding: $spacing-m;
    border-radius: $radius-md;
    background: $surface-soft;

    h2 {
      margin: 0;
      font-size: 1.1rem;
    }

    .toggle {
      display: flex;
      align-items: center;
      gap: $spacing-xs;
      cursor: pointer;
    }

    .row {
      display: flex;
      flex-wrap: wrap;
      gap: $spacing-s;

      .field {
        flex: 1;
        min-width: 10rem;
      }
    }

    .fields {
      display: flex;
      flex-direction: column;
      gap: $spacing-s;
    }

    .field {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .label {
      font-size: 0.8rem;
      font-weight: 600;
      color: $text-secondary;
    }

    input,
    select,
    textarea {
      padding: $spacing-xs $spacing-s;
      border: 1px solid $divider-color;
      border-radius: $radius-sm;
      font-family: inherit;
      font-size: 0.9rem;
    }

    .hint {
      margin: 0;
      font-size: 0.8rem;
      color: $text-secondary;
    }

    .pictos {
      display: flex;
      flex-direction: column;
      gap: $spacing-xs;

      .picto {
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        gap: $spacing-xs;

        input {
          flex: 1;
          min-width: 8rem;
        }
      }
    }
  }

  .actions {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: $spacing-s;

    .success {
      margin: 0;
      font-size: 0.85rem;
    }
  }
}
</style>
