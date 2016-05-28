package crazypants.enderio.config.recipes.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.config.recipes.StaxFactory;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Smelting extends AbstractCrafting {

  private float exp;

  private Item input;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (exp < 0) {
        throw new InvalidRecipeConfigException("Invalid negative value for 'exp'");
      }
      if (exp > 1) {
        throw new InvalidRecipeConfigException("Invalid value for 'exp', above 100%");
      }
      if (input == null) {
        throw new InvalidRecipeConfigException("Missing <input>");
      }

      valid = valid && input.isValid();

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <crafting>");
    }
    return this;
  }

  @Override
  public void register() {
    if (isValid() && isActive()) {
      GameRegistry.addSmelting(input.getItemStack(), getOutput().getItemStack(), exp);
    }
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("exp".equals(name)) {
      this.exp = Float.parseFloat(value);
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("input".equals(name)) {
      if (input == null) {
        input = factory.read(new Item(), startElement);
        return true;
      }
    }

    return super.setElement(factory, name, startElement);
  }

}