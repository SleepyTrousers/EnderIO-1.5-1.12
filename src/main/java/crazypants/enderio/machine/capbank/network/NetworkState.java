package crazypants.enderio.machine.capbank.network;

import io.netty.buffer.ByteBuf;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.util.BlockCoord;

public class NetworkState {

  private final long energyStored;
  private final long maxEnergyStored;
  private final int maxIO;
  private final int maxInput;
  private final int maxOutput;
  private final RedstoneControlMode inputMode;
  private final RedstoneControlMode outputMode;
  private final BlockCoord invImplLoc;
  private final float averageChange;

  public NetworkState() {
    this(0, 0, 0, 0, 0, RedstoneControlMode.IGNORE, RedstoneControlMode.IGNORE, null, 0);
  }

  public NetworkState(long energyStored, long maxEnergyStored, int maxIO, int maxInput, int maxOutput, RedstoneControlMode inputMode,
      RedstoneControlMode outputMode, BlockCoord invImplLoc, float averageChange) {
    this.energyStored = energyStored;
    this.maxEnergyStored = maxEnergyStored;
    this.maxIO = maxIO;
    this.maxInput = maxInput;
    this.maxOutput = maxOutput;
    this.inputMode = inputMode;
    this.outputMode = outputMode;
    this.invImplLoc = invImplLoc;
    this.averageChange = averageChange;
  }

  public NetworkState(ICapBankNetwork network) {
    energyStored = network.getEnergyStoredL();
    maxEnergyStored = network.getMaxEnergyStoredL();
    maxIO = network.getMaxIO();
    maxInput = network.getMaxInput();
    maxOutput = network.getMaxOutput();
    inputMode = network.getInputControlMode();
    outputMode = network.getOutputControlMode();
    TileCapBank cb = network.getInventory().getCapBank();
    if(cb != null) {
      invImplLoc = cb.getLocation();
    } else {
      invImplLoc = null;
    }
    averageChange = network.getAverageChangePerTick();
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

  public BlockCoord getInventoryImplLocation() {
    return invImplLoc;
  }

  public float getAverageChange() {
    return averageChange;
  }

  public void writeToBuf(ByteBuf buf) {
    buf.writeLong(energyStored);
    buf.writeLong(maxEnergyStored);
    buf.writeInt(maxIO);
    buf.writeInt(maxInput);
    buf.writeInt(maxOutput);
    buf.writeShort(inputMode.ordinal());
    buf.writeShort(outputMode.ordinal());
    buf.writeBoolean(invImplLoc != null);
    if(invImplLoc != null) {
      invImplLoc.writeToBuf(buf);
    }
    buf.writeFloat(averageChange);
  }

  public static NetworkState readFromBuf(ByteBuf buf) {
    return new NetworkState(buf.readLong(), buf.readLong(), buf.readInt(), buf.readInt(), buf.readInt(),
        RedstoneControlMode.values()[buf.readShort()], RedstoneControlMode.values()[buf.readShort()],
        buf.readBoolean() ? BlockCoord.readFromBuf(buf) : null, buf.readFloat());
  }

  @Override
  public String toString() {
    return "NetworkClientState [energyStored=" + energyStored + ", maxEnergyStored=" + maxEnergyStored + ", maxIO=" + maxIO + ", maxInput=" + maxInput
        + ", maxOutput=" + maxOutput + "]";
  }

}
