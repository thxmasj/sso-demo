package it.thomasjohansen.hello.config;

import it.thomasjohansen.hello.HelloPort;
import it.thomasjohansen.hello.ws.PasswordCallbackHandler;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SpringCxfConfig {

    @Autowired
    private HelloPort service;

    @Bean
    public Bus cxf() {
        return new SpringBus();
    }

    @Bean
    public Server jaxWsService() {
        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setBus(cxf());
        factory.setServiceBean(service);
        // WSDL location and service name must be set to enable policies
        // set on service in WSDL.
        factory.setWsdlLocation("Hello.wsdl");
        factory.setServiceName(
                new QName("http://thomasjohansen.it/hello", "HelloService")
        );
        factory.setAddress("/hello");
        factory.setProperties(jaxwsProperties());
        return factory.create();
    }

    private Map<String, Object> jaxwsProperties() {
        Map<String, Object> properties = new HashMap<>();
        // This specifies the key used for decrypting symmetric key in SAML tokens
        properties.put("security.signature.properties", "/sts-crypto.properties");
        // Password used for key in keystore above is supplied by this callback handler (not Merlin props, which
        // provides password for keystore) TODO: Try with different passwords!
        properties.put("security.callback-handler", PasswordCallbackHandler.class.getName());
        // Microsoft Active Directory Federation Services 3.0 is not compliant with Basic Security Profile
        // rule 5426. See https://issues.apache.org/jira/browse/WSS-519.
        properties.put("ws-security.is-bsp-compliant", "false");
        return properties;
    }

}
