<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>{{ $t('admin.sous-categories.title') }}</h1>
    </div>

    <p v-if="loading">{{ $t('admin.sous-categories.loading') }}</p>
    <p v-if="failure" class="failure" role="alert">{{ $t(`admin.sous-categories.errors.${failure}`) }}</p>

    <section v-for="categorie in categories" :key="categorie.key" class="categorie">
      <h2 class="name">{{ categorie.name }}</h2>

      <ul class="rows">
        <li v-for="sousCategorie in categorie.subcategories" :key="sousCategorie.key" class="row">
          <div class="identity">
            <span class="key">{{ sousCategorie.key }}</span>
            <span class="count">
              {{
                $t(
                  'admin.sous-categories.prestataire-count',
                  { n: sousCategorie.prestataireCount },
                  sousCategorie.prestataireCount,
                )
              }}
            </span>
          </div>

          <div v-if="drafts[sousCategorie.key]" class="fields">
            <input
              v-model="draftOf(sousCategorie.key).name"
              type="text"
              :aria-label="$t('admin.sous-categories.name')"
            />
            <select
              v-model="draftOf(sousCategorie.key).categoryKey"
              :aria-label="$t('admin.sous-categories.category')"
            >
              <option v-for="option in categories" :key="option.key" :value="option.key">
                {{ option.name }}
              </option>
            </select>
          </div>

          <div class="actions">
            <button
              type="button"
              class="move"
              :aria-label="$t('admin.sous-categories.move-up')"
              @click="move(sousCategorie.key, 'UP')"
            >
              <ArrowUpIcon class="icon" aria-hidden="true" />
            </button>
            <button
              type="button"
              class="move"
              :aria-label="$t('admin.sous-categories.move-down')"
              @click="move(sousCategorie.key, 'DOWN')"
            >
              <ArrowDownIcon class="icon" aria-hidden="true" />
            </button>
            <SgiltButton variant="secondary" @click="save(sousCategorie.key)">
              {{ $t('admin.sous-categories.save') }}
            </SgiltButton>
            <SgiltButton variant="tertiary" @click="remove(sousCategorie.key)">
              {{ $t('admin.sous-categories.delete') }}
            </SgiltButton>
          </div>
        </li>
      </ul>
    </section>

    <section class="create-form">
      <h2>{{ $t('admin.sous-categories.create-title') }}</h2>
      <div class="fields">
        <input v-model="newSousCategorie.key" type="text" :placeholder="$t('admin.sous-categories.key')" />
        <input v-model="newSousCategorie.name" type="text" :placeholder="$t('admin.sous-categories.name')" />
        <select v-model="newSousCategorie.categoryKey">
          <option value="" disabled>{{ $t('admin.sous-categories.category') }}</option>
          <option v-for="option in categories" :key="option.key" :value="option.key">
            {{ option.name }}
          </option>
        </select>
      </div>
      <p class="hint">{{ $t('admin.sous-categories.key-hint') }}</p>
      <SgiltButton @click="onCreate">{{ $t('admin.sous-categories.create') }}</SgiltButton>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ArrowDownIcon, ArrowUpIcon } from '@remixicons/vue/line'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'

definePageMeta({ layout: 'admin' })

const { categories, loading, failure, load, fail, create, update, move, remove } = useAdminSousCategories()

// Brouillon éditable de chaque sous-catégorie, réinitialisé à chaque modification des catégories
// (deep : les écritures mettent à jour les listes de sous-catégories sur place).
const drafts = ref<Record<string, { name: string; categoryKey: string }>>({})

watch(
  categories,
  (value) => {
    drafts.value = Object.fromEntries(
      value.flatMap((categorie) =>
        categorie.subcategories.map((sousCategorie) => [
          sousCategorie.key,
          { name: sousCategorie.name, categoryKey: sousCategorie.categoryKey },
        ]),
      ),
    )
  },
  { immediate: true, deep: true },
)

// Toujours présent pour une sous-catégorie affichée : le watch ci-dessus le crée avant le rendu.
function draftOf(key: string) {
  return drafts.value[key]!
}

async function save(key: string) {
  const draft = drafts.value[key]
  if (!draft?.name.trim()) {
    fail('INCOMPLETE')
    return
  }
  await update(key, draft.name.trim(), draft.categoryKey)
}

// Clé en kebab-case : minuscules, chiffres, tirets simples (ex. photo-video).
const SOUS_CATEGORIE_KEY_PATTERN = /^[a-z0-9]+(-[a-z0-9]+)*$/

const emptySousCategorie = () => ({ key: '', name: '', categoryKey: '' })
const newSousCategorie = reactive(emptySousCategorie())

async function onCreate() {
  const key = newSousCategorie.key.trim()
  const name = newSousCategorie.name.trim()
  if (!key || !name || !newSousCategorie.categoryKey) {
    fail('INCOMPLETE')
    return
  }
  // Même règle que le back : sans elle, son 400 de validation passerait pour une clé déjà prise.
  if (!SOUS_CATEGORIE_KEY_PATTERN.test(key)) {
    fail('INVALID_KEY')
    return
  }
  if (await create(key, name, newSousCategorie.categoryKey)) {
    Object.assign(newSousCategorie, emptySousCategorie())
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
  max-width: 45rem;
  margin: 0 auto;
}

.page-header {
  h1 {
    margin: 0;
  }
}

.failure {
  margin: 0;
  padding: $spacing-xs $spacing-s;
  border-radius: $radius-sm;
  background: $surface-soft;
  font-size: 0.85rem;
  color: $state-error;
}

.categorie {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;

  .name {
    margin: 0;
    font-size: 1.1rem;
  }

  .rows {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
    margin: 0;
    padding: 0;
    list-style: none;

    .row {
      display: flex;
      flex-direction: column;
      gap: $spacing-xs;
      padding: $spacing-s;
      border-radius: $radius-md;
      background: $surface-soft;

      .identity {
        display: flex;
        align-items: baseline;
        justify-content: space-between;
        gap: $spacing-s;

        .key {
          font-family: monospace;
          font-size: 0.85rem;
        }

        .count {
          font-size: 0.75rem;
          color: $text-secondary;
        }
      }

      .fields {
        display: flex;
        flex-wrap: wrap;
        gap: $spacing-xs;

        input,
        select {
          flex: 1;
          min-width: 10rem;
          padding: $spacing-xs $spacing-s;
          border: 1px solid $divider-color;
          border-radius: $radius-sm;
          font-family: inherit;
          font-size: 0.9rem;
        }
      }

      .actions {
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        gap: $spacing-xs;

        .move {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 2rem;
          height: 2rem;
          border: 1px solid $divider-color;
          border-radius: $radius-sm;
          background: transparent;
          cursor: pointer;

          .icon {
            width: 1rem;
            height: 1rem;
          }
        }
      }
    }
  }
}

.create-form {
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

  .hint {
    margin: 0;
    font-size: 0.8rem;
    color: $text-secondary;
  }
}
</style>
