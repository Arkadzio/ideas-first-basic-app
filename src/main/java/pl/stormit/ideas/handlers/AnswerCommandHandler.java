package pl.stormit.ideas.handlers;

import java.util.List;
import java.util.logging.Logger;
import pl.stormit.ideas.dao.CategoryDao;
import pl.stormit.ideas.dao.QuestionDao;
import pl.stormit.ideas.input.UserInputCommand;
import pl.stormit.ideas.model.Answer;
import pl.stormit.ideas.model.Category;
import pl.stormit.ideas.model.Question;

public class AnswerCommandHandler extends BaseCommandHandler {

  private static Logger LOG = Logger.getLogger(AnswerCommandHandler.class.getName());
  private static final String COMMAND_NAME = "answer";
  private QuestionDao questionDao;
  private CategoryDao categoryDao;

  public AnswerCommandHandler() {
    this.questionDao = new QuestionDao();
    this.categoryDao = new CategoryDao();
  }

  @Override
  protected String getCommandName() {
    return COMMAND_NAME;
  }

  @Override
  public void handle(UserInputCommand command) {
    if (command.getAction() == null) {
      throw new IllegalArgumentException("action can't be null");
    }

    switch ((command.getAction())) {
      case LIST:
        LOG.info("List of answers...");

        if (command.getParam().size() != 1) {
          throw new IllegalArgumentException("category list doesn't support additional params");
        }

        String questionName = command.getParam().get(0);
        Question question = questionDao.findOne(questionName)
            .orElseThrow(() -> new IllegalArgumentException("Question not found " + questionName));

        displayQuestion(question);
        break;

      case ADD:
        LOG.info("Add answer");

        if (command.getParam().size() != 2) {
          throw new IllegalArgumentException("wrong command format. Check help for more info");
        }

        questionName = command.getParam().get(0);
        String answerName = command.getParam().get(1);

        question = questionDao.findOne(questionName)
            .orElseThrow(() -> new IllegalArgumentException("Question not found " + questionName));
        questionDao.addAnswer(question, new Answer(answerName));

        break;

      default: {
        throw new IllegalArgumentException(String.format("Unknown action: %s from command: %s", command.getAction(), command.getCommand()));

      }
    }

  }

  private void displayQuestion(Question question) {
    System.out.println(question.getName());
    question.getAnswers().forEach(System.out::println);
  }
}
