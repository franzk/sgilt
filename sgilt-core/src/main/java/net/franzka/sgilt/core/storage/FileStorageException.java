package net.franzka.sgilt.core.storage;

/**
 * Exception levée lorsqu'une opération de stockage d'image échoue.
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
