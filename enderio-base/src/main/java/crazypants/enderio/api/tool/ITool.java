package crazypants.enderio.api.tool;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface ITool extends IHideFacades {

  boolean canUse(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull BlockPos pos);

  void used(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull BlockPos pos);

}
