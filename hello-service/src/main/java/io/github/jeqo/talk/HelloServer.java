package io.github.jeqo.talk;

import brave.Tracing;
import brave.http.HttpTracing;
import brave.sampler.Sampler;
import com.google.gson.GsonBuilder;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.annotation.Default;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.annotation.Param;
import com.typesafe.config.ConfigFactory;

import static io.github.jeqo.talk.HelloService.DEFAULT_LANG;

public class HelloServer {
  public static void main(String[] args) {
    final var config = ConfigFactory.load();
    //Tracing instrumentation
    //final var sender = URLConnectionSender.newBuilder().build();
    //.endpoint(configuration.getZipkinEndpoint()).build();
    //final var reporter = AsyncReporter.builder(sender).build();
    final var tracing = Tracing.newBuilder().localServiceName("hello-service")
        .sampler(Sampler.ALWAYS_SAMPLE)
        //.spanReporter(reporter)
        .build();
    final var httpTracing = HttpTracing.newBuilder(tracing).build();
    //Preparing server
    final var translationClient = TranslationClient.create(config.getString("translation-service.url"));
    final var helloService = new HelloService(translationClient);
    final var gson = new GsonBuilder().setPrettyPrinting().create();
    final var server = new ServerBuilder()
        .http(8080)
        .service("/", (ctx, req) -> HttpResponse.of("Welcome to hello server"))
        .annotatedService(
            new Object() {
              @Get("/hello/{name}")
              public HttpResponse greet(@Param("name") String name,
                  @Param("lang") @Default(DEFAULT_LANG) String lang) {
                HelloRepresentation hello = helloService.sayHello(lang, name);
                String json = gson.toJson(hello);
                return HttpResponse.of(MediaType.JSON, json);
              }
            })
        .build();
    server.start().join();
  }

  static class Builder {

  }
}
