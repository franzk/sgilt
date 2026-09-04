package net.franzka.sgilt.integration.parcours;

import com.intuit.karate.junit5.Karate;
import net.franzka.sgilt.integration.IntegrationTestContext;
import org.junit.jupiter.api.Tag;

@Tag("integration")
class ParcoursRecherchePrestatairesKarateTest extends IntegrationTestContext {

    @Karate.Test
    Karate testRecherchePrestataires() {
        return Karate.run("ParcoursRecherchePrestataires").relativeTo(getClass());
    }
}
