package crazypants.enderio.machine.crafter;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.network.MessageTileEntity;

public class PacketBufferSize extends MessageTileEntity<TileCrafter> implements IMessageHandler<PacketBufferSize, IMessage> {

  boolean bufferStacks;

  public PacketBufferSize() {
  }

  public PacketBufferSize(TileCrafter tile) {
    super(tile);
    bufferStacks = tile.isBufferStacks();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeBoolean(bufferStacks);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    bufferStacks = buf.readBoolean();
  }

  @Override
  public IMessage onMessage(PacketBufferSize message, MessageContext ctx) {
    TileCrafter te = message.getTileEntity(ctx.getServerHandler().playerEntity.worldObj);
    if(te != null) {
      te.setBufferStacks(message.bufferStacks);
    }
    return null;
  }

  
  
  
}
