package crazypants.enderio.gui.xml;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.gui.xml.builder.IXMLBuilder;

public class IndexedScaler extends Scaler {

  private float step = 1f;
  private List<Data> data = new ArrayList<>();

  @Override
  public void validate() throws InvalidRecipeConfigException {
    if (step <= 0f) {
      throw new InvalidRecipeConfigException("'step' is invalid");
    }
    if (data.isEmpty()) {
      throw new InvalidRecipeConfigException("no <data>");
    }
  }

  @Override
  public boolean setAttribute(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull String value)
      throws InvalidRecipeConfigException, XMLStreamException {
    if ("step".equals(name)) {
      this.step = Float.parseFloat(value);
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull StartElement startElement)
      throws InvalidRecipeConfigException, XMLStreamException {
    if ("data".equals(name)) {
      data.add(factory.read(new Data(), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

  @Override
  public @Nonnull String getScalerString() {
    StringBuffer sb = new StringBuffer();
    sb.append("idx(");
    sb.append(step);
    sb.append(")");
    for (Data f : data) {
      sb.append(":");
      sb.append(f.getValue());
    }
    return sb.toString();
  }

  @Override
  @Nonnull
  public ElementList getSubElements() {
    return super.getSubElements().add(data);
  }

  // @Override
  // public String toString() {
  // StringBuilder builder = new StringBuilder();
  // builder.append("<indexed step='");
  // builder.append(step);
  // builder.append("'>");
  // for (Data f : data) {
  // builder.append(f);
  // }
  // builder.append("</indexed>");
  // return builder.toString();
  // }

  @Override
  public void write(@Nonnull IXMLBuilder parent) {
    IXMLBuilder ƒ = parent.child("indexed").attribute("step", step);
    data.forEach(e -> e.write(ƒ));
  }

  protected void setStep(float step) {
    this.step = step;
  }

  protected void addData(float value) {
    Data d = new Data();
    d.setValue(value);
    data.add(d);
  }

}
