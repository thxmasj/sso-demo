package it.thomasjohansen.hello.service;

import it.thomasjohansen.hello.HelloPort;
import it.thomasjohansen.hello.types.HelloRequest;
import it.thomasjohansen.hello.types.HelloResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * @author thomas@thomasjohansen.it
 */
@Path("/")
@Service
@Produces("application/json")
public class HelloResource {

    @Autowired
    private HelloPort service;

    @POST
    @Path("/hello")
    public Response sayHello(String greeting) {
        HelloRequest request = new HelloRequest();
        request.setGreeting(greeting);
        HelloResponse response = service.sayHello(request);
        return Response.ok(response).build();
    }

}
