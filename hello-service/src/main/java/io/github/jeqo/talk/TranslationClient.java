package io.github.jeqo.talk;

import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import java.nio.charset.Charset;

import static io.github.jeqo.talk.HelloService.DEFAULT_LANG;

public interface TranslationClient {
  String translate(String to, String word);

  static TranslationClient create(String uri) {
    return new DefaultTranslationClient(uri);
  }

  class DefaultTranslationClient implements TranslationClient {
    final HttpClient httpClient;

    public DefaultTranslationClient(String uri) {
      this.httpClient = HttpClient.of(uri);
    }

    @Override public String translate(String to, String word) {
      try {
        AggregatedHttpResponse response =
            httpClient.get(String.format("/translate/%s/%s/%s", DEFAULT_LANG, to, word))
                .aggregate()
                .join();
        return response.content(Charset.defaultCharset());
      } catch (Exception e) {
        //TODO log exception
        return null;
      }
    }
  }
}
