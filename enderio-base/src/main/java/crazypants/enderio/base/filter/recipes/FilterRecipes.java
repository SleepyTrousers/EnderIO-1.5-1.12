package crazypants.enderio.base.filter.recipes;

import java.util.UUID;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class FilterRecipes {

  public static void addRecipes() {
    ForgeRegistries.RECIPES.register(new ClearFilterRecipe().setRegistryName(UUID.randomUUID().toString()));
    ForgeRegistries.RECIPES.register(new CopyFilterRecipe().setRegistryName(UUID.randomUUID().toString()));
  }

}
