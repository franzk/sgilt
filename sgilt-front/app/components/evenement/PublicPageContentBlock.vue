<!--
  Bloc « Contenu de la page » de l'écran Page publique du board : ce que l'organisateur saisit pour
  sa page /e/[slug] (adresse, étiquette, sous-titre, présentation, infos, points forts), enregistré
  par son propre bouton. Le premier enregistrement crée la page, en brouillon. Une fois la page
  publiée, son adresse ne change plus : elle s'affiche sans champ. Vérifie le format avant
  d'émettre `save` ; les refus du back arrivent par `failure`.
-->
<template>
  <section class="content-block">
    <h2 class="section-title">{{ $t('evenement.page-publique.content.title') }}</h2>

    <div class="field">
      <span class="label">{{ $t('evenement.page-publique.content.slug') }}</span>
      <template v-if="slugLocked">
        <p class="locked-slug">/e/{{ form.slug }}</p>
        <p class="hint">{{ $t('evenement.page-publique.content.slug-locked') }}</p>
      </template>
      <template v-else>
        <div class="slug-input">
          <span class="prefix">/e/</span>
          <input v-model="form.slug" type="text" placeholder="les-jeudis-du-taennel" autocapitalize="off" />
        </div>
        <p class="hint">{{ $t('evenement.page-publique.content.slug-hint') }}</p>
      </template>
    </div>

    <label class="field">
      <span class="label">{{ $t('evenement.page-publique.content.tag') }}</span>
      <input v-model="form.tag" type="text" />
    </label>
    <label class="field">
      <span class="label">{{ $t('evenement.page-publique.content.subtitle') }}</span>
      <input v-model="form.subtitle" type="text" />
    </label>
    <label class="field">
      <span class="label">{{ $t('evenement.page-publique.content.section-title') }}</span>
      <input v-model="form.sectionTitle" type="text" />
    </label>
    <label class="field">
      <span class="label">{{ $t('evenement.page-publique.content.description') }}</span>
      <textarea v-model="form.description" rows="5" />
    </label>
    <label class="field">
      <span class="label">{{ $t('evenement.page-publique.content.important-info') }}</span>
      <textarea v-model="importantInfoText" rows="3" />
      <span class="hint">{{ $t('evenement.page-publique.content.lines-hint') }}</span>
    </label>
    <label class="field">
      <span class="label">{{ $t('evenement.page-publique.content.practical-info') }}</span>
      <textarea v-model="practicalInfoText" rows="3" />
      <span class="hint">{{ $t('evenement.page-publique.content.lines-hint') }}</span>
    </label>

    <div class="field">
      <span class="label">{{ $t('evenement.page-publique.content.pictos') }}</span>
      <div v-for="(picto, index) in form.pictos" :key="index" class="picto">
        <select v-model="picto.icon" :aria-label="$t('evenement.page-publique.content.picto-icon')">
          <option v-for="icon in PUBLIC_EVENT_PICTO_ICONS" :key="icon" :value="icon">
            {{ $t(`evenement.page-publique.content.icons.${icon}`) }}
          </option>
        </select>
        <input v-model="picto.label" type="text" :placeholder="$t('evenement.page-publique.content.picto-label')" />
        <input
          v-model="picto.sublabel"
          type="text"
          :placeholder="$t('evenement.page-publique.content.picto-sublabel')"
        />
        <button type="button" class="link danger" @click="removePicto(index)">
          {{ $t('evenement.page-publique.content.picto-remove') }}
        </button>
      </div>
      <button type="button" class="link" @click="addPicto">
        <AddIcon class="icon" aria-hidden="true" />
        {{ $t('evenement.page-publique.content.picto-add') }}
      </button>
    </div>

    <div class="actions">
      <SgiltButton :loading="saving" @click="onSave">
        {{ $t('evenement.page-publique.content.save') }}
      </SgiltButton>
      <p v-if="failure" class="failure" role="alert">{{ $t(`evenement.page-publique.errors.${failure}`) }}</p>
      <p v-else-if="saved" class="success" role="status">{{ $t('evenement.page-publique.content.saved') }}</p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { AddIcon } from '@remixicons/vue/line'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import type { OrganizerPage, PageContentForm } from '~/data/ticketing/domain/OrganizerPublicPage'
import { PUBLIC_EVENT_PICTO_ICONS } from '~/data/ticketing/domain/PublicEvent'

const props = defineProps<{
  // Absente tant que l'organisateur n'a pas enregistré de contenu.
  page?: OrganizerPage
  saving: boolean
  saved: boolean
  // Motif du dernier échec (suffixe de clé i18n evenement.page-publique.errors.*), ou null.
  failure: string | null
}>()

