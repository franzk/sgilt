import type { ReservationStatus, ReservationStatusKey } from '@/types/ReservationStatus'
import { colorLuminance } from '@/utils/ColorUtils'
import { defineStore } from 'pinia'
import { ref } from 'vue'

interface StatusColorStyleArgs {
  cssParameter: string
  statusKey?: ReservationStatusKey
  startTime?: Date
  opacity?: number
  luminance?: number
}

/**
 * Store to manage the status of a reservation
 */
export const useReservationStatusStore = defineStore('statusStore', () => {
  /**
   * Get the status object by key
   * @param key
   * @returns ReservationStatus | undefined
   */
  const getStatus = (key?: ReservationStatusKey) => (key ? statusMap.value.get(key) : undefined)

  /**
   * Map of status with their colors, icon, state, action and subtext
   */
  const statusMap = ref<Map<ReservationStatusKey, ReservationStatus>>(
    new Map([
      [
        'pending',
        {
          colors: ['#FFD180', '#FFAB40', '#FF9100'], // orange shades 0-8h, 8-16h, 16-24h
          icon: 'Mail',
          state: 'Demande envoyée',
          action: 'En attente de réponse du partenaire.',
          subtext: 'Votre demande a été envoyée, le partenaire doit répondre.',
        },
      ],
      [
        'viewed',
        {
          colors: ['#FFCCBC', '#FF8A65', '#FF5722'], // blue shades 0-8h, 8-16h, 16-24h
          icon: 'Eye',
          state: 'Demande vue',
          action: "En attente d'acceptation.",
          subtext: "Votre demande a été vue par le partenaire, il doit maintenant l'accepter.",
        },
      ],
      [
        'approved',
        {
          colors: ['#FF8A80', '#E57373', '#D32F2F'], // red shades 0-24h, 24-48h, 48-72h
          icon: 'CreditCard',
          state: 'Demande acceptée',
          action: 'En attente de paiement.',
          actionColor: 'orange',
          subtext: 'Votre demande a été acceptée, vous pouvez maintenant payer.',
        },
      ],
      [
        'paid',
        {
          colors: ['#28a745'], // green
          icon: 'Rocket',
          state: 'Réservation confirmée',
          action: 'Votre réservation est confirmée.',
          subtext:
            'Votre réservation est confirmée, vous pouvez maintenant profiter de votre événement.',
        },
      ],
      [
        'cancelled',
        {
          colors: ['#343a40'], // dark grey,
          icon: 'Cross',
          state: 'Demande annulée',
          action: 'Votre demande a été annulée.',
          subtext: 'Votre demande a été annulée, vous pouvez en faire une nouvelle.',
        },
      ],
    ]),
  )

  /**
   * return a style object with the color of the status according to the elapsed time
   * e.g. { backgroundColor: '#FFD180' }
   * @param args cssParameter: string, statusKey?: ReservationStatusKey, startTime?: Date, opacity?: number
   * @returns object
   */
  const statusColorStyle = (args: StatusColorStyleArgs): object => {
    if (!args.statusKey) return {}
    const status = statusMap.value.get(args.statusKey)
    if (!status) return {}

    const elapsedHours = args.startTime
      ? (new Date().getTime() - new Date(args.startTime).getTime()) / 1000 / 3600
      : 0

    let color = status.colors[0] // takes the first color by default

    if (['pending', 'viewed'].includes(args.statusKey)) {
      if (elapsedHours > 16) color = status.colors[2]
      else if (elapsedHours > 8) color = status.colors[1]
    } else if (args.statusKey === 'approved') {
      if (elapsedHours > 48) color = status.colors[2]
      else if (elapsedHours > 24) color = status.colors[1]
    } else if (args.statusKey === 'expired') {
      // Gris foncé pour annulation
    }

    if (args.opacity) {
      const hexOpacity = Math.round(255 * args.opacity)
        .toString(16)
        .padStart(2, '0')
      color = color + hexOpacity
    }

    if (args.luminance) {
      color = colorLuminance(color, args.luminance)
    }

    return { [args.cssParameter]: color }
  }

  const getBoxStyle = (statusKey: ReservationStatusKey, startTime: Date): object => {
    return {
      ...statusColorStyle({
        cssParameter: 'border-color',
        statusKey,
        startTime,
        opacity: 1,
        luminance: 0.5,
      }),
      ...statusColorStyle({
        cssParameter: 'background-color',
        statusKey,
        startTime,
        opacity: 0.2,
      }),
    }
  }

  return { getStatus, statusColorStyle, getBoxStyle }
})
