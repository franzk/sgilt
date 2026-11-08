package net.franzka.sgilt.core.evenement.billetterie.commande.ebillet;

/**
 * Libellés de l'e-billet partagés par ses supports générés côté back (e-mail, PDF).
 */
public final class EBilletLabelUtils {

    private EBilletLabelUtils() {
    }

    /**
     * Pour combien de personnes vaut l'e-billet : « 1 personne », « 4 personnes ».
     *
     * @param quantity le nombre de places de la commande
     * @return le libellé, accordé
     */
    public static String validFor(int quantity) {
        return quantity + (quantity > 1 ? " personnes" : " personne");
    }
}
