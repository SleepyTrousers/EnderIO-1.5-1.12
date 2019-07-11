package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.recipes.IRecipeConfigElement;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class Hiding extends AbstractConditional {

  public interface IHidingElement extends IRecipeConfigElement {
    void register(String recipeName);
  }

  private final NNList<IHidingElement> elements = new NNList<>();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (elements.isEmpty()) {
        throw new InvalidRecipeConfigException("Missing <item> or <fluid>");
      }

      valid = true;
      for (IHidingElement element : elements) {
        valid = valid && element.isValid();
      }

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <hiding>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    for (IHidingElement element : elements) {
      element.enforceValidity();
    }
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (isValid() && isActive()) {
      for (IHidingElement element : elements) {
        element.register(recipeName);
      }
    }
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("item".equals(name)) {
      elements.add(factory.read(new ItemHiding(), startElement));
      return true;
    }
    if ("fluid".equals(name)) {
      elements.add(factory.read(new FluidHiding(), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

}
