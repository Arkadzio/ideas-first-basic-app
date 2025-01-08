package pl.arkadiusz.urbanski.ideas.handlers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.arkadiusz.urbanski.ideas.Actions;
import pl.arkadiusz.urbanski.ideas.dao.QuestionDao;
import pl.arkadiusz.urbanski.ideas.input.UserInputCommand;
import pl.arkadiusz.urbanski.ideas.model.Answer;
import pl.arkadiusz.urbanski.ideas.model.Category;
import pl.arkadiusz.urbanski.ideas.model.Question;

@ExtendWith(MockitoExtension.class)
class AnswerCommandHandlerTest {

  @Mock
  private QuestionDao mockQuestionDao;
  @Mock
  private UserInputCommand mockCommand;
  @InjectMocks
  private AnswerCommandHandler answerCommandHandler;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    answerCommandHandler = new AnswerCommandHandler(mockQuestionDao);
  }

  @Test
  void shouldHandleListActionSuccessfully() {

    //Given
    when(mockCommand.getAction()).thenReturn(Actions.LIST);
    when(mockCommand.getParam()).thenReturn(Collections.singletonList("sampleQuestion"));

    Question mockQuestion = new Question("sampleQuestion");
    mockQuestion.getAnswers().add(new Answer("Sample Answer"));
    when(mockQuestionDao.findOne("sampleQuestion")).thenReturn(Optional.of(mockQuestion));

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));

    //When
    Assertions.assertDoesNotThrow(() -> answerCommandHandler.handle(mockCommand));

    //Then
    String output = outputStream.toString();
    Assertions.assertTrue(output.contains("sampleQuestion"));
    Assertions.assertTrue(output.contains("Sample Answer"));

    verify(mockQuestionDao, times(1)).findOne("sampleQuestion");
  }

  @Test
  void shouldHandleAddActionSuccessfully() {

    //Given
    UserInputCommand command = mock(UserInputCommand.class);
    when(command.getAction()).thenReturn(Actions.ADD);
    when(command.getParam()).thenReturn(Collections.singletonList("\"Sample Question\", \"Sample Answer\""));

    Question mockQuestion = new Question("sample question");
    when(mockQuestionDao.findOne("sample question")).thenReturn(Optional.of(mockQuestion));

    //When
    answerCommandHandler.handle(command);

    //then
    verify(mockQuestionDao, times(1)).addAnswer(eq(mockQuestion), any(Answer.class));
  }

  @Test
  void shouldThrowExceptionWhenActionIsNull() {

    when(mockCommand.getAction()).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () -> {
      answerCommandHandler.handle(mockCommand);
    });
  }

  @Test
  void shouldFindQuestionByNameUsingReflection() throws Exception {

    QuestionDao mockQuestionDao = mock(QuestionDao.class);
    String questionName = "test question";
    Question mockQuestion = new Question(questionName);

    when(mockQuestionDao.findOne(questionName)).thenReturn(Optional.of(mockQuestion));
    AnswerCommandHandler handler = new AnswerCommandHandler(mockQuestionDao);

    Method method = AnswerCommandHandler.class.getDeclaredMethod("findQuestionByName", String.class);
    method.setAccessible(true);

    Question result = (Question) method.invoke(handler, questionName);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(questionName, result.getName());
    verify(mockQuestionDao, times(1)).findOne(questionName);
  }

  @Test
  void shouldDisplayQuestionWithAnswers() throws Exception {
    List<Answer> answers = List.of(new Answer("Answer 1"), new Answer("Answer 2"));
    Category category = new Category("Example category");
    Question question = new Question("Example question", category);
    question.getAnswers().addAll(answers);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));

    Method displayQuestion = AnswerCommandHandler.class.getDeclaredMethod("displayQuestion", Question.class);
    displayQuestion.setAccessible(true);
    displayQuestion.invoke(answerCommandHandler, question);

    String expectedOutput = "Example question\r\n - Answer 1\r\n - Answer 2\r\n";
    Assertions.assertEquals(expectedOutput, outputStream.toString());
  }

  @Test
  void shouldSplitParamsCorrectlyWithQuotes() throws Exception {
    List<String> params = List.of("\"First parameter\"", "\"Second parameter\"");
    AnswerCommandHandler handler = new AnswerCommandHandler();

    Method splitParams = AnswerCommandHandler.class.getDeclaredMethod("splitParams", List.class);
    splitParams.setAccessible(true);
    String[] result = (String[]) splitParams.invoke(handler, params);

    String[] expected = {"First parameter", "Second parameter"};
    Assertions.assertArrayEquals(expected, result);
  }

  @Test
  void shouldThrowExceptionForMismatchedQuotes() throws Exception {
    List<String> params = List.of("sampleQuestion", "\"firstParameter", "secondParameter");

    InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
      Method splitParamsMethod = AnswerCommandHandler.class.getDeclaredMethod("splitParams", List.class);
      splitParamsMethod.setAccessible(true);
      try {
        splitParamsMethod.invoke(answerCommandHandler, params);
      } catch (InvocationTargetException ex) {
        throw ex;
      }
    });
    Assertions.assertTrue(exception.getCause() instanceof IllegalArgumentException);
    Assertions.assertEquals(" Mismatched quotes in command parameters ", exception.getCause().getMessage());
  }

  @Test
  void shouldReturnSingleParameterWhenNoQuotesProvided() throws Exception {

    List<String> params = List.of("Nothing", "Quotation marks");
    AnswerCommandHandler handler = new AnswerCommandHandler();

    Method splitParams = AnswerCommandHandler.class.getDeclaredMethod("splitParams", List.class);
    splitParams.setAccessible(true);
    String[] result = (String[]) splitParams.invoke(handler, params);

    String[] expected = {"Nothing Quotation marks"};
    Assertions.assertArrayEquals(expected, result);
  }
}
