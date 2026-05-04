package net.franzka.sgilt.core.utilisateur.exception;

public class UtilisateurNotFoundException extends RuntimeException {

    public UtilisateurNotFoundException() {
        super("Utilisateur introuvable");
    }
}
