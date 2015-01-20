package crazypants.enderio.machine.capbank.packet;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.machine.capbank.network.ClientNetworkManager;
import crazypants.enderio.machine.capbank.network.ICapBankNetwork;

public class PacketNetworkEnergyResponse implements IMessage, IMessageHandler<PacketNetworkEnergyResponse, IMessage> {

  private int id;
  private long energyStored;
  private float avChange;

  public PacketNetworkEnergyResponse() {
  }

  public PacketNetworkEnergyResponse(ICapBankNetwork network) {
    id = network.getId();
    energyStored = network.getEnergyStoredL();
    avChange = network.getAverageChangePerTick();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(id);
    buf.writeLong(energyStored);
    buf.writeFloat(avChange);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    id = buf.readInt();
    energyStored = buf.readLong();
    avChange = buf.readFloat();
  }

  @Override
  public IMessage onMessage(PacketNetworkEnergyResponse message, MessageContext ctx) {
    ClientNetworkManager.getInstance().updateEnergy(message.id, message.energyStored, message.avChange);
    return null;
  }

}
