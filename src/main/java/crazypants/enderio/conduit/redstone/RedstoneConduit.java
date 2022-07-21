package crazypants.enderio.conduit.redstone;

import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.DyeColor;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;
import dan200.computercraft.api.ComputerCraftAPI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetOutputNode;

public class RedstoneConduit extends AbstractConduit implements IRedstoneConduit {

    static final Map<String, IIcon> ICONS = new HashMap<String, IIcon>();

    @SideOnly(Side.CLIENT)
    public static void initIcons() {
        IconUtil.addIconProvider(new IconUtil.IIconProvider() {

            @Override
            public void registerIcons(IIconRegister register) {
                ICONS.put(KEY_CORE_OFF_ICON, register.registerIcon(KEY_CORE_OFF_ICON));
                ICONS.put(KEY_CORE_ON_ICON, register.registerIcon(KEY_CORE_ON_ICON));
                ICONS.put(KEY_CONDUIT_ICON, register.registerIcon(KEY_CONDUIT_ICON));
                ICONS.put(KEY_TRANSMISSION_ICON, register.registerIcon(KEY_TRANSMISSION_ICON));
            }

            @Override
            public int getTextureType() {
                return 0;
            }
        });
    }

    protected RedstoneConduitNetwork network;

    protected final List<Set<Signal>> externalSignals = new ArrayList<Set<Signal>>();

    protected boolean neighbourDirty = true;

    @SuppressWarnings("unused")
    public RedstoneConduit() {
        for (ForgeDirection ignored : ForgeDirection.VALID_DIRECTIONS) {
            externalSignals.add(new HashSet<Signal>());
        }
    }

    @Override
    public ItemStack createItem() {
        return new ItemStack(EnderIO.itemRedstoneConduit, 1, 0);
    }

    @Override
    public Class<? extends IConduit> getBaseConduitType() {
        return IRedstoneConduit.class;
    }

    @Override
    public AbstractConduitNetwork<IRedstoneConduit, IRedstoneConduit> getNetwork() {
        return network;
    }

    @Override
    public boolean setNetwork(AbstractConduitNetwork<?, ?> network) {
        this.network = (RedstoneConduitNetwork) network;
        return true;
    }

    @Override
    public boolean canConnectToExternal(ForgeDirection direction, boolean ignoreDisabled) {
        return false;
    }

    @Override
    public void updateNetwork() {
        World world = getBundle().getEntity().getWorldObj();
        if (world != null) {
            updateNetwork(world);
        }
    }

    @Override
    public void onChunkUnload(World worldObj) {
        RedstoneConduitNetwork network = (RedstoneConduitNetwork) getNetwork();
        if (network != null) {
            Set<Signal> oldSignals = Sets.newHashSet(network.getSignals());
            List<IRedstoneConduit> conduits = Lists.newArrayList(network.getConduits());
            super.onChunkUnload(worldObj);
            network.afterChunkUnload(conduits, oldSignals);
        }
    }

    protected boolean acceptSignalsForDir(ForgeDirection dir) {
        BlockCoord loc = getLocation().getLocation(dir);
        return ConduitUtil.getConduit(
                        getBundle().getEntity().getWorldObj(), loc.x, loc.y, loc.z, IRedstoneConduit.class)
                == null;
    }

    @Override
    public Set<Signal> getNetworkInputs() {
        return getNetworkInputs(null);
    }

