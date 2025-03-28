import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const isConnected = ref<boolean>(false)

  return { isConnected }
})
