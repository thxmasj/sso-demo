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
import org.apache.cxf.ws.security.trust.STSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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

//    @Autowired
//    private MerchantProvisioningAudit wsService;
    @Autowired
    private HelloResource rsService;
//    @Autowired
//    private ConnectionFactory jmsConnectionFactory;

    @Bean
    public Bus cxf() {
        return new SpringBus();
    }

    @Bean
    public Server jaxRsService() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBus(cxf());
        factory.setProvider(jacksonJaxbJsonProvider());
        factory.setServiceBean(rsService);
        factory.setAddress("/rs");
        return factory.create();
    }

    @Bean
    public HelloPort webServiceClient() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setBus(cxf());
        factory.setServiceClass(HelloPort.class);
//        factory.setAddress("https://localhost:8080/hello");
        factory.setAddress("https://localhost:8084/app/provisioning/merchant");
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
        stsClient.setWsdlLocation("https://federation.test.payex.com/adfs/services/trust/mex");
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

//    @Bean
//    public Server jaxWsService() {
//        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
//        factory.setBus(cxf());
//        factory.setServiceClass(MerchantProvisioningAuditWebService.class);
//        factory.setServiceBean(wsService);
//        factory.setAddress("/");
//        factory.setProperties(jaxwsProperties());
//        return factory.create();
//    }

//    @Bean
//    public Object jaxWsJmsService() {
//        EndpointImpl ep = (EndpointImpl) Endpoint.create(wsService);
//        ep.getFeatures().add(new ConnectionFactoryFeature(jmsConnectionFactory));
//        ep.publish("jms:queue:PosPay.Provisioning.Merchant.Notify");
//        return null;
//    }

//    private Map<String, Object> jaxwsProperties() {
//        Map<String, Object> properties = new HashMap<>();
//        // This specifies the key used for decrypting symmetric key in SAML tokens
//        properties.put("ws-security.signature.properties", "/symmetric-key-crypto.properties");
//        // Password used for key in keystore above is supplied by this callback handler (not Merlin props, which
//        // provides password for keystore) TODO: Try with different passwords!
//        properties.put("ws-security.callback-handler", "com.payex.provisioning.merchant.ws.PasswordCallbackHandler");
//        // Microsoft Active Directory Federation Services 3.0 is not compliant with Basic Security Profile
//        // rule 5426. See https://issues.apache.org/jira/browse/WSS-519.
//        properties.put("ws-security.is-bsp-compliant", "false");
//        return properties;
//    }

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