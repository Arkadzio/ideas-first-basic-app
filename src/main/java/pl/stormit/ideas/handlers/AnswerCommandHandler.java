package pl.stormit.ideas.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import pl.stormit.ideas.dao.GenericDao;
import pl.stormit.ideas.input.UserInputCommand;
import pl.stormit.ideas.model.Answer;
import pl.stormit.ideas.model.Question;

public class AnswerCommandHandler extends BaseCommandHandler {

  private static final Logger LOG = Logger.getLogger(AnswerCommandHandler.class.getName());
  private static final String COMMAND_NAME = "answer";
  private final GenericDao<Question> questionDao;

  public AnswerCommandHandler() {
    this.questionDao = new GenericDao<>("./questions.txt", new TypeReference<>() {
    });
  }

  @Override
  protected String getCommandName() {
    return COMMAND_NAME;
  }

  @Override
  public void handle(UserInputCommand command) {
    if (command.getAction() == null) {
      throw new IllegalArgumentException(" Action can't be null ");
    }

    switch (command.getAction()) {
      case LIST:
        handleListAction(command);
        break;
      case ADD:
        handleAddAction(command);
        break;
      default:
        throw new IllegalArgumentException(String.format(" Unknown action: %s from command: %s ", command.getAction(), command.getCommand()));
    }
  }

  private void handleListAction(UserInputCommand command) {
//    LOG.info(" List of answers... ");
    LOG.info(" Original command params: " + command.getParam());

//    if (command.getParam().isEmpty()) {
//      throw new IllegalArgumentException(" No question name provided ");
//    }
    String questionName = String.join(" ", command.getParam()).replace(",", "").trim();
    LOG.info(" Processed question name: " + questionName);
    Question question = findQuestionByName(questionName);
    displayQuestion(question);
  }

  private void handleAddAction(UserInputCommand command) {
    LOG.info(" Add answer ");

    if (command.getParam().isEmpty()) {
      throw new IllegalArgumentException(" No parameters provided for 'add' action");
    }

    String[] params = splitParams(command.getParam());
    if (params.length != 2) {
      throw new IllegalArgumentException(" Please provide exactly two parameters in quotes: question and answer");
    }

    String questionName = params[0];
    String answerName = params[1];

    Question question = findQuestionByName(questionName);
    addAnswerToQuestion(question, new Answer(answerName));
  }

  private Question findQuestionByName(String questionName) {
    LOG.info(" Searching for question: [" + questionName + "]");
    return questionDao.findOne(q -> {
          LOG.info(" Comparing with question: [" + q.getName() + "]");
          return q.getName().trim().equalsIgnoreCase(questionName.trim());
        })
        .orElseThrow(() -> new IllegalArgumentException(" Question not found: [" + questionName + "]"));
  }

  private void addAnswerToQuestion(Question question, Answer answer) {
    question.getAnswers().add(answer);
    questionDao.saveAll(questionDao.findAll());
    LOG.info(" Saved updated questions: " + questionDao.findAll());
  }

  private void displayQuestion(Question question) {
    System.out.println(question.getName());
    question.getAnswers().forEach(answer -> System.out.println(" - " + answer.getName()));
  }

  private String[] splitParams(List<String> params) {
    String joined = String.join(" ", params);
    List<String> extracted = new ArrayList<>();
    int startIndex = 0;

    while ((startIndex = joined.indexOf("\"", startIndex)) != -1) {
      int endIndex = joined.indexOf("\"", startIndex + 1);
      if (endIndex == -1) {
        throw new IllegalArgumentException(" Mismatched quotes in command parameters ");
      }
      extracted.add(joined.substring(startIndex + 1, endIndex).trim());
      startIndex = endIndex + 1;
    }

//    if (extracted.size() != 2) {
//      throw new IllegalArgumentException(" Please provide exactly two parameters in quotes: question and answer ");

    if (extracted.size() == 1) {
      extracted.add("");

    }
    return extracted.toArray(new String[0]);
  }
}
