package net.franzka.sgilt.core;

import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ConfirmationTokenProperties.class)
public class SgiltCoreApplication {

	static void main(String[] args) {
		SpringApplication.run(SgiltCoreApplication.class, args);
	}

}
