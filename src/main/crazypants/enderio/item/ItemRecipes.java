package crazypants.enderio.item;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.ModObject;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.MachinePart;

public class ItemRecipes {

  public static void addRecipes() {
    ItemStack basicGear = new ItemStack(ModObject.itemMachinePart.actualId, 1, MachinePart.BASIC_GEAR.ordinal());
    ItemStack electricalSteel = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.ELECTRICAL_STEEL.ordinal());
    ItemStack wrench = new ItemStack(ModObject.itemYetaWrench.actualId, 1, 0);

    // Wrench
    GameRegistry.addShapedRecipe(wrench, "s s", " b ", " s ", 's', electricalSteel, 'b', basicGear);
  }

}
