package pl.arkadiusz.urbanski.ideas.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pl.arkadiusz.urbanski.ideas.Actions;

public class UserInputCommand {

  private String command;
  private Actions action;
  private List<String> param;

  public UserInputCommand() {
  }

  public UserInputCommand(String line) {
    if (line == null) {
      command = null;
      action = null;
      param = new ArrayList<>();
    }
    if (line != null && !line.isBlank()) {
      List<String> parts = new ArrayList<>();
      boolean insideQuotes = false;
      StringBuilder current = new StringBuilder();

      for (char c : line.toCharArray()) {
        if (c == '"' && !insideQuotes) {
          insideQuotes = true;
          current.append(c);
        } else if (c == '"' && insideQuotes) {
          insideQuotes = false;
          current.append(c);
        } else if (c == ' ' && !insideQuotes) {
          if (current.length() > 0) {
            parts.add(current.toString());
            current.setLength(0);
          }
        } else {
          current.append(c);
        }
      }
      if (insideQuotes) {
        throw new IllegalArgumentException("Mismatched quotes in input");
      }
      if (current.length() > 0) {
        parts.add(current.toString());
      }

      if (parts.size() > 0) {
        command = parts.get(0);
      }
      if (parts.size() > 1) {
        action = Actions.of(parts.get(1));
        if (action == null) {
          throw new IllegalArgumentException(" Unknown action: " + parts.get(1));
        }
      }
      param = new ArrayList<>();
      if (parts.size() > 2) {
        param.addAll(parts.subList(2, parts.size()));
      }
    }
  }

  public String getCommand() {
    return command;
  }

  public Actions getAction() {
    return action;
  }

  public List<String> getParam() {
    return param;
  }

  @Override
  public String toString() {
    return "UserInputCommand{" +
        "command='" + command + '\'' +
        ", action='" + action + '\'' +
        ", param=" + param +
        '}';
  }
}
