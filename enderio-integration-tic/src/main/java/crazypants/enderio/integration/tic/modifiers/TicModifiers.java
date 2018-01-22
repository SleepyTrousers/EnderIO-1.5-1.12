package crazypants.enderio.integration.tic.modifiers;

import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.integration.tic.traits.TraitPickup;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.tools.TinkerTools;

public class TicModifiers {

  public static void register() {
    Material material = TinkerRegistry.getMaterial(Alloy.VIBRANT_ALLOY.getBaseName());
    ItemStack itemstackWithMaterial = TinkerTools.largePlate.getItemstackWithMaterial(material);
    TraitPickup.instance.addRecipeMatch(new RecipeMatch.ItemCombination(1, itemstackWithMaterial));
  }

}
