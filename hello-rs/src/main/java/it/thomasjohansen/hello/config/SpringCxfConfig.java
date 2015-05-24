package it.thomasjohansen.hello.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import it.thomasjohansen.hello.HelloPort;
import it.thomasjohansen.hello.service.HelloResource;
import it.thomasjohansen.hello.ws.CredentialsInterceptor;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.rs.security.saml.sso.RequestAssertionConsumerService;
import org.apache.cxf.rs.security.saml.sso.SamlRedirectBindingFilter;
import org.apache.cxf.rs.security.saml.sso.state.EHCacheSPStateManager;
import org.apache.cxf.rs.security.saml.sso.state.SPStateManager;
import org.apache.cxf.ws.security.trust.STSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author thomas@thomasjohansen.it
 */
@Configuration
@ImportResource("classpath:applicationContext.xml")
public class SpringCxfConfig {

    @Autowired
    private HelloResource rsService;

    @Bean
    public Bus cxf() {
        return new SpringBus();
    }

    @Bean
    public SamlRedirectBindingFilter redirectGetFilter() {
        SamlRedirectBindingFilter filter = new SamlRedirectBindingFilter();
        filter.setIdpServiceAddress("https://thomasjohansen.it/adfs/ls/");
        filter.setAssertionConsumerServiceAddress("http://thomasjohansen.it:8443/racs/sso");
        filter.setStateProvider(stateManager());
        return filter;
    }

    @Bean
    public RequestAssertionConsumerService requestAssertionConsumerService() {
        RequestAssertionConsumerService service = new RequestAssertionConsumerService();
        service.setStateProvider(stateManager());
        service.setSupportBase64Encoding(false);
        return service;
    }

    @Bean
    public SPStateManager stateManager() {
        return new EHCacheSPStateManager(cxf());
    }

    @Bean
    public Server jaxRsService() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBus(cxf());
        factory.setProvider(jacksonJaxbJsonProvider());
        factory.setProvider(redirectGetFilter());
        factory.setServiceBean(rsService);
        factory.setAddress("/rs");
        return factory.create();
    }

    @Bean
    public Server jaxRsRequestAssertionConsumerService() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBus(cxf());
        factory.setServiceBean(requestAssertionConsumerService());
        factory.setAddress("/racs");
        return factory.create();
    }

    @Bean
    public HelloPort webServiceClient() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setBus(cxf());
        factory.setServiceClass(HelloPort.class);
        factory.setAddress("https://hello.thomasjohansen.it:8080/hello");
        factory.setServiceName(
                new QName("http://thomasjohansen.it/hello", "HelloService")
        );
        factory.setWsdlLocation("Hello.wsdl");
        factory.setProperties(jaxWsProperties());
        return (HelloPort)factory.create();
    }

    @Bean
    public Map<String, Object> jaxWsProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("ws-security.sts.client", stsClient());
        return properties;
    }

    @Bean
    public STSClient stsClient() {
        STSClient stsClient = new STSClient(cxf());
        // ADFS does not respect SecondaryParameters set from RequestSecurityTemplate in policy, and it creates
        // SAML 1.1 tokens by default. By setting this property here the RequestSecurityToken request will contain a
        // direct TokenType element which ADFS do respect. Note that token type must not be specified in the template
        // then, as CXF then will ignore this property!
        stsClient.setTokenType("urn:oasis:names:tc:SAML:2.0:assertion");
        // Key type specifies which type of key should be used for encrypting and signing the token. Default is
        // symmetric according to WS-Trust.
        stsClient.setKeyType("http://docs.oasis-open.org/ws-sx/ws-trust/200512/SymmetricKey");
        stsClient.setWsdlLocation("https://thomasjohansen.it/adfs/services/trust/mex");
        stsClient.setEndpointQName(new QName(
                "http://schemas.microsoft.com/ws/2008/06/identity/securitytokenservice",
                "UserNameWSTrustBinding_IWSTrust13Async"
        ));
        stsClient.setServiceQName(new QName(
                "http://schemas.microsoft.com/ws/2008/06/identity/securitytokenservice",
                "SecurityTokenService"
        ));
        // This will ask for user name and password on the console and set them on the message context properties
        // 'ws-security.username'/'ws-security.password', which STSClient then will use for generating UsernameToken
        // which is requested by ADFS' policy on the endpoint configured above ('endpointName' property) -->
        stsClient.getOutInterceptors().add(new CredentialsInterceptor());
//        stsClient.setProperties(stsProperties());
        // ADFS 3.0 does not support the Renewing element on RST.
        stsClient.setSendRenewing(false);
        // ADFS 3.0 does not support the «Renew» RequestType on RST.
        stsClient.setAllowRenewing(false);
        // To enable Lifetime on RST. Not respected by ADFS 3.0, however.
//        stsClient.setEnableLifetime(true);
        // Calculates Expires on Lifetime. Value is in seconds.
//        stsClient.setTtl(300);
        return stsClient;
    }

    @Bean
    public JacksonJaxbJsonProvider jacksonJaxbJsonProvider() {
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(jsonMapper());
        return provider;
    }

    @Bean
    public ObjectMapper jsonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return mapper;
    }

}