    @Override
    public Set<Signal> getNetworkInputs(ForgeDirection side) {
        if (network != null) {
            network.setNetworkEnabled(false);
        }

        Set<Signal> res = new HashSet<Signal>();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if ((side == null || dir == side) && acceptSignalsForDir(dir)) {
                int input = getExternalPowerLevel(dir);
                if (input > 1) { // need to degrade external signals by one as they
                    // enter
                    BlockCoord loc = getLocation().getLocation(dir);
                    Signal signal = new Signal(loc.x, loc.y, loc.z, dir, input - 1, getSignalColor(dir));
                    res.add(signal);
                }

                if (Loader.isModLoaded("MineFactoryReloaded")) {
                    // Add stored RedNet input. See onInputsChanged below for more information.
                    res.addAll(externalSignals.get(dir.ordinal()));

                    // Manually check if neighbors are outputting bundled redstone signals.
                    // This is required to directly support other blocks implementing the
                    // RedNet API, without requiring a piece of RedNet cable in-between.
                    int[] bundledInput = getExternalBundledPowerLevel(dir);
                    if (bundledInput != null) {
                        BlockCoord loc = getLocation().getLocation(dir);
                        for (int subnet = 0; subnet < bundledInput.length; ++subnet) {
                            if (bundledInput[subnet] > 1) { // force signal strength reduction to avoid cycles
                                int color = convertColorForRedNet(subnet);
                                Signal signal = new Signal(
                                        loc.x, loc.y, loc.z, dir, bundledInput[subnet] - 1, DyeColor.fromIndex(color));
                                res.add(signal);
                            }
                        }
                    }
                }
                if (Loader.isModLoaded("ComputerCraft") && canConnectToExternal(dir, false)) {
                    BlockCoord loc = getLocation().getLocation(dir);
                    int bundledInput = getComputerCraftBundledPowerLevel(dir);
                    if (bundledInput >= 0) {
                        for (int i = 0; i < 16; i++) {
                            int color = bundledInput >>> i & 1;
                            Signal signal = new Signal(
                                    loc.x,
                                    loc.y,
                                    loc.z,
                                    dir,
                                    color == 1 ? 16 : 0,
                                    DyeColor.fromIndex(Math.max(0, 15 - i)));
                            res.add(signal);
                        }
                    }
                }
            }
        }

        if (network != null) {
            network.setNetworkEnabled(true);
        }

