package pl.arkadiusz.urbanski.ideas.input;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.arkadiusz.urbanski.ideas.Actions;

class UserInputCommandTest {

  @Test
  void shouldParseCommandAndActionCorrectly() {
    // given
    String input = "CREATE ADD param1 param2";

    // when
    UserInputCommand userInputCommand = new UserInputCommand(input);

    // then
    assertNotNull(userInputCommand);
    assertEquals("CREATE", userInputCommand.getCommand());
    assertEquals(Actions.ADD, userInputCommand.getAction());
    assertEquals(List.of("param1", "param2"), userInputCommand.getParam());
  }

  @Test
  void shouldParseCommandWithoutAction() {
    // given
    String input = "CREATE";

    // when
    UserInputCommand userInputCommand = new UserInputCommand(input);

    // then
    assertNotNull(userInputCommand);
    assertEquals("CREATE", userInputCommand.getCommand());
    assertNull(userInputCommand.getAction());
    assertTrue(userInputCommand.getParam().isEmpty());
  }

  @Test
  void shouldParseCommandWithActionOnly() {
    // given
    String input = "UPDATE LIST";

    // when
    UserInputCommand userInputCommand = new UserInputCommand(input);

    // then
    Assertions.assertNotNull(userInputCommand);
    Assertions.assertEquals("UPDATE", userInputCommand.getCommand());
    Assertions.assertEquals(Actions.LIST, userInputCommand.getAction());
    Assertions.assertTrue(userInputCommand.getParam().isEmpty());
  }

  @Test
  void shouldHandleEmptyString() {
    // given
    String input = "";

    // when
    UserInputCommand userInputCommand = new UserInputCommand(input);

    // then
    Assertions.assertNotNull(userInputCommand);
    Assertions.assertNull(userInputCommand.getCommand());
    Assertions.assertNull(userInputCommand.getAction());
  }

  @Test
  void shouldHandleNullInput() {
    // given
    String input = null;

    // when
    UserInputCommand userInputCommand = new UserInputCommand(input);

    // then
    Assertions.assertNotNull(userInputCommand);
    Assertions.assertNull(userInputCommand.getCommand());
    Assertions.assertTrue(userInputCommand.getParam().isEmpty());
  }

  @Test
  void shouldThrowExceptionForMismatchedQuotes() {
    // given
    String input = "CREATE ADD \"param1 param2";

    // when & then
    IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new UserInputCommand(input);
    });
    Assertions.assertEquals("Mismatched quotes in input", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForUnknownAction() {
    // given
    String input = "CREATE UNKNOWN param1";

    // when & then
    IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new UserInputCommand(input);
    });
    assertEquals("Unknown action: UNKNOWN", exception.getMessage().trim());
  }
}