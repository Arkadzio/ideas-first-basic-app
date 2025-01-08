package pl.arkadiusz.urbanski.ideas.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.arkadiusz.urbanski.ideas.model.Category;

@ExtendWith(MockitoExtension.class)
class CategoryDaoTest {

  @Mock
  private GenericDao<Category> genericDaoMock;

  @InjectMocks
  private CategoryDao categoryDao;

  @Captor
  private ArgumentCaptor<Category> categoryCaptor;

  @Test
  void shouldAddCategorySuccessfully() {
    //Given
    Category category = new Category("Test Category");

    //When
    categoryDao.add(category);

    //Then
    verify(genericDaoMock, times(1)).add(categoryCaptor.capture());
    Category capturedCategory = categoryCaptor.getValue();
    assertEquals("Test Category", capturedCategory.getName());
  }

  @Test
  void shouldReturnAllCategories() {
    //Given
    Category category1 = new Category("Category 1");
    Category category2 = new Category("Category 2");
    List<Category> categories = List.of(category1, category2);
    when(genericDaoMock.findAll()).thenReturn(categories);

    //When
    List<Category> result = categoryDao.findAll();

    //Then
    assertEquals(2, result.size());
    assertTrue(result.contains(category1));
    assertTrue(result.contains(category2));
  }

  @Test
  void shouldLogAllCategories() {
    //Given
    Category category1 = new Category("Category 1");
    Category category2 = new Category("Category 2");
    List<Category> categories = List.of(category1, category2);
    when(genericDaoMock.findAll()).thenReturn(categories);

    //When
    categoryDao.logAllCategories();

    //Then
    verify(genericDaoMock, times(1)).findAll();

  }
}