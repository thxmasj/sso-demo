package it.thomasjohansen.hello.config;

import org.springframework.context.annotation.Import;

/**
 * @author thomas@thomasjohansen.it
 */
@Import({
        SpringServiceConfig.class,
        SpringCxfConfig.class
})
public class SpringConfig {
}
