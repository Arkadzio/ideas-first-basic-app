package pl.stormit.ideas.handlers;

import java.util.List;
import java.util.logging.Logger;
import pl.stormit.ideas.dao.CategoryDao;
import pl.stormit.ideas.input.UserInputCommand;
import pl.stormit.ideas.model.Category;

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

    if (command.getParam() == null || command.getParam().size() != 1) {
      throw new IllegalArgumentException(" wrong command format. Provide exactly one parameter. Check help for more info ");
    }

    String categoryName = command.getParam().get(0).trim();
    if (categoryName.isEmpty()) {
      throw new IllegalArgumentException(" Category can't be empty");
    }

    boolean categoryExists = categoryDao.findAll().stream()
        .anyMatch(category -> category.getName().equalsIgnoreCase(categoryName));

    if (categoryExists) {
      throw new IllegalArgumentException(" Category already exists: " + categoryName);
    }

    categoryDao.add(new Category(categoryName));
    LOG.info(" Category added: " + categoryName);
  }
}
