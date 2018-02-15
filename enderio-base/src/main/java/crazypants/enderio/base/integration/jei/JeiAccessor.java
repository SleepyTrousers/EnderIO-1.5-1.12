package crazypants.enderio.base.integration.jei;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import net.minecraft.item.crafting.IRecipe;

public class JeiAccessor {

  static boolean jeiRuntimeAvailable = false;

  public static boolean isJeiRuntimeAvailable() {
    return jeiRuntimeAvailable;
  }

  public static void setFilterText(@Nonnull String filterText) {
    if (jeiRuntimeAvailable) {
      JeiPlugin.setFilterText(filterText);
    }
  }

  public static @Nonnull String getFilterText() {
    if (jeiRuntimeAvailable) {
      return JeiPlugin.getFilterText();
    }
    return "";
  }

  static final @Nonnull NNList<IRecipe> ALTERNATIVES = new NNList<>();

  public static void addAlternativeRecipe(@Nonnull IRecipe recipe) {
    ALTERNATIVES.add(recipe);
  }

}