const emit = defineEmits<{
  save: [form: PageContentForm]
  // Refus détecté ici, sans appel au back.
  fail: [reason: string]
}>()

// Slug en kebab-case : minuscules, chiffres, tirets simples (même règle que le back).
const SLUG_PATTERN = /^[a-z0-9]+(-[a-z0-9]+)*$/

const emptyForm = (): PageContentForm => ({
  slug: '',
  tag: '',
  subtitle: '',
  sectionTitle: '',
  description: '',
  importantInfo: [],
  practicalInfo: [],
  pictos: [],
})

// Copie de travail, réinitialisée à chaque page reçue (chargement, enregistrement).
const form = ref<PageContentForm>(emptyForm())
// Infos importantes / pratiques : une ligne par info, gardées telles que tapées (une liste filtrée
// à chaque frappe empêcherait de passer à la ligne), converties à l'enregistrement.
const importantInfoText = ref('')
const practicalInfoText = ref('')

watch(
  () => props.page,
  (page) => {
    const content = page?.content
    form.value = content
      ? { ...content, pictos: content.pictos.map((picto) => ({ ...picto })) }
      : emptyForm()
    importantInfoText.value = content?.importantInfo.join('\n') ?? ''
    practicalInfoText.value = content?.practicalInfo.join('\n') ?? ''
  },
  { immediate: true },
)

const slugLocked = computed(() => props.page?.slugLocked ?? false)

function addPicto() {
  form.value.pictos.push({ icon: 'music', label: '', sublabel: '' })
}

function removePicto(index: number) {
  form.value.pictos.splice(index, 1)
}

const toLines = (text: string): string[] =>
  text
    .split('\n')
    .map((line) => line.trim())
    .filter(Boolean)

function onSave() {
  const slug = form.value.slug.trim()
  if (!slug) return emit('fail', 'SLUG_REQUIRED')
  if (!SLUG_PATTERN.test(slug)) return emit('fail', 'INVALID_SLUG')
  if (form.value.pictos.some((picto) => !picto.label.trim())) return emit('fail', 'PICTO_INCOMPLETE')
  emit('save', {
    ...form.value,
    slug,
    importantInfo: toLines(importantInfoText.value),
    practicalInfo: toLines(practicalInfoText.value),
  })
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.content-block {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

  .section-title {
    margin: 0;
    font-size: 0.7rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: $text-secondary;
  }

  .field {
    display: flex;
    flex-direction: column;
    gap: 0.3rem;

    .label {
      font-size: $font-size-sm;
      font-weight: 600;
      color: $text-primary;
    }

    .hint {
      margin: 0;
      font-size: 0.8rem;
      color: $text-secondary;
    }

    .locked-slug {
      margin: 0;
      font-size: $font-size-sm;
      color: $text-primary;
    }

    .slug-input {
      display: flex;
      align-items: center;
      border: 1px solid $divider-color;
      border-radius: $radius-md;
      background: $surface-white;

      .prefix {
        padding-left: $spacing-s;
        font-size: $font-size-sm;
        color: $text-secondary;
      }

      input {
        flex: 1;
        min-width: 0;
        border: none;
        padding-left: 0.1rem;
      }
    }

    input,
    select,
    textarea {
      width: 100%;
      padding: $spacing-xs $spacing-s;
      border: 1px solid $divider-color;
      border-radius: $radius-md;
      background: $surface-white;
      font-family: inherit;
      font-size: 1rem;
      color: $text-primary;
    }

    textarea {
      resize: vertical;
    }

    .picto {
      display: flex;
      flex-direction: column;
      gap: $spacing-xs;
      padding: $spacing-s;
      border-radius: $radius-md;
      background: $surface-soft;
    }

    .link {
      display: inline-flex;
      align-items: center;
      align-self: flex-start;
      gap: $spacing-xxs;
      padding: 0;
      border: none;
      background: none;
      color: $brand-primary;
      font-family: inherit;
      font-size: $font-size-sm;
      font-weight: 500;
      cursor: pointer;

      &.danger {
        color: $state-error;
      }

      .icon {
        width: 1.125rem;
        height: 1.125rem;
      }
    }
  }

  .actions {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;

    .failure {
      margin: 0;
      font-size: $font-size-sm;
      color: $state-error;
    }

    .success {
      margin: 0;
      font-size: $font-size-sm;
      color: $state-success;
    }
  }
}
</style>
