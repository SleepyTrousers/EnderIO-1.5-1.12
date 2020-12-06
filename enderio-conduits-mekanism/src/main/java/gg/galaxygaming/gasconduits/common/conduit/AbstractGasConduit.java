package gg.galaxygaming.gasconduits.common.conduit;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.common.util.DyeColor;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.conduits.conduit.AbstractConduit;
import crazypants.enderio.util.EnumReader;
import gg.galaxygaming.gasconduits.client.GasSettings;
import gg.galaxygaming.gasconduits.common.utils.GasWrapper;
import java.util.EnumMap;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractGasConduit extends AbstractConduit implements IGasConduit {

    protected final EnumMap<EnumFacing, RedstoneControlMode> extractionModes = new EnumMap<>(EnumFacing.class);
    protected final EnumMap<EnumFacing, DyeColor> extractionColors = new EnumMap<>(EnumFacing.class);

    public static IGasHandler getExternalGasHandler(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        return world.getTileEntity(pos) instanceof IConduitBundle ? null : GasWrapper.getGasHandler(world, pos, side);
    }

    public IGasHandler getExternalHandler(@Nonnull EnumFacing direction) {
        return getExternalGasHandler(getBundle().getBundleworld(), getBundle().getLocation().offset(direction), direction.getOpposite());
    }

    @Override
    public boolean canConnectToExternal(@Nonnull EnumFacing direction, boolean ignoreDisabled) {
        return getExternalHandler(direction) != null;
    }

    @Override
    @Nonnull
    public Class<? extends IConduit> getBaseConduitType() {
        return IGasConduit.class;
    }

    @Override
    public void setExtractionRedstoneMode(@Nonnull RedstoneControlMode mode, @Nonnull EnumFacing dir) {
        extractionModes.put(dir, mode);
    }

    @Override
    @Nonnull
    public RedstoneControlMode getExtractionRedstoneMode(@Nonnull EnumFacing dir) {
        RedstoneControlMode res = extractionModes.get(dir);
        return res == null ? RedstoneControlMode.NEVER : res;
    }

    @Override
    public void setExtractionSignalColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col) {
        extractionColors.put(dir, col);
    }

    @Override
    @Nonnull
    public DyeColor getExtractionSignalColor(@Nonnull EnumFacing dir) {
        DyeColor result = extractionColors.get(dir);
        return result == null ? DyeColor.RED : result;
    }

    @Override
    public boolean canOutputToDir(@Nonnull EnumFacing dir) {
        return canInputToDir(dir) && (conduitConnections.contains(dir) || externalConnections.contains(dir));
    }

    protected boolean autoExtractForDir(@Nonnull EnumFacing dir) {
        if (!canExtractFromDir(dir)) {
            return false;
        }
        RedstoneControlMode mode = getExtractionRedstoneMode(dir);
        return ConduitUtil.isRedstoneControlModeMet(this, mode, getExtractionSignalColor(dir), dir);
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
                nbtRoot.setShort("extRM." + entry.getKey().name(), (short) entry.getValue().ordinal());
            }
        }

        for (Entry<EnumFacing, DyeColor> entry : extractionColors.entrySet()) {
            if (entry.getValue() != null) {
                nbtRoot.setShort("extSC." + entry.getKey().name(), (short) entry.getValue().ordinal());
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
                    extractionModes.put(dir, EnumReader.get(RedstoneControlMode.class, ord));
                }
            }
            key = "extSC." + dir.name();
            if (nbtRoot.hasKey(key)) {
                short ord = nbtRoot.getShort(key);
                if (ord >= 0 && ord < DyeColor.values().length) {
                    extractionColors.put(dir, EnumReader.get(DyeColor.class, ord));
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Nonnull
    @Override
    public ITabPanel createGuiPanel(@Nonnull IGuiExternalConnection gui, @Nonnull IClientConduit con) {
        return new GasSettings(gui, con);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean updateGuiPanel(@Nonnull ITabPanel panel) {
        return panel instanceof GasSettings && ((GasSettings) panel).updateConduit(this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getGuiPanelTabOrder() {
        return 1;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == Capabilities.GAS_HANDLER_CAPABILITY) {
            if (side != null && containsExternalConnection(side)) {
                ConnectionMode mode = getConnectionMode(side);
                return mode.acceptsInput() || mode.acceptsOutput();
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing side) {
        return hasCapability(capability, side) ? (T) getGasDir(side) : null;
    }

    @Override
    @Nullable
    public IGasHandler getGasDir(@Nullable EnumFacing dir) {
        return dir != null ? new ConnectionGasSide(dir) : null;
    }

    /**
     * Inner class for holding the direction of capabilities.
     */
    protected class ConnectionGasSide implements IGasHandler, ICapabilityProvider {

        @Nonnull
        protected EnumFacing side;

        public ConnectionGasSide(@Nonnull EnumFacing side) {
            this.side = side;
        }

        @Override
        public int receiveGas(EnumFacing facing, GasStack resource, boolean doFill) {
            if (canReceiveGas(facing, resource.getGas())) {
                return AbstractGasConduit.this.receiveGas(facing, resource, doFill);
            }
            return 0;
        }

        @Override
        public GasStack drawGas(EnumFacing facing, int maxDrain, boolean doDrain) {
            if (canDrawGas(facing, null)) {
                return AbstractGasConduit.this.drawGas(facing, maxDrain, doDrain);
            }
            return null;
        }

        @Override
        public boolean canReceiveGas(EnumFacing facing, Gas gas) {
            if (side.equals(facing) && getConnectionMode(facing).acceptsInput()) {
                return ConduitUtil.isRedstoneControlModeMet(AbstractGasConduit.this, getExtractionRedstoneMode(facing), getExtractionSignalColor(facing), facing);
            }
            return false;
        }

        @Override
        public boolean canDrawGas(EnumFacing facing, Gas gas) {
            if (side.equals(facing) && getConnectionMode(facing).acceptsOutput()) {
                return ConduitUtil.isRedstoneControlModeMet(AbstractGasConduit.this, getExtractionRedstoneMode(facing), getExtractionSignalColor(facing), facing);
            }
            return false;
        }

        @Override
        @Nonnull
        public GasTankInfo[] getTankInfo() {
            return AbstractGasConduit.this.getTankInfo();
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            if (capability == Capabilities.GAS_HANDLER_CAPABILITY) {
                if (!side.equals(facing)) {
                    return false;
                }
                ConnectionMode connectionMode = getConnectionMode(facing);
                return connectionMode.acceptsOutput() || connectionMode.acceptsInput();
            }
            return false;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return hasCapability(capability, facing) ? Capabilities.GAS_HANDLER_CAPABILITY.cast(this) : null;
        }
    }
}