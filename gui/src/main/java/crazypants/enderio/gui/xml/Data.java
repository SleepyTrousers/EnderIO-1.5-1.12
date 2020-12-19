package crazypants.enderio.gui.xml;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.gui.xml.builder.IXMLBuilder;

public class Data implements IRecipeConfigElement {

  private float value = Float.NaN;

  @Override
  public void validate() throws InvalidRecipeConfigException {
    if (Float.isNaN(value)) {
      throw new InvalidRecipeConfigException("'value' is invalid");
    }
  }

  @Override
  public boolean setAttribute(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull String value)
      throws InvalidRecipeConfigException, XMLStreamException {
    if ("value".equals(name)) {
      this.value = Float.parseFloat(value);
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull StartElement startElement)
      throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  public float getValue() {
    return value;
  }

  protected void setValue(float value) {
    this.value = value;
  }

  // @Override
  // public String toString() {
  // StringBuilder builder = new StringBuilder();
  // builder.append("<data value='");
  // builder.append(value);
  // builder.append("' />");
  // return builder.toString();
  // }

  @Override
  public void write(@Nonnull IXMLBuilder parent) {
    parent.child("data").attribute("value", value);
  }

}
