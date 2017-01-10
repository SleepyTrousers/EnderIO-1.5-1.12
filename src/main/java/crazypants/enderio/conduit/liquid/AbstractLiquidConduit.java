package crazypants.enderio.conduit.liquid;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.fluid.IFluidWrapper;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.machine.RedstoneControlMode;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public abstract class AbstractLiquidConduit extends AbstractConduit implements ILiquidConduit {

  protected final EnumMap<EnumFacing, RedstoneControlMode> extractionModes = new EnumMap<EnumFacing, RedstoneControlMode>(EnumFacing.class);
  protected final EnumMap<EnumFacing, DyeColor> extractionColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);

  protected final Map<EnumFacing, Integer> externalRedstoneSignals = new HashMap<EnumFacing, Integer>();
  protected boolean redstoneStateDirty = true;

  public static IFluidWrapper getExternalFluidHandler(IBlockAccess world, BlockPos pos, EnumFacing side) {
    if (world.getTileEntity(pos) instanceof IConduitBundle) {
      return null;
    }
    return FluidWrapper.wrap(world, pos, side);
  }

  public IFluidWrapper getExternalHandler(EnumFacing direction) {
    return getExternalFluidHandler(getBundle().getBundleWorldObj(), getLocation().getLocation(direction).getBlockPos(), direction.getOpposite());
  }

  @Override
  public boolean canConnectToExternal(EnumFacing direction, boolean ignoreDisabled) {
    IFluidWrapper h = getExternalHandler(direction);
    if(h == null) {
      return false;
    }
    return true;
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return ILiquidConduit.class;
  }

  @Override
  public boolean onNeighborBlockChange(Block blockId) {
    redstoneStateDirty = true;
    return super.onNeighborBlockChange(blockId);
  }

  @Override
  public void setExtractionRedstoneMode(RedstoneControlMode mode, EnumFacing dir) {
    extractionModes.put(dir, mode);
    redstoneStateDirty = true;
  }

  @Override
  public RedstoneControlMode getExtractionRedstoneMode(EnumFacing dir) {
    RedstoneControlMode res = extractionModes.get(dir);
    if(res == null) {
      res = RedstoneControlMode.ON;
    }
    return res;
  }

  @Override
  public void setExtractionSignalColor(EnumFacing dir, DyeColor col) {
    extractionColors.put(dir, col);
  }

  @Override
  public DyeColor getExtractionSignalColor(EnumFacing dir) {
    DyeColor result = extractionColors.get(dir);
    if(result == null) {
      return DyeColor.RED;
    }
    return result;
  }

  @Override
  public boolean canOutputToDir(EnumFacing dir) {
    if(!canInputToDir(dir)) {
      return false;
    }
    if(conduitConnections.contains(dir)) {
      return true;
    }
    if(!externalConnections.contains(dir)) {
      return false;
    }
    IFluidWrapper ext = getExternalHandler(dir);
    // if(ext instanceof TileReservoir) { // dont push to an auto ejecting
    // // resevoir or we loop
    // TileReservoir tr = (TileReservoir) ext;
    // return !tr.isAutoEject();
    // }
    return true;
  }

  protected boolean autoExtractForDir(EnumFacing dir) {
    if(!canExtractFromDir(dir)) {
      return false;
    }
    RedstoneControlMode mode = getExtractionRedstoneMode(dir);
    if(mode == RedstoneControlMode.IGNORE) {
      return true;
    }
    if(mode == RedstoneControlMode.NEVER) {
      return false;
    }
    if(redstoneStateDirty) {
      externalRedstoneSignals.clear();
      redstoneStateDirty = false;
    }

    DyeColor col = getExtractionSignalColor(dir);
    int signal = ConduitUtil.getInternalSignalForColor(getBundle(), col);
    
    boolean res;
    if(mode == RedstoneControlMode.OFF) {
      //if checking for no signal, must be no signal from both
      res = RedstoneControlMode.isConditionMet(mode, signal) && (col != DyeColor.RED || isConditionMetByExternalSignal(dir, mode, col));     
    } else {
      //if checking for a signal, either is fine
      res = RedstoneControlMode.isConditionMet(mode, signal) || (col == DyeColor.RED && isConditionMetByExternalSignal(dir, mode, col));
    }
    return res;
  }

  private boolean isConditionMetByExternalSignal(EnumFacing dir, RedstoneControlMode mode, DyeColor col) {
    int externalSignal = 0;
    if(col == DyeColor.RED) {
      Integer val = externalRedstoneSignals.get(dir);
      if(val == null) {
        TileEntity te = getBundle().getEntity();
        externalSignal = ConduitUtil.isBlockIndirectlyGettingPoweredIfLoaded(te.getWorld(), te.getPos());
        externalRedstoneSignals.put(dir, externalSignal);
      } else {
        externalSignal = val;
      }
    }

    return RedstoneControlMode.isConditionMet(mode, externalSignal);
  }

  @Override
  public boolean canExtractFromDir(EnumFacing dir) {
    return getConnectionMode(dir).acceptsInput();
  }
  
  @Override
  public boolean canInputToDir(EnumFacing dir) {
    return getConnectionMode(dir).acceptsOutput() && !autoExtractForDir(dir);
  }

  protected boolean hasExtractableMode() {
    return hasConnectionMode(ConnectionMode.INPUT) || hasConnectionMode(ConnectionMode.IN_OUT);
  }

  @Override
  protected void readTypeSettings(EnumFacing dir, NBTTagCompound dataRoot) {
    setExtractionSignalColor(dir, DyeColor.values()[dataRoot.getShort("extractionSignalColor")]);
    setExtractionRedstoneMode(RedstoneControlMode.values()[dataRoot.getShort("extractionRedstoneMode")], dir);
  }

  @Override
  protected void writeTypeSettingsToNbt(EnumFacing dir, NBTTagCompound dataRoot) {
    dataRoot.setShort("extractionSignalColor", (short)getExtractionSignalColor(dir).ordinal());
    dataRoot.setShort("extractionRedstoneMode", (short)getExtractionRedstoneMode(dir).ordinal());
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);

    for (Entry<EnumFacing, RedstoneControlMode> entry : extractionModes.entrySet()) {
      if(entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("extRM." + entry.getKey().name(), ord);
      }
    }

    for (Entry<EnumFacing, DyeColor> entry : extractionColors.entrySet()) {
      if(entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("extSC." + entry.getKey().name(), ord);
      }
    }

  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot, short nbtVersion) {
    super.readFromNBT(nbtRoot, nbtVersion);

    for (EnumFacing dir : EnumFacing.VALUES) {
      String key = "extRM." + dir.name();
      if(nbtRoot.hasKey(key)) {
        short ord = nbtRoot.getShort(key);
        if(ord >= 0 && ord < RedstoneControlMode.values().length) {
          extractionModes.put(dir, RedstoneControlMode.values()[ord]);
        }
      }
      key = "extSC." + dir.name();
      if(nbtRoot.hasKey(key)) {
        short ord = nbtRoot.getShort(key);
        if(ord >= 0 && ord < DyeColor.values().length) {
          extractionColors.put(dir, DyeColor.values()[ord]);
        }
      }
    }
  }

}
