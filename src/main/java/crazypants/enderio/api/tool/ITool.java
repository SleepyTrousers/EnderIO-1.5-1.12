package crazypants.enderio.api.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;


public interface ITool extends IHideFacades {
  
  boolean canUse(ItemStack stack, EntityPlayer player, BlockPos pos);

  void used(ItemStack stack, EntityPlayer player, BlockPos pos);

}
