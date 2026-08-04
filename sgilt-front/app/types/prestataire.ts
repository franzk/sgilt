/**
 * 'preview' : rendu identique à 'display' (lecture seule) — utilisé par l'onglet Preview de
 * l'éditeur. Le chrome de réservation (datepicker + bouton) est visible en desktop, en disabled,
 * pour se faire une idée du rendu ; toujours masqué en mobile (sticky-cta absent en preview).
 */
export type DisplayMode = 'display' | 'edit' | 'preview' | 'IA'
