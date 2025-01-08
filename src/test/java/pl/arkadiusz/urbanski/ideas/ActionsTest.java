package pl.arkadiusz.urbanski.ideas;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ActionsTest {

  @Test
  void shouldRecognizeAddAction() {
    Assertions.assertDoesNotThrow(() -> Actions.of("ADD"));
    Assertions.assertEquals(Actions.ADD, Actions.of("ADD"));
    Assertions.assertEquals(Actions.ADD, Actions.of("add"));
    Assertions.assertEquals(Actions.LIST, Actions.of("LIST"));
  }
}