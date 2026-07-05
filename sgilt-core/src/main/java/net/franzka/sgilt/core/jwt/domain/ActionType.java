package net.franzka.sgilt.core.jwt.domain;

/**
 * Discriminant du flow d'action auquel un {@link ActionToken} se rattache.
 * Chaque constante décrit son propre flow : le chemin front à atteindre et l'action que
 * le front doit y déclencher une fois le lien consommé.
 */
public enum ActionType {

    PRESTATAIRE_ONBOARDING("/verify", "PRESTATAIRE_ONBOARDING");

    private final String frontPath;
    private final String action;

    ActionType(String frontPath, String action) {
        this.frontPath = frontPath;
        this.action = action;
    }

    /**
     * Retourne le chemin front auquel ce type d'action doit rediriger.
     *
     * @return le chemin front (ex. {@code /verify})
     */
    public String frontPath() {
        return frontPath;
    }

    /**
     * Retourne l'action que le front doit déclencher une fois sur ce chemin.
     *
     * @return le nom de l'action (ex. {@code SET_PASSWORD})
     */
    public String action() {
        return action;
    }
}
