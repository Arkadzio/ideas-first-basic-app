package pl.arkadiusz.urbanski.ideas.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.arkadiusz.urbanski.ideas.Actions;
import pl.arkadiusz.urbanski.ideas.dao.CategoryDao;
import pl.arkadiusz.urbanski.ideas.input.UserInputCommand;
import pl.arkadiusz.urbanski.ideas.model.Category;

@ExtendWith(MockitoExtension.class)
class CategoryCommandHandlerTest {

  @InjectMocks
  private CategoryCommandHandler categoryCommandHandler;
  @Mock
  private CategoryDao categoryDaoMock;
  @Mock
  private UserInputCommand userInputCommandMock;

  @BeforeEach
  void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);

    categoryCommandHandler = new CategoryCommandHandler();

    Field categoryDaoField = CategoryCommandHandler.class.getDeclaredField("categoryDao");
    categoryDaoField.setAccessible(true);
    categoryDaoField.set(categoryCommandHandler, categoryDaoMock);
  }

  @Test
  void shouldHandleListActionWithValidCommandAndWithoutParams() throws Exception {
    //Given
    List<Category> mockCategories = List.of(new Category("Category1"), new Category("Category2"));
    when(categoryDaoMock.findAll()).thenReturn(mockCategories);
    when(userInputCommandMock.getParam()).thenReturn(List.of());

    //When
    Method handleListActionMethod = CategoryCommandHandler.class.getDeclaredMethod("handleListAction", UserInputCommand.class);
    handleListActionMethod.setAccessible(true);
    handleListActionMethod.invoke(categoryCommandHandler, userInputCommandMock);

    //Then
    verify(categoryDaoMock, times(1)).findAll();

  }

  @Test
  void shouldExtractSingleQuotedParamValidInput() {
    //Given
    List<String> params = List.of("\"Test Category\"");

    //When
    String result = categoryCommandHandler.extractSingleQuotedParam(params);

    //Then
    assertEquals("Test Category", result);
  }

  @Test
  void shouldExtractSingleQuotedParamWithValidInputAndWithoutQuotes() {
    //Given
    List<String> params = List.of("Test Category");

    //When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      categoryCommandHandler.extractSingleQuotedParam(params);
    });
    assertEquals(" Invalid parameter format. Expected a single quoted string. ", exception.getMessage());
  }

  @Test
  void shouldExtractSingleQuotedParamWithEmptyString() {
    //Given
    List<String> params = List.of("\"\"");

    //When
    String result = categoryCommandHandler.extractSingleQuotedParam(params);

    //Then
    assertEquals("", result);
  }

  @Test
  void shouldThrowExceptionWhenCommandIsNull() {
    // Given
    UserInputCommand nullCommand = null;

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      categoryCommandHandler.handle(nullCommand);
    });
    assertEquals(" Command and action can't be null ", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenActionIsNull() throws Exception {
    // Given
    UserInputCommand commandWithNullAction = new UserInputCommand();

    Field actionField = UserInputCommand.class.getDeclaredField("action");
    actionField.setAccessible(true);
    actionField.set(commandWithNullAction, null);

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      categoryCommandHandler.handle(commandWithNullAction);
    });
    assertEquals(" Command and action can't be null ", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenParamsAreProvidedForListAction() {
    // Given
    when(userInputCommandMock.getAction()).thenReturn(Actions.LIST);
    when(userInputCommandMock.getParam()).thenReturn(List.of("param1", "param2"));

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      categoryCommandHandler.handle(userInputCommandMock);
    });
    assertEquals(" category list doesn't support additional params ", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenAddActionHasNoParams() {
    // Given
    when(userInputCommandMock.getAction()).thenReturn(Actions.ADD);
    when(userInputCommandMock.getParam()).thenReturn(List.of());

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      categoryCommandHandler.handle(userInputCommandMock);
    });
    assertEquals(" No parameters provided for 'add' action ", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenCategoryAlreadyExists() {
    // Given
    String existingCategory = "Existing Category";
    when(userInputCommandMock.getAction()).thenReturn(Actions.ADD);
    when(userInputCommandMock.getParam()).thenReturn(List.of("\"" + existingCategory + "\""));

    List<Category> mockCategories = List.of(new Category(existingCategory));
    when(categoryDaoMock.findAll()).thenReturn(mockCategories);

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      categoryCommandHandler.handle(userInputCommandMock);
    });
    assertEquals(" Category already exists: " + existingCategory, exception.getMessage());
  }

  @Test
  void shouldAddCategorySuccessfully() {
    // Given
    String newCategory = "New Category";
    when(userInputCommandMock.getAction()).thenReturn(Actions.ADD);
    when(userInputCommandMock.getParam()).thenReturn(List.of("\"" + newCategory + "\""));

    List<Category> mockCategories = List.of();
    when(categoryDaoMock.findAll()).thenReturn(mockCategories);

    // When
    categoryCommandHandler.handle(userInputCommandMock);

    // Then
    verify(categoryDaoMock, times(1)).add(argThat(category -> category.getName().equals(newCategory)));
  }
}