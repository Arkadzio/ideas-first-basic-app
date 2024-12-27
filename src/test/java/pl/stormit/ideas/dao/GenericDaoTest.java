package pl.stormit.ideas.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.junit.jupiter.api.Test;
import pl.stormit.ideas.model.Category;

class GenericDaoTest {

  @Test
  public void shouldAddAndFindAll() {
    GenericDao<Category> dao = new GenericDao<>("test_categories.txt", new TypeReference<List<Category>>() {
    });
    Category category = new Category("Test Category");
    dao.add(category);

    List<Category> categories = dao.findAll();
    assertEquals(categories.size(), categories.size());
    assertEquals("Test Category", categories.get(0).getName());
  }
}
