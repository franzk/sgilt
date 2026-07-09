/**
 * Composable — état de la liste admin des prestataires (back-office)
 */
import {
  fetchAdminPrestataires,
  publishPrestataire,
  sendPrestataireBackToReview,
  provisionPrestataire,
} from './service/adminService'
import type { PrestataireAdminFormat } from './domain/PrestataireAdminFormat'
import type { PrestataireProvisioning } from './domain/PrestataireProvisioning'

const rows = ref<PrestataireAdminFormat[]>([])
const loading = ref(false)
const provisioning = ref(false)
const provisionError = ref(false)
const lastProvisionedSlug = ref<string | null>(null)

export function useAdminPrestataires() {
  async function load() {
    loading.value = true
    try {
      rows.value = await fetchAdminPrestataires()
    } finally {
      loading.value = false
    }
  }

  async function publish(id: string): Promise<void> {
    await publishPrestataire(id)
    await load()
  }

  async function sendBackToReview(id: string): Promise<void> {
    await sendPrestataireBackToReview(id)
    await load()
  }

  async function provision(input: PrestataireProvisioning): Promise<boolean> {
    provisioning.value = true
    provisionError.value = false
    lastProvisionedSlug.value = null
    try {
      const result = await provisionPrestataire(input)
      lastProvisionedSlug.value = result.slug
      await load()
      return true
    } catch {
      provisionError.value = true
      return false
    } finally {
      provisioning.value = false
    }
  }

  return {
    rows,
    loading,
    load,
    publish,
    sendBackToReview,
    provision,
    provisioning,
    provisionError,
    lastProvisionedSlug,
  }
}
