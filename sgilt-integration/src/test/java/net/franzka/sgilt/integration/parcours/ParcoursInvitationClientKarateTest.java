package net.franzka.sgilt.integration.parcours;

import com.intuit.karate.junit5.Karate;
import net.franzka.sgilt.integration.IntegrationTestContext;
import org.junit.jupiter.api.Tag;

@Tag("integration")
class ParcoursInvitationClientKarateTest extends IntegrationTestContext {

    @Karate.Test
    Karate testInvitationClient() {
        return Karate.run("ParcoursInvitationClient").relativeTo(getClass());
    }

    @Karate.Test
    Karate testInvitationClientLienExpire() {
        return Karate.run("ParcoursInvitationClientLienExpire").relativeTo(getClass());
    }

    @Karate.Test
    Karate testInvitationClientMotDePasseOublie() {
        return Karate.run("ParcoursInvitationClientMotDePasseOublie").relativeTo(getClass());
    }
}
