package it.thomasjohansen.hello.config;

import it.thomasjohansen.hello.HelloPort;
import it.thomasjohansen.hello.service.HelloService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author thomas@thomasjohansen.it
 */
@Configuration
public class SpringServiceConfig {

    @Bean
    public HelloPort helloPort() {
        return new HelloService();
    }

}
