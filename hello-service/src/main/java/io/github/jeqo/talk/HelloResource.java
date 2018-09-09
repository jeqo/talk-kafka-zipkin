package io.github.jeqo.talk;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("hello")
public class HelloResource {

  @GET
  @Path("{name}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response sayHello (@PathParam("name") final String name) {
    final HelloRepresentation hello = new HelloRepresentation();
    hello.setHello(String.format("%s, %s", "Hello", name));
    hello.setLang("en");
    return Response.ok(hello).build();
  }
}
