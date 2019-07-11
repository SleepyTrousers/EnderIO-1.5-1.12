package crazypants.enderio.base.config.recipes.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;

public abstract class AbstractCrafting extends AbstractConditional {

  protected final NNList<Output> outputs = new NNList<>();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    super.readResolve();
    if (outputs.isEmpty()) {
      throw new InvalidRecipeConfigException("Missing <output>");
    }

    final List<Output> activeOutputs = getOutputs();
    valid = checkOutputCount(activeOutputs.size());
    for (Output output : activeOutputs) {
      valid = valid && output.isValid();
    }

    return this;
  }

  protected boolean checkOutputCount(int count) {
    return count == 1;
  }

  public Output getOutput() {
    for (Output output : outputs) {
      if (output.isActive()) {
        return output;
      }
    }
    throw new RuntimeException("Missing <output>");
  }

  public List<Output> getOutputs() {
    List<Output> result = new ArrayList<Output>();
    for (Output output : outputs) {
      if (output.isActive()) {
        result.add(output);
      }
    }
    return result;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("output".equals(name)) {
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
