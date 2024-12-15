package pl.stormit.ideas.input;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserInputCommandTest {

  @Test
  void shouldBuildCorrectUserInputCommand() {
    //given
    String input = "category add CategoryName";

    //when
    UserInputCommand userInputCommand = new UserInputCommand(input);

    //then
    Assertions.assertEquals("category", userInputCommand.getCommand());
    Assertions.assertEquals("add", userInputCommand.getAction());
    Assertions.assertLinesMatch(List.of("CategoryName"), userInputCommand.getParam());

  }

  @Test
  void shouldBuildCorrectUserInputCommandWithMultipleParams() {
    //given
    String input = "command action param1 param2 param3";

    //when
    UserInputCommand userInputCommand = new UserInputCommand(input);

    //then
    Assertions.assertEquals("command", userInputCommand.getCommand());
    Assertions.assertEquals("action", userInputCommand.getAction());
    Assertions.assertLinesMatch(List.of("param1", "param2", "param3"), userInputCommand.getParam());
  }

  @Test
  void shouldBuildCorrectUserInputCommandWithoutParams() {
    //given
    String input = "command action";

    //when
    UserInputCommand userInputCommand = new UserInputCommand(input);

    //then
    Assertions.assertEquals("command", userInputCommand.getCommand());
    Assertions.assertEquals("action", userInputCommand.getAction());
    Assertions.assertEquals(0, userInputCommand.getParam().size());
  }

}