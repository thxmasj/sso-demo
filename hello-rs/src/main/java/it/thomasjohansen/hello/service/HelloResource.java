package it.thomasjohansen.hello.service;

import it.thomasjohansen.hello.HelloPort;
import it.thomasjohansen.hello.types.HelloRequest;
import it.thomasjohansen.hello.types.HelloResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.xml.ws.WebServiceContext;

/**
 * @author thomas@thomasjohansen.it
 */
@Path("/")
@Service
@Produces("application/json; charset=UTF-8")
public class HelloResource {

    @Autowired
    private HelloPort service;
    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/hello")
    public Response sayHello(
            @QueryParam("greeting") String greeting
    ) {
        HelloRequest request = new HelloRequest();
        request.setGreeting(greeting);
//        HelloResponse response = service.sayHello(request);
        HelloResponse response = new HelloResponse();
        response.setGreetingResponse(String.format(
                "Well, «%s» back to you, %s!",
                greeting,
                securityContext != null ? securityContext.getUserPrincipal().getName() : "stranger"
        ));
        return Response.ok(response).build();
    }

}
