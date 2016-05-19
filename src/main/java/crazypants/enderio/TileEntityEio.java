package crazypants.enderio;

import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import info.loenwind.autosave.annotations.Store.StoreFor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

import com.enderio.core.common.TileEntityBase;

public abstract class TileEntityEio extends TileEntityBase {

  protected boolean doingOtherNbt = false;

  @Override
  public final Packet<?> getDescriptionPacket() {
    NBTTagCompound root = new NBTTagCompound();
    try {
      doingOtherNbt = true;
      super.writeToNBT(root);
    } finally {
      doingOtherNbt = false;
    }
    Writer.write(StoreFor.CLIENT, root, this);
    return new SPacketUpdateTileEntity(getPos(), 1, root);
  }

  @Override
  public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    NBTTagCompound root = pkt.getNbtCompound();
    Reader.read(StoreFor.CLIENT, root, this);
    try {
      doingOtherNbt = true;
      super.readFromNBT(root);
    } finally {
      doingOtherNbt = false;
    }
    onAfterDataPacket();
  }

  protected void onAfterDataPacket() {
  }

  @Override
  protected void writeCustomNBT(NBTTagCompound root) {
    if (!doingOtherNbt) {
      Writer.write(StoreFor.SAVE, root, this);
    }
  }

  @Override
  protected void readCustomNBT(NBTTagCompound root) {
    if (!doingOtherNbt) {
      Reader.read(StoreFor.SAVE, root, this);
    }
  }

  public void readContentsFromNBT(NBTTagCompound nbtRoot) {
    Reader.read(StoreFor.ITEM, nbtRoot, this);
  }

  public void writeContentsToNBT(NBTTagCompound nbtRoot) {
    Writer.write(StoreFor.ITEM, nbtRoot, this);
  }

}
