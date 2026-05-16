package net.franzka.sgilt.core.image;

/**
 * Exception levée lorsqu'une opération de stockage d'image échoue.
 */
public class ImageStorageException extends RuntimeException {

    public ImageStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
