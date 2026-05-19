package net.franzka.sgilt.core.storage;

public class FileTooLargeException extends RuntimeException {
    public FileTooLargeException(long maxBytes) {
        super("Le fichier dépasse la taille maximale autorisée (" + maxBytes / (1024 * 1024) + " Mo)");
    }
}
