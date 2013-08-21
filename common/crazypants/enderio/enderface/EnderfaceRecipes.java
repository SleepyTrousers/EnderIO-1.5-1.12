package crazypants.enderio.enderface;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;

public class EnderfaceRecipes {

  public static void addRecipes() {
    GameRegistry.addRecipe(new ItemStack(EnderIO.itemEnderface), " x ", "xyx", " x ", 'x', new ItemStack(Item.diamond), 'y', new ItemStack(Item.eyeOfEnder));
    GameRegistry.addRecipe(new ItemStack(EnderIO.blockEnderIo), "zxz", "xyx", "zxz", 'x', new ItemStack(Item.eyeOfEnder), 'y', new ItemStack(Block.enderChest),
        'z',
        new ItemStack(Item.diamond));
  }

}
