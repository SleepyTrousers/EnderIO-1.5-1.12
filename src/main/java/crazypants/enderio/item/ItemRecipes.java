package crazypants.enderio.item;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.MachinePart;

public class ItemRecipes {

  public static void addRecipes() {
    ItemStack basicGear = new ItemStack(EnderIO.itemMachinePart, 1, MachinePart.BASIC_GEAR.ordinal());
    ItemStack electricalSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal());
    ItemStack wrench = new ItemStack(EnderIO.itemYetaWench, 1, 0);

    // Wrench
    GameRegistry.addShapedRecipe(wrench, "s s", " b ", " s ", 's', electricalSteel, 'b', basicGear);
  }

}
