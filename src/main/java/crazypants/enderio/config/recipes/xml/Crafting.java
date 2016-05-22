package crazypants.enderio.config.recipes.xml;

import java.util.Arrays;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import crazypants.enderio.Log;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class Crafting extends AbstractCrafting {

  @XStreamAlias("grid")
  private Grid grid;

  @XStreamAlias("shapeless")
  private Shapeless shapeless;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (grid != null) {
        if (shapeless != null) {
          throw new InvalidRecipeConfigException("Cannot have both <grid> and <shapeless>");
        }
        valid = valid && grid.isValid();
      } else if (shapeless != null) {
        valid = valid && shapeless.isValid();
      } else {
        throw new InvalidRecipeConfigException("Missing <grid> and <shapeless>");
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <crafting>");
    }
    return this;
  }

  @Override
  public void register() {
    if (valid && active) {
      if (grid != null) {
        Log.debug("Registering ShapedOreRecipe: "+getOutput().getItemStack()+": "+Arrays.toString(grid.getElements()));
        GameRegistry.addRecipe(new ShapedOreRecipe(getOutput().getItemStack(), grid.getElements()));
      } else {
        Log.debug("Registering Shaped ShapelessOreRecipe: "+getOutput().getItemStack()+": "+Arrays.toString(shapeless.getElements()));
        GameRegistry.addRecipe(new ShapelessOreRecipe(getOutput().getItemStack(), shapeless.getElements()));
      }
    } else {
      Log.debug("Skipping Crafting '" + (getOutput() == null ? "null" : getOutput().getItemStack()) + "' (valid=" + valid + ", active=" + active + ")");
    }
  }

}