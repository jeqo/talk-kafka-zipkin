package io.github.jeqo.talk;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("hello")
public class HelloResource {

	private final HelloTranslationServiceClient translationServiceClient;

	HelloResource(HelloTranslationServiceClient translationServiceClient) {
		this.translationServiceClient = translationServiceClient;
	}

	@GET
	@Path("{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sayHello(@PathParam("name") final String name,
			@QueryParam("lang") final String lang) throws IOException {
		final String helloTranslated = translationServiceClient.translateHello(lang);
		final HelloRepresentation hello = new HelloRepresentation();
		hello.setHello(String.format("%s, %s", helloTranslated, name));
		hello.setLang("en");
		return Response.ok(hello).build();
	}

}
