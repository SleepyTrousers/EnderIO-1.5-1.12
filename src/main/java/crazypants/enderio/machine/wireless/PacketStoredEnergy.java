package crazypants.enderio.machine.wireless;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;

public class PacketStoredEnergy implements IMessage, IMessageHandler<PacketStoredEnergy, IMessage> {

  private int x;
  private int y;
  private int z;
  private int storedEnergy;

  public PacketStoredEnergy() {
  }

  public PacketStoredEnergy(TileWirelessCharger ent) {
    x = ent.xCoord;
    y = ent.yCoord;
    z = ent.zCoord;
    storedEnergy = ent.storedEnergyRF;
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
  public IMessage onMessage(PacketStoredEnergy message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
    if(te instanceof TileWirelessCharger) {
      TileWirelessCharger me = (TileWirelessCharger) te;
      boolean doRender = (me.storedEnergyRF <= 0 && message.storedEnergy > 0) ||
          (me.storedEnergyRF > 0 && message.storedEnergy <= 0);
      me.storedEnergyRF = message.storedEnergy;
      if(doRender) {        
        player.worldObj.markBlockRangeForRenderUpdate(message.x, message.y, message.z, message.x, message.y, message.z);
      }

    }
    return null;
  }

}
