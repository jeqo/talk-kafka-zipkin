package io.github.jeqo.talk;

import java.util.HashMap;
import java.util.Map;

public class TranslationRepository {

  public static Map<String, String> helloTranslations =
      new HashMap<>();
  static {
    helloTranslations.put("en", "Hello");
    helloTranslations.put("es", "Hola");
    helloTranslations.put("no", "Hallo");
  }
}
