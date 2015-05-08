package it.thomasjohansen.hello.config;

import it.thomasjohansen.hello.service.HelloResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author thomas@thomasjohansen.it
 */
@Configuration
public class SpringServiceConfig {

    @Bean
    public HelloResource helloResource() {
        return new HelloResource();
    }

}
