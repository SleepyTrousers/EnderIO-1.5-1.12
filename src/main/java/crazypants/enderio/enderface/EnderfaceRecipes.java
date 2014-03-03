package crazypants.enderio.enderface;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.material.Alloy;

public class EnderfaceRecipes {

  public static void addRecipes() {

    ItemStack phasedGold = new ItemStack(EnderIO.itemAlloy, 1, Alloy.PHASED_GOLD.ordinal());
    ItemStack fusedQuartz = new ItemStack(EnderIO.blockFusedQuartz, 1, 0);
    ItemStack electricalSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal());
    GameRegistry.addRecipe(new ItemStack(EnderIO.blockEnderIo), "sqs", "qeq", "sqs", 's', electricalSteel, 'q', fusedQuartz, 'e', new ItemStack(
        Items.ender_eye));
  }

}
