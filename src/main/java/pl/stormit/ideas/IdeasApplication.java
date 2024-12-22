package pl.stormit.ideas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.stormit.ideas.handlers.AnswerCommandHandler;
import pl.stormit.ideas.handlers.CategoryCommandHandler;
import pl.stormit.ideas.handlers.CommandHandler;
import pl.stormit.ideas.handlers.HelpCommandHandler;
import pl.stormit.ideas.handlers.QuestionCommandHandler;
import pl.stormit.ideas.handlers.QuitCommandHandler;
import pl.stormit.ideas.input.UserInputCommand;
import pl.stormit.ideas.input.UserInputManager;

public class IdeasApplication {

  private static Logger LOG = Logger.getLogger(IdeasApplication.class.getName());

  public static void main(String[] args) {
    new IdeasApplication().start();
  }

  private void start() {
    LOG.info("Start app..");

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
