package crazypants.enderio.machine;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.network.IPacketEio;

public class PacketPowerStorage implements IPacketEio {

  private int x;
  private int y;
  private int z;
  private float storedEnergy;

  public PacketPowerStorage() {
  }

  public PacketPowerStorage(AbstractMachineEntity ent) {
    x = ent.xCoord;
    y = ent.yCoord;
    z = ent.zCoord;
    storedEnergy = ent.storedEnergy;
  }
  
  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeFloat(storedEnergy);    

  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    storedEnergy = buf.readFloat();
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    handle(player);
  }

  @Override
  public void handleServerSide(EntityPlayer player) {
    handle(player);
  }

  private void handle(EntityPlayer player) {    
    TileEntity te = player.worldObj.getTileEntity(x, y, z);
    if(te instanceof AbstractMachineEntity) {
      AbstractMachineEntity me = (AbstractMachineEntity) te;      
      me.storedEnergy = storedEnergy;
      me.powerHandler.setEnergy(storedEnergy);
    } 
  }


}
