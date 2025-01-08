package pl.arkadiusz.urbanski.ideas.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import pl.arkadiusz.urbanski.ideas.dao.QuestionDao;
import pl.arkadiusz.urbanski.ideas.input.UserInputCommand;
import pl.arkadiusz.urbanski.ideas.model.Answer;
import pl.arkadiusz.urbanski.ideas.model.Question;

public class AnswerCommandHandler extends BaseCommandHandler {

  private static final Logger LOG = Logger.getLogger(AnswerCommandHandler.class.getName());
  private static final String COMMAND_NAME = "answer";
  private final QuestionDao questionDao;

  public AnswerCommandHandler() {
    this.questionDao = new QuestionDao();
  }

  public AnswerCommandHandler(QuestionDao questionDao) {
    this.questionDao = questionDao;
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
    String questionName = String.join(" ", command.getParam())
        .replace("\"", "")
        .trim();
    LOG.info(" Processed question name: " + questionName);
    Question question = findQuestionByName(questionName);
    displayQuestion(question);
  }

  private void handleAddAction(UserInputCommand command) {
    LOG.info(" Add answer ");

    if (command.getParam().isEmpty()) {
      throw new IllegalArgumentException(" No parameters provided for 'add' action");
    }

    String joinedParams = String.join(" ", command.getParam());
    long quoteCount = joinedParams.chars().filter(c -> c == '"').count();
    if (quoteCount % 2 != 0) {
      throw new IllegalArgumentException(" Mismatched quotes in command parameters");
    }

    String[] params = splitParams(command.getParam());
    if (params.length != 2) {
      throw new IllegalArgumentException(" Please provide exactly two parameters in quotes: question and answer");
    }

    String questionName = params[0].trim().toLowerCase();
    String answerName = params[1].trim();

    Question question = findQuestionByName(questionName);
    if (question == null) {
      throw new IllegalArgumentException((" Question not found: ") + questionName);
    }

    Answer answer = new Answer(answerName);
    questionDao.addAnswer(question, answer);

//    questionDao.save(question);

    LOG.info(" Answer added successfully to question: " + question.getName());
  }

  private Question findQuestionByName(String questionName) {
    Optional<Question> optionalQuestion = questionDao.findOne(questionName);

    if (optionalQuestion.isPresent()) {
    } else {
      LOG.warning(" Question not found: [" + questionName + "]");
    }
    return optionalQuestion.orElseThrow(() -> new IllegalArgumentException(" Question not found: [" + questionName + "]"));
  }

  private void displayQuestion(Question question) {
    if (question.getAnswers().isEmpty()) {
      System.out.println(" No answers available. ");
    }
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

    if (extracted.isEmpty()) {
      extracted.add(joined.trim());

    }
    return extracted.toArray(new String[0]);
  }
}
