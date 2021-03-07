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
import crazypants.enderio.conduits.gui.LiquidSettings;
import crazypants.enderio.util.EnumReader;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractLiquidConduit extends AbstractConduit implements ILiquidConduit {

  protected final EnumMap<EnumFacing, RedstoneControlMode> extractionModes = new EnumMap<EnumFacing, RedstoneControlMode>(EnumFacing.class);
  protected final EnumMap<EnumFacing, DyeColor> extractionColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);
  private int ticksSinceFailedExtract;

  public @Nullable IFluidWrapper getExternalHandler(@Nonnull EnumFacing direction) {
    IBlockAccess world = getBundle().getBundleworld();
    BlockPos pos = getBundle().getLocation().offset(direction);
    TileEntity tileEntity = world.getTileEntity(pos);
    if (tileEntity instanceof IConduitBundle) {
      return null;
    }
    return FluidWrapper.wrap(tileEntity, direction.getOpposite());
  }

  @Override
  public boolean canConnectToExternal(@Nonnull EnumFacing direction, boolean ignoreDisabled) {
    return getExternalHandler(direction) != null;
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

  /**
   * Checks if the given direction is configured for input ({@link #canExtractFromDir(EnumFacing)} and its redstone control allows it to operate.
   * <p>
   * Note that it does not check if there actually is an external connection on the given side!
   * 
   * @param dir
   *          The side to check
   * @return <code>true</code> if we can extract from that side
   */
  protected boolean autoExtractForDir(@Nonnull EnumFacing dir) {
    return canExtractFromDir(dir) && ConduitUtil.isRedstoneControlModeMet(this, getExtractionRedstoneMode(dir), getExtractionSignalColor(dir), dir);
  }

  @Override
  public boolean canExtractFromDir(@Nonnull EnumFacing dir) {
    return getConnectionMode(dir).acceptsInput();
  }

  @Override
  public boolean canInputToDir(@Nonnull EnumFacing dir) {
    return getConnectionMode(dir).acceptsOutput() && !autoExtractForDir(dir);
  }

  /**
   * Checks if this conduit has any external connection it can pull stuff from. This does not check for redstone control, only for existence of a connection and
   * the mode that is set.
   * 
   * @return <code>true</code> if there are any external connections with {@link ConnectionMode#acceptsInput()} <code>== true</code>
   */
  protected boolean hasExtractableMode() {
    if (hasExternalConnections()) {
      for (EnumFacing side : getExternalConnections()) {
        if (side != null && canExtractFromDir(side)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void updateEntity(@Nonnull World world) {
    super.updateEntity(world);
    if (!world.isRemote) {
      doExtract();
    }
  }

  protected void doExtract() {

    if (!hasExtractableMode() || getNetwork() == null) {
      return;
    }

    if ((ticksSinceFailedExtract++ & 0b1111) != 0) {
      // only check every 16 ticks, but counter gets reset on successful extraction
      // failure modes are: redstone control, empty source tank, no target tank, full target tank
      return;
    }

    for (EnumFacing dir : getExternalConnections()) {
      if (dir != null && autoExtractForDir(dir) && doExtract(dir)) {
        ticksSinceFailedExtract = 0;
      }
    }
  }

  protected abstract boolean doExtract(@Nonnull EnumFacing dir);

  @Override
  protected void readTypeSettings(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    setExtractionSignalColor(dir, EnumReader.get(DyeColor.class, dataRoot.getShort("extractionSignalColor")));
    setExtractionRedstoneMode(RedstoneControlMode.fromOrdinal(dataRoot.getShort("extractionRedstoneMode")), dir);
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
    return new LiquidSettings(gui, con);
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
      return getExternalConnections().contains(facing);
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
  @Nullable
  public IFluidHandler getFluidDir(@Nullable EnumFacing dir) {
    if (dir != null) {
      return new ConnectionLiquidSide(dir);
    }
    return null;
  }

  /**
   * While locking on the level of a conduit is not perfect, it will prevent loops from being infinite. There still are some ways to build loops that loop for a
   * long time (the more connection points between 2 networks...), but a slow down is much better than a stack overflow...
   */
  protected boolean reenter = false;

  /**
   * Inner class for holding the direction of capabilities.
   */
  protected class ConnectionLiquidSide implements IFluidHandler {
    protected @Nonnull EnumFacing side;

    public ConnectionLiquidSide(@Nonnull EnumFacing side) {
      this.side = side;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
      if (reenter) {
        return new FluidTankProperties[0];
      }
      try {
        reenter = true;
        return AbstractLiquidConduit.this.getTankProperties();
      } finally {
        reenter = false;
      }
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
      if (!reenter && canFill(side, resource)) {
        try {
          reenter = true;
          return AbstractLiquidConduit.this.fill(resource, doFill);
        } finally {
          reenter = false;
        }
      }
      return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
      if (!reenter && canDrain(side, resource)) {
        try {
          reenter = true;
          return AbstractLiquidConduit.this.drain(resource, doDrain);
        } finally {
          reenter = false;
        }
      }
      return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
      if (!reenter && canDrain(side, null)) {
        try {
          reenter = true;
          return AbstractLiquidConduit.this.drain(maxDrain, doDrain);
        } finally {
          reenter = false;
        }
      }
      return null;
    }
  }

}
