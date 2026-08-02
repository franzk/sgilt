/**
 * Composable — état singleton du profil éditable de l'utilisateur courant.
 * Le consommateur lie ses champs directement à `profile` ; save() persiste l'état courant,
 * sans argument. cancelEdit() restaure l'état capturé par startEdit().
 */
import { fetchUtilisateurEditProfile, updateUtilisateurProfile } from './service/utilisateurService'
import type { UtilisateurEditProfile } from './dto/UtilisateurEditProfile'

const profile = reactive<UtilisateurEditProfile>({
  firstName: '',
  lastName: '',
  phone: null,
  email: '',
})
const loading = ref(true)
const editing = ref(false)
const saving = ref(false)
const saveError = ref(false)
let snapshot: UtilisateurEditProfile | null = null
let _loaded = false

export function useUtilisateur() {
  onMounted(() => {
    if (!_loaded) load()
  })

  async function load(): Promise<void> {
    _loaded = true
    loading.value = true
    try {
      Object.assign(profile, await fetchUtilisateurEditProfile())
    } finally {
      loading.value = false
    }
  }

  function startEdit(): void {
    snapshot = { ...profile }
    editing.value = true
  }

  function cancelEdit(): void {
    if (snapshot) Object.assign(profile, snapshot)
    editing.value = false
  }

  async function save(): Promise<void> {
    saving.value = true
    saveError.value = false
    try {
      await updateUtilisateurProfile({
        firstName: profile.firstName,
        lastName: profile.lastName,
        phone: profile.phone,
      })
      editing.value = false
    } catch {
      saveError.value = true
    } finally {
      saving.value = false
    }
  }

  return {
    profile,
    loading,
    editing,
    saving,
    saveError,
    startEdit,
    cancelEdit,
    save,
  }
}