        return res;
    }

    @Override
    public DyeColor getSignalColor(ForgeDirection dir) {
        return DyeColor.RED;
    }

    @Override
    public Set<Signal> getNetworkOutputs(ForgeDirection side) {
        if (network == null) {
            return Collections.emptySet();
        }
        return network.getSignals();
    }

    @Override
    public boolean onNeighborBlockChange(Block blockId) {
        World world = getBundle().getEntity().getWorldObj();
        if (world.isRemote) {
            return false;
        }
        boolean res = super.onNeighborBlockChange(blockId);
        if (network == null || network.updatingNetwork) {
            return false;
        }
        neighbourDirty |= blockId != EnderIO.blockConduitBundle;
        return res;
    }

    @Override
    public void updateEntity(World world) {
        super.updateEntity(world);
        if (!world.isRemote && neighbourDirty) {
            network.destroyNetwork();
            updateNetwork(world);
            neighbourDirty = false;
        }
    }

    // returns 16 for string power inputs
    protected int getExternalPowerLevel(ForgeDirection dir) {
        World world = getBundle().getEntity().getWorldObj();
        BlockCoord loc = getLocation();
        loc = loc.getLocation(dir);

        int strong = world.isBlockProvidingPowerTo(loc.x, loc.y, loc.z, dir.ordinal());
        if (strong > 0) {
            return 16;
        }

        int res = world.getIndirectPowerLevelTo(loc.x, loc.y, loc.z, dir.ordinal());
        if (res < 15 && world.getBlock(loc.x, loc.y, loc.z) == Blocks.redstone_wire) {
            int wireIn = world.getBlockMetadata(loc.x, loc.y, loc.z);
            res = Math.max(res, wireIn);
        }
        return res;
    }

    protected int[] getExternalBundledPowerLevel(ForgeDirection dir) {
        World world = getBundle().getEntity().getWorldObj();
        BlockCoord loc = getLocation();
        loc = loc.getLocation(dir);

        Block block = world.getBlock(loc.x, loc.y, loc.z);
        if (block instanceof IRedNetOutputNode) {
            return ((IRedNetOutputNode) block).getOutputValues(world, loc.x, loc.y, loc.z, dir.getOpposite());
        }

        return null;
    }

    @Method(modid = "ComputerCraft")
    protected int getComputerCraftBundledPowerLevel(ForgeDirection dir) {
        BlockCoord loc = getLocation().getLocation(dir);
        return ComputerCraftAPI.getBundledRedstoneOutput(
                getBundle().getWorld(), loc.x, loc.y, loc.z, dir.getOpposite().ordinal());
    }

    @Override
    public int isProvidingStrongPower(ForgeDirection toDirection) {
        return 0;
    }

    @Override
    public int isProvidingWeakPower(ForgeDirection toDirection) {
        if (network == null || !network.isNetworkEnabled()) {
            return 0;
        }
        int result = 0;
        for (Signal signal : getNetworkOutputs(toDirection.getOpposite())) {
            result = Math.max(result, signal.strength);
        }
        return result;
    }

    @Override
    public IIcon getTextureForState(CollidableComponent component) {
        if (component.dir == ForgeDirection.UNKNOWN) {
            return isActive() ? ICONS.get(KEY_CORE_ON_ICON) : ICONS.get(KEY_CORE_OFF_ICON);
        }
        return isActive() ? ICONS.get(KEY_TRANSMISSION_ICON) : ICONS.get(KEY_CONDUIT_ICON);
    }

    @Override
    public IIcon getTransmitionTextureForState(CollidableComponent component) {
        return null;
    }

    @Override
    public String toString() {
        return "RedstoneConduit [network=" + network + " connections=" + conduitConnections + " active=" + active + "]";
    }

    @Override
    public int[] getOutputValues(World world, int x, int y, int z, ForgeDirection side) {
        int[] result = new int[16];

        Set<Signal> outs = network != null ? network.getSignals() : null;
        if (outs != null) {
            BlockCoord loc = getLocation().getLocation(side);
            for (Signal s : outs) {
                // Avoid "feedback loops", i.e. don't report an output on a side where
                // we have an input (otherwise a RedNet cable connected to a conduit
                // will keep a signal high, even if the original source vanishes).
                // Note that it's still possible to get loops if there are two
                // connections between a conduit set and a RedNet network. I'm not
                // sure there's anything we could do to avoid this, though, nor am I
                // convinced we should.
                if (s.dir != side || s.x != loc.x || s.y != loc.y || s.z != loc.z) {
                    int subnet = convertColorForRedNet(s.color.ordinal());
                    result[subnet] = s.strength;
                }
            }
        }

        return result;
    }

    @Override
    public int getOutputValue(World world, int x, int y, int z, ForgeDirection side, int subnet) {
        Set<Signal> outs = network != null ? network.getSignals() : null;
        if (outs != null) {
            BlockCoord loc = getLocation().getLocation(side);
            int color = convertColorForRedNet(subnet);
            for (Signal s : outs) {
                // Avoid "feedback loops", see comment in getOutputValues.
                if (s.dir != side || s.x != loc.x || s.y != loc.y || s.z != loc.z) {
                    if (s.color.ordinal() == color) {
                        return s.strength;
                    }
                }
            }
        }

        return 0;
    }

    @Override
    public void onInputsChanged(World world, int x, int y, int z, ForgeDirection side, int[] inputValues) {
        // Check if anything changed, if so mark neighbor dirty to trigger an
        // update in the next tick. We have to iterate over the colors in the
        // outer loop to make sure we check all of them, because for channels
        // with zero signal strength no signals are stored.
        Set<Signal> inputs = getNetworkInputs(side);
        externalSignals.get(side.ordinal()).clear();
        BlockCoord loc = getLocation().getLocation(side);
        for (int subnet = 0; subnet < inputValues.length; ++subnet) {
            int color = convertColorForRedNet(subnet);
            int newInput = inputValues[subnet];
            int oldInput = 0;
            for (Signal input : inputs) {
                if (input.color.ordinal() == color) {
                    oldInput = input.strength;
                    break;
                }
            }

            neighbourDirty |= oldInput != newInput;

            // Store external inputs to allow regenerating the global list of signals
            // in getNetworkInputs. This is required for RedNet cables to work, e.g.
            if (newInput > 1) { // force signal strength reduction to avoid cycles
                externalSignals
                        .get(side.ordinal())
                        .add(new Signal(loc.x, loc.y, loc.z, side, newInput - 1, DyeColor.fromIndex(color)));
            }
        }
    }

    @Override
    public boolean onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        return false;
    }

    // RedNet refers to colors in inverse order...
    private static int convertColorForRedNet(int colorOrSubnet) {
        return 15 - colorOrSubnet;
    }
}
