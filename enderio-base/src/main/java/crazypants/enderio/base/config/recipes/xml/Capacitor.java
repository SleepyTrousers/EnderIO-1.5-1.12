package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.capacitor.CapacitorKeyRegistry;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.recipe.RecipeLevel;
import net.minecraft.util.ResourceLocation;

public class Capacitor extends AbstractConditional {

  private Optional<ResourceLocation> key = empty();

  private boolean required;

  private boolean disabled;

  private int base = Integer.MIN_VALUE;

  private Optional<Scaler> scaler = empty();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (disabled) {
      return this;
    }
    try {
      super.readResolve();
      if (!scaler.isPresent()) {
        throw new InvalidRecipeConfigException("Missing <scaler> or <indexed>");
      }
      if (base == Integer.MIN_VALUE) {
        throw new InvalidRecipeConfigException("'base' is invalid");
      }
      if (key.isPresent()) {
        valid = CapacitorKeyRegistry.contains(get(key));
        if (required && !valid && active) {
          throw new InvalidRecipeConfigException("'key' is invalid");
        }
      } else {
        throw new InvalidRecipeConfigException("'key' is invalid");
      }

      valid = valid && scaler.get().isValid();
      if (required && !valid && active) {
        throw new InvalidRecipeConfigException("No valid <scaler> or <indexed>");
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <capacitor>");
    }
    return this;
  }

  @SuppressWarnings("null")
  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (disabled || !active) {
      return;
    }
    scaler.get().enforceValidity();
    if (!key.isPresent() || !CapacitorKeyRegistry.contains(key.get())) {
      throw new InvalidRecipeConfigException("'key' '" + key.get() + "' is invalid");
    }
  }

  @SuppressWarnings("null")
  @Override
  public void register(@Nonnull String recipeName, @Nonnull RecipeLevel recipeLevel) {
    if (!disabled && valid && active) {
      Log.debug("Registering XML recipe '" + getName() + "'");

      // TODO 1.16: we need to know if this is the base value (setValue) or an override (setOverride) here. Alternatively we can remove the registry's override
      // funtionality and rely on re-registering core values. That's what other registries will be doing. It's just that we always need default values, unlike
      // real recipe registries.
      CapacitorKeyRegistry.setValue(key.get(), base, scaler.get().getScaler(), scaler.get().getScalerString());
    } else {
      Log.debug("Skipping XML recipe '" + getName() + "' (valid=" + valid + ", active=" + active + ", required=" + required + ", disabled=" + disabled + ")");
    }
  }

  @Override
  public @Nonnull String getName() {
    if (key.isPresent()) {
      return key.get().toString();
    }
    return "unnamed recipe";
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("key".equals(name)) {
      this.key = of(new ResourceLocation(NullHelper.first(value, "")));
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
    if ("base".equals(name)) {
      this.base = Integer.parseInt(value);
      return true;
    }
    if ("scaler".equals(name) && !scaler.isPresent()) {
      scaler = of(new Scaler(value).readResolve());
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("scaler".equals(name) && !scaler.isPresent()) {
      scaler = of(factory.read(new Scaler(), startElement));
      return true;
    }
    if ("indexed".equals(name) && !scaler.isPresent()) {
      scaler = of(factory.read(new IndexedScaler(), startElement));
      return true;
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

}
