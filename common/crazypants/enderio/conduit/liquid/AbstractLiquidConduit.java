package crazypants.enderio.conduit.liquid;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.reservoir.TileReservoir;
import crazypants.util.BlockCoord;
import crazypants.util.DyeColor;

public abstract class AbstractLiquidConduit extends AbstractConduit implements ILiquidConduit {

  protected final EnumMap<ForgeDirection, RedstoneControlMode> extractionModes = new EnumMap<ForgeDirection, RedstoneControlMode>(ForgeDirection.class);
  protected final EnumMap<ForgeDirection, DyeColor> extractionColors = new EnumMap<ForgeDirection, DyeColor>(ForgeDirection.class);

  protected final Map<ForgeDirection, Integer> externalRedstoneSignals = new HashMap<ForgeDirection, Integer>();
  protected boolean redstoneStateDirty = true;

  public IFluidHandler getExternalHandler(ForgeDirection direction) {
    IFluidHandler con = getTankContainer(getLocation().getLocation(direction));
    return (con != null && !(con instanceof IConduitBundle)) ? con : null;
  }

  public IFluidHandler getTankContainer(BlockCoord bc) {
    return getTankContainer(bc.x, bc.y, bc.z);
  }

  public IFluidHandler getTankContainer(int x, int y, int z) {
    TileEntity te = getBundle().getEntity().worldObj.getBlockTileEntity(x, y, z);
    if(te instanceof IFluidHandler) {
      if(te instanceof IPipeTile) {
        if(((IPipeTile) te).getPipeType() != PipeType.FLUID) {
          return null;
        }
      }
      return (IFluidHandler) te;
    }
    return null;
  }

  @Override
  public boolean canConnectToExternal(ForgeDirection direction, boolean ignoreDisabled) {
    return getExternalHandler(direction) != null;
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return ILiquidConduit.class;
  }

  @Override
  public boolean onNeighborBlockChange(int blockId) {
    redstoneStateDirty = true;
    return super.onNeighborBlockChange(blockId);
  }

  @Override
  public void setExtractionRedstoneMode(RedstoneControlMode mode, ForgeDirection dir) {
    extractionModes.put(dir, mode);
    redstoneStateDirty = true;
  }

  @Override
  public RedstoneControlMode getExtractioRedstoneMode(ForgeDirection dir) {
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
    if(isExtractingFromDir(dir) || getConectionMode(dir) == ConnectionMode.DISABLED) {
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
    if(!isExtractingFromDir(dir)) {
      return false;
    }
    RedstoneControlMode mode = getExtractioRedstoneMode(dir);
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
    if(mode.isConditionMet(mode, signal)) {
      return true;
    }

    int externalSignal = 0;
    if(col == DyeColor.RED) {
      Integer val = externalRedstoneSignals.get(dir);
      if(val == null) {
        TileEntity te = getBundle().getEntity();
        externalSignal = te.worldObj.getStrongestIndirectPower(te.xCoord, te.yCoord, te.zCoord);
        externalRedstoneSignals.put(dir, externalSignal);
      } else {
        externalSignal = val;
      }
    }

    return mode.isConditionMet(mode, externalSignal);
  }

  @Override
  public boolean isExtractingFromDir(ForgeDirection dir) {
    return getConectionMode(dir) == ConnectionMode.INPUT;
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
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);

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
