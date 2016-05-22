package crazypants.util;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeUtil {

  public static void addShaped(@Nonnull Item res, Object... recipe) {
    addShaped(new ItemStack(res), recipe);
  }

  public static void addShaped(@Nonnull Block res, Object... recipe) {
    addShaped(new ItemStack(res), recipe);
  }

  public static void addShaped(@Nonnull ItemStack res, Object... recipe) {
    GameRegistry.addRecipe(new ShapedOreRecipe(res, recipe));
  }

  public static void addShapeless(@Nonnull Item res, Object... recipe) {
    addShapeless(new ItemStack(res), recipe);
  }

  public static void addShapeless(@Nonnull Block res, Object... recipe) {
    addShapeless(new ItemStack(res), recipe);
  }

  public static void addShapeless(@Nonnull ItemStack res, Object... recipe) {
    GameRegistry.addRecipe(new ShapelessOreRecipe(res, recipe));
  }
}
