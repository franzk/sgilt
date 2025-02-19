<template>
  <div class="guest-list">
    <h3>👥 Invités</h3>
    <div class="guests">
      <div v-for="guest in guests" :key="guest.id" class="guest">
        <img :src="guest.avatar" alt="Guest Avatar" />
        <p>{{ guest.name }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

defineProps({
  guests: Array,
})
</script>

<style scoped>
.guest-list {
  background: white;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 2px 4px 10px rgba(0, 0, 0, 0.2);
  width: 200px;
}
.guests {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.guest {
  text-align: center;
  font-size: 12px;
}
.guest img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}
</style>
