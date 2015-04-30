package crazypants.enderio.machine;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.common.util.IProgressTile;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.MessageTileEntity;

public class PacketProgress extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketProgress, IMessage>  {

  private float progress;

  public PacketProgress() {
  }

  public PacketProgress(IProgressTile tile) {
    super(tile.getTileEntity());
    progress = tile.getProgress();
    if (progress == 0) {
      progress = -1;
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeFloat(progress);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    progress = buf.readFloat();
  }

  @Override
  public IMessage onMessage(PacketProgress message, MessageContext ctx) {
    TileEntity tile = message.getTileEntity(EnderIO.proxy.getClientWorld());
    if(tile instanceof IProgressTile) {
      ((IProgressTile) tile).setProgress(message.progress);
    }
    return null;
  }
}
