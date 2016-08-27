package crazypants.enderio.teleport.telepad.packet;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.EnderIO;
import crazypants.enderio.teleport.telepad.TelepadTarget;
import crazypants.enderio.teleport.telepad.TileTelePad;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateCoords extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketUpdateCoords, IMessage>{

  public PacketUpdateCoords() {
    super();
  }
  
  private int targetX, targetY, targetZ, targetDim;
  
  public PacketUpdateCoords(TileTelePad te, int x, int y, int z, int targetDim) {
    super(te.getTileEntity());
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
    TileEntity te = message.getTileEntity(ctx.side.isClient() ? EnderIO.proxy.getClientWorld() : message.getWorld(ctx));
    if(te instanceof TileTelePad) {
      TileTelePad tp = (TileTelePad)te;
      TelepadTarget target = tp.getTarget();
      target.setLocation(new BlockPos(message.targetX, message.targetY, message.targetZ)).setDimension(message.targetDim);      
      tp.setTarget(target); //set it back so its marked dirty etc
    }
    return null;
  }
}
