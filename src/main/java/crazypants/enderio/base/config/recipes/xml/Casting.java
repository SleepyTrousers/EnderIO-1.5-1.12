package crazypants.enderio.base.config.recipes.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.integration.tic.TicProxy;

public class Casting extends AbstractCrafting {

  private FloatItem input;
  private Item cast;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (input == null) {
        throw new InvalidRecipeConfigException("Missing <input>");
      }

      valid = valid && input.isValid() && (cast == null ? true : cast.isValid());

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <casting>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    super.enforceValidity();
    input.enforceValidity();

    String tableCast = TicProxy.registerTableCast(getOutput().getItemStack(), cast.getItemStack(), input.getItemStack(), input.amount, true);
    if (tableCast != null) {
      throw new InvalidRecipeConfigException(tableCast);
    }
  }

  @Override
  public boolean isActive() {
    return TicProxy.isLoaded() && super.isActive();
  }

  @Override
  public void register() {
    if (isValid() && isActive()) {

      String tableCast = TicProxy.registerTableCast(getOutput().getItemStack(), cast.getItemStack(), input.getItemStack(), input.amount, false);
      if (tableCast != null) {
        throw new RuntimeException(tableCast);
      }
    }
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("input".equals(name)) {
      if (input == null) {
        input = factory.read(new FloatItem(), startElement);
        return true;
      }
    }
    if ("cast".equals(name)) {
      if (cast == null) {
        cast = factory.read(new Item(), startElement);
        return true;
      }
    }

    return super.setElement(factory, name, startElement);
  }

}