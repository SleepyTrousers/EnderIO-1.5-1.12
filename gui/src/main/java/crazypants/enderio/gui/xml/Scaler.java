package crazypants.enderio.gui.xml;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.gui.xml.builder.IXMLBuilder;

public class Scaler implements IRecipeConfigElement {

  private @Nonnull Optional<String> name = empty();
  private @Nonnull Optional<ScalerValues> scaler = empty();

  public Scaler() {
  }

  public Scaler(String name) {
    this.name = ofString(name);
  }

  @Override
  public void validate() throws InvalidRecipeConfigException {
    if (!name.isPresent()) {
      throw new InvalidRecipeConfigException("name is missing");
    }
    if (!scaler.isPresent()) {
      throw new InvalidRecipeConfigException("'name' '" + name.get() + "' is invalid");
    }
  }

  @Override
  @Nonnull
  public Scaler readResolve() throws XMLStreamException {
    if (name.isPresent()) {
      IndexedScaler indexedScaler = parseIndexed(get(name));
      if (indexedScaler != null) {
        return indexedScaler.readResolve();
      }
      scaler = ofNullable(parseEnum(get(name)));
    }
    return this;
  }

  @Override
  public boolean setAttribute(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull String value)
      throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = ofString(value);
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull StartElement startElement)
      throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  public String getScalerString() {
    return get(name);
  }

  public void setScalerString(String s) {
    name = ofString(s);
    scaler = ofNullable(parseEnum(get(name)));
  }

  // @Override
  // public String toString() {
  // StringBuilder builder = new StringBuilder();
  // builder.append("<scaler name='");
  // builder.append(get(name));
  // builder.append("' />");
  // return builder.toString();
  // }

  @Override
  public void write(@Nonnull IXMLBuilder parent) {
    parent.child("scaler").attribute("name", name);
  }

  public static @Nullable IndexedScaler parseIndexed(@Nullable String s) {
    if (s == null) {
      return null;
    }
    if (s.startsWith("idx(")) {
      s = s.replace('(', ':').replace(')', ':').replaceAll("::", ":");
    }
    if (s.startsWith("idx:")) {
      try {
        IndexedScaler indexedScaler = new IndexedScaler();
        String[] split = s.split(":");
        int i = -2;
        for (String sub : split) {
          if (i >= -1) {
            Float value = Float.valueOf(sub);
            if (i == -1) {
              indexedScaler.setStep(value);
            } else {
              indexedScaler.addData(value);
            }
          }
          i++;
        }
        return indexedScaler;
      } catch (NumberFormatException e) {
        return null;
      }
    }
    return null;
  }

  public static @Nullable ScalerValues parseEnum(@Nullable String s) {
    try {
      return ScalerValues.valueOf(s);
    } catch (Exception e) {
      return null;
    }
  }

  public enum ScalerValues {
    INVALID,
    IDENTITY,
    LINEAR_0_8,
    QUADRATIC,
    QUADRATIC_1_8,
    CUBIC,
    OCTADIC_1_8,
    POWER,
    CHARGE,
    SPEED,
    POWER10,
    RANGE,
    FIXED,
    SPAWNER,
    BURNTIME,
    CHEMICAL,
    DROPOFF,
    CENT,

    ;
  }
}
