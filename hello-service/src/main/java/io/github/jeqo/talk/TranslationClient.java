package io.github.jeqo.talk;

import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface TranslationClient {
  String translate(String to, String word);

  static TranslationClient create(HttpClient httpClient) {
    return new DefaultTranslationClient(httpClient);
  }

  class DefaultTranslationClient implements TranslationClient {
    static final Logger LOG = LoggerFactory.getLogger(DefaultTranslationClient.class);

    final HttpClient httpClient;

    public DefaultTranslationClient(HttpClient httpClient) {
      this.httpClient = httpClient;
    }

    @Override public String translate(String to, String word) {
      try {
        AggregatedHttpResponse response =
            httpClient.get(String.format("/translate/%s/%s", to, word))
                .aggregate()
                .join();
        if (response.status().equals(HttpStatus.OK)) {
          return response.content(Charset.defaultCharset());
        } else {
          LOG.error("Error calling translation service: {}", response.content());
          return null;
        }
      } catch (Exception e) {
        LOG.error("Error calling translation service", e);
        return null;
      }
    }
  }
}
