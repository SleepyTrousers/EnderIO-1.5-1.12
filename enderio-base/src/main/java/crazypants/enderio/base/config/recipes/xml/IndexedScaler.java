package crazypants.enderio.base.config.recipes.xml;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class IndexedScaler extends Scaler {

  private float step = 1f;
  private List<Data> data = new NNList<>();

  @Override
  public Scaler readResolve() throws InvalidRecipeConfigException, XMLStreamException {
    try {
      if (step <= 0f) {
        throw new InvalidRecipeConfigException("'step' is invalid");
      }

      boolean valid = !data.isEmpty();

      float[] dataArray = new float[data.size()];
      for (int i = 0; i < data.size(); i++) {
        valid &= data.get(i).isValid();
        if (valid) {
          dataArray[i] = data.get(i).getValue();
        }
      }

      if (!valid) {
        throw new InvalidRecipeConfigException("no <data>");
      }

      scaler = new crazypants.enderio.base.capacitor.IndexedScaler(step, dataArray);
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <indexed>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
  }

  @Override
  public boolean isValid() {
    return scaler != null;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("step".equals(name)) {
      this.step = Float.parseFloat(value);
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("data".equals(name)) {
      data.add(factory.read(new Data(), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

}
