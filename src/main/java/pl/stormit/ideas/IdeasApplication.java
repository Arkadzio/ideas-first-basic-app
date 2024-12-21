package pl.stormit.ideas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import pl.stormit.ideas.handlers.CategoryCommandHandler;
import pl.stormit.ideas.handlers.CommandHandler;
import pl.stormit.ideas.handlers.HelpCommandHandler;
import pl.stormit.ideas.handlers.QuestionCommandHandler;
import pl.stormit.ideas.handlers.QuitCommandHandler;
import pl.stormit.ideas.input.UserInputCommand;
import pl.stormit.ideas.input.UserInputManager;

public class IdeasApplication {

  public static void main(String[] args) {
    new IdeasApplication().start();
  }

  private void start() {
    System.out.println("Start app..");

    boolean applicationLoop = true;
    UserInputManager userInputManager = new UserInputManager();

    List<CommandHandler> handlers = new ArrayList<>();
    handlers.add(new HelpCommandHandler());
    handlers.add(new QuitCommandHandler());
    handlers.add(new CategoryCommandHandler());
    handlers.add(new QuestionCommandHandler());

    while (applicationLoop) {
      try {
        UserInputCommand userInputCommand = userInputManager.nextCommand();
        System.out.println(userInputCommand);

        Optional<CommandHandler> currentHandler = Optional.empty();
        for (CommandHandler handler : handlers) {
          if (handler.supports(userInputCommand.getCommand())) {
            currentHandler = Optional.of(handler);
            break;
          }
        }
        currentHandler
            .orElseThrow(() -> new IllegalArgumentException("Unknown handler: " + userInputCommand.getCommand()))
            .handle(userInputCommand);

      } catch (QuitIdeasApplicationException e) {
        System.out.println("Quit...");
        applicationLoop = false;

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
