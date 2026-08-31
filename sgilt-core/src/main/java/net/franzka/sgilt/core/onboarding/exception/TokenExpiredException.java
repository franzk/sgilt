package net.franzka.sgilt.core.onboarding.exception;

import lombok.Getter;
import net.franzka.sgilt.core.onboarding.domain.OnboardingFlow;

@Getter
public class TokenExpiredException extends RuntimeException {

    private final OnboardingFlow flow;

    public TokenExpiredException() {
        super("Token has expired");
        this.flow = null;
    }

    public TokenExpiredException(OnboardingFlow flow) {
        super("Token has expired");
        this.flow = flow;
    }
}
