import type { EventDetail } from '~/types/event'

export const DEFAULT_COVERS: Record<string, string> = {
  mariage:
    'https://images.unsplash.com/photo-1519741497674-611481863552?w=1400&auto=format&fit=crop',
  anniversaire:
    'https://images.unsplash.com/photo-1530103862676-de8c9debad1d?w=1400&auto=format&fit=crop',
  soiree_privee:
    'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=1400&auto=format&fit=crop',
  fete_entreprise:
    'https://images.unsplash.com/photo-1511578314322-379afb476865?w=1400&auto=format&fit=crop',
  autre: 'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=1400&auto=format&fit=crop',
}

export function resolveEventCover(
  event: Pick<EventDetail, 'coverImage' | 'eventType'>,
): string {
  return event.coverImage ?? DEFAULT_COVERS[event.eventType ?? ''] ?? DEFAULT_COVERS.autre!
}
