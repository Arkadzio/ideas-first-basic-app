package pl.stormit.ideas.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericDao<T> {

  private static final Logger LOG = Logger.getLogger(GenericDao.class.getName());

  private final ObjectMapper objectMapper;
  private final String filePath;
  private final TypeReference<List<T>> typeReference;


  public GenericDao(String filePath, TypeReference<List<T>> typeReference) {
    this.objectMapper = new ObjectMapper();
    this.filePath = filePath;
    this.typeReference = typeReference;
  }

  public List<T> findAll() {
    return loadFromFile();
  }

  public void add(T entity) {
    if (entity == null) {
      throw new IllegalArgumentException(" Entity cannot be null ");
    }
    List<T> entities = loadFromFile();
    entities.add(entity);
    saveToFile(entities);
  }

  public Optional<T> findOne(Predicate<T> predicate) {
    return loadFromFile().stream().filter(predicate).findFirst();
  }

  public void saveAll(List<T> entities) {
    saveToFile(entities);
  }

  private List<T> loadFromFile() {
    try {
      if (!Files.exists(Paths.get(filePath))) {
        LOG.warning(" File does not exist: " + filePath + " . Initializing as empty list. ");
        return new ArrayList<>();
      }
      String content = Files.readString(Paths.get(filePath));
      LOG.info(" File content: " + content);
      if (content.isBlank()) {
        LOG.warning(" File is empty or blank: " + filePath);
        return new ArrayList<>();
      }
      return objectMapper.readValue(content, typeReference);
    } catch (IOException e) {
      LOG.log(Level.WARNING, " Error reading from file: " + filePath, e);
      return new ArrayList<>();
    }
  }

  private void saveToFile(List<T> entities) {
    try {
//      LOG.info(" Saving entities to " + filePath + " : " + entities);
      Files.writeString(Paths.get(filePath), objectMapper.writeValueAsString(entities));
    } catch (IOException e) {
      LOG.log(Level.SEVERE, " Error writing to file: " + filePath, e);
      throw new IllegalStateException(" Unable to save data to file: " + filePath, e);
    }
  }
}