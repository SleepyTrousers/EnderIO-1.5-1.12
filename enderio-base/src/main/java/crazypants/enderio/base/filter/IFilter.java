package crazypants.enderio.base.filter;

import javax.annotation.Nonnull;

import crazypants.enderio.base.init.ModObjectRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interface for all filters
 *
 */
public interface IFilter {

  default void openGui(@Nonnull EntityPlayer player, @Nonnull ItemStack filter, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing dir,
      int param1) {
    ModObjectRegistry.getModObjectNN(filter.getItem()).openGui(worldIn, pos, player, dir, param1);
  }

  default void openGui(@Nonnull EntityPlayer player, @Nonnull ItemStack filter, @Nonnull World worldIn, @Nonnull BlockPos pos) {
    ModObjectRegistry.getModObjectNN(filter.getItem()).openGui(worldIn, pos, player);
  }

  void readFromNBT(@Nonnull NBTTagCompound nbtRoot);

  void writeToNBT(@Nonnull NBTTagCompound nbtRoot);

  void writeToByteBuf(@Nonnull ByteBuf buf);

  void readFromByteBuf(@Nonnull ByteBuf buf);

  default void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
  }

  @Nonnull
  default ItemStack getInventorySlotContents(int slot) {
    return ItemStack.EMPTY;
  }

  default boolean hasGui() {
    return true;
  }

  boolean isEmpty();

}
