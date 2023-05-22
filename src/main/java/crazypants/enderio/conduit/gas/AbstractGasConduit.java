package crazypants.enderio.conduit.gas;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.DyeColor;

import cpw.mods.fml.common.Optional.Method;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.reservoir.TileReservoir;
import mekanism.api.gas.IGasHandler;

public abstract class AbstractGasConduit extends AbstractConduit implements IGasConduit {

    protected final EnumMap<ForgeDirection, RedstoneControlMode> extractionModes = new EnumMap<ForgeDirection, RedstoneControlMode>(
            ForgeDirection.class);
    protected final EnumMap<ForgeDirection, DyeColor> extractionColors = new EnumMap<ForgeDirection, DyeColor>(
            ForgeDirection.class);

    protected final Map<ForgeDirection, Integer> externalRedstoneSignals = new HashMap<ForgeDirection, Integer>();
    protected boolean redstoneStateDirty = true;

    @Method(modid = GasUtil.API_NAME)
    public IGasHandler getExternalHandler(ForgeDirection direction) {
        IGasHandler con = GasUtil.getExternalGasHandler(getBundle().getWorld(), getLocation().getLocation(direction));
        return (con != null && !(con instanceof IConduitBundle)) ? con : null;
    }

    @Method(modid = GasUtil.API_NAME)
    public IGasHandler getTankContainer(BlockCoord bc) {
        return GasUtil.getGasHandler(getBundle().getWorld(), bc);
    }

    @Override
    public boolean canConnectToExternal(ForgeDirection direction, boolean ignoreDisabled) {
        IGasHandler h = getExternalHandler(direction);
        if (h == null) {
            return false;
        }
        return true;
    }

    @Override
    public Class<? extends IConduit> getBaseConduitType() {
        return IGasConduit.class;
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
        if (res == null) {
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
        if (result == null) {
            return DyeColor.RED;
        }
        return result;
    }

    @Override
    public boolean canOutputToDir(ForgeDirection dir) {
        if (isExtractingFromDir(dir) || getConnectionMode(dir) == ConnectionMode.DISABLED) {
            return false;
        }
        if (conduitConnections.contains(dir)) {
            return true;
        }
        if (!externalConnections.contains(dir)) {
            return false;
        }
        IGasHandler ext = getExternalHandler(dir);
        if (ext instanceof TileReservoir) { // dont push to an auto ejecting
            // resevoir or we loop
            TileReservoir tr = (TileReservoir) ext;
            return !tr.isMultiblock() || !tr.isAutoEject();
        }
        return true;
    }

    protected boolean autoExtractForDir(ForgeDirection dir) {
        if (!isExtractingFromDir(dir)) {
            return false;
        }
        RedstoneControlMode mode = getExtractionRedstoneMode(dir);
        if (mode == RedstoneControlMode.IGNORE) {
            return true;
        }
        if (mode == RedstoneControlMode.NEVER) {
            return false;
        }
        if (redstoneStateDirty) {
            externalRedstoneSignals.clear();
            redstoneStateDirty = false;
        }

        DyeColor col = getExtractionSignalColor(dir);
        int signal = ConduitUtil.getInternalSignalForColor(getBundle(), col);
        if (RedstoneControlMode.isConditionMet(mode, signal) && mode != RedstoneControlMode.OFF) {
            return true;
        }

        return isConditionMetByExternalSignal(dir, mode, col);
    }

    private boolean isConditionMetByExternalSignal(ForgeDirection dir, RedstoneControlMode mode, DyeColor col) {
        int externalSignal = 0;
        if (col == DyeColor.RED) {
            Integer val = externalRedstoneSignals.get(dir);
            if (val == null) {
                TileEntity te = getBundle().getEntity();
                externalSignal = te.getWorldObj().getStrongestIndirectPower(te.xCoord, te.yCoord, te.zCoord);
                externalRedstoneSignals.put(dir, externalSignal);
            } else {
                externalSignal = val;
            }
        }

        return RedstoneControlMode.isConditionMet(mode, externalSignal);
    }

    @Override
    public boolean isExtractingFromDir(ForgeDirection dir) {
        return getConnectionMode(dir) == ConnectionMode.INPUT;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtRoot) {
        super.writeToNBT(nbtRoot);

        for (Entry<ForgeDirection, RedstoneControlMode> entry : extractionModes.entrySet()) {
            if (entry.getValue() != null) {
                short ord = (short) entry.getValue().ordinal();
                nbtRoot.setShort("extRM." + entry.getKey().name(), ord);
            }
        }

        for (Entry<ForgeDirection, DyeColor> entry : extractionColors.entrySet()) {
            if (entry.getValue() != null) {
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
}
