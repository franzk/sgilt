/**
 * Composable — administration des sous-catégories (back-office), affichées dans leurs catégories.
 * Chaque écriture met la liste à jour à partir de la sous-catégorie renvoyée par le back ; un
 * refus est exposé dans `failure`.
 */
import {
  fetchCategoriesAdmin,
  createSousCategorie,
  updateSousCategorie,
  moveSousCategorie,
  deleteSousCategorie,
  toSousCategorieFailure,
} from './service/adminService'
import type { CategorieAdmin, MoveDirection, SousCategorieAdmin } from './domain/CategorieAdmin'

const categories = ref<CategorieAdmin[]>([])
const loading = ref(false)
// Motif du dernier échec (suffixe de la clé i18n admin.sous-categories.errors.*), ou null.
const failure = ref<string | null>(null)

export function useAdminSousCategories() {
  async function load() {
    loading.value = true
    failure.value = null
    try {
      categories.value = await fetchCategoriesAdmin()
    } catch {
      failure.value = 'TECHNICAL'
    } finally {
      loading.value = false
    }
  }

  // Enveloppe commune des écritures sur les sous-catégories (create, update, move, remove) :
  //  1. efface l'échec précédent ;
  //  2. exécute `operation` (l'appel au back) ;
  //  3. succès : renvoie son résultat, que l'action utilise pour mettre la liste à jour ;
  //  4. échec : renseigne `failure` et renvoie null — l'action s'arrête alors et renvoie false.
  // `refusal` est le motif affiché si le back refuse l'opération (400) : chaque opération n'a
  // qu'un refus métier attendu (clé déjà prise, sous-catégorie utilisée…) ; toute autre erreur
  // est TECHNICAL. Une opération sans résultat (suppression) renvoie true pour signaler le succès.
  async function run<T>(operation: () => Promise<T>, refusal: string): Promise<T | null> {
    failure.value = null
    try {
      return await operation()
    } catch (e) {
      failure.value = toSousCategorieFailure(e, refusal)
      return null
    }
  }

  function fail(reason: string) {
    failure.value = reason
  }

  function subcategoriesOf(categoryKey: string): SousCategorieAdmin[] {
    return categories.value.find((categorie) => categorie.key === categoryKey)?.subcategories ?? []
  }

  // Retire une sous-catégorie de la catégorie qui la contient ; renvoie sa position, ou -1.
  function detachFromList(key: string): number {
    for (const categorie of categories.value) {
      const index = categorie.subcategories.findIndex((sousCategorie) => sousCategorie.key === key)
      if (index >= 0) {
        categorie.subcategories.splice(index, 1)
        return index
      }
    }
    return -1
  }

  async function create(key: string, name: string, categoryKey: string): Promise<boolean> {
    const created = await run(() => createSousCategorie(key, name, categoryKey), 'KEY_ALREADY_EXISTS')
    if (!created) return false
    subcategoriesOf(created.categoryKey).push(created)
    return true
  }

  // Même catégorie : remplacée à sa place. Autre catégorie : placée en fin, comme le fait le back.
  async function update(key: string, name: string, categoryKey: string): Promise<boolean> {
    const updated = await run(() => updateSousCategorie(key, name, categoryKey), 'TECHNICAL')
    if (!updated) return false
    const siblings = subcategoriesOf(updated.categoryKey)
    const index = siblings.findIndex((sousCategorie) => sousCategorie.key === key)
    if (index >= 0) {
      siblings.splice(index, 1, updated)
    } else {
      detachFromList(key)
      siblings.push(updated)
    }
    return true
  }

  // Le back échange la sous-catégorie avec sa voisine ; la liste fait le même échange.
  async function move(key: string, direction: MoveDirection): Promise<boolean> {
    const moved = await run(() => moveSousCategorie(key, direction), 'TECHNICAL')
    if (!moved) return false
    const siblings = subcategoriesOf(moved.categoryKey)
    const index = siblings.findIndex((sousCategorie) => sousCategorie.key === key)
    const target = direction === 'UP' ? index - 1 : index + 1
    if (index < 0 || target < 0 || target >= siblings.length) return true
    siblings.splice(index, 1)
    siblings.splice(target, 0, moved)
    return true
  }

  async function remove(key: string): Promise<boolean> {
    const done = await run(async () => {
      await deleteSousCategorie(key)
      return true
    }, 'SOUS_CATEGORIE_IN_USE')
    if (!done) return false
    detachFromList(key)
    return true
  }

  return {
    categories,
    loading,
    failure,
    load,
    fail,
    create,
    update,
    move,
    remove,
  }
}
