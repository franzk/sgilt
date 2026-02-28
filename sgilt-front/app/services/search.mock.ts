// app/services/search.mock.ts
import type {
  PrestataireCardDetail,
  PrestataireDetail,
  PrestataireSearchResponse,
} from '~/types/prestataire'
import { MOCK_PROVIDERS } from '~/utils/mockData'

export const SearchMockService = {
  async search(params: {
    date: string
    categoryId: string
    subcats: string[]
  }): Promise<PrestataireSearchResponse> {
    // Simulation du délai réseau (indispensable pour tester tes loaders)
    await new Promise((resolve) => setTimeout(resolve, 100))

    // 1. Filtrage des résultats (ProviderCard[])
    const filteredResults = MOCK_PROVIDERS.filter((p) => {
      // Filtre catégorie : si '1', on prend tout, sinon on match l'ID
      const matchCat = params.categoryId === '1' || p.category === params.categoryId

      // Filtre sous-catégories : si aucune cochée, on prend tout le parent.
      // Sinon, on vérifie si le prestataire a au moins une des subcats sélectionnées.
      const matchSub =
        params.subcats.length === 0 || p.subcats.some((s) => params.subcats.includes(s))

      return matchCat && matchSub
    }).map(
      (p) =>
        ({
          id: p.id,
          name: p.name,
          shortDescription: p.shortDescription,
          image: p.image,
          slug: p.slug,
          // map categoryPicto
          categoryPicto: APP_CATEGORIES.find((c) => c.id === p.category)?.picto,
        }) as PrestataireCardDetail,
    )

    // 2. Calcul des compteurs
    const countsByCategory: Record<string, number> = { '1': MOCK_PROVIDERS.length }
    const subcatCounts: Record<string, number> = {}

    MOCK_PROVIDERS.forEach((p) => {
      // Compte toujours pour les catégories parentes (pour les onglets)
      countsByCategory[p.category] = (countsByCategory[p.category] || 0) + 1

      // Compte les subcats UNIQUEMENT si elles appartiennent à la catégorie active
      // et que cette catégorie n'est pas "ALL" (car pas d'affichage de subcats sur ALL)
      if (params.categoryId !== '1' && p.category === params.categoryId) {
        p.subcats.forEach((s) => {
          subcatCounts[s] = (subcatCounts[s] || 0) + 1
        })
      }
    })

    return {
      results: filteredResults,
      countsByCategory,
      subcatCounts,
    }
  },

  async getBySlug(slug: string): Promise<PrestataireDetail | undefined> {
    await new Promise((resolve) => setTimeout(resolve, 300))
    return MOCK_PROVIDERS.find((p) => p.slug === slug) as PrestataireDetail | undefined
  },
}
