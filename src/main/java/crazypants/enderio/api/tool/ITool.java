package crazypants.enderio.api.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;


public interface ITool extends IHideFacades {

  boolean canUse(ItemStack stack, EntityPlayer player, int x, int y, int z);

  void used(ItemStack stack, EntityPlayer player, int x, int y, int z);

}
