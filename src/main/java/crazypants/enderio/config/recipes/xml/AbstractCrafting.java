package crazypants.enderio.config.recipes.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public abstract class AbstractCrafting extends AbstractConditional {

  @XStreamAlias("output")
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

}