package pl.arkadiusz.urbanski.ideas.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import pl.arkadiusz.urbanski.ideas.model.Answer;
import pl.arkadiusz.urbanski.ideas.model.Question;

public class QuestionDao {

  private static final Logger LOG = Logger.getLogger(QuestionDao.class.getName());
  private final GenericDao<Question> genericDao;

  public QuestionDao() {
    this.genericDao = new GenericDao<>("./src/main/resources/questions.txt", new TypeReference<>() {
    });
  }

  public QuestionDao(GenericDao<Question> genericDao) {
    this.genericDao = genericDao;
  }

  public void add(Question question) {
    genericDao.add(question);
  }

  public Optional<Question> findOne(String name) {
    return genericDao.findOne(q -> q.getName().equalsIgnoreCase(name.trim()));
  }

  public void addAnswer(Question question, Answer answer) {
    Optional<Question> optionalQuestion = findOne(question.getName());
    if (optionalQuestion.isPresent()) {
      Question foundQuestion = optionalQuestion.get();
      foundQuestion.getAnswers().add(answer);
      save(foundQuestion);
    } else {
      throw new IllegalArgumentException(" Question not found: " + question.getName());
    }
  }

  public void save(Question question) {
    List<Question> questions = genericDao.findAll();
    questions.removeIf(q -> q.getName().equalsIgnoreCase(question.getName()));
    questions.add(question);
    genericDao.saveAll(questions);
    LOG.info(" Saved question: " + question.getName());
  }

  public void logAllQuestions() {
    genericDao.findAll().forEach(q -> System.out.println(" Question: " + q.getName()));
  }
}
