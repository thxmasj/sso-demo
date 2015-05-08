package it.thomasjohansen.hello.ws;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * Created by thomas on 23.01.15.
 */
public class PasswordCallbackHandler implements CallbackHandler {

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback)
                handle((WSPasswordCallback)callback);
        }

    }

    private void handle(WSPasswordCallback callback) {
        callback.setPassword("changeit");
    }

}
