package it.thomasjohansen.hello.service;

import it.thomasjohansen.hello.HelloPort;
import it.thomasjohansen.hello.types.HelloRequest;
import it.thomasjohansen.hello.types.HelloResponse;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

/**
 * @author thomas@thomasjohansen.it
 */
public class HelloService implements HelloPort {

    @Resource
    private WebServiceContext webServiceContext;

    public HelloResponse sayHello(HelloRequest request) {
        HelloResponse response = new HelloResponse();
        String greeter = webServiceContext.getUserPrincipal().getName();
        response.setGreetingResponse(String.format(
                "Hi, how are you %s?",
                greeter
        ));
        return response;
    }

}
