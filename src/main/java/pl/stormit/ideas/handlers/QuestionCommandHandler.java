package pl.stormit.ideas.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import pl.stormit.ideas.dao.GenericDao;
import pl.stormit.ideas.input.UserInputCommand;
import pl.stormit.ideas.model.Category;
import pl.stormit.ideas.model.Question;

public class QuestionCommandHandler extends BaseCommandHandler {

  private static final Logger LOG = Logger.getLogger(QuestionCommandHandler.class.getName());
  private static final String COMMAND_NAME = "question";
  private final GenericDao<Question> questionDao;
  private final GenericDao<Category> categoryDao;

  public QuestionCommandHandler() {
    this.questionDao = new GenericDao<>("./questions.txt", new TypeReference<>() {
    });
    this.categoryDao = new GenericDao<>("./categories.txt", new TypeReference<>() {
    });
  }

  @Override
  protected String getCommandName() {
    return COMMAND_NAME;
  }

  @Override
  public void handle(UserInputCommand command) {
    if (command.getAction() == null) {
      throw new IllegalArgumentException(" Action can't be null ");
    }

    switch (command.getAction()) {
      case LIST:
        handleListAction(command);
        break;
      case ADD:
        handleAddAction(command);
        break;
      default:
        throw new IllegalArgumentException(String.format(" Unknown action: %s from command: %s ", command.getAction(), command.getCommand()));

    }
  }

  private void handleListAction(UserInputCommand command) {
    LOG.info(" List of questions... ");

    if (!command.getParam().isEmpty()) {
      throw new IllegalArgumentException(" Question list doesn't support additional params ");
    }

    List<Question> questions = questionDao.findAll();
    questions.forEach(System.out::println);
  }

  private void handleAddAction(UserInputCommand command) {
    LOG.info(" Add question ");

    if (command.getParam().isEmpty()) {
      throw new IllegalArgumentException(" No parameters provided for 'add' action ");
    }

    String[] params = splitParams(command.getParam());
    if (params.length != 2) {
      throw new IllegalArgumentException(" Please provide exactly two parameters in quotes: category and question");
    }

    String categoryName = params[0];
    String questionName = params[1];

    Category category = findCategoryByName(categoryName);
    questionDao.add(new Question(questionName, category));
  }

  private Category findCategoryByName(String categoryName) {
    return categoryDao.findOne(c -> c.getName().equalsIgnoreCase(categoryName.trim()))
        .orElseThrow(() -> new IllegalArgumentException(" Category not found " + categoryName));
  }

  private String[] splitParams(List<String> params) {
    String joined = String.join(" ", params);
    List<String> extracted = new ArrayList<>();
    int startIndex = 0;

    while ((startIndex = joined.indexOf("\"", startIndex)) != -1) {
      int endIndex = joined.indexOf("\"", startIndex + 1);
      if (endIndex == -1) {
        throw new IllegalArgumentException(" Mismatched quotes in command parameters ");
      }
      extracted.add(joined.substring(startIndex + 1, endIndex));
      startIndex = endIndex + 1;
    }

    if (extracted.size() != 2) {
      throw new IllegalArgumentException(" Please provide exactly two parameters in quotes: question and answer ");
    }
    return extracted.toArray(new String[0]);
  }
}
