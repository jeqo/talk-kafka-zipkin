package io.github.jeqo.talk;

import brave.Tracing;
import brave.http.HttpTracing;
import brave.sampler.Sampler;
import com.google.gson.GsonBuilder;
import com.linecorp.armeria.client.HttpClientBuilder;
import com.linecorp.armeria.client.brave.BraveClient;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.common.brave.RequestContextCurrentTraceContext;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.annotation.Default;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.annotation.Param;
import com.linecorp.armeria.server.brave.BraveService;
import com.typesafe.config.ConfigFactory;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import static io.github.jeqo.talk.HelloService.DEFAULT_LANG;

public class HelloServer {
  public static void main(String[] args) {
    final var config = ConfigFactory.load();
    //Tracing instrumentation
    final var sender = URLConnectionSender.newBuilder()
        .endpoint(config.getString("zipkin.http.url")).build();
    final var reporter = AsyncReporter.builder(sender).build();
    final var tracing = Tracing.newBuilder()
        .localServiceName("hello-service")
        .sampler(Sampler.ALWAYS_SAMPLE)
        .spanReporter(reporter)
        .currentTraceContext(RequestContextCurrentTraceContext.ofDefault())
        .build();
    final var httpTracing = HttpTracing.newBuilder(tracing).build();
    //Preparing server
    final var httpClient = new HttpClientBuilder(config.getString("translation-service.url"))
        .decorator(BraveClient.newDecorator(httpTracing.clientOf("translation-service")))
        .build();
    final var translationClient = TranslationClient.create(httpClient);
    final var helloService = HelloService.create(translationClient);
    final var gson = new GsonBuilder().setPrettyPrinting().create();
    final var server = new ServerBuilder()
        .http(config.getInt("http.port"))
        .decorator(BraveService.newDecorator(httpTracing))
        .service("/", (ctx, req) -> HttpResponse.of("Welcome to hello server"))
        .annotatedService(
            new Object() {
              @Get("/hello/{name}")
              public HttpResponse greet(@Param("name") String name,
                  @Param("lang") @Default(DEFAULT_LANG) String lang) {
                try {
                  HelloRepresentation hello = helloService.sayHello(lang, name);
                  String json = gson.toJson(hello);
                  return HttpResponse.of(MediaType.JSON, json);
                } catch (Exception e) {
                  e.printStackTrace();
                  return HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR,
                      MediaType.PLAIN_TEXT_UTF_8, e.getMessage());
                }
              }
            })
        .build();
    server.start().join();
  }

  static class Builder {

  }
}
