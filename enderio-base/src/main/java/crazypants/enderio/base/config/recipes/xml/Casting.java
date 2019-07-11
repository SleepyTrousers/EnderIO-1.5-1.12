package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.integration.tic.TicProxy;

public class Casting extends AbstractCrafting {

  private Optional<ItemFloatAmount> input = empty();
  private Optional<ItemConsumable> cast = empty();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (!input.isPresent()) {
        throw new InvalidRecipeConfigException("Missing <input>");
      }

      valid = valid && input.get().isValid() && (!cast.isPresent() || cast.get().isValid());

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <casting>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    super.enforceValidity();
    input.get().enforceValidity();
  }

  @Override
  public boolean isActive() {
    return super.isActive();
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (isValid() && isActive()) {
      if (TicProxy.isLoaded()) {
        if (cast.isPresent()) {
          TicProxy.registerTableCast(getOutput().getThing(), cast.get().getThing(), input.get().getThing(), input.get().amount, cast.get().getConsumed());
        } else {
          TicProxy.registerTableCast(getOutput().getThing(), new Things(), input.get().getThing(), input.get().amount, false);
        }
      } else {
        Log.warn("TiC recipe is active, but TiC integration is not loaded");
      }
    }
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("input".equals(name) && !input.isPresent()) {
      input = of(factory.read(new ItemFloatAmount().setAllowDelaying(true), startElement));
        return true;
    }
    if ("cast".equals(name) && !cast.isPresent()) {
      cast = of(factory.read(new ItemConsumable().setAllowDelaying(true), startElement));
        return true;
    }

    return super.setElement(factory, name, startElement);
  }

}