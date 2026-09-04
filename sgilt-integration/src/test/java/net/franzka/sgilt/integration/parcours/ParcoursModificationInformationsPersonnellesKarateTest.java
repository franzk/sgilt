package net.franzka.sgilt.integration.parcours;

import com.intuit.karate.junit5.Karate;
import net.franzka.sgilt.integration.IntegrationTestContext;
import org.junit.jupiter.api.Tag;

@Tag("integration")
class ParcoursModificationInformationsPersonnellesKarateTest extends IntegrationTestContext {

    @Karate.Test
    Karate testModificationInformationsPersonnelles() {
        return Karate.run("ParcoursModificationInformationsPersonnelles").relativeTo(getClass());
    }
}
