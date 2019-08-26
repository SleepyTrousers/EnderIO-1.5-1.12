package crazypants.enderio.powertools.machine.capbank.packet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.DiagnosticsConfig;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.powertools.machine.capbank.TileCapBank;
import crazypants.enderio.powertools.machine.capbank.network.ICapBankNetwork;
import crazypants.enderio.util.EnumReader;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGuiChange extends PacketCapBank<PacketGuiChange, IMessage> {

  private int maxSend;
  private int maxRec;
  private @Nonnull RedstoneControlMode inputMode = RedstoneControlMode.OFF;
  private @Nonnull RedstoneControlMode outputMode = RedstoneControlMode.OFF;

  public PacketGuiChange() {
  }

  public PacketGuiChange(@Nonnull TileCapBank capBank) {
    super(capBank);
    ICapBankNetwork network = capBank.getNetwork();
    maxSend = network.getMaxOutput();
    maxRec = network.getMaxInput();
    inputMode = network.getInputControlMode();
    outputMode = network.getOutputControlMode();
    if (DiagnosticsConfig.debugTraceCapLimitsExtremelyDetailed.get()) {
      StringBuilder sb = new StringBuilder("CapBankNetwork ").append(network).append(" sending network package in=").append(maxRec).append(" out=")
          .append(maxSend);
      for (StackTraceElement elem : new Exception("Stackstrace").getStackTrace()) {
        sb.append(" at ").append(elem);
      }
      Log.warn(sb);
    }
  }

  @Override
  public void write(ByteBuf buf) {
    buf.writeInt(maxSend);
    buf.writeInt(maxRec);
    buf.writeShort(inputMode.ordinal());
    buf.writeShort(outputMode.ordinal());
  }

  @Override
  public void read(ByteBuf buf) {
    maxSend = buf.readInt();
    maxRec = buf.readInt();
    inputMode = EnumReader.get(RedstoneControlMode.class, buf.readShort());
    outputMode = EnumReader.get(RedstoneControlMode.class, buf.readShort());
  }

  @Override
  protected @Nullable IMessage handleMessage(TileCapBank te, PacketGuiChange message, MessageContext ctx) {
    ICapBankNetwork net = te.getNetwork();
    if (net == null) {
      return null;
    }
    net.setMaxOutput(message.maxSend);
    net.setMaxInput(message.maxRec);
    net.setInputControlMode(message.inputMode);
    net.setOutputControlMode(message.outputMode);
    return null;
  }

}
