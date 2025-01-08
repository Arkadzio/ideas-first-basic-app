package pl.arkadiusz.urbanski.ideas.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.arkadiusz.urbanski.ideas.Actions;
import pl.arkadiusz.urbanski.ideas.dao.GenericDao;
import pl.arkadiusz.urbanski.ideas.input.UserInputCommand;
import pl.arkadiusz.urbanski.ideas.model.Category;
import pl.arkadiusz.urbanski.ideas.model.Question;

@ExtendWith(MockitoExtension.class)
class QuestionCommandHandlerTest {

  @InjectMocks
  private QuestionCommandHandler questionCommandHandler;

  @Mock
  private GenericDao<Question> questionDaoMock;

  @Mock
  private GenericDao<Category> categoryDaoMock;

  @Mock
  private UserInputCommand userInputCommandMock;

  @Captor
  private ArgumentCaptor<Question> questionCaptor;

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    questionCommandHandler = new QuestionCommandHandler();

    Field questionDaoField = QuestionCommandHandler.class.getDeclaredField("questionDao");
    questionDaoField.setAccessible(true);
    questionDaoField.set(questionCommandHandler, questionDaoMock);

    Field categoryDaoField = QuestionCommandHandler.class.getDeclaredField("categoryDao");
    categoryDaoField.setAccessible(true);
    categoryDaoField.set(questionCommandHandler, categoryDaoMock);
  }

  @Test
  void shouldListQuestionsSuccessfully() {
    //Given
    when(userInputCommandMock.getAction()).thenReturn(Actions.LIST);
    when(userInputCommandMock.getParam()).thenReturn(List.of());
    List<Question> questions = List.of(
        new Question("What is Java?", new Category("Programming")),
        new Question("What is Python", new Category("Programming"))
    );
    when(questionDaoMock.findAll()).thenReturn(questions);

    //When
    questionCommandHandler.handle(userInputCommandMock);

    //Then
    verify(questionDaoMock, times(1)).findAll();
  }

  @Test
  void shouldThrowExceptionWhenListHasAdditionalParams() {
    // Given
    when(userInputCommandMock.getAction()).thenReturn(Actions.LIST);
    when(userInputCommandMock.getParam()).thenReturn(List.of("extra"));

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        questionCommandHandler.handle(userInputCommandMock)
    );
  }

  @Test
  void shouldAddQuestionSuccessfully() {
    // Given
    when(userInputCommandMock.getAction()).thenReturn(Actions.ADD);
    when(userInputCommandMock.getParam()).thenReturn(List.of("\"Programming\"", "\"What is Java?\""));

    Category category = new Category("Programming");
    when(categoryDaoMock.findOne(any())).thenReturn(Optional.of(category));

    // When
    questionCommandHandler.handle(userInputCommandMock);

    // Then
    verify(questionDaoMock, times(1)).add(questionCaptor.capture());
    Question capturedQuestion = questionCaptor.getValue();
    assertEquals("What is Java?", capturedQuestion.getName());
    assertEquals("Programming", capturedQuestion.getCategory().getName());
  }

  @Test
  void shouldThrowExceptionWhenAddHasNoParams() {
    // Given
    when(userInputCommandMock.getAction()).thenReturn(Actions.ADD);
    when(userInputCommandMock.getParam()).thenReturn(List.of());

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        questionCommandHandler.handle(userInputCommandMock)
    );
    assertEquals(" No parameters provided for 'add' action ", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenAddHasMismatchedQuotes() {
    // Given
    when(userInputCommandMock.getAction()).thenReturn(Actions.ADD);
    when(userInputCommandMock.getParam()).thenReturn(List.of("\"Programming", "What is Java?\""));

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        questionCommandHandler.handle(userInputCommandMock)
    );
    assertEquals(" Please provide exactly two parameters in quotes: question and answer ", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenCategoryNotFound() {
    // Given
    when(userInputCommandMock.getAction()).thenReturn(Actions.ADD);
    when(userInputCommandMock.getParam()).thenReturn(List.of("\"Nonexistent\"", "\"What is Java?\""));

    when(categoryDaoMock.findOne(any())).thenReturn(Optional.empty());

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        questionCommandHandler.handle(userInputCommandMock)
    );
    assertEquals(" Category not found Nonexistent", exception.getMessage());
  }
}
