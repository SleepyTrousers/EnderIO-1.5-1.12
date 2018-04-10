package crazypants.enderio.conduits.conduit.liquid;

import java.util.EnumMap;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.fluid.IFluidWrapper;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.conduits.conduit.AbstractConduit;
import crazypants.enderio.conduits.gui.GuiExternalConnection;
import crazypants.enderio.conduits.gui.LiquidSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractLiquidConduit extends AbstractConduit implements ILiquidConduit {

  protected final EnumMap<EnumFacing, RedstoneControlMode> extractionModes = new EnumMap<EnumFacing, RedstoneControlMode>(EnumFacing.class);
  protected final EnumMap<EnumFacing, DyeColor> extractionColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);

  public static IFluidWrapper getExternalFluidHandler(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    if (world.getTileEntity(pos) instanceof IConduitBundle) {
      return null;
    }
    return FluidWrapper.wrap(world, pos, side);
  }

  public IFluidWrapper getExternalHandler(EnumFacing direction) {
    return getExternalFluidHandler(getBundle().getBundleworld(), getBundle().getLocation().offset(direction), direction.getOpposite());
  }

  @Override
  public boolean canConnectToExternal(@Nonnull EnumFacing direction, boolean ignoreDisabled) {
    IFluidWrapper h = getExternalHandler(direction);
    if (h == null) {
      return false;
    }
    return true;
  }

  @Override
  @Nonnull
  public Class<? extends IConduit> getBaseConduitType() {
    return ILiquidConduit.class;
  }

  @Override
  public void setExtractionRedstoneMode(@Nonnull RedstoneControlMode mode, @Nonnull EnumFacing dir) {
    extractionModes.put(dir, mode);
  }

  @Override
  @Nonnull
  public RedstoneControlMode getExtractionRedstoneMode(@Nonnull EnumFacing dir) {
    RedstoneControlMode res = extractionModes.get(dir);
    if (res == null) {
      res = RedstoneControlMode.NEVER;
    }
    return res;
  }

  @Override
  public void setExtractionSignalColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col) {
    extractionColors.put(dir, col);
  }

  @Override
  @Nonnull
  public DyeColor getExtractionSignalColor(@Nonnull EnumFacing dir) {
    DyeColor result = extractionColors.get(dir);
    if (result == null) {
      return DyeColor.RED;
    }
    return result;
  }

  @Override
  public boolean canOutputToDir(@Nonnull EnumFacing dir) {
    if (!canInputToDir(dir)) {
      return false;
    }
    if (conduitConnections.contains(dir)) {
      return true;
    }
    if (!externalConnections.contains(dir)) {
      return false;
    }
    return true;
  }

  protected boolean autoExtractForDir(@Nonnull EnumFacing dir) {
    if (!canExtractFromDir(dir)) {
      return false;
    }
    RedstoneControlMode mode = getExtractionRedstoneMode(dir);
    return ConduitUtil.isRedstoneControlModeMet(this, mode, getExtractionSignalColor(dir));
  }

  @Override
  public boolean canExtractFromDir(@Nonnull EnumFacing dir) {
    return getConnectionMode(dir).acceptsInput();
  }

  @Override
  public boolean canInputToDir(@Nonnull EnumFacing dir) {
    return getConnectionMode(dir).acceptsOutput() && !autoExtractForDir(dir);
  }

  protected boolean hasExtractableMode() {
    return supportsConnectionMode(ConnectionMode.INPUT) || supportsConnectionMode(ConnectionMode.IN_OUT);
  }

  @Override
  protected void readTypeSettings(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    setExtractionSignalColor(dir, DyeColor.values()[dataRoot.getShort("extractionSignalColor")]);
    setExtractionRedstoneMode(RedstoneControlMode.values()[dataRoot.getShort("extractionRedstoneMode")], dir);
  }

  @Override
  protected void writeTypeSettingsToNbt(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    dataRoot.setShort("extractionSignalColor", (short) getExtractionSignalColor(dir).ordinal());
    dataRoot.setShort("extractionRedstoneMode", (short) getExtractionRedstoneMode(dir).ordinal());
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);

    for (Entry<EnumFacing, RedstoneControlMode> entry : extractionModes.entrySet()) {
      if (entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("extRM." + entry.getKey().name(), ord);
      }
    }

    for (Entry<EnumFacing, DyeColor> entry : extractionColors.entrySet()) {
      if (entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("extSC." + entry.getKey().name(), ord);
      }
    }

  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);

    for (EnumFacing dir : EnumFacing.VALUES) {
      String key = "extRM." + dir.name();
      if (nbtRoot.hasKey(key)) {
        short ord = nbtRoot.getShort(key);
        if (ord >= 0 && ord < RedstoneControlMode.values().length) {
          extractionModes.put(dir, RedstoneControlMode.values()[ord]);
        }
      }
      key = "extSC." + dir.name();
      if (nbtRoot.hasKey(key)) {
        short ord = nbtRoot.getShort(key);
        if (ord >= 0 && ord < DyeColor.values().length) {
          extractionColors.put(dir, DyeColor.values()[ord]);
        }
      }
    }
  }

  @SideOnly(Side.CLIENT)
  @Nonnull
  @Override
  public ITabPanel createGuiPanel(@Nonnull IGuiExternalConnection gui, @Nonnull IClientConduit con) {
    return new LiquidSettings((GuiExternalConnection) gui, con);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean updateGuiPanel(@Nonnull ITabPanel panel) {
    if (panel instanceof LiquidSettings) {
      return ((LiquidSettings) panel).updateConduit(this);
    }
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public int getGuiPanelTabOrder() {
    return 1;
  }

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return facing == null || getExternalConnections().contains(facing);
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return (T) getFluidDir(facing);
    }
    return null;
  }

  @Override
  public IFluidHandler getFluidDir(@Nullable EnumFacing dir) {
    if (dir != null) {
      return new ConnectionLiquidSide(dir);
    }
    return this;
  }

  @Override
  @Nonnull
  public String getConduitProbeInfo(@Nonnull EntityPlayer player) {
    return "";
  }

  /**
   * Inner class for holding the direction of capabilities.
   */
  protected class ConnectionLiquidSide implements IFluidHandler {
    protected EnumFacing side;

    public ConnectionLiquidSide(EnumFacing side) {
      this.side = side;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
      return AbstractLiquidConduit.this.getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
      if (canFill(side, resource)) {
        return AbstractLiquidConduit.this.fill(resource, doFill);
      }
      return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
      if (canDrain(side, resource)) {
        return AbstractLiquidConduit.this.drain(resource, doDrain);
      }
      return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
      if (canDrain(side, null)) {
        return AbstractLiquidConduit.this.drain(maxDrain, doDrain);
      }
      return null;
    }
  }

}
