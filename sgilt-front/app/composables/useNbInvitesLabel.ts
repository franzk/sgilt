import { NB_INVITES_UNKNOWN } from '~/types/evenement'

// Nombre d'invités tel qu'affiché : « je ne sais pas encore » (stocké sous sa clé) par son
// libellé, sinon « N personnes » (tranche ou nombre exact).
export function useNbInvitesLabel() {
  const { t } = useI18n()

  function nbInvitesLabel(nbInvites: string | null | undefined): string | null {
    if (!nbInvites) return null
    if (nbInvites === NB_INVITES_UNKNOWN) return t('event-info.nb-invites-unknown')
    return t('event-info.nb-invites-value', { value: nbInvites })
  }

  return { nbInvitesLabel }
}
