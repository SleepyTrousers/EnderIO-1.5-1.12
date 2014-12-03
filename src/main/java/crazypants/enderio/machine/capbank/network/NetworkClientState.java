package crazypants.enderio.machine.capbank.network;

import io.netty.buffer.ByteBuf;
import crazypants.enderio.machine.RedstoneControlMode;

public class NetworkClientState {

  private final long energyStored;
  private final long maxEnergyStored;
  private final int maxIO;
  private final int maxInput;
  private final int maxOutput;
  private final RedstoneControlMode inputMode;
  private final RedstoneControlMode outputMode;

  public NetworkClientState() {
    this(0, 0, 0, 0, 0, RedstoneControlMode.IGNORE, RedstoneControlMode.IGNORE);
  }

  public NetworkClientState(long energyStored, long maxEnergyStored, int maxIO, int maxInput, int maxOutput, RedstoneControlMode inputMode,
      RedstoneControlMode ouputMode) {
    this.energyStored = energyStored;
    this.maxEnergyStored = maxEnergyStored;
    this.maxIO = maxIO;
    this.maxInput = maxInput;
    this.maxOutput = maxOutput;
    this.inputMode = inputMode;
    outputMode = ouputMode;
  }

  public NetworkClientState(ICapBankNetwork network) {
    energyStored = network.getEnergyStored();
    maxEnergyStored = network.getMaxEnergyStored();
    maxIO = network.getMaxIO();
    maxInput = network.getMaxEnergyRecieved();
    maxOutput = network.getMaxEnergySent();
    inputMode = network.getInputControlMode();
    outputMode = network.getOutputControlMode();
  }

  public long getEnergyStored() {
    return energyStored;
  }

  public long getMaxEnergyStored() {
    return maxEnergyStored;
  }

  public int getMaxOutput() {
    return maxOutput;
  }

  public int getMaxInput() {
    return maxInput;
  }

  public int getMaxIO() {
    return maxIO;
  }

  public RedstoneControlMode getInputMode() {
    return inputMode;
  }

  public RedstoneControlMode getOutputMode() {
    return outputMode;
  }

  public void writeToBuf(ByteBuf buf) {
    buf.writeLong(energyStored);
    buf.writeLong(maxEnergyStored);
    buf.writeInt(maxIO);
    buf.writeInt(maxInput);
    buf.writeInt(maxOutput);
    buf.writeShort(inputMode.ordinal());
    buf.writeShort(outputMode.ordinal());
  }

  public static NetworkClientState readFromBuf(ByteBuf buf) {
    return new NetworkClientState(buf.readLong(), buf.readLong(), buf.readInt(), buf.readInt(), buf.readInt(),
        RedstoneControlMode.values()[buf.readShort()], RedstoneControlMode.values()[buf.readShort()]);
  }

  @Override
  public String toString() {
    return "NetworkClientState [energyStored=" + energyStored + ", maxEnergyStored=" + maxEnergyStored + ", maxIO=" + maxIO + ", maxInput=" + maxInput
        + ", maxOutput=" + maxOutput + "]";
  }

}
