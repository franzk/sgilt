package net.franzka.sgilt.integration.utilisateur;

import com.intuit.karate.junit5.Karate;
import net.franzka.sgilt.integration.IntegrationTestContext;
import org.junit.jupiter.api.Tag;

@Tag("integration")
class UtilisateurKarateTest extends IntegrationTestContext {

    @Karate.Test
    Karate testUtilisateurController() {
        return Karate.run("UtilisateurController").relativeTo(getClass());
    }
}
