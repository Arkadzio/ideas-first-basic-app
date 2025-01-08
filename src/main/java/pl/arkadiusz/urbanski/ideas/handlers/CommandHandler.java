package pl.arkadiusz.urbanski.ideas.handlers;

import pl.arkadiusz.urbanski.ideas.input.UserInputCommand;

public interface CommandHandler {

  void handle(UserInputCommand command);

  boolean supports(String name);
}
