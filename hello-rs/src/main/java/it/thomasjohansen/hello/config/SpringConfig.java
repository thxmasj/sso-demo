package it.thomasjohansen.hello.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author thomas@thomasjohansen.it
 */
@Configuration
@Import({
        SpringCxfConfig.class,
        SpringServiceConfig.class
})
public class SpringConfig {
}
