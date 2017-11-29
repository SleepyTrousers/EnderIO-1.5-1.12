package crazypants.enderio.machines.machine.wireless;

import crazypants.enderio.base.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketStoredEnergy implements IMessage, IMessageHandler<PacketStoredEnergy, IMessage> {

  private long pos;
  private int storedEnergy;

  public PacketStoredEnergy() {
  }

  public PacketStoredEnergy(TileWirelessCharger ent) {
    pos = ent.getPos().toLong();
    storedEnergy = ent.storedEnergyRF;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeLong(pos);
    buf.writeInt(storedEnergy);

  }

  @Override
  public void fromBytes(ByteBuf buf) {
    pos = buf.readLong();
    storedEnergy = buf.readInt();
  }

  @Override
  public IMessage onMessage(PacketStoredEnergy message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity te = player.world.getTileEntity(BlockPos.fromLong(message.pos));
    if (te instanceof TileWirelessCharger) {
      TileWirelessCharger me = (TileWirelessCharger) te;
      boolean doRender = (me.storedEnergyRF > 0) != (message.storedEnergy > 0);
      me.storedEnergyRF = message.storedEnergy;
      if (doRender) {
        me.onAfterDataPacket();
      }
    }
    return null;
  }

}
