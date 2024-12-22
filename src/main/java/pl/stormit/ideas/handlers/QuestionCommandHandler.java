package pl.stormit.ideas.handlers;

import java.util.List;
import java.util.logging.Logger;
import pl.stormit.ideas.dao.CategoryDao;
import pl.stormit.ideas.dao.QuestionDao;
import pl.stormit.ideas.input.UserInputCommand;
import pl.stormit.ideas.model.Category;
import pl.stormit.ideas.model.Question;

public class QuestionCommandHandler extends BaseCommandHandler {

  private static Logger LOG = Logger.getLogger(QuestionCommandHandler.class.getName());
  private static final String COMMAND_NAME = "question";
  private QuestionDao questionDao;
  private CategoryDao categoryDao;

  public QuestionCommandHandler() {
    this.questionDao = new QuestionDao();
    this.categoryDao = new CategoryDao();
  }

  @Override
  protected String getCommandName() {
    return COMMAND_NAME;
  }

  @Override
  public void handle(UserInputCommand command) {
    if (command.getAction() == null) {
      throw new IllegalArgumentException(" action can't be null ");
    }

    switch ((command.getAction())) {
      case LIST:
        LOG.info("List of questions...");

        if (!command.getParam().isEmpty()) {
          throw new IllegalArgumentException(" category list doesn't support additional params ");
        }

        List<Question> questions = questionDao.findAll();
        questions.forEach(System.out::println);
        break;

      case ADD:
      LOG.info("Add question");

        if (command.getParam().size() != 2) {
          throw new IllegalArgumentException(" wrong command format. Check help for more info ");
        }

        String categoryName = command.getParam().get(0);
        String questionName = command.getParam().get(1);

        Category category = categoryDao.findOne(categoryName)
            .orElseThrow(() -> new IllegalArgumentException(" Category not found " + categoryName));

        questionDao.add(new Question(questionName, category));
        break;

      default: {
        throw new IllegalArgumentException(String.format("Unknown action: %s from command: %s", command.getAction(), command.getCommand()));

      }
    }

  }
}
