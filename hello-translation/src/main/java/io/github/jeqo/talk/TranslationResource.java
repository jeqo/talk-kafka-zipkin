package io.github.jeqo.talk;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static io.github.jeqo.talk.TranslationRepository.helloTranslations;

@Path("translate")
public class TranslationResource {
  @GET
  @Path("{lang}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response translateHello(@PathParam("lang") final String lang) {
    final String hello = helloTranslations.getOrDefault(lang, helloTranslations.get("en"));
    return Response.ok(hello).build();
  }

}
