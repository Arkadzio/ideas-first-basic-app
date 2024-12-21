package pl.stormit.ideas;

import java.util.Objects;

public enum Actions {
  LIST("list"), ADD("add");

  private final String value;

  Actions(String value) {
    this.value = value;
  }

  public static Actions of(String value) {
    for (Actions action : values()) {
      if (Objects.equals(action.value, value)) {
        return action;
      }
    }
    throw new IllegalArgumentException("Unknown action: " + value);
  }
}
