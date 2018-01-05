package crazypants.enderio.base.config.recipes.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;

public abstract class AbstractCrafting extends AbstractConditional {

  private List<Output> outputs;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    super.readResolve();
    if (outputs == null || outputs.isEmpty()) {
      throw new InvalidRecipeConfigException("Missing <output>");
    }

    int count = 0;
    for (Output output : outputs) {
      if (output.isValid() && output.isActive()) {
        count++;
      }
    }

    valid = count == 1;

    return this;
  }

  public Output getOutput() {
    for (Output output : outputs) {
      if (output.isValid() && output.isActive()) {
        return output;
      }
    }
    return null;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("output".equals(name)) {
      if (outputs == null) {
        outputs = new ArrayList<Output>();
      }
      outputs.add(factory.read(new Output(), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    for (Output output : outputs) {
      if (output.isActive()) {
        output.enforceValidity();
      }
    }
  }

}