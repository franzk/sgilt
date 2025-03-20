import type { ReservationStatusKey } from '@/types/ReservationStatus'

const STATUS_COLORS = {
  pending: ['#FFD180', '#FFAB40', '#FF9100'], // 0-8h, 8-16h, 16-24h
  viewed: ['#FFCCBC', '#FF8A65', '#FF5722'],
  approved: ['#FF8A80', '#E57373', '#D32F2F'], // 0-24h, 24-48h, 48-72h
  expired: ['#5a5a5a'],
  paid: ['#28a745'],
  cancelled: ['#343a40'],
  confirmed: ['#007bff'],
  undefined: ['#FFFFFF'],
}

export function useReservationStatusColor() {
  const statusColorStyle = (
    cssParameter: string,
    statusKey?: ReservationStatusKey,
    startTime?: Date,
  ) => {
    const elapsedHours = startTime
      ? (new Date().getTime() - new Date(startTime).getTime()) / 1000 / 3600
      : 0

    const status = (statusKey?.toString() || 'undefined') as keyof typeof STATUS_COLORS

    let color = STATUS_COLORS[status][0] // Par défaut, on prend la première couleur

    if (status === 'pending' || status === 'viewed') {
      if (elapsedHours > 16) color = STATUS_COLORS[status][2]
      else if (elapsedHours > 8) color = STATUS_COLORS[status][1]
    } else if (status === 'approved') {
      if (elapsedHours > 48)
        color = STATUS_COLORS[status][2] // Rouge foncé
      else if (elapsedHours > 24) color = STATUS_COLORS[status][1]
    } else if (status === 'expired') {
      color = STATUS_COLORS.expired[0] // Gris foncé pour annulation
    } else {
      color = STATUS_COLORS[status][0] // Unique couleur pour les autres
    }

    return { [cssParameter]: color }
  }

  return { statusColorStyle }
}
