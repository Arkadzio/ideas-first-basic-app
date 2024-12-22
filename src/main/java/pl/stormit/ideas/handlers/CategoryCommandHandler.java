package pl.stormit.ideas.handlers;

import java.util.List;
import java.util.logging.Logger;
import pl.stormit.ideas.dao.CategoryDao;
import pl.stormit.ideas.input.UserInputCommand;
import pl.stormit.ideas.model.Category;

public class CategoryCommandHandler extends BaseCommandHandler {

  private static Logger LOG = Logger.getLogger(CategoryCommandHandler.class.getName());
  private static final String COMMAND_NAME = "category";
  private CategoryDao categoryDao;

  public CategoryCommandHandler() {
    this.categoryDao = new CategoryDao();
  }

  @Override
  protected String getCommandName() {
    return COMMAND_NAME;
  }

  @Override
  public void handle(UserInputCommand command) {
    if (command.getAction() == null) {
      throw new IllegalArgumentException("action can't be null");
    }

    switch (command.getAction()) {
      case LIST:
        LOG.info("List of categories...");

        if (!command.getParam().isEmpty()) {
          throw new IllegalArgumentException(" category list doesn't support additional params");
        }

        List<Category> categories = categoryDao.findAll();
        categories.forEach(System.out::println);
        break;

      case ADD:
        LOG.info("Add category");

        if (command.getParam().size() != 1) {
          throw new IllegalArgumentException(" wrong command format. Check help for more info");
        }

        String categoryName = command.getParam().get(0);
        categoryDao.add(new Category(categoryName));
        break;

      default: {
        throw new IllegalArgumentException(String.format("Unknown action: %s from command: %s", command.getAction(), command.getCommand()));

      }
    }
  }
}
