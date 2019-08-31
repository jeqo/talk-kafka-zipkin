package io.github.jeqo.talk;

import javax.annotation.Nullable;

public class HelloService {
  public static final String DEFAULT_LANG = "en";
  public static final String HELLO_WORD = "hello";
  public static final String DEFAULT_REPLY_TO = "you";
  final TranslationClient translationClient;

  public HelloService(TranslationClient translationClient) {
    this.translationClient = translationClient;
  }

  /**
   *
   */
  public HelloRepresentation sayHello(String lang, @Nullable String name) {
    //prepare hello
    String hello = translationClient.translate(HELLO_WORD, lang);
    if (hello == null) hello = HELLO_WORD;
    //prepare reply
    String replyTo = null;
    if (name != null) replyTo = name;
    if (name == null) replyTo = translationClient.translate(DEFAULT_REPLY_TO, lang);
    if (name == null) replyTo = DEFAULT_REPLY_TO;
    return new HelloRepresentation(String.format("%s, %s", hello, replyTo), lang);
  }
}
