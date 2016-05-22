package crazypants.enderio.config.recipes.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class Smelting extends AbstractCrafting {

  @XStreamAsAttribute
  @XStreamAlias("exp")
  private float exp;

  @XStreamAlias("input")
  private Output input;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (exp < 0) {
        throw new InvalidRecipeConfigException("Invalid negative value for 'exp'");
      }
      if (exp > 1) {
        throw new InvalidRecipeConfigException("Invalid value for 'exp', above 100%");
      }
      if (input == null) {
        throw new InvalidRecipeConfigException("Missing <input>");
      }

      valid = valid && input.isValid();

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <crafting>");
    }
    return this;
  }

  @Override
  public void register() {
    if (isValid() && isActive()) {
      GameRegistry.addSmelting(input.getItemStack(), getOutput().getItemStack(), exp);
    }
  }

}