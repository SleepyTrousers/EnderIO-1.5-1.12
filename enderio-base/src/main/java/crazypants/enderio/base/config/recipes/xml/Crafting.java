package crazypants.enderio.base.config.recipes.xml;

import java.util.Locale;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import org.apache.logging.log4j.util.Strings;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.ShapedRecipe;
import crazypants.enderio.base.config.recipes.ShapelessRecipe;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.integration.jei.JeiAccessor;
import crazypants.enderio.base.recipe.RecipeLevel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class Crafting extends AbstractCrafting {

  private Optional<Grid> grid = empty();

  private Optional<Shapeless> shapeless = empty();

  private boolean upgrade = false;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (grid.isPresent() == shapeless.isPresent()) {
        throw new InvalidRecipeConfigException("Exactly one of either <grid> or <shapeless> must be specified");
      }
      if (grid.isPresent()) {
        valid = valid && grid.get().isValid();
      } else {
        valid = valid && shapeless.get().isValid();
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <crafting>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    super.enforceValidity();
    if (grid.isPresent()) {
      grid.get().enforceValidity();
    } else if (shapeless.isPresent()) {
      shapeless.get().enforceValidity();
    }
  }

  public static ResourceLocation mkRL(String recipeName, Object... params) {
    return mkRL(String.format(recipeName, params));
  }

  public static ResourceLocation mkRL(String recipeName) {
    String s = recipeName.replaceAll("[^A-Za-z0-9]", "_").replaceAll("([A-Z])", "_$0").replaceAll("__+", "_").replaceFirst("^_", "").replaceFirst("_$", "")
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

  private void log(String recipeType, String recipeName, ResourceLocation recipeRL, ItemStack result, NNList<Ingredient> nnList) {
    Log.debug("Registering " + recipeType + " for '" + recipeName + "' as '" + recipeRL + "': " + result + ": " + nnList);
  }

  @Override
  public void register(@Nonnull String recipeName, @Nonnull RecipeLevel recipeLevel) {
    if (valid && active) {
      final ResourceLocation recipeRL = mkRL(recipeName);
      final IForgeRegistry<IRecipe> registry = ForgeRegistries.RECIPES;
      if (grid.isPresent()) {
        @SuppressWarnings("hiding")
        Grid grid = this.grid.get();
        if (upgrade) {
          log("ShapedRecipe.Upgrade", recipeName, recipeRL, getOutput().getItemStack(), grid.getIngredients());
          registry.register(new ShapedRecipe.Upgrade(recipeRL, grid.getWidth(), grid.getHeight(), grid.getIngredients(), getOutput().getThing()));
        } else {
          log("ShapedRecipe", recipeName, recipeRL, getOutput().getItemStack(), grid.getIngredients());
          registry.register(new ShapedRecipe(recipeRL, grid.getWidth(), grid.getHeight(), grid.getIngredients(), getOutput().getThing()));
        }
        if (getOutput().hasAlternatives()) {
          getOutput().getAlternatives().apply(new Callback<ItemStack>() {
            @Override
            public void apply(@Nonnull ItemStack alternative) {
              Log.debug("Providing synthetic alternative recipe to JEI for oredicted output: " + alternative + ": " + grid.getIngredients());
              JeiAccessor.addAlternativeRecipe(new ShapedRecipes("", grid.getWidth(), grid.getHeight(), grid.getIngredients(), alternative));
            }
          });
        }
      } else {
        @SuppressWarnings("hiding")
        Shapeless shapeless = this.shapeless.get();
        if (upgrade) {
          log("GenericUpgradeRecipeShapeless", recipeName, recipeRL, getOutput().getItemStack(), shapeless.getIngredients());
          registry.register(new ShapelessRecipe.Upgrade(recipeRL, shapeless.getIngredients(), getOutput().getThing()));
        } else {
          log("ShapelessOreRecipe", recipeName, recipeRL, getOutput().getItemStack(), shapeless.getIngredients());
          registry.register(new ShapelessRecipe(recipeRL, shapeless.getIngredients(), getOutput().getThing()));
        }
        if (getOutput().hasAlternatives()) {
          getOutput().getAlternatives().apply(new Callback<ItemStack>() {
            @Override
            public void apply(@Nonnull ItemStack alternative) {
              Log.debug("Providing synthetic alternative recipe to JEI for oredicted output: " + alternative + ": " + shapeless.getIngredients());
              JeiAccessor.addAlternativeRecipe(new ShapelessRecipes("", alternative, shapeless.getIngredients()));
            }
          });
        }
      }

      if (recipeLevel != RecipeLevel.IGNORE) {
        Log.warn("Ignoring recipe level " + recipeLevel + " configured for vanilla crafting recipe '" + recipeName
            + "'---the vanilla crafting table doesn't have (or support) levels");
        // TODO: We could add the level to the crafting recipes for our Crafter. But unless we also get to show that in JEI it would make thsi weird for
        // players. So I'm skipping this for now.
      }
    } else {
      Log.debug("Skipping Crafting '" + (getOutputs().isEmpty() ? "null" : getOutput().getItemStack()) + "' (valid=" + valid + ", active=" + active + ")");
    }
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("upgrade".equals(name)) {
      upgrade = !Strings.isBlank(value) && !"no".equals(value.toLowerCase(Locale.ENGLISH)) && !"false".equals(value.toLowerCase(Locale.ENGLISH));
      return true;
    }
    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("grid".equals(name) && !grid.isPresent()) {
      grid = of(factory.read(new Grid(), startElement));
      return true;
    }
    if ("shapeless".equals(name) && !shapeless.isPresent()) {
      shapeless = of(factory.read(new Shapeless(), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

}
