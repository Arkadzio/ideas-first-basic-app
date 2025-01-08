package pl.arkadiusz.urbanski.ideas.handlers;

import pl.arkadiusz.urbanski.ideas.QuitIdeasApplicationException;
import pl.arkadiusz.urbanski.ideas.input.UserInputCommand;

public class QuitCommandHandler extends BaseCommandHandler {
  public static final String COMMAND_NAME = "quit";

  @Override
  public void handle(UserInputCommand command) {
   throw new QuitIdeasApplicationException();
  }

  @Override
  protected String getCommandName() {
    return COMMAND_NAME;
  }
}