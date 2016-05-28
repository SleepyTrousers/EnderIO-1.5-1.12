package crazypants.enderio.config.recipes.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.config.recipes.StaxFactory;

public abstract class AbstractCrafting extends AbstractConditional {

  private Output output;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    super.readResolve();
    if (output == null) {
      throw new InvalidRecipeConfigException("Missing <output>");
    }

    valid = output.isValid();

    return this;
  }

  public Output getOutput() {
    return output;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("output".equals(name)) {
      Output outputIn = factory.read(new Output(), startElement);
      if (outputIn.isActive()) {
        if (output == null) {
          output = outputIn;
          return true;
        } else {
          throw new InvalidRecipeConfigException("Duplicate <output>");
        }
      } else {
        return true;
      }
    }

    return super.setElement(factory, name, startElement);
  }

}