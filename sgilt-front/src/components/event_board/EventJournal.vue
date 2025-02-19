<template>
  <div class="event-messages">
    <h3>💬 Messages</h3>
    <div v-for="message in messages" :key="message.id" class="message">
      <strong>{{ message.sender }}:</strong> {{ message.text }}
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

defineProps({
  messages: Array,
})
</script>

<style scoped>
.event-messages {
  background: white;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 2px 4px 10px rgba(0, 0, 0, 0.2);
  width: 200px;
}
.message {
  background: #f9f9f9;
  padding: 8px;
  margin-top: 5px;
  border-radius: 5px;
  font-size: 14px;
}
</style>
