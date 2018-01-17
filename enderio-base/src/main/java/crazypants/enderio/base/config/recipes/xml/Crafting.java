package crazypants.enderio.base.config.recipes.xml;

import java.util.Arrays;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.GenericUpgradeRecipe;
import crazypants.enderio.base.config.recipes.GenericUpgradeRecipeShapeless;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.integration.jei.JeiAccessor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class Crafting extends AbstractCrafting {

  private Grid grid;

  private Shapeless shapeless;

  private boolean upgrade = false;

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
  public void enforceValidity() throws InvalidRecipeConfigException {
    super.enforceValidity();
    if (grid != null) {
      grid.enforceValidity();
    } else if (shapeless != null) {
      shapeless.enforceValidity();
    }
  }

  @Override
  public void register() {
    if (valid && active) {
      if (grid != null) {
        if (upgrade) {
          Log.debug("Registering GenericUpgradeRecipe: " + getOutput().getItemStack() + ": " + Arrays.toString(grid.getElements()));
          GameRegistry.addRecipe(new GenericUpgradeRecipe(getOutput().getItemStack(), grid.getElements()));
        } else {
          Log.debug("Registering ShapedOreRecipe: " + getOutput().getItemStack() + ": " + Arrays.toString(grid.getElements()));
          GameRegistry.addRecipe(new ShapedOreRecipe(getOutput().getItemStack(), grid.getElements()));
        }
        if (getOutput().hasAlternatives()) {
          getOutput().getAlternatives().apply(new Callback<ItemStack>() {
            @Override
            public void apply(@Nonnull ItemStack alternative) {
              Log.debug("Providing synthetic alternative recipe to JEI for oredicted output: " + alternative + ": " + Arrays.toString(grid.getElements()));
              JeiAccessor.addAlternativeRecipe(new ShapedOreRecipe(alternative, grid.getElements()));
            }
          });
        }
      } else {
        if (upgrade) {
          Log.debug("Registering GenericUpgradeRecipeShapeless: " + getOutput().getItemStack() + ": " + Arrays.toString(shapeless.getElements()));
          GameRegistry.addRecipe(new GenericUpgradeRecipeShapeless(getOutput().getItemStack(), shapeless.getElements()));
        } else {
          Log.debug("Registering ShapelessOreRecipe: " + getOutput().getItemStack() + ": " + Arrays.toString(shapeless.getElements()));
          GameRegistry.addRecipe(new ShapelessOreRecipe(getOutput().getItemStack(), shapeless.getElements()));
        }
        if (getOutput().hasAlternatives()) {
          getOutput().getAlternatives().apply(new Callback<ItemStack>() {
            @Override
            public void apply(@Nonnull ItemStack alternative) {
              Log.debug("Providing synthetic alternative recipe to JEI for oredicted output: " + alternative + ": " + Arrays.toString(shapeless.getElements()));
              JeiAccessor.addAlternativeRecipe(new ShapelessOreRecipe(alternative, shapeless.getElements()));
            }
          });
        }
      }
    } else {
      Log.debug("Skipping Crafting '" + (getOutput() == null ? "null" : getOutput().getItemStack()) + "' (valid=" + valid + ", active=" + active + ")");
    }
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("upgrade".equals(name)) {
      upgrade = value != null && value.trim().length() > 0 && !"no".equals(value.toLowerCase(Locale.ENGLISH))
          && !"false".equals(value.toLowerCase(Locale.ENGLISH));
      return true;
    }
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