package net.franzka.sgilt.core;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
@Disabled("le démarrage du contexte est couvert par les TI Karate (sgilt-integration).")
class SgiltCoreApplicationTests {

	@Test
	void contextLoads() {
	}

}
