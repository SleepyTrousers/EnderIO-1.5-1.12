package crazypants.enderio.base.config.recipes.xml;

import javax.xml.stream.XMLStreamException;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class OutputWithChance extends Output {

  private float chance = 1f;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    super.readResolve();
    if (chance < 0) {
      throw new InvalidRecipeConfigException("Invalid negative chance in <output>");
    }
    if (chance > 1) {
      throw new InvalidRecipeConfigException("Invalid chance above 100% in <output>");
    }

    return this;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("chance".equals(name)) {
      this.chance = Float.valueOf(value);
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

  public float getChance() {
    return chance;
  }

}
