package crazypants.enderio.machine.capbank.network;

import com.enderio.core.common.util.BlockCoord;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.machine.capbank.InfoDisplayType;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.packet.PacketNetworkEnergyRequest;
import crazypants.enderio.machine.capbank.packet.PacketNetworkStateRequest;
import crazypants.enderio.machine.modes.RedstoneControlMode;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.IPowerStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CapBankClientNetwork implements ICapBankNetwork {

  private final int id;
  private final Map<BlockPos, TileCapBank> members = new HashMap<>();
  private int maxEnergySent;
  private int maxEnergyRecieved;

  private int stateUpdateCount;
  private int maxIO;
  private long maxEnergyStored;
  private long energyStored;

  private RedstoneControlMode inputControlMode = RedstoneControlMode.IGNORE;
  private RedstoneControlMode outputControlMode = RedstoneControlMode.IGNORE;

  private final InventoryImpl inventory = new InventoryImpl();

  private float avgInput;
  private float avgOutput;

  private long lastPowerRequestTick = -1;

  private Map<DisplayInfoKey, IOInfo> ioDisplayInfoCache;

  public CapBankClientNetwork(int id) {
    this.id = id;
  }

  @Override
  public int getId() {
    return id;
  }

  public void requestPowerUpdate(TileCapBank capBank, int interval) {
    long curTick = EnderIO.proxy.getTickCount();
    if(lastPowerRequestTick == -1 || curTick - lastPowerRequestTick >= interval) {
      if(stateUpdateCount == 0) {
        PacketHandler.INSTANCE.sendToServer(new PacketNetworkStateRequest(capBank));
        // the network state also contains the energy data
      } else {
        PacketHandler.INSTANCE.sendToServer(new PacketNetworkEnergyRequest(capBank));
      }
      lastPowerRequestTick = curTick;
    }
  }

  public void setState(World world, NetworkState state) {
    maxEnergyRecieved = state.getMaxInput();
    maxEnergySent = state.getMaxOutput();
    maxIO = state.getMaxIO();
    maxEnergyStored = state.getMaxEnergyStored();
    energyStored = state.getEnergyStored();
    inputControlMode = state.getInputMode();
    outputControlMode = state.getOutputMode();

    BlockPos pos = state.getInventoryImplLocation();
    if(pos == null) {
      inventory.setCapBank(null);
    } else if(world != null) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof TileCapBank) {
        inventory.setCapBank((TileCapBank) te);
      }
    }
    avgInput = state.getAverageInput();
    avgOutput = state.getAverageOutput();

    stateUpdateCount++;
  }

  public int getStateUpdateCount() {
    return stateUpdateCount;
  }

  public void setStateUpdateCount(int stateUpdateCount) {
    this.stateUpdateCount = stateUpdateCount;
  }

  @Override
  public void addMember(TileCapBank capBank) {
    members.put(capBank.getLocation(), capBank);
    invalidateDisplayInfoCache();
  }

  @Override
  public Collection<TileCapBank> getMembers() {
    return members.values();
  }

  @Override
  public void destroyNetwork() {
    for (TileCapBank cb : members.values()) {
      cb.setNetworkId(-1);
      cb.setNetwork(null);
    }
    invalidateDisplayInfoCache();
  }

  @Override
  public int getMaxIO() {
    return maxIO;
  }

  @Override
  public long getMaxEnergyStoredL() {
    return maxEnergyStored;
  }

  public void setMaxEnergyStoredL(long maxEnergyStored) {
    this.maxEnergyStored = maxEnergyStored;
  }

  public void setEnergyStored(long energyStored) {
    this.energyStored = energyStored;
  }

  @Override
  public long getEnergyStoredL() {
    return energyStored;
  }

  @Override
  public int getMaxOutput() {
    return maxEnergySent;
  }

  @Override
  public void setMaxOutput(int max) {
    maxEnergySent = MathHelper.clamp(max, 0, maxIO);
  }

  @Override
  public int getMaxInput() {
    return maxEnergyRecieved;
  }

  @Override
  public void setMaxInput(int max) {
    maxEnergyRecieved = MathHelper.clamp(max, 0, maxIO);
  }

  public double getEnergyStoredRatio() {
    if(getMaxEnergyStoredL() <= 0) {
      return 0;
    }
    return (double) getEnergyStoredL() / getMaxEnergyStoredL();
  }

  @Override
  public RedstoneControlMode getInputControlMode() {
    return inputControlMode;
  }

  @Override
  public void setInputControlMode(RedstoneControlMode inputControlMode) {
    this.inputControlMode = inputControlMode;
  }

  @Override
  public RedstoneControlMode getOutputControlMode() {
    return outputControlMode;
  }

  @Override
  public void setOutputControlMode(RedstoneControlMode outputControlMode) {
    this.outputControlMode = outputControlMode;
  }

  @Override
  public InventoryImpl getInventory() {
    return inventory;
  }

  @Override
  public float getAverageChangePerTick() {
    return avgInput - avgOutput;
  }

  @Override
  public float getAverageInputPerTick() {
    return avgInput;
  }

  @Override
  public float getAverageOutputPerTick() {
    return avgOutput;
  }

  public void setAverageIOPerTick(float input, float output) {
    this.avgInput = input;
    this.avgOutput = output;
  }

  @Override
  public NetworkState getState() {
    return new NetworkState(this);
  }

  @Override
  public void addEnergy(int energy) {
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return 0;
  }

  @Override
  public void removeReceptors(Collection<EnergyReceptor> receptors) {
  }

  @Override
  public void addReceptors(Collection<EnergyReceptor> receptors) {
  }

  @Override
  public void updateRedstoneSignal(TileCapBank tileCapBank, boolean recievingSignal) {
  }

  @Override
  public boolean isOutputEnabled() {
    return true;
  }

  @Override
  public boolean isInputEnabled() {
    return true;
  }

  @Override
  public IPowerStorage getController() {
    return this;
  }

  @Override
  public boolean isOutputEnabled(@Nonnull EnumFacing direction) {
    return isOutputEnabled();
  }

  @Override
  public boolean isInputEnabled(@Nonnull EnumFacing direction) {
    return isInputEnabled();
  }

  @Override
  public boolean isCreative() {
    return false;
  }

  @Override
  public boolean isNetworkControlledIo(@Nonnull EnumFacing direction) {
    return true;
  }

  @Override
  public void invalidateDisplayInfoCache() {
    ioDisplayInfoCache = null;
  }

  public IOInfo getIODisplayInfo(BlockPos pos, EnumFacing face) {
    return getIODisplayInfo(pos.getX(), pos.getY(), pos.getZ(), face);
  }
  
  public IOInfo getIODisplayInfo(int x, int y, int z, EnumFacing face) {
    DisplayInfoKey key = new DisplayInfoKey(x, y, z, face);
    if(ioDisplayInfoCache == null) {
      ioDisplayInfoCache = new HashMap<DisplayInfoKey, IOInfo>();
    }
    IOInfo value = ioDisplayInfoCache.get(key);
    if(value == null) {
      value = computeIODisplayInfo(x, y, z, face);
      ioDisplayInfoCache.put(key, value);
    }
    return value;
  }

  private IOInfo computeIODisplayInfo(int xOrg, int yOrg, int zOrg, EnumFacing dir) {
    if(dir.getFrontOffsetY() != 0) {
      return IOInfo.SINGLE;
    }

    TileCapBank cb = getCapBankAt(xOrg, yOrg, zOrg);
    if(cb == null) {
      return IOInfo.SINGLE;
    }

    CapBankType type = cb.getType();
    EnumFacing left = dir.rotateYCCW();
    EnumFacing right = left.getOpposite();

    int hOff = 0;
    int vOff = 0;

    // step 1: find top left
    while(isIOType(xOrg+left.getFrontOffsetX(), yOrg, zOrg+left.getFrontOffsetZ(), dir, type)) {
      xOrg += left.getFrontOffsetX();
      zOrg += left.getFrontOffsetZ();
      hOff++;
    }

    while(isIOType(xOrg, yOrg+1, zOrg, dir, type)) {
      yOrg++;
      vOff++;
    }

    if (isIOType(xOrg + left.getFrontOffsetX(), yOrg, zOrg + left.getFrontOffsetZ(), dir, type)) {
      // not a rectangle
      return IOInfo.SINGLE;
    }

    // step 2: find width
    int width = 1;
    int height = 1;
    int xTmp = xOrg;
    int yTmp = yOrg;
    int zTmp = zOrg;
    while(isIOType(xTmp+right.getFrontOffsetX(), yTmp, zTmp+right.getFrontOffsetZ(), dir, type)) {
      if(isIOType(xTmp+right.getFrontOffsetX(), yTmp+1, zTmp+right.getFrontOffsetZ(), dir, type)) {
        // not a rectangle
        return IOInfo.SINGLE;
      }
      xTmp += right.getFrontOffsetX();
      zTmp += right.getFrontOffsetZ();
      width++;
    }

    // step 3: find height
    while(isIOType(xOrg, yTmp-1, zOrg, dir, type)) {
      xTmp = xOrg;
      yTmp--;
      zTmp = zOrg;

      if(isIOType(xTmp+left.getFrontOffsetX(), yTmp, zTmp+left.getFrontOffsetZ(), dir, type)) {
        // not a rectangle
        return IOInfo.SINGLE;
      }

      for(int i=1 ; i<width ; i++) {
        xTmp += right.getFrontOffsetX();
        zTmp += right.getFrontOffsetZ();

        if(!isIOType(xTmp, yTmp, zTmp, dir, type)) {
          // not a rectangle
          return IOInfo.SINGLE;
        }
      }

      if(isIOType(xTmp+right.getFrontOffsetX(), yTmp, zTmp+right.getFrontOffsetZ(), dir, type)) {
        // not a rectangle
        return IOInfo.SINGLE;
      }

      height++;
    }

    xTmp = xOrg;
    yTmp--;
    zTmp = zOrg;

    for(int i=0 ; i<width ; i++) {
      if(isIOType(xTmp, yTmp, zTmp, dir, type)) {
        // not a rectangle
        return IOInfo.SINGLE;
      }

      xTmp += right.getFrontOffsetX();
      zTmp += right.getFrontOffsetZ();
    }

    if(width == 1 && height == 1) {
      return IOInfo.SINGLE;
    }

    if(hOff > 0 || vOff > 0) {
      return IOInfo.INSIDE;
    }

    return new IOInfo(width, height);
  }

  private boolean isIOType(int x, int y, int z, EnumFacing face, CapBankType type) {
    TileCapBank cb = getCapBankAt(x, y, z);
    return cb != null && type == cb.getType() && cb.getDisplayType(face) == InfoDisplayType.IO;
  }

  private TileCapBank getCapBankAt(int x, int y, int z) {
    return members.get(new BlockPos(x, y, z));
  }

  public static final class DisplayInfoKey {
    final int x;
    final int y;
    final int z;
    final EnumFacing face;

    public DisplayInfoKey(int x, int y, int z, EnumFacing face) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.face = face;
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 97 * hash + this.x;
      hash = 97 * hash + this.y;
      hash = 97 * hash + this.z;
      hash = 97 * hash + this.face.hashCode();
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if(!(obj instanceof DisplayInfoKey)) {
        return false;
      }
      final DisplayInfoKey other = (DisplayInfoKey) obj;
      return (this.x == other.x) &&
              (this.y == other.y) &&
              (this.z == other.z) &&
              (this.face == other.face);
    }
  }

  public static class IOInfo {
    public final int width;
    public final int height;

    static final IOInfo SINGLE = new IOInfo(1, 1);
    static final IOInfo INSIDE = new IOInfo(0, 0);

    IOInfo(int width, int height) {
      this.width = width;
      this.height = height;
    }

    public boolean isInside() {
      return width == 0;
    }
  }

  @Override
  public int getAverageIOPerTick() {
    return Math.round(getAverageChangePerTick());
  }

}
