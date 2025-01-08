package pl.arkadiusz.urbanski.ideas.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.arkadiusz.urbanski.ideas.model.Category;
import pl.arkadiusz.urbanski.ideas.model.Question;

class GenericDaoTest {

  private GenericDao<Category> dao;
  private GenericDao<Question> genericDao;
  private String filePathCategories = "test_categories.txt";
  private String filePathQuestions = "test_questions.txt";
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() throws IOException {
    objectMapper = new ObjectMapper();

    File file = new File(filePathCategories);
    if (file.exists()) {
      file.delete();
    }

    Category category = new Category("Test Category");
    dao = new GenericDao<>(objectMapper, filePathCategories, new TypeReference<List<Category>>() {
    });
    dao.add(category);

    file = new File(filePathQuestions);
    if (file.exists()) {
      file.delete();
    }

    Question question = new Question("Test Question", null);
    List<Question> questionList = List.of(question);
    Files.write(Paths.get(filePathQuestions), objectMapper.writeValueAsBytes(questionList));
  }

  @Test
  void shouldAddAndFindAll() throws IOException {
    //Given
    dao = new GenericDao<>(objectMapper, filePathCategories, new TypeReference<List<Category>>() {
    });

    //When
    List<Category> categories = dao.findAll();

    //Then
    assertEquals(1, categories.size());
    assertEquals("Test Category", categories.get(0).getName());
  }

  @Test
  void shouldFindOne() throws Exception {

    objectMapper = mock(ObjectMapper.class);
    genericDao = new GenericDao<>(objectMapper, filePathQuestions, new TypeReference<>() {
    });

    // Given
    Question question = new Question("Test Question", null);
    List<Question> questionList = List.of(question);

    when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(questionList);

    //When
    Optional<Question> result = genericDao.findOne(q -> q.getName().equalsIgnoreCase("Test Question"));

    //Then
    Assertions.assertTrue(result.isPresent(), " Expected question to be found. ");
    Assertions.assertEquals("Test Question", result.get().getName(), "The question names should match. ");
  }

  @Test
  void shouldNotFindOne() throws Exception {

    objectMapper = mock(ObjectMapper.class);
    genericDao = new GenericDao<>(objectMapper, filePathQuestions, new TypeReference<>() {
    });

    //Given
    List<Question> questionList = List.of();

    when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(questionList);

    //When
    Optional<Question> result = genericDao.findOne(q -> q.getName().equalsIgnoreCase("Test Question"));

    //Then
    Assertions.assertFalse(result.isPresent(), "Expected no result to be found. ");
  }

  @Test
  void shouldLoadFromFile() throws Exception {

    objectMapper = mock(ObjectMapper.class);
    genericDao = new GenericDao<>(objectMapper, filePathQuestions, new TypeReference<>() {
    });

    //Given
    Question question = new Question("Test Question", null);
    List<Question> questionList = List.of(question);

    when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(questionList);

    //When
    List<Question> result = genericDao.findAll();

    //Then
    Assertions.assertNotNull(result, "List should not be null. ");
    Assertions.assertEquals(1, result.size(), "List should contain one element. ");
    Assertions.assertEquals("Test Question", result.get(0).getName(), "The question name should match. ");
  }

  @Test
  void shouldLoadFromFileEmpty() throws Exception {

    objectMapper = mock(ObjectMapper.class);
    genericDao = new GenericDao<>(objectMapper, filePathQuestions, new TypeReference<>() {
    });

    //Given
    when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(List.of());

    //When
    List<Question> result = genericDao.findAll();

    //Then
    Assertions.assertNotNull(result, "List should not be null. ");
    Assertions.assertTrue(result.isEmpty(), "List should be empty");
  }
}
