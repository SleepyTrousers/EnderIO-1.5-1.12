package crazypants.enderio.base.config.recipes.xml;

import java.lang.reflect.Type;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import crazypants.enderio.base.config.recipes.IRecipeRoot;

@SuppressWarnings("null")
public class Serializer {

  private static final Gson GSON_INSTANCE = new GsonBuilder() //
      .registerTypeAdapter(ConditionConfig.class, new ConditionConfigSerializer()) //
      .registerTypeAdapter(ConditionDependency.class, new ConditionDependencySerializer()) //
      .registerTypeAdapter(Alias.class, new AliasSerializer()) //
      .registerTypeAdapter(Aliases.class, new AliasesSerializer()) //
      .create();

  public static String serialize(IRecipeRoot src) {
    return GSON_INSTANCE.toJson(src);
  }

  static <T> void addListOptional(JsonObject jsonobject, String name, Iterable<T> list, JsonSerializationContext context) {
    JsonArray jsonArray = new JsonArray();
    for (T elem : list) {
      jsonArray.add(context.serialize(elem));
    }
    if (jsonArray.size() > 0) {
      jsonobject.add(name, jsonArray);
    }
  }

  static <T> void addOptional(JsonObject jsonobject, String name, Optional<T> value, JsonSerializationContext context) {
    if (value.isPresent()) {
      jsonobject.add(name, context.serialize(value.get()));
    }
  }

  public static class ConditionConfigSerializer implements JsonSerializer<ConditionConfig> {
    @Override
    public JsonObject serialize(@Nullable ConditionConfig src, @Nullable Type typeOfSrc, @Nullable JsonSerializationContext context) {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("name", src.getName());
      jsonobject.addProperty("section", src.getSection());
      jsonobject.addProperty("value", src.getValue());
      return jsonobject;
    }
  }

  public static class ConditionDependencySerializer implements JsonSerializer<ConditionDependency> {
    @Override
    public JsonObject serialize(@Nullable ConditionDependency src, @Nullable Type typeOfSrc, @Nullable JsonSerializationContext context) {
      JsonObject jsonobject = new JsonObject();
      addOptional(jsonobject, "item", src.getItemString(), context);
      addOptional(jsonobject, "mod", src.getModString(), context);
      addOptional(jsonobject, "upgrade", src.getUpgradeString(), context);
      jsonobject.addProperty("reverse", src.isReverse());
      return jsonobject;
    }
  }

  public static abstract class AbstractConditionalSerializer<A extends AbstractConditional> implements JsonSerializer<A> {
    @Override
    public JsonObject serialize(@Nullable A src, @Nullable Type typeOfSrc, @Nullable JsonSerializationContext context) {
      JsonObject jsonobject = new JsonObject();
      addListOptional(jsonobject, "config", src.getConfigReferences(), context);
      addListOptional(jsonobject, "dependency", src.getDependencies(), context);
      return jsonobject;
    }
  }

  public static class AliasSerializer extends AbstractConditionalSerializer<Alias> {
    @Override
    public JsonObject serialize(@Nullable Alias src, @Nullable Type typeOfSrc, @Nullable JsonSerializationContext context) {
      JsonObject jsonobject = super.serialize(src, typeOfSrc, context);
      jsonobject.addProperty("name", src.getName());
      jsonobject.addProperty("item", src.getItem());
      return jsonobject;
    }
  }

  public static class AliasesSerializer implements JsonSerializer<Aliases> {
    @Override
    public JsonArray serialize(@Nullable Aliases src, @Nullable Type typeOfSrc, @Nullable JsonSerializationContext context) {
      JsonArray jsonArray = new JsonArray();
      for (Alias elem : src.getAliases()) {
        jsonArray.add(context.serialize(elem));
      }
      return jsonArray;
    }
  }

}
