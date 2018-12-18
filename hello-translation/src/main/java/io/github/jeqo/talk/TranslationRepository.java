package io.github.jeqo.talk;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

class TranslationRepository {

	private final static Map<String, String> helloTranslations = new HashMap<>();

	private static final String DEFAULT_LANG = "en";

	static {
		helloTranslations.put("en", "Hello");
		helloTranslations.put("es", "Hola");
		helloTranslations.put("no", "Hallo");
	}

	String find(String lang) {
		String hello;
		try {
			TimeUnit.MILLISECONDS.sleep(new Random().nextInt(100) + 100);
			hello = helloTranslations.getOrDefault(lang, helloTranslations.get("en"));
		}
		catch (InterruptedException e) {
			e.printStackTrace();
			hello = helloTranslations.get(DEFAULT_LANG);
		}
		return hello;
	}

}
