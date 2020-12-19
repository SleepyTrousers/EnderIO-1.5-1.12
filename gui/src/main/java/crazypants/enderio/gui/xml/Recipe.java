package crazypants.enderio.gui.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.gui.xml.builder.IXMLBuilder;

public class Recipe extends AbstractConditional {

  private @Nonnull Optional<String> name = empty();

  private final @Nonnull List<AbstractConditional> craftings = new ArrayList<AbstractConditional>();

  private @Nonnull Optional<String> levelName = empty();
  private @Nonnull Optional<RecipeLevel> level = empty();

  @Override
  public void validate() throws InvalidRecipeConfigException {
    super.validate();
    if (!disabled) {
      if (craftings.isEmpty()) {
        throw new InvalidRecipeConfigException("No recipe elements");
      }
      if (levelName.isPresent()) {
        level = ofNullable(RecipeLevel.valueOf(get(levelName)));
        if (!level.isPresent()) {
          throw new InvalidRecipeConfigException("'level' '" + levelName.get() + "' is invalid");
        }
      }
      int count = 0;
      for (AbstractConditional crafting : craftings) {
        if (!crafting.isConditional()) {
          count++;
        }
      }
      if (count > 1) {
        throw new InvalidRecipeConfigException("Multiple active recipe elements");
      }
    }
  }

  @Override
  @Nonnull
  public Object readResolve() throws XMLStreamException {
    if (levelName.isPresent()) {
      level = ofNullable(RecipeLevel.valueOf(get(levelName)));
    } else {
      level = of(RecipeLevel.IGNORE);
    }
    return this;
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull String getName() {
    return name.orElse("unnamed recipe");
  }

  @Override
  public boolean setAttribute(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull String value)
      throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = ofString(value);
      return true;
    }
    if ("required".equals(name)) {
      this.required = Boolean.parseBoolean(value);
      return true;
    }
    if ("disabled".equals(name)) {
      this.disabled = Boolean.parseBoolean(value);
      return true;
    }
    if ("level".equals(name)) {
      this.levelName = ofString(value);
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull StartElement startElement)
      throws InvalidRecipeConfigException, XMLStreamException {
    AbstractConditional element = get(name);
    if (element != null) {
      craftings.add(factory.read(element, startElement));
      return true;
    }
    factory.skip(startElement);
    return true;
    // return super.setElement(factory, name, startElement);
  }

  // @Override
  // public String toString() {
  // StringBuilder builder = new StringBuilder();
  // builder.append("<recipe name='");
  // builder.append(name);
  // builder.append("' required='");
  // builder.append(required);
  // builder.append("'");
  // if (level.isPresent() && get(level) != RecipeLevel.IGNORE) {
  // builder.append(" level='");
  // builder.append(get(getLevelName()));
  // builder.append("'");
  // }
  // if (disabled) {
  // if (craftings.isEmpty()) {
  // builder.append(">");
  // builder.append("<disabled />");
  // } else {
  // builder.append(" disabled='true'>");
  // }
  // }
  // for (AbstractConditional crafting : craftings) {
  // builder.append(crafting);
  // }
  // builder.append("</recipe>");
  // return builder.toString();
  // }

  @Override
  public void write(@Nonnull IXMLBuilder parent) {
    IXMLBuilder ƒ = parent.child("recipe").superCall(super::write).attribute("name", name).attribute("required", required);
    if (level.isPresent() && get(level) != RecipeLevel.IGNORE) {
      ƒ.attribute("level", get(getLevelName()));
    }
    if (disabled) {
      if (craftings.isEmpty()) {
        ƒ.child("disabled");
      } else {
        ƒ.attribute("disabled", disabled);
      }
    }
    craftings.forEach(recipe -> recipe.write(ƒ));
  }

  @Override
  @Nonnull
  public ElementList getSubElements() {
    return super.getSubElements().add(craftings);
  }

  private static final Map<String, Class<? extends AbstractConditional>> MAPPING = new HashMap<>();

  static {
    register(/*
              * Alloying.class, Casting.class, Crafting.class, Brewing.class, Enchanting.class, Fermenting.class, Sagmilling.class, Slicing.class,
              * Smelting.class, Soulbinding.class, Spawning.class, Tanking.class, Hiding.class, Fuel.class, Coolant.class, Disabled.class
              */);
  }

  public static void register(String tagname, Class<? extends AbstractConditional> clazz) {
    MAPPING.put(tagname, clazz);
  }

  @SafeVarargs
  public static void register(Class<? extends AbstractConditional>... clazzes) {
    for (Class<? extends AbstractConditional> clazz : clazzes) {
      MAPPING.put(clazz.getSimpleName().toLowerCase(Locale.ENGLISH), clazz);
    }
  }

  public static @Nullable AbstractConditional get(String tagname) {
    Class<? extends AbstractConditional> clazz = MAPPING.get(tagname);
    if (clazz != null) {
      try {
        return clazz.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }

  public String getNameForMappping(Class<? extends AbstractConditional> clazz) {
    for (Entry<String, Class<? extends AbstractConditional>> entry : MAPPING.entrySet()) {
      if (entry.getValue() == clazz) {
        return entry.getKey();
      }
    }
    return "other";
  }

  public @Nonnull Optional<String> getLevelName() {
    return level.isPresent() ? ofString(get(level).name()) : levelName;
  }

  public void setLevelName(@Nonnull Optional<String> levelName) {
    this.levelName = levelName;
    try {
      readResolve();
    } catch (XMLStreamException e) {
      e.printStackTrace();
    }
  }

  public @Nonnull Optional<RecipeLevel> getLevel() {
    return level;
  }

  public void setLevel(@Nonnull Optional<RecipeLevel> level) {
    this.level = level;
  }

  public void setName(@Nonnull Optional<String> name) {
    this.name = name;
  }

  public List<AbstractConditional> getCraftings() {
    return craftings;
  }

}
