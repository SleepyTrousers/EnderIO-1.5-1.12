package crazypants.enderio.base.config.recipes.xml;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import info.loenwind.autoconfig.util.NullHelper;

public class Recipe extends AbstractConditional {

  private Optional<String> name = empty();

  private boolean required;

  private boolean disabled;

  private final NNList<AbstractConditional> craftings = new NNList<AbstractConditional>();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (disabled) {
      return this;
    }
    try {
      super.readResolve();
      if (craftings.isEmpty()) {
        throw new InvalidRecipeConfigException("No recipe elements");
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <recipe> '" + getName() + "'");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (disabled || !active) {
      return;
    }
    try {
      int count = 0;
      for (AbstractConditional crafting : craftings) {
        if (required) {
          if (crafting.isActive()) {
            crafting.enforceValidity();
            if (crafting.isValid()) {
              count++;
            }
          }
        } else {
          if (crafting.isActive() && crafting.isValid()) {
            count++;
          }
        }
      }
      if (count > 1) {
        throw new InvalidRecipeConfigException("Multiple active recipe elements");
      } else if (count < 1) {
        if (required) {
          throw new InvalidRecipeConfigException("No valid recipe elements");
        } else {
          valid = false;
        }
      } else {
        valid = true;
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <recipe> '" + getName() + "'");
    }
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (!disabled && valid && active) {
      Log.debug("Registering XML recipe '" + getName() + "'");
      for (AbstractConditional crafting : craftings) {
        if (crafting.isValid() && crafting.isActive()) {
          crafting.register(recipeName);
          return;
        }
      }
    } else {
      Log.debug("Skipping XML recipe '" + getName() + "' (valid=" + valid + ", active=" + active + ", required=" + required + ", disabled=" + disabled + ")");
    }
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull String getName() {
    return name.orElse("unnamed recipe");
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
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

    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    try {
      AbstractConditional element = get(NullHelper.first(name, ""));
      if (element != null) {
        craftings.add(factory.read(element, startElement));
        return true;
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <" + name + "> in <recipe name=\"" + getName() + "\"");
    }

    return super.setElement(factory, name, startElement);
  }

  @Override
  public boolean isValid() {
    return disabled || super.isValid();
  }

  @Override
  public boolean isActive() {
    return !disabled && super.isActive();
  }

  private static final Map<String, Class<? extends AbstractConditional>> MAPPING = new HashMap<>();

  static {
    register(Alloying.class, Casting.class, Crafting.class, Brewing.class, Enchanting.class, Fermenting.class, Sagmilling.class, Slicing.class, Smelting.class,
        Soulbinding.class, Spawning.class, Tanking.class, Hiding.class, Fuel.class, Coolant.class, Disabled.class);
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
        return NullHelper.notnullJ(clazz.newInstance(), "Class.newInstance()");
      } catch (InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }

}
