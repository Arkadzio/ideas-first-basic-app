package pl.stormit.ideas.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import pl.stormit.ideas.model.Category;

public class CategoryDao {

  private final GenericDao<Category> genericDao;

  public CategoryDao() {
    this.genericDao = new GenericDao<>("./categories.txt", new TypeReference<>() {
    });
  }

  public void add(Category category) {
    genericDao.add(category);
  }

  public List<Category> findAll() {
    return genericDao.findAll();
  }

  public void logAllCategories() {
    genericDao.findAll().forEach(c -> System.out.println(" Category: " + c.getName()));
  }
}
