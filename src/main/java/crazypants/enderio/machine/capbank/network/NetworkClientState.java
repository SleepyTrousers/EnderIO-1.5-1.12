package crazypants.enderio.machine.capbank.network;

import io.netty.buffer.ByteBuf;

public class NetworkClientState {

  private final long energyStored;
  private final long maxEnergyStored;
  private final int maxIO;
  private final int maxInput;
  private final int maxOutput;

  public NetworkClientState() {
    this(0, 0, 0, 0, 0);
  }

  public NetworkClientState(long energyStored, long maxEnergyStored, int maxIO, int maxInput, int maxOutput) {
    this.energyStored = energyStored;
    this.maxEnergyStored = maxEnergyStored;
    this.maxIO = maxIO;
    this.maxInput = maxInput;
    this.maxOutput = maxOutput;
  }

  public NetworkClientState(CapBankNetwork network) {
    energyStored = network.getEnergyStored();
    maxEnergyStored = network.getMaxEnergyStored();
    maxIO = network.getMaxIO();
    maxInput = network.getMaxEnergyRecieved();
    maxOutput = network.getMaxEnergySent();
  }

  public void writeToBuf(ByteBuf buf) {
    buf.writeLong(energyStored);
    buf.writeLong(maxEnergyStored);
    buf.writeInt(maxIO);
    buf.writeInt(maxInput);
    buf.writeInt(maxOutput);
  }

  public static NetworkClientState readFromBuf(ByteBuf buf) {
    return new NetworkClientState(buf.readLong(), buf.readLong(), buf.readInt(), buf.readInt(), buf.readInt());
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

  @Override
  public String toString() {
    return "NetworkClientState [energyStored=" + energyStored + ", maxEnergyStored=" + maxEnergyStored + ", maxIO=" + maxIO + ", maxInput=" + maxInput
        + ", maxOutput=" + maxOutput + "]";
  }

}
