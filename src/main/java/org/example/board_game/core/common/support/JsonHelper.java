package org.example.board_game.core.common.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class JsonHelper {

  private static final ObjectMapper jsonMapper = jsonMapper();

  private JsonHelper() {
  }

  public static ObjectMapper jsonMapper() {
    return JsonMapper
      .builder()
      .serializationInclusion(JsonInclude.Include.NON_EMPTY)
      .addModule(new JavaTimeModule())
      .addModules(new Jdk8Module())
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .disable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY)
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .build();
  }

  public static <T> String writeAsString(T object) {
    try {
      return jsonMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new AssertionError("Error serializing object: " + e.getMessage(), e);
    }
  }

  public static <T> T readFromJson(String json, Class<T> clazz) {
    try {
      return jsonMapper.readValue(json, clazz);
    } catch (IOException e) {
      throw new AssertionError("Error reading value from json: " + e.getMessage(), e);
    }
  }

  public static <T> List<T> convertJsonToList(String json, Class<T> elementType) {
    try {
      return jsonMapper.readValue(json, jsonMapper.getTypeFactory().constructCollectionType(List.class, elementType));
    } catch (IOException e) {
      throw new IllegalArgumentException("Error converting JSON to List<" + elementType.getSimpleName() + ">: " + e.getMessage(), e);
    }
  }

  public static <T> Map<String, T> convertJsonToMap(String json) {
    try {
      return jsonMapper.readValue(json, new TypeReference<Map<String, T>>() {
      });
    } catch (IOException e) {
      throw new AssertionError("Error reading value from json: " + e.getMessage(), e);

    }
  }
}
