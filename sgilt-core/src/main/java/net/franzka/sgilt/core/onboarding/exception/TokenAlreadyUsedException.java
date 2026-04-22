package net.franzka.sgilt.core.onboarding.exception;

public class TokenAlreadyUsedException extends RuntimeException {
    public TokenAlreadyUsedException() {
        super("Token has already been used");
    }
}
