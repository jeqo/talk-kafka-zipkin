package io.github.jeqo.talk;

import brave.ScopedSpan;
import brave.Tracer;
import brave.Tracing;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("translate")
public class TranslationResource {

	private final Tracer tracer;

	private final TranslationRepository repository;

	TranslationResource(TranslationRepository repository) {
		this.repository = repository;
		tracer = Tracing.currentTracer();
	}

	@GET
	@Path("{lang}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response translateHello(@PathParam("lang") final String lang) {
		final ScopedSpan span = tracer.startScopedSpan("query-repository");
		try {
			span.annotate("query-started");
			span.tag("lang", Optional.ofNullable(lang).orElse(""));
			final String hello = repository.find(lang);
			span.annotate("query-finished");
			return Response.ok(hello).build();
		}
		catch (RuntimeException | Error e) {
			span.error(e);
			throw e;
		}
		finally {
			span.finish();
		}
	}

}
