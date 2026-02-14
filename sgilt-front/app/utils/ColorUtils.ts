/**
 * Adjusts the luminance of a hex color.
 *
 * @param hex - A hex color string (e.g. "#3498db", "3498db", or "abc")
 * @param luminance - Luminance factor:
 *   - 0   → no change
 *   - > 0 → lighter (e.g. 0.2 = +20%)
 *   - < 0 → darker  (e.g. -0.2 = -20%)
 *
 * @returns A new hex color string in the format "#rrggbb"
 */
export function adjustColorLuminance(hex: string, luminance: number = 0): string {
  // Remove non-hex characters (like "#")
  const cleanHex = hex.replace(/[^0-9a-f]/gi, '')

  // Expand short hex format (#abc → #aabbcc)
  const fullHex =
    cleanHex.length === 3
      ? cleanHex
          .split('')
          .map((char) => char + char)
          .join('')
      : cleanHex

  // Safety fallback (invalid input)
  if (fullHex.length !== 6) {
    throw new Error(`Invalid hex color: "${hex}"`)
  }

  // Process R, G, B channels
  const adjusted = Array.from({ length: 3 }, (_, index) => {
    const channelHex = fullHex.slice(index * 2, index * 2 + 2)
    const channel = parseInt(channelHex, 16)

    // Apply luminance and clamp between 0–255
    const adjustedChannel = Math.round(Math.min(255, Math.max(0, channel + channel * luminance)))

    // Convert back to 2-digit hex
    return adjustedChannel.toString(16).padStart(2, '0')
  })

  return `#${adjusted.join('')}`
}
