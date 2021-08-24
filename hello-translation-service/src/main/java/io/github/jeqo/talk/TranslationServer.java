package io.github.jeqo.talk;

import brave.Tracing;
import brave.http.HttpTracing;
import brave.sampler.Sampler;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.brave.RequestContextCurrentTraceContext;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.brave.BraveService;
import com.typesafe.config.ConfigFactory;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

public class TranslationServer {
  public static void main(String[] args) {
    final var config = ConfigFactory.load();
    //Tracing instrumentation
    final var sender = URLConnectionSender.newBuilder()
        .endpoint(config.getString("zipkin.http.url")).build();
    final var reporter = AsyncReporter.builder(sender).build();
    final var tracing = Tracing.newBuilder()
        .localServiceName("translation-service")
        .sampler(Sampler.ALWAYS_SAMPLE)
        .spanReporter(reporter)
        .currentTraceContext(RequestContextCurrentTraceContext.ofDefault())
        .build();
    final var httpTracing = HttpTracing.newBuilder(tracing).build();
    //Preparing server
    final var store = TranslationStore.create();
    final var server = new ServerBuilder()
        .http(config.getInt("http.port"))
        .decorator(BraveService.newDecorator(httpTracing))
        .service("/", (ctx, req) -> HttpResponse.of("Welcome to translation server"))
        .service("/translate/{to}/{word}",
            (ctx, req) ->
                HttpResponse.of(store.find(ctx.pathParam("to"), ctx.pathParam("word"))))
        .build();
    server.start().join();
  }
}
