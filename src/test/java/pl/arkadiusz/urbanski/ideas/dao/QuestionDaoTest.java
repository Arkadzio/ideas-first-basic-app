package pl.arkadiusz.urbanski.ideas.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.arkadiusz.urbanski.ideas.model.Answer;
import pl.arkadiusz.urbanski.ideas.model.Category;
import pl.arkadiusz.urbanski.ideas.model.Question;

class QuestionDaoTest {

  private GenericDao<Question> genericDao;
  private QuestionDao questionDao;
  private Question question;
  private Answer answer;

  @BeforeEach
  void setUp() {
    genericDao = Mockito.mock(GenericDao.class);
    questionDao = new QuestionDao(genericDao);
    Category category = new Category("Some Category");
    question = new Question("Test Question", category);
    answer = new Answer("Test Answer");
    when(genericDao.findOne(any())).thenReturn(Optional.of(question));
    when(genericDao.findAll()).thenReturn(new ArrayList<>(List.of(question)));
  }

  @Test
  void shouldAdd() {

    //Given
    Question questionToAdd = new Question("New Question", null);

    //When
    questionDao.add((questionToAdd));

    //Then
    verify(genericDao, times(1)).add(questionToAdd);
  }

  @Test
  void shouldFindOne() throws Exception {

    //Given
    String questionName = "Test Question";

    //When
    Optional<Question> result = questionDao.findOne(questionName);

    //Then
    Assertions.assertTrue(result.isPresent(), "Expected question to be found. ");
    Assertions.assertEquals("Test Question", result.get().getName(), "The question name should match. ");
  }

  @Test
  void shouldAddAnswer() {

    //Given
    Answer newAnswer = new Answer("Another Test Answer");

    //When
    questionDao.addAnswer(question, newAnswer);

    //Then
    verify(genericDao, times(1)).saveAll(anyList());
    Assertions.assertTrue(question.getAnswers().contains(newAnswer), "The answer should be added to the question. ");
  }

  @Test
  void shouldSave() {

    //Given
    Category category = new Category("Updated Category");
    Question updatedQuestion = new Question("Test Question", category);
    updatedQuestion.getAnswers().add(answer);

    //When
    questionDao.save(updatedQuestion);

    //Then
    verify(genericDao, times(1)).saveAll(anyList());
  }

  @Test
  void shouldLogAllQuestions() {

    //Given
    when(genericDao.findAll()).thenReturn(List.of(question));

    //When
    questionDao.logAllQuestions();

    //Then
    verify(genericDao, times(1)).findAll();
  }

  @Test
  void shouldThrowExceptionWhenQuestionNotFound() {

    //Given
    when(genericDao.findOne(any())).thenReturn(Optional.empty());

    //When & Then
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      questionDao.addAnswer(question, answer);
    });
    Assertions.assertEquals(" Question not found: Test Question", thrown.getMessage());
  }
}
