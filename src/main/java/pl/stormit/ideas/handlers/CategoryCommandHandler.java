package pl.stormit.ideas.handlers;

import java.util.List;
import pl.stormit.ideas.dao.CategoryDao;
import pl.stormit.ideas.input.UserInputCommand;
import pl.stormit.ideas.model.Category;

public class CategoryCommandHandler extends BaseCommandHandler {

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

    switch ((command.getAction())) {
      case LIST:
        System.out.println("List of categories...");
        List<Category> categories = categoryDao.findAll();
        categories.forEach(System.out::println);
        break;

      case ADD:
        System.out.println("Add category");
        String categoryName = command.getParam().get(0);
        categoryDao.add(new Category(categoryName));
        break;

      default: {
        throw new IllegalArgumentException(String.format("Unknown action: %s from command: %s", command.getAction(), command.getCommand()));

      }
    }

  }
}
