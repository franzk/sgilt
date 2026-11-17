/**
 * Types — clés personnalisées de definePageMeta, typées pour qu'une faute de frappe ne passe pas
 * en silence (sans cette déclaration, PageMeta accepte n'importe quelle clé).
 */
declare module '#app' {
  interface PageMeta {
    // Page plein écran (ex. le scan des e-billets) : la route parente d'un événement masque sa sidebar.
    fullscreen?: boolean
  }
}

export {}
