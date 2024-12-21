package pl.stormit.ideas.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import pl.stormit.ideas.model.Question;

public class QuestionDao {

  private ObjectMapper objectMapper;

  public QuestionDao() {
    this.objectMapper = new ObjectMapper();
  }

  public List<Question> findAll() {

    return getQuestions();
  }

  private List<Question> getQuestions() {
    try {
      return objectMapper.readValue(Files.readString(Paths.get("./questions.txt")), new TypeReference<>() {
      });
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void add(Question question) {
    try {
      List<Question> questions = getQuestions();
      questions.add(question);

      Files.writeString(Paths.get("./questions.txt"), objectMapper.writeValueAsString(question));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
