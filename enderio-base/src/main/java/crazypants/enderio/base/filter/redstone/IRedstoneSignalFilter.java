package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import crazypants.enderio.base.filter.IFilter;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public interface IRedstoneSignalFilter extends IFilter {

  @Override
  default void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
  }

  @Override
  default void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
  }

  @Override
  default void writeToByteBuf(@Nonnull ByteBuf buf) {
  }

  @Override
  default void readFromByteBuf(@Nonnull ByteBuf buf) {
  }

  default boolean isDefault() {
    return true;
  }

}
