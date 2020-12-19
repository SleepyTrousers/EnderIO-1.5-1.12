package crazypants.enderio.gui.gamedata;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import crazypants.enderio.gui.xml.ResourceLocation;

public class ValueRepository {

  private static @Nonnull Map<String, Map<String, List<String>>> VALUES = new HashMap<>();

  public static final @Nonnull ValueRepository POTIONS = new ValueRepository("potions");
  public static final @Nonnull ValueRepository CAP_KEYS = new ValueRepository("capkeys");
  public static final @Nonnull ValueRepository CONFIGS = new ValueRepository("configs");
  public static final @Nonnull ValueRepository ITEMS = new ValueRepository("items");
  public static final @Nonnull ValueRepository BLOCKS = new ValueRepository("blocks");
  public static final @Nonnull ValueRepository OREDICTS = new ValueRepository("oredict");
  public static final @Nonnull ValueRepository COREFILES = new ValueRepository("corefiles");

  private final @Nonnull String valueType;

  private ValueRepository(@Nonnull String valueType) {
    this.valueType = valueType;
    VALUES.put(valueType, new LinkedHashMap<>());
  }

  public @Nonnull List<String> getAllValues() {
    return VALUES.get(valueType).keySet().stream().collect(Collectors.toList());
  }

  public @Nonnull List<ResourceLocation> getAllResourceLocations() {
    return VALUES.get(valueType).keySet().stream().map(ResourceLocation::new).collect(Collectors.toList());
  }

  public boolean isValid(@Nonnull String value) {
    return VALUES.get(valueType).containsKey(value);
  }

  public boolean isValid(@Nonnull ResourceLocation value) {
    return isValid(value.toString());
  }

  public List<String> getDescription(String value) {
    return VALUES.get(valueType).get(value);
  }

  public void addValue(@Nonnull String value, List<String> description) {
    VALUES.get(valueType).put(value, description);
  }

  public void addValue(@Nonnull ResourceLocation value, List<String> description) {
    addValue(value.toString(), description);
  }

  public static void save() {
    if (GameLocation.isValid()) {
      try (FileOutputStream fout = new FileOutputStream(GameLocation.getDATA()); ObjectOutputStream oos = new ObjectOutputStream(fout);) {
        oos.writeObject(VALUES);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  @SuppressWarnings({ "unchecked", "null" })
  public static String read() {
    if (GameLocation.isValid()) {
      try (FileInputStream streamIn = new FileInputStream(GameLocation.getDATA()); ObjectInputStream objectinputstream = new ObjectInputStream(streamIn)) {
        VALUES = (Map<String, Map<String, List<String>>>) objectinputstream.readObject();
        return null;
      } catch (Exception e) {
        e.printStackTrace();
        return e.getLocalizedMessage();
      }
    } else {
      return "Game Location not set";
    }
  }

  public static @Nonnull Map<String, Integer> getCounts() {
    Map<String, Integer> result = new HashMap<>();
    for (Entry<String, Map<String, List<String>>> entry : VALUES.entrySet()) {
      result.put(entry.getKey(), entry.getValue().size());
    }
    return result;
  }

}
