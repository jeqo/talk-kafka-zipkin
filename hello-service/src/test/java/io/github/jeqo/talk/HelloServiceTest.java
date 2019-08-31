package io.github.jeqo.talk;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloServiceTest {

  @Test void should_replyWithYou() {
    TranslationClient translationClient = (to, word) -> word;
    HelloService helloService = HelloService.create(translationClient);
    helloService.sayHello("es", null);
  }
}