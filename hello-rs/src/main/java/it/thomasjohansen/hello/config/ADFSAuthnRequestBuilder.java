package it.thomasjohansen.hello.config;

import org.apache.cxf.message.Message;
import org.apache.cxf.rs.security.saml.sso.DefaultAuthnRequestBuilder;
import org.apache.cxf.rs.security.saml.sso.SamlpRequestComponentBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;

import java.util.Collections;

/**
 * AuthnRequestBuilder that works with Microsoft ADFS 3.0:
 * <ul>
 *     <li>NameIDPolicy is set to transient (default is persistent)</li>
 *     <li>ID starts with _ (ADFS does not accept first char numeric)</li>
 * </ul>
 *
 * @author thomas@thomasjohansen.it
 */
public class ADFSAuthnRequestBuilder extends DefaultAuthnRequestBuilder {

    @Override
    public AuthnRequest createAuthnRequest(
            Message message,
            String issuerId,
            String assertionConsumerServiceAddress
    ) throws Exception {
        Issuer issuer =
                SamlpRequestComponentBuilder.createIssuer(issuerId);

        NameIDPolicy nameIDPolicy = null;
//                SamlpRequestComponentBuilder.createNameIDPolicy(
//                        true, "urn:oasis:names:tc:SAML:2.0:nameid-format:transient", issuerId
//                );

        AuthnContextClassRef authnCtxClassRef =
                SamlpRequestComponentBuilder.createAuthnCtxClassRef(
                        "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport"
                );
        RequestedAuthnContext authnCtx =
                SamlpRequestComponentBuilder.createRequestedAuthnCtxPolicy(
                        AuthnContextComparisonTypeEnumeration.EXACT,
                        Collections.singletonList(authnCtxClassRef), null
                );

        AuthnRequest request = SamlpRequestComponentBuilder.createAuthnRequest(
                assertionConsumerServiceAddress,
                isForceAuthn(),
                isPassive(),
                getProtocolBinding(),
                SAMLVersion.VERSION_20,
                issuer,
                nameIDPolicy,
                authnCtx
        );
        request.setID('_' + request.getID());
        return request;
    }

}