import { useCategories } from '~/data/prestataire/useCategories'

// Catégories chargées avant le rendu : les filtres de recherche et les libellés de catégorie en
// dépendent dès le SSR. Côté client, l'état arrive déjà rempli par le payload.
export default defineNuxtPlugin(async () => {
  await useCategories().load()
})
