package io.github.jeqo.talk;

public interface HelloService {
  String DEFAULT_LANG = "en";
  String HELLO_WORD = "hello";
  String DEFAULT_REPLY_TO = "you";

  HelloRepresentation sayHello(String lang, String name);

  static HelloService create(TranslationClient translationClient) {
    return new DefaultHelloService(translationClient);
  }

  class DefaultHelloService implements HelloService {
    final TranslationClient translationClient;

    DefaultHelloService(TranslationClient translationClient) {
      this.translationClient = translationClient;
    }

    /**
     *
     */
    public HelloRepresentation sayHello(String lang, String name) {
      //prepare hello
      String hello = translationClient.translate(lang, HELLO_WORD);
      if (hello == null) hello = HELLO_WORD;
      //prepare reply
      String replyTo = null;
      if (name != null) replyTo = name;
      if (name == null) replyTo =
        translationClient.translate(lang, DEFAULT_REPLY_TO);
      if (name == null) replyTo = DEFAULT_REPLY_TO;
      return new HelloRepresentation(
        String.format("%s, %s", hello, replyTo),
        lang
      );
    }
  }
}
