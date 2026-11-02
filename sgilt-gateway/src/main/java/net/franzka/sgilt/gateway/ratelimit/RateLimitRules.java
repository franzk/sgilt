package net.franzka.sgilt.gateway.ratelimit;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Toutes les limites de débit de la gateway, appliquées par {@link RateLimitFilter}. Ajouter une
 * limite = ajouter une règle ici.
 */
final class RateLimitRules {

    /**
     * Les deux entrées d'init-onboarding — {@code POST /api/v1/onboarding} (événement complet) et
     * {@code POST /api/v1/onboarding/demande} (demande unique) — partagent un compteur ; les autres
     * routes de {@code /api/v1/onboarding/**} (verify, confirm-account) ne sont pas limitées. Chaque
     * appel envoie un email : la limite protège contre l'envoi en masse.
     */
    static final RateLimitRule INIT_ONBOARDING = new RateLimitRule(
            "init-onboarding", "POST", Set.of("/api/v1/onboarding", "/api/v1/onboarding/demande"),
            5, Duration.ofMinutes(10));

    /**
     * Création de commande de billets : chaque appel bloque des places et crée un PaymentIntent
     * Stripe. Limite large : un réessai après une carte refusée crée une nouvelle commande, et le
     * soir de l'événement plusieurs acheteurs peuvent partager la même IP (wifi du lieu).
     */
    static final RateLimitRule COMMANDES = new RateLimitRule(
            "commandes", "POST", Set.of("/api/v1/commandes"),
            20, Duration.ofMinutes(10));

    static final List<RateLimitRule> RULES = List.of(INIT_ONBOARDING, COMMANDES);

    private RateLimitRules() {
    }
}
