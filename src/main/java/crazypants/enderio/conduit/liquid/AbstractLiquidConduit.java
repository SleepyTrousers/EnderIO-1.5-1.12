package crazypants.enderio.conduit.liquid;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.FluidUtil;

import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.reservoir.TileReservoir;

public abstract class AbstractLiquidConduit extends AbstractConduit implements ILiquidConduit {

  protected final EnumMap<ForgeDirection, RedstoneControlMode> extractionModes = new EnumMap<ForgeDirection, RedstoneControlMode>(ForgeDirection.class);
  protected final EnumMap<ForgeDirection, DyeColor> extractionColors = new EnumMap<ForgeDirection, DyeColor>(ForgeDirection.class);

  protected final Map<ForgeDirection, Integer> externalRedstoneSignals = new HashMap<ForgeDirection, Integer>();
  protected boolean redstoneStateDirty = true;

  public static IFluidHandler getExternalFluidHandler(IBlockAccess world, BlockCoord bc) {
    IFluidHandler con = FluidUtil.getFluidHandler(world, bc);
    return (con != null && !(con instanceof IConduitBundle)) ? con : null;
  }

  public IFluidHandler getExternalHandler(ForgeDirection direction) {
    IFluidHandler con = getExternalFluidHandler(getBundle().getWorld(), getLocation().getLocation(direction));
    return (con != null && !(con instanceof IConduitBundle)) ? con : null;
  }

  public IFluidHandler getTankContainer(BlockCoord bc) {
    return FluidUtil.getFluidHandler(getBundle().getWorld(), bc);
  }

  @Override
  public boolean canConnectToExternal(ForgeDirection direction, boolean ignoreDisabled) {
    IFluidHandler h = getExternalHandler(direction);
    if(h == null) {
      return false;
    }
    //TODO: This check was added to work around a bug in dynamic tanks, but
    //it causes issues with not conecting to empty tanks such as dim. trans +
    //BC fluid pipes, so I am removing it for now.

    //    FluidTankInfo[] info = h.getTankInfo(direction.getOpposite());
    //    if(info == null) {
    //      return false;
    //    }
    //    return  info.length > 0;
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
  public void setExtractionRedstoneMode(RedstoneControlMode mode, ForgeDirection dir) {
    extractionModes.put(dir, mode);
    redstoneStateDirty = true;
  }

  @Override
  public RedstoneControlMode getExtractionRedstoneMode(ForgeDirection dir) {
    RedstoneControlMode res = extractionModes.get(dir);
    if(res == null) {
      res = RedstoneControlMode.ON;
    }
    return res;
  }

  @Override
  public void setExtractionSignalColor(ForgeDirection dir, DyeColor col) {
    extractionColors.put(dir, col);
  }

  @Override
  public DyeColor getExtractionSignalColor(ForgeDirection dir) {
    DyeColor result = extractionColors.get(dir);
    if(result == null) {
      return DyeColor.RED;
    }
    return result;
  }

  @Override
  public boolean canOutputToDir(ForgeDirection dir) {
    if(!canInputToDir(dir)) {
      return false;
    }
    if(conduitConnections.contains(dir)) {
      return true;
    }
    if(!externalConnections.contains(dir)) {
      return false;
    }
    IFluidHandler ext = getExternalHandler(dir);
    if(ext instanceof TileReservoir) { // dont push to an auto ejecting
      // resevoir or we loop
      TileReservoir tr = (TileReservoir) ext;
      return !tr.isMultiblock() || !tr.isAutoEject();
    }
    return true;
  }

  protected boolean autoExtractForDir(ForgeDirection dir) {
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
      res = mode.isConditionMet(mode, signal) && (col != DyeColor.RED || isConditionMetByExternalSignal(dir, mode, col));     
    } else {
      //if checking for a signal, either is fine
      res = mode.isConditionMet(mode, signal) || (col == DyeColor.RED && isConditionMetByExternalSignal(dir, mode, col));
    }
    return res;
  }

  private boolean isConditionMetByExternalSignal(ForgeDirection dir, RedstoneControlMode mode, DyeColor col) {
    int externalSignal = 0;
    if(col == DyeColor.RED) {
      Integer val = externalRedstoneSignals.get(dir);
      if(val == null) {
        TileEntity te = getBundle().getEntity();
        externalSignal = te.getWorldObj().getStrongestIndirectPower(te.xCoord, te.yCoord, te.zCoord);
        externalRedstoneSignals.put(dir, externalSignal);
      } else {
        externalSignal = val;
      }
    }

    return mode.isConditionMet(mode, externalSignal);
  }

  @Override
  public boolean canExtractFromDir(ForgeDirection dir) {
    return getConnectionMode(dir).acceptsInput();
  }
  
  @Override
  public boolean canInputToDir(ForgeDirection dir) {
    return getConnectionMode(dir).acceptsOutput() && !autoExtractForDir(dir);
  }

  protected boolean hasExtractableMode() {
    return hasConnectionMode(ConnectionMode.INPUT) || hasConnectionMode(ConnectionMode.IN_OUT);
  }

  @Override
  protected void readTypeSettings(ForgeDirection dir, NBTTagCompound dataRoot) {
    setExtractionSignalColor(dir, DyeColor.values()[dataRoot.getShort("extractionSignalColor")]);
    setExtractionRedstoneMode(RedstoneControlMode.values()[dataRoot.getShort("extractionRedstoneMode")], dir);
  }

  @Override
  protected void writeTypeSettingsToNbt(ForgeDirection dir, NBTTagCompound dataRoot) {
    dataRoot.setShort("extractionSignalColor", (short)getExtractionSignalColor(dir).ordinal());
    dataRoot.setShort("extractionRedstoneMode", (short)getExtractionRedstoneMode(dir).ordinal());
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);

    for (Entry<ForgeDirection, RedstoneControlMode> entry : extractionModes.entrySet()) {
      if(entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("extRM." + entry.getKey().name(), ord);
      }
    }

    for (Entry<ForgeDirection, DyeColor> entry : extractionColors.entrySet()) {
      if(entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("extSC." + entry.getKey().name(), ord);
      }
    }

  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot, short nbtVersion) {
    super.readFromNBT(nbtRoot, nbtVersion);

    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
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
