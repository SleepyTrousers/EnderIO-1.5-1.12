package crazypants.enderio.teleport.telepad;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.network.MessageTileEntity;
import crazypants.util.BlockCoord;

public class PacketUpdateCoords extends MessageTileEntity<TileTelePad> implements IMessageHandler<PacketUpdateCoords, IMessage>{

  public PacketUpdateCoords() {
    super();
  }
  
  private int targetX, targetY, targetZ;
  
  public PacketUpdateCoords(TileTelePad te, int x, int y, int z) {
    super(te);
    this.targetX = x;
    this.targetY = y;
    this.targetZ = z;
  }
  
  public PacketUpdateCoords(TileTelePad te, BlockCoord bc) {
    this(te, bc.x, bc.y, bc.z);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(targetX);
    buf.writeInt(targetY);
    buf.writeInt(targetZ);
  }
  
  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    targetX = buf.readInt();
    targetY = buf.readInt();
    targetZ = buf.readInt();
  }
  
  @Override
  public IMessage onMessage(PacketUpdateCoords message, MessageContext ctx) {
    TileTelePad te = message.getTileEntity(message.getWorld(ctx));
    if(te != null) {
      te.setX(message.targetX);
      te.setY(message.targetY);
      te.setZ(message.targetZ);
    }
    return null;
  }
}
