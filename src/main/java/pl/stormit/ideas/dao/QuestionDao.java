package pl.stormit.ideas.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Optional;
import pl.stormit.ideas.model.Answer;
import pl.stormit.ideas.model.Question;

public class QuestionDao {

  private final GenericDao<Question> genericDao;

  public QuestionDao() {
    this.genericDao = new GenericDao<>("./questions.txt", new TypeReference<>() {
    });
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
    }
  }

  public void save(Question question) {
    List<Question> questions = genericDao.findAll();
    questions.removeIf(q -> q.getName().equalsIgnoreCase(question.getName()));
    questions.add(question);
    genericDao.saveAll(questions);
  }

  public void logAllQuestions() {
    genericDao.findAll().forEach(q -> System.out.println(" Question: " + q.getName()));
  }
}
