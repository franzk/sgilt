package net.franzka.sgilt.integration.parcours;

import com.intuit.karate.junit5.Karate;
import net.franzka.sgilt.integration.IntegrationTestContext;
import org.junit.jupiter.api.Tag;

@Tag("integration")
class ParcoursPrestataireKarateTest extends IntegrationTestContext {

    @Karate.Test
    Karate testParcoursPrestataireOnboarding() {
        return Karate.run("ParcoursPrestataireOnboarding").relativeTo(getClass());
    }
}
