<template>
  <template v-if="!editable">
    <div class="faq-item">
      <p class="question">{{ model.question }}</p>
      <p class="answer">{{ model.answer }}</p>
    </div>
  </template>
  <template v-else>
    <div class="faq-item">
      <EditableText
        ref="questionRef"
        as="p"
        :model-value="questionModel"
        field="faq.question"
        :editable="true"
        class="question"
        @update:model-value="setQuestion"
        @enter="answerRef?.startEdit()"
      />
      <EditableText
        ref="answerRef"
        as="p"
        :model-value="answerModel"
        field="faq.answer"
        :editable="true"
        class="answer"
        @update:model-value="setAnswer"
      />
    </div>
  </template>
</template>

<!-- exports module-level : importables par le parent (impossible depuis <script setup>) -->
<script lang="ts">
export const newItem = (): FaqItem => ({ question: '', answer: '' })
export const isEmpty = (item: FaqItem): boolean =>
  !item.question.trim() && !item.answer.trim()
</script>

<script setup lang="ts">
import EditableText from '~/components/prestataire/EditableText.vue'
import type { FaqItem } from '~/data/prestataire/domain/FaqItem'

defineProps<{ editable?: boolean }>()

const model = defineModel<FaqItem>({ required: true })

const questionModel = computed(() => model.value.question)
const answerModel = computed(() => model.value.answer)

function setQuestion(val: string | null) {
  model.value = { ...model.value, question: val ?? '' }
}
function setAnswer(val: string | null) {
  model.value = { ...model.value, answer: val ?? '' }
}

const questionRef = ref<{ startEdit: () => void } | null>(null)
const answerRef = ref<{ startEdit: () => void } | null>(null)

function startEdit() {
  questionRef.value?.startEdit()
}

defineExpose({ startEdit })
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.faq-item {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}

.question {
  font-size: 0.9rem;
  font-weight: 600;
  color: $color-primary;
  margin: 0;
}

.answer {
  font-size: 0.9rem;
  line-height: 1.6;
  color: $text-secondary;
  margin: 0;
}
</style>
