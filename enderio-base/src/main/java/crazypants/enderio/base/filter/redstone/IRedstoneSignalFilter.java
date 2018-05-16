package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.NetworkUtil;

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
    NBTTagCompound root = new NBTTagCompound();
    writeToNBT(root);
    NetworkUtil.writeNBTTagCompound(root, buf);
  }

  @Override
  default void readFromByteBuf(@Nonnull ByteBuf buf) {
    NBTTagCompound tag = NetworkUtil.readNBTTagCompound(buf);
    readFromNBT(tag);
  }

}
