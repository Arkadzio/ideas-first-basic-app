package pl.arkadiusz.urbanski.ideas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.arkadiusz.urbanski.ideas.handlers.CategoryCommandHandler;
import pl.arkadiusz.urbanski.ideas.handlers.QuestionCommandHandler;
import pl.arkadiusz.urbanski.ideas.handlers.AnswerCommandHandler;
import pl.arkadiusz.urbanski.ideas.handlers.CommandHandler;
import pl.arkadiusz.urbanski.ideas.handlers.HelpCommandHandler;
import pl.arkadiusz.urbanski.ideas.handlers.QuitCommandHandler;
import pl.arkadiusz.urbanski.ideas.input.UserInputCommand;
import pl.arkadiusz.urbanski.ideas.input.UserInputManager;

public class IdeasApp {

  private static final Logger LOG = Logger.getLogger(IdeasApp.class.getName());

  public static void main(String[] args) {
    new IdeasApp().start();
  }

  public void runApplication() {
    start();
  }

  private void start() {
    LOG.info("Start app...");

    boolean applicationLoop = true;
    UserInputManager userInputManager = new UserInputManager();

    List<CommandHandler> handlers = new ArrayList<>();
    handlers.add(new HelpCommandHandler());
    handlers.add(new QuitCommandHandler());
    handlers.add(new CategoryCommandHandler());
    handlers.add(new QuestionCommandHandler());
    handlers.add(new AnswerCommandHandler());

    while (applicationLoop) {
      try {
        UserInputCommand userInputCommand = userInputManager.nextCommand();
        LOG.info(userInputCommand.toString());

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
        LOG.info("Quit...");
        applicationLoop = false;

      } catch (IllegalArgumentException e) {
        LOG.log(Level.WARNING, "Validation exception" + e.getMessage());

      } catch (Exception e) {
        LOG.log(Level.SEVERE, "Unknown error", e);
      }
    }
  }
}
