<template>
  <template v-if="!editable">
    <blockquote class="testimony">
      <p class="text">« {{ model.text }} »</p>
      <footer class="footer">
        <span class="author">{{ model.author }}</span>
        <span v-if="model.eventType" class="event">{{ model.eventType }}</span>
      </footer>
    </blockquote>
  </template>
  <template v-else>
    <div class="testimony-edit">
      <EditableText
        ref="textRef"
        as="p"
        :model-value="textModel"
        field="testimonials.text"
        :editable="true"
        class="text"
        @update:model-value="setText"
      />
      <div class="footer">
        <EditableText
          :model-value="authorModel"
          field="testimonials.author"
          :editable="true"
          class="author"
          @update:model-value="setAuthor"
        />
        <EditableText
          :model-value="eventTypeModel"
          field="testimonials.eventType"
          :editable="true"
          class="event"
          @update:model-value="setEventType"
        />
      </div>
    </div>
  </template>
</template>

<!-- exports module-level : importables par le parent (impossible depuis <script setup>) -->
<script lang="ts">
export const newItem = (): Testimonial => ({ text: '', author: '', eventType: '' })
export const isEmpty = (item: Testimonial): boolean =>
  !item.text.trim() && !item.author.trim() && !(item.eventType ?? '').trim()
</script>

<script setup lang="ts">
import EditableText from '~/components/prestataire/EditableText.vue'
import type { Testimonial } from '~/data/prestataire/domain/Testimonial'

defineProps<{ editable?: boolean }>()

const model = defineModel<Testimonial>({ required: true })

const textModel = computed(() => model.value.text)
const authorModel = computed(() => model.value.author)
const eventTypeModel = computed(() => model.value.eventType ?? '')

function setText(val: string | null) {
  model.value = { ...model.value, text: val ?? '' }
}
function setAuthor(val: string | null) {
  model.value = { ...model.value, author: val ?? '' }
}
function setEventType(val: string | null) {
  model.value = { ...model.value, eventType: val ?? '' }
}

const textRef = ref<{ startEdit: () => void } | null>(null)

function startEdit() {
  textRef.value?.startEdit()
}

defineExpose({ startEdit })
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// ── Display mode ──────────────────────────────────────────────────────────────

.testimony {
  margin: 0;
  padding: $spacing-m;
  background: #fafafa;
  border-radius: $radius-md;
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.text {
  font-family: 'Cormorant Garamond', serif;
  font-size: 1.05rem;
  font-style: italic;
  line-height: 1.6;
  color: $color-primary;
  margin: 0 0 0.75rem;
}

.footer {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.author {
  font-size: 0.85rem;
  font-weight: 600;
  color: $text-secondary;
}

.event {
  font-size: 0.8rem;
  color: $text-secondary;
  opacity: 0.6;

  &::before {
    content: '· ';
  }
}

// ── Edit mode ─────────────────────────────────────────────────────────────────

.testimony-edit {
  padding: $spacing-m;
  background: #fafafa;
  border-radius: $radius-md;
  border: 1px solid rgba(0, 0, 0, 0.06);
}
</style>
