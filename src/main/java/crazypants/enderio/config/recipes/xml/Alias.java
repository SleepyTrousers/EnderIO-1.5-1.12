package crazypants.enderio.config.recipes.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import crazypants.util.Things;

public class Alias extends AbstractConditional {

  @XStreamAlias("name")
  @XStreamAsAttribute
  private String name;

  @XStreamAlias("item")
  @XStreamAsAttribute
  private String item;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in alias '" + item + "'");
    }
    if (isActive()) {
      Things.addAlias(name, item);
    }
    return this;
  }

  @Override
  public void register() {
  }

}
