package io.github.jeqo.talk;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

interface TranslationStore {

  static TranslationStore create() {
    return new DefaultTranslationStore();
  }

  String find(String to, String word);

  class DefaultTranslationStore implements TranslationStore {
    private final static Map<String, String> helloTranslations = new HashMap<>();
    private final static Map<String, String> youTranslations = new HashMap<>();
    private final static Map<String, Map<String, String>> translations = new HashMap<>();

    static {
      youTranslations.put("en", "u");
      youTranslations.put("es", "tu");
      youTranslations.put("no", "du");

      helloTranslations.put("en", "Hello");
      helloTranslations.put("es", "Hola");
      helloTranslations.put("no", "Hallo");

      translations.put("hello", helloTranslations);
      translations.put("you", youTranslations);
    }

    @Override public String find(String to, String word) {
      String translation = null;
      try {
        TimeUnit.MILLISECONDS.sleep(new Random().nextInt(100) + 100);
        translation = translations.getOrDefault(word, Collections.emptyMap())
            .getOrDefault(to, null);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return translation;
    }
  }
}