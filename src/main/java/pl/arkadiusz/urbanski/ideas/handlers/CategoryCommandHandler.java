package pl.arkadiusz.urbanski.ideas.handlers;

import java.util.List;
import java.util.logging.Logger;
import pl.arkadiusz.urbanski.ideas.dao.CategoryDao;
import pl.arkadiusz.urbanski.ideas.input.UserInputCommand;
import pl.arkadiusz.urbanski.ideas.model.Category;

public class CategoryCommandHandler extends BaseCommandHandler {

  private static final Logger LOG = Logger.getLogger(CategoryCommandHandler.class.getName());
  private static final String COMMAND_NAME = "category";
  private final CategoryDao categoryDao;

  public CategoryCommandHandler() {
    this.categoryDao = new CategoryDao();
  }

  @Override
  protected String getCommandName() {
    return COMMAND_NAME;
  }

  @Override
  public void handle(UserInputCommand command) {
    if (command == null || command.getAction() == null) {
      throw new IllegalArgumentException(" Command and action can't be null ");
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
    LOG.info("List of categories...");

    if (command.getParam() != null && !command.getParam().isEmpty()) {
      throw new IllegalArgumentException(" category list doesn't support additional params ");
    }

    List<Category> categories = categoryDao.findAll();
    categories.forEach(System.out::println);
  }

  private void handleAddAction(UserInputCommand command) {
    LOG.info(" Add category ");

    List<String> params = command.getParam();
    if (params == null || params.isEmpty()) {
      throw new IllegalArgumentException(" No parameters provided for 'add' action ");
    }

    String categoryName = extractSingleQuotedParam(params);
    if (categoryName == null || categoryName.isEmpty()) {
      throw new IllegalArgumentException(" Category cannot be empty ");
    }

    boolean categoryExists = categoryDao.findAll().stream()
        .anyMatch(category -> category.getName().equalsIgnoreCase(categoryName));

    if (categoryExists) {
      throw new IllegalArgumentException(" Category already exists: " + categoryName);
    }

    categoryDao.add(new Category(categoryName));
    LOG.info(" Category added: " + categoryName);
  }

  protected String extractSingleQuotedParam(List<String> params) {
    String joinedParams = String.join(" ", params).trim();
    if (joinedParams.startsWith("\"") && joinedParams.endsWith("\"")) {
      return joinedParams.substring(1, joinedParams.length() - 1).trim();
    }
    throw new IllegalArgumentException(" Invalid parameter format. Expected a single quoted string. ");
  }
}
