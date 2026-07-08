const OUTPUT_MAX_LONGEST_SIDE_PX = 2400
const INPUT_MINIMAL_SIZE_BYTES = 500 * 1024
const OUTPUT_QUALITY = 0.8
const OUTPUT_MIME_TYPE = 'image/webp'
const OUTPUT_EXTENSION = 'webp'

/**
 * Redimensionne et recompresse une image côté client avant upload (cap ~2400px de long côté, WebP q0.8).
 * En dessous de 500 Ko, le fichier est renvoyé tel quel.
 * Si le décodage échoue (format non supporté par le navigateur, fichier corrompu), on retombe sur le
 * fichier original s'il reste sous maxUploadSizeBytes ; au-delà, on lève une erreur explicite.
 */
export async function resizeImageForUpload(file: File, maxUploadSizeBytes: number): Promise<File> {
  if (file.size <= INPUT_MINIMAL_SIZE_BYTES) return file

  try {
    return await encodeResized(file)
  } catch (e) {
    console.error('[imageResize] resize failed, falling back to original file', e)
    if (file.size <= maxUploadSizeBytes) return file
    throw new Error('IMAGE_TOO_LARGE')
  }
}

async function encodeResized(file: File): Promise<File> {
  const bitmap = await createImageBitmap(file, { imageOrientation: 'from-image' })
  const { width, height } = targetDimensions(bitmap.width, bitmap.height)

  const canvas = document.createElement('canvas')
  canvas.width = width
  canvas.height = height
  const ctx = canvas.getContext('2d')
  if (!ctx) {
    bitmap.close()
    throw new Error('CANVAS_CONTEXT_UNAVAILABLE')
  }
  ctx.drawImage(bitmap, 0, 0, width, height)
  bitmap.close()

  const blob = await new Promise<Blob | null>((resolve) =>
    canvas.toBlob(resolve, OUTPUT_MIME_TYPE, OUTPUT_QUALITY),
  )
  if (!blob) throw new Error('CANVAS_ENCODE_FAILED')

  return new File([blob], withExtension(file.name, OUTPUT_EXTENSION), { type: OUTPUT_MIME_TYPE })
}

function targetDimensions(width: number, height: number): { width: number; height: number } {
  const longestSide = Math.max(width, height)
  if (longestSide <= OUTPUT_MAX_LONGEST_SIDE_PX) return { width, height }
  const scale = OUTPUT_MAX_LONGEST_SIDE_PX / longestSide
  return { width: Math.round(width * scale), height: Math.round(height * scale) }
}

function withExtension(filename: string, ext: string): string {
  return `${filename.replace(/\.[^./]+$/, '')}.${ext}`
}
