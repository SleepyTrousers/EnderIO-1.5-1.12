package crazypants.enderio.machine.capbank.packet;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.network.CapBankClientNetwork;
import crazypants.enderio.machine.capbank.network.ICapBankNetwork;

public class PacketGuiChange extends PacketCapBank<PacketGuiChange, IMessage> {

  private int maxSend;
  private int maxRec;
  private RedstoneControlMode inputMode;
  private RedstoneControlMode outputMode;

  public PacketGuiChange() {
  }

  public PacketGuiChange(TileCapBank capBank, CapBankClientNetwork network) {
    super(capBank);
    maxSend = network.getMaxEnergySent();
    maxRec = network.getMaxEnergyRecieved();
    inputMode = network.getInputControlMode();
    outputMode = network.getOutputControlMode();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(maxSend);
    buf.writeInt(maxRec);
    buf.writeShort(inputMode.ordinal());
    buf.writeShort(outputMode.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    maxSend = buf.readInt();
    maxRec = buf.readInt();
    inputMode = RedstoneControlMode.values()[buf.readShort()];
    outputMode = RedstoneControlMode.values()[buf.readShort()];
  }

  @Override
  protected IMessage handleMessage(TileCapBank te, PacketGuiChange message, MessageContext ctx) {
    ICapBankNetwork net = te.getNetwork();
    if(net == null) {
      return null;
    }
    net.setMaxEnergySend(message.maxSend);
    net.setMaxEnergyReccieved(message.maxRec);
    net.setInputControlMode(message.inputMode);
    net.setOutputControlMode(message.outputMode);
    return null;
  }

}
