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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
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

  private @Nonnull ResourceLocation mkRL(@Nonnull String recipeName) {
    String s = recipeName.replaceAll("[^A-Za-z]", "_").replaceAll("([A-Z])", "_$0").replaceAll("__+", "_").replaceFirst("^_", "").replaceFirst("_$", "")
        .toLowerCase(Locale.ENGLISH);
    ModContainer activeMod = Loader.instance().activeModContainer();
    if (activeMod == null) {
      throw new RuntimeException("I really doubt Mojang would ever use our recipe handler, so something just went really wrong because "
          + "Forge just told us that we were called by vanilla code...");
    }
    String modId = activeMod.getModId();
    if (modId == null) {
      throw new RuntimeException("Why does a mod without an ID use our recipe handler?");
    }
    return new ResourceLocation(modId, s);
  }

  private void log(String recipeType, String recipeName, ResourceLocation recipeRL, ItemStack result, Object[] inputs) {
    Log.debug("Registering " + recipeType + " for '" + recipeName + "' as '" + recipeRL + "': " + result + ": " + Arrays.toString(inputs));
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (valid && active) {
      final ResourceLocation recipeRL = mkRL(recipeName);
      if (grid != null) {
        if (upgrade) {
          log("GenericUpgradeRecipe", recipeName, recipeRL, getOutput().getItemStack(), grid.getElements());
          ForgeRegistries.RECIPES.register(new GenericUpgradeRecipe(getOutput().getItemStack(), grid.getElements()).setRegistryName(recipeRL));
        } else {
          log("ShapedOreRecipe", recipeName, recipeRL, getOutput().getItemStack(), grid.getElements());
          ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, getOutput().getItemStack(), grid.getElements()).setRegistryName(recipeRL));
        }
        if (getOutput().hasAlternatives()) {
          getOutput().getAlternatives().apply(new Callback<ItemStack>() {
            @Override
            public void apply(@Nonnull ItemStack alternative) {
              Log.debug("Providing synthetic alternative recipe to JEI for oredicted output: " + alternative + ": " + Arrays.toString(grid.getElements()));
              JeiAccessor.addAlternativeRecipe(new ShapedOreRecipe(null, alternative, grid.getElements()));
            }
          });
        }
      } else {
        if (upgrade) {
          log("GenericUpgradeRecipeShapeless", recipeName, recipeRL, getOutput().getItemStack(), shapeless.getElements());
          ForgeRegistries.RECIPES.register(new GenericUpgradeRecipeShapeless(getOutput().getItemStack(), shapeless.getElements()).setRegistryName(recipeRL));
        } else {
          log("ShapelessOreRecipe", recipeName, recipeRL, getOutput().getItemStack(), shapeless.getElements());
          ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, getOutput().getItemStack(), shapeless.getElements()).setRegistryName(recipeRL));
        }
        if (getOutput().hasAlternatives()) {
          getOutput().getAlternatives().apply(new Callback<ItemStack>() {
            @Override
            public void apply(@Nonnull ItemStack alternative) {
              Log.debug("Providing synthetic alternative recipe to JEI for oredicted output: " + alternative + ": " + Arrays.toString(shapeless.getElements()));
              JeiAccessor.addAlternativeRecipe(new ShapelessOreRecipe(null, alternative, shapeless.getElements()));
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