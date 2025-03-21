import type { ReservationStatusKey } from '@/types/ReservationStatus'

const STATUS_COLORS = {
  pending: ['#FFD180', '#FFAB40', '#FF9100'], // orange shades 0-8h, 8-16h, 16-24h
  viewed: ['#FFCCBC', '#FF8A65', '#FF5722'], // blue shades 0-8h, 8-16h, 16-24h
  approved: ['#FF8A80', '#E57373', '#D32F2F'], // red shades 0-24h, 24-48h, 48-72h
  expired: ['#5a5a5a'], // grey
  paid: ['#28a745'], // green
  cancelled: ['#343a40'], // dark grey
  undefined: ['#FFFFFF'], // white
}

interface StatusColorStyleArgs {
  cssParameter: string
  statusKey?: ReservationStatusKey
  startTime?: Date
  opacity?: number
}

export function useReservationStatusColor() {
  const statusColorStyle = (
    args: StatusColorStyleArgs,
    /*cssParameter: string,
    statusKey?: ReservationStatusKey,
    startTime?: Date,*/
  ) => {
    const elapsedHours = args.startTime
      ? (new Date().getTime() - new Date(args.startTime).getTime()) / 1000 / 3600
      : 0

    const status = (args.statusKey?.toString() || 'undefined') as keyof typeof STATUS_COLORS

    let color = STATUS_COLORS[status][0] // takes the first color by default

    if (['pending', 'viewed'].includes(status)) {
      if (elapsedHours > 16) color = STATUS_COLORS[status][2]
      else if (elapsedHours > 8) color = STATUS_COLORS[status][1]
    } else if (status === 'approved') {
      if (elapsedHours > 48) color = STATUS_COLORS[status][2]
      else if (elapsedHours > 24) color = STATUS_COLORS[status][1]
    } else if (status === 'expired') {
      // Gris foncé pour annulation
    } else {
      color = STATUS_COLORS[status][0]
    }

    if (args.opacity) {
      const hexOpacity = Math.round(255 * args.opacity)
        .toString(16)
        .padStart(2, '0')
      color = color + hexOpacity
    }

    console.log({ [args.cssParameter]: color })

    return { [args.cssParameter]: color }
  }

  return { statusColorStyle }
}
