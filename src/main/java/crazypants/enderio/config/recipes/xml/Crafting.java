package crazypants.enderio.config.recipes.xml;

import java.util.Arrays;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.Log;
import crazypants.enderio.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.config.recipes.StaxFactory;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class Crafting extends AbstractCrafting {

  private Grid grid;

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
        Log.debug("Registering ShapelessOreRecipe: " + getOutput().getItemStack() + ": " + Arrays.toString(shapeless.getElements()));
        GameRegistry.addRecipe(new ShapelessOreRecipe(getOutput().getItemStack(), shapeless.getElements()));
      }
    } else {
      Log.debug("Skipping Crafting '" + (getOutput() == null ? "null" : getOutput().getItemStack()) + "' (valid=" + valid + ", active=" + active + ")");
    }
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("grid".equals(name)) {
      if (grid == null) {
        grid = factory.read(new Grid(), startElement);
        return true;
      }
    }
    if ("shapeless".equals(name)) {
      if (shapeless == null) {
        shapeless = factory.read(new Shapeless(), startElement);
        return true;
      }
    }

    return super.setElement(factory, name, startElement);
  }

}