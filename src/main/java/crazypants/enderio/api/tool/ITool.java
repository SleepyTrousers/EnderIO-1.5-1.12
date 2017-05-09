package crazypants.enderio.api.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;


public interface ITool extends IHideFacades {
  
  boolean canUse(@Nonnull ItemStack stack, EntityPlayer player, BlockPos pos);

  void used(@Nonnull ItemStack stack, EntityPlayer player, BlockPos pos);

}
