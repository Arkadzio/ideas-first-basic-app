package pl.stormit.ideas.handlers;

import pl.stormit.ideas.input.UserInputCommand;

public class HelpCommandHandler extends BaseCommandHandler {

  public static final String COMMAND_NAME = "help";

  @Override
  public void handle(UserInputCommand command) {
    System.out.println("Help...");
    System.out.println("Allowed commands: help, quit, category, question, answer");
    System.out.println("Command pattern: <command> <action> <param1> <param2>");
    System.out.println("Example: category add CategoryName");
  }

  @Override
  protected String getCommandName() {
    return COMMAND_NAME;
  }

}
