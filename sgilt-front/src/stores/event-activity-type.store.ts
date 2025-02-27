import type { EventActivityType } from '@/types/EventActivityType'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useEventActivityStore = defineStore('eventActivity', () => {
  const activities = ref<EventActivityType[]>([
    {
      id: 'reservation-sent',
      color: '#F7B731', // golden yellow
      icon: 'Mail',
    },
    {
      id: 'reservation-viewed',
      color: '#45A0D9', // Light blue
      icon: 'Eye',
    },
    {
      id: 'reservation-approved',
      color: '#4CAF50', // Green
      icon: 'Check',
    },
    {
      id: 'reservation-canceled',
      color: '#E74C3C', // Red
      icon: 'Cancel',
    },
    {
      id: 'payment-required',
      color: '#E67E22', // Orange
      icon: 'CreditCard',
    },
    {
      id: 'payment-confirmed',
      color: '#27AE60', // Dark green
      icon: 'Done',
    },
    {
      id: 'payment-failed',
      color: '#D35400', // Dark orange
      icon: 'Warning',
    },
    {
      id: 'message-sent',
      color: '#8E44AD', // Violet
      icon: 'Chat',
    },
    {
      id: 'message-received',
      color: '#8E44AD', // Violet
      icon: 'ChatBubble',
    },
    {
      id: 'reservation-updated',
      color: '#3498DB', // Bright blue
      icon: 'EditNote',
    },
    {
      id: 'partner-unavailable',
      color: '#C0392B', // Dark red
      icon: 'Block',
    },
  ])

  return { activities }
})
