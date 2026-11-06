package net.franzka.sgilt.pay;

import net.franzka.sgilt.pay.config.StripeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StripeProperties.class)
public class SgiltPayApplication {

    static void main(String[] args) {
        SpringApplication.run(SgiltPayApplication.class, args);
    }
}
