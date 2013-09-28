package crazypants.enderio.enderface;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.material.Alloy;

public class EnderfaceRecipes {

  public static void addRecipes() {
    ItemStack phasedGold = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.PHASED_GOLD.ordinal());
    ItemStack phasedIron = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.PHASED_IRON.ordinal());
    GameRegistry.addRecipe(new ItemStack(EnderIO.itemEnderface), "nxn", "xyx", "nxn", 'x', new ItemStack(Item.diamond), 'y', new ItemStack(Item.eyeOfEnder),'n', phasedGold);
    GameRegistry.addRecipe(new ItemStack(EnderIO.blockEnderIo), "zxz", "xyx", "zxz", 'x', phasedIron, 'y', new ItemStack(Block.enderChest),'z',new ItemStack(Item.diamond));
  }

}
