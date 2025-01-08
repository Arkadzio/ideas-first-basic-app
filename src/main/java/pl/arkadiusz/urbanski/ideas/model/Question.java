package pl.arkadiusz.urbanski.ideas.model;

import java.util.ArrayList;
import java.util.List;

public class Question {

  private String name;
  private Category category;
  private List<Answer> answers;

  public Question() {
    this.answers = new ArrayList<>();
  }

  public Question(String name) {
    this.name = name;
    this.answers = new ArrayList<>();
  }

  public Question(String name, Category category) {
    this.name = name;
    this.category = category;
    this.answers = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public Category getCategory() {
    return category;
  }

  public List<Answer> getAnswers() {
    return answers;
  }

  @Override
  public String toString() {
    return "Question{" +
        "name='" + name + '\'' +
        ", category=" + category +
        ", answers=" + answers +
        '}';
  }
}
