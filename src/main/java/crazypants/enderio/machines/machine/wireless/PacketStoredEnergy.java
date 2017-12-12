package crazypants.enderio.machines.machine.wireless;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketStoredEnergy extends MessageTileEntity<TileWirelessCharger> implements IMessage, IMessageHandler<PacketStoredEnergy, IMessage> {

  private int storedEnergy;

  public PacketStoredEnergy() {
  }

  public PacketStoredEnergy(@Nonnull TileWirelessCharger ent) {
    super(ent);
    storedEnergy = ent.storedEnergyRF;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(storedEnergy);

  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    storedEnergy = buf.readInt();
  }

  @Override
  public IMessage onMessage(PacketStoredEnergy message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileWirelessCharger te = message.getTileEntity(player.world);
    if (te != null) {
      boolean doRender = (te.storedEnergyRF > 0) != (message.storedEnergy > 0);
      te.storedEnergyRF = message.storedEnergy;
      if (doRender) {
        te.onAfterDataPacket();
      }
    }
    return null;
  }

}
