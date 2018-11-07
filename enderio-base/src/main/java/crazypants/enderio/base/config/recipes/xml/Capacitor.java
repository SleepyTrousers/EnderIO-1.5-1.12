package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.capacitor.CapacitorKeyRegistry;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraft.util.ResourceLocation;

public class Capacitor extends AbstractConditional {

  private ResourceLocation key;

  private boolean required;

  private boolean disabled;

  private int base = Integer.MIN_VALUE;

  private Scaler scaler;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (disabled) {
      return this;
    }
    try {
      super.readResolve();
      if (scaler == null) {
        throw new InvalidRecipeConfigException("Missing <scaler> or <indexed>");
      }
      if (base == Integer.MIN_VALUE) {
        throw new InvalidRecipeConfigException("'base' is invalid");
      }
      if (key != null) {
        valid = CapacitorKeyRegistry.contains(key);
        if (required && !valid && active) {
          throw new InvalidRecipeConfigException("'key' is invalid");
        }
      } else {
        throw new InvalidRecipeConfigException("'key' is invalid");
      }

      valid = valid && scaler.isValid();
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
    scaler.enforceValidity();
    if (key == null || !CapacitorKeyRegistry.contains(key)) {
      throw new InvalidRecipeConfigException("'key' '" + key + "' is invalid");
    }
  }

  @SuppressWarnings("null")
  @Override
  public void register(@Nonnull String recipeName) {
    if (!disabled && valid && active) {
      Log.debug("Registering XML recipe '" + getName() + "'");

      CapacitorKeyRegistry.setValue(key, base, scaler.getScaler(), scaler.getScalerString());

    } else {
      Log.debug("Skipping XML recipe '" + getName() + "' (valid=" + valid + ", active=" + active + ", required=" + required + ", disabled=" + disabled + ")");
    }
  }

  @Override
  public @Nonnull String getName() {
    if (key != null) {
      return key.toString();
    }
    return "unnamed recipe";
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("key".equals(name)) {
      this.key = new ResourceLocation(NullHelper.first(value, ""));
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
    if ("scaler".equals(name)) {
      if (scaler == null) {
        scaler = new Scaler(value).readResolve();
        return true;
      }
    }

    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("scaler".equals(name)) {
      if (scaler == null) {
        scaler = factory.read(new Scaler(), startElement);
        return true;
      }
    }
    if ("indexed".equals(name)) {
      if (scaler == null) {
        scaler = factory.read(new IndexedScaler(), startElement);
        return true;
      }
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
