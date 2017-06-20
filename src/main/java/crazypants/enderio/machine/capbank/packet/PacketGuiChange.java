package crazypants.enderio.machine.capbank.packet;

import crazypants.enderio.Log;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.network.ICapBankNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGuiChange extends PacketCapBank<PacketGuiChange, IMessage> {

  private int maxSend;
  private int maxRec;
  private RedstoneControlMode inputMode;
  private RedstoneControlMode outputMode;

  public PacketGuiChange() {
  }

  public PacketGuiChange(TileCapBank capBank) {
    super(capBank);
    ICapBankNetwork network = capBank.getNetwork();
    maxSend = network.getMaxOutput();
    maxRec = network.getMaxInput();
    inputMode = network.getInputControlMode();
    outputMode = network.getOutputControlMode();
    if (Config.debugTraceCapLimitsExtremelyDetailed) {
      StringBuilder sb = new StringBuilder("CapBankNetwork ").append(network).append(" sending network package in=").append(maxRec).append(" out=")
          .append(maxSend);
      for (StackTraceElement elem : new Exception("Stackstrace").getStackTrace()) {
        sb.append(" at ").append(elem);
      }
      Log.warn(sb);
    }
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
    net.setMaxOutput(message.maxSend);
    net.setMaxInput(message.maxRec);
    net.setInputControlMode(message.inputMode);
    net.setOutputControlMode(message.outputMode);
    return null;
  }

}
