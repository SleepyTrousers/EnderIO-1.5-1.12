package crazypants.enderio.teleport.telepad;

import io.netty.buffer.ByteBuf;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.util.BlockCoord;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;

public class PacketUpdateCoords extends MessageTileEntity<TileTelePad> implements IMessageHandler<PacketUpdateCoords, IMessage>{

  public PacketUpdateCoords() {
    super();
  }
  
  private int targetX, targetY, targetZ, targetDim;
  
  public PacketUpdateCoords(TileTelePad te, int x, int y, int z, int targetDim) {
    super(te);
    this.targetX = x;
    this.targetY = y;
    this.targetZ = z;
    this.targetDim = targetDim;
  }
  
  public PacketUpdateCoords(TileTelePad te, BlockCoord bc, int targetDim) {
    this(te, bc.x, bc.y, bc.z, targetDim);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(targetX);
    buf.writeInt(targetY);
    buf.writeInt(targetZ);
    buf.writeInt(targetDim);
  }
  
  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    targetX = buf.readInt();
    targetY = buf.readInt();
    targetZ = buf.readInt();
    targetDim = buf.readInt();
  }
  
  @Override
  public IMessage onMessage(PacketUpdateCoords message, MessageContext ctx) {
    TileTelePad te = message.getTileEntity(ctx.side.isClient() ? EnderIO.proxy.getClientWorld() : message.getWorld(ctx));
    if(te != null) {
      te.setX(message.targetX);
      te.setY(message.targetY);
      te.setZ(message.targetZ);
      te.setTargetDim(message.targetDim);
    }
    return null;
  }
}
