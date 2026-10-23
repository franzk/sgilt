/**
 * Fichier calendrier (.ics, RFC 5545) d'un événement, généré et téléchargé côté navigateur —
 * utilisé par « Ajouter au calendrier » sur la confirmation d'achat de billets. Les heures sont
 * « flottantes » (sans fuseau) : l'événement a lieu à l'heure locale du lieu, que les agendas
 * affichent telle quelle.
 */

export interface IcsEvent {
  // Identifiant stable : ré-importer le fichier met à jour l'événement au lieu de le dupliquer.
  uid: string
  title: string
  description: string
  location: string
  url: string
  // Heure locale du lieu, sans fuseau ('YYYY-MM-DDTHH:mm').
  startsAt: string
  endsAt: string
}

// Longueur maximale d'une ligne, en octets ; au-delà, la ligne est repliée (RFC 5545 §3.1).
const MAX_LINE_BYTES = 75

// '2026-03-12T19:00' → '20260312T190000'
function toIcsLocalDateTime(isoLocal: string): string {
  return `${isoLocal.replace(/[-:]/g, '')}00`
}

// Horodatage UTC de génération du fichier. Ex : '20261023T123456Z'
function toIcsUtcNow(): string {
  return new Date()
    .toISOString()
    .replace(/[-:]/g, '')
    .replace(/\.\d{3}/, '')
}

function escapeText(text: string): string {
  return text
    .replace(/\\/g, '\\\\')
    .replace(/;/g, '\\;')
    .replace(/,/g, '\\,')
    .replace(/\r?\n/g, '\\n')
}

// Replie une ligne trop longue : les lignes de continuation commencent par une espace, qui compte
// dans la limite. Coupe entre deux caractères, jamais au milieu d'un caractère multi-octets.
function foldLine(line: string): string {
  const encoder = new TextEncoder()
  const parts: string[] = []
  let current = ''
  let currentBytes = 0
  for (const char of line) {
    const bytes = encoder.encode(char).length
    const limit = parts.length ? MAX_LINE_BYTES - 1 : MAX_LINE_BYTES
    if (currentBytes + bytes > limit) {
      parts.push(current)
      current = ''
      currentBytes = 0
    }
    current += char
    currentBytes += bytes
  }
  parts.push(current)
  return parts.join('\r\n ')
}

export function buildIcsEvent(event: IcsEvent): string {
  const lines = [
    'BEGIN:VCALENDAR',
    'VERSION:2.0',
    'PRODID:-//Sgilt//Billetterie//FR',
    'CALSCALE:GREGORIAN',
    'METHOD:PUBLISH',
    'BEGIN:VEVENT',
    `UID:${event.uid}`,
    `DTSTAMP:${toIcsUtcNow()}`,
    `DTSTART:${toIcsLocalDateTime(event.startsAt)}`,
    `DTEND:${toIcsLocalDateTime(event.endsAt)}`,
    `SUMMARY:${escapeText(event.title)}`,
    `DESCRIPTION:${escapeText(event.description)}`,
    `LOCATION:${escapeText(event.location)}`,
    `URL:${event.url}`,
    'END:VEVENT',
    'END:VCALENDAR',
  ]
  return `${lines.map(foldLine).join('\r\n')}\r\n`
}

export function downloadIcs(fileName: string, content: string): void {
  const blob = new Blob([content], { type: 'text/calendar;charset=utf-8' })
  const objectUrl = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = objectUrl
  a.download = fileName
  a.click()
  URL.revokeObjectURL(objectUrl)
}
