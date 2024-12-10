package pl.stormit.ideas.model;

import java.util.List;

public class Question {

  private String name;
  private Category category;
  private List<Answer> answers;

  public String getName() {
    return name;
  }

  public Category getCategory() {
    return category;
  }

  public List<Answer> getAnswers() {
    return answers;
  }
}
