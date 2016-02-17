package crazypants.enderio.machine.capbank.packet;

import crazypants.enderio.machine.capbank.network.ClientNetworkManager;
import crazypants.enderio.machine.capbank.network.ICapBankNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
