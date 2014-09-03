package crazypants.enderio.machine.hypercube;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;

public class PacketStoredPower implements IMessage, IMessageHandler<PacketStoredPower, IMessage> {

  private int x;
  private int y;
  private int z;
  private int storedEnergy;

  public PacketStoredPower() {
  }

  public PacketStoredPower(TileHyperCube ent) {
    x = ent.xCoord;
    y = ent.yCoord;
    z = ent.zCoord;
    storedEnergy = ent.getEnergyStored();    
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeInt(storedEnergy);

  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    storedEnergy = buf.readInt();
  }

  @Override
  public IMessage onMessage(PacketStoredPower message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
    if(te instanceof TileHyperCube) {
      TileHyperCube me = (TileHyperCube) te;      
      me.setEnergyStored(message.storedEnergy);      
    }
    return null;
  }

}
