package pl.arkadiusz.urbanski.ideas;

import java.util.Arrays;

public enum Actions {
  LIST("list"), ADD("add");

  private final String value;

  Actions(String value) {
    this.value = value;
  }

  public static Actions of(String value) {
if (value == null || value.trim().isEmpty()) {
  throw new IllegalArgumentException(" Action value cannot be null or empty ");
}
    return Arrays.stream(values())
        .filter(a -> a.value.equalsIgnoreCase(value.trim()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(" Unknown action: " + value + " "));
  }
}
