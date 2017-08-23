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
  private float avgInput;
  private float avgOutput;

  public PacketNetworkEnergyResponse() {
  }

  public PacketNetworkEnergyResponse(ICapBankNetwork network) {
    id = network.getId();
    energyStored = network.getEnergyStoredL();
    avgInput = network.getAverageInputPerTick();
    avgOutput = network.getAverageOutputPerTick();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(id);
    buf.writeLong(energyStored);
    buf.writeFloat(avgInput);
    buf.writeFloat(avgOutput);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    id = buf.readInt();
    energyStored = buf.readLong();
    avgInput = buf.readFloat();
    avgOutput = buf.readFloat();
  }

  @Override
  public IMessage onMessage(PacketNetworkEnergyResponse message, MessageContext ctx) {
    ClientNetworkManager.getInstance().updateEnergy(message.id, message.energyStored, message.avgInput, message.avgOutput);
    return null;
  }

}
