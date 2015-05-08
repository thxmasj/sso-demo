package it.thomasjohansen.hello.ws;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptor;
import org.apache.cxf.ws.security.SecurityConstants;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class CredentialsInterceptor implements PhaseInterceptor<Message> {

    @Override
    public void handleMessage(Message message) throws Fault {
        message.put(SecurityConstants.USERNAME, "thj");
        message.put(SecurityConstants.PASSWORD, "x");
    }

    @Override
    public void handleFault(Message message) {
    }

    @Override
    public Set<String> getAfter() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getBefore() {
        return Collections.emptySet();
    }

    @Override
    public String getId() {
        return getClass().getName();
    }

    @Override
    public String getPhase() {
        return Phase.PRE_PROTOCOL;
    }

    @Override
    public Collection<PhaseInterceptor<? extends Message>> getAdditionalInterceptors() {
        return null;
    }
}
