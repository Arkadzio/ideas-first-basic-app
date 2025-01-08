package pl.arkadiusz.urbanski.ideas.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import pl.arkadiusz.urbanski.ideas.model.Category;

public class CategoryDao {

  private final GenericDao<Category> genericDao;

  public CategoryDao() {
    this.genericDao = new GenericDao<>("./src/main/resources/categories.txt", new TypeReference<>() {
    });
  }

  public CategoryDao(GenericDao<Category> genericDao) {
    this.genericDao = genericDao;
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
