package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block.SoundType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.DyeColor;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.Log;
import crazypants.enderio.api.tool.IHideFacades;
import crazypants.enderio.conduit.IConduitBundle.FacadeRenderState;
import crazypants.enderio.conduit.gas.GasUtil;
import crazypants.enderio.conduit.me.MEUtil;
import crazypants.enderio.conduit.oc.OCUtil;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.conduit.redstone.Signal;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.tool.ToolUtil;

public class ConduitUtil {

    public static final Random RANDOM = new Random();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void ensureValidNetwork(IConduit conduit) {
        TileEntity te = conduit.getBundle().getEntity();
        World world = te.getWorldObj();
        Collection<? extends IConduit> connections = ConduitUtil
                .getConnectedConduits(world, te.xCoord, te.yCoord, te.zCoord, conduit.getBaseConduitType());

        if (reuseNetwork(conduit, connections, world)) {
            return;
        }

        AbstractConduitNetwork res = conduit.createNetworkForType();
        res.init(conduit.getBundle(), connections, world);
        return;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static boolean reuseNetwork(IConduit con, Collection<? extends IConduit> connections, World world) {
        AbstractConduitNetwork network = null;
        for (IConduit conduit : connections) {
            if (network == null) {
                network = conduit.getNetwork();
            } else if (network != conduit.getNetwork()) {
                return false;
            }
        }
        if (network == null) {
            return false;
        }
        if (con.setNetwork(network)) {
            network.addConduit(con);
            network.notifyNetworkOfUpdate();
            return true;
        }
        return false;
    }

    public static <T extends IConduit> void disconectConduits(T con, ForgeDirection connDir) {
        con.conduitConnectionRemoved(connDir);
        BlockCoord loc = con.getLocation().getLocation(connDir);
        IConduit neighbour = ConduitUtil
                .getConduit(con.getBundle().getEntity().getWorldObj(), loc.x, loc.y, loc.z, con.getBaseConduitType());
        if (neighbour != null) {
            neighbour.conduitConnectionRemoved(connDir.getOpposite());
            if (neighbour.getNetwork() != null) {
                neighbour.getNetwork().destroyNetwork();
            }
        }
        if (con.getNetwork() != null) { // this should have been destroyed when destroying the neighbours network but
                                        // lets just make
            // sure
            con.getNetwork().destroyNetwork();
        }
        con.connectionsChanged();
        if (neighbour != null) {
            neighbour.connectionsChanged();
        }
    }

    public static <T extends IConduit> boolean joinConduits(T con, ForgeDirection faceHit) {
        BlockCoord loc = con.getLocation().getLocation(faceHit);
        IConduit neighbour = ConduitUtil
                .getConduit(con.getBundle().getEntity().getWorldObj(), loc.x, loc.y, loc.z, con.getBaseConduitType());
        if (neighbour != null && con.canConnectToConduit(faceHit, neighbour)
                && neighbour.canConnectToConduit(faceHit.getOpposite(), con)) {
            con.conduitConnectionAdded(faceHit);
            neighbour.conduitConnectionAdded(faceHit.getOpposite());
            if (con.getNetwork() != null) {
                con.getNetwork().destroyNetwork();
            }
            if (neighbour.getNetwork() != null) {
                neighbour.getNetwork().destroyNetwork();
            }
            con.connectionsChanged();
            neighbour.connectionsChanged();
            return true;
        }
        return false;
    }

    public static boolean forceSkylightRecalculation(World worldObj, int xCoord, int yCoord, int zCoord) {
        int height = worldObj.getHeightValue(xCoord, zCoord);
        if (height <= yCoord) {
            for (int i = 1; i < 12; i++) {
                if (worldObj.isAirBlock(xCoord, yCoord + i, zCoord)) {
                    // We need to force the re-lighting of the column due to a change
                    // in the light reaching bellow the block from the sky. To avoid
                    // modifying core classes to expose this functionality I am just placing then breaking
                    // a block above this one to force the check
                    worldObj.setBlock(xCoord, yCoord + i, zCoord, Blocks.stone, 0, 3);
                    worldObj.setBlockToAir(xCoord, yCoord + i, zCoord);

                    return true;
                }
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public static FacadeRenderState getRequiredFacadeRenderState(IConduitBundle bundle, EntityPlayer player) {
        if (!bundle.hasFacade()) {
            return FacadeRenderState.NONE;
        }
        if (isFacadeHidden(bundle, player)) {
            return FacadeRenderState.WIRE_FRAME;
        }
        return FacadeRenderState.FULL;
    }

    public static boolean isSolidFacadeRendered(IConduitBundle bundle, EntityPlayer player) {
        return bundle.getFacadeId() != null && !isFacadeHidden(bundle, player);
    }

    public static boolean isFacadeHidden(IConduitBundle bundle, EntityPlayer player) {
        return bundle.getFacadeId() != null && shouldHeldItemHideFacades(player);
    }

    public static ConduitDisplayMode getDisplayMode(EntityPlayer player) {
        player = player == null ? EnderIO.proxy.getClientPlayer() : player;
        if (player == null) {
            return ConduitDisplayMode.ALL;
        }
        ItemStack equipped = player.getCurrentEquippedItem();
        if (equipped == null) {
            return ConduitDisplayMode.ALL;
        }

        ConduitDisplayMode result = ConduitDisplayMode.getDisplayMode(equipped);
        if (result == null) {
            return ConduitDisplayMode.ALL;
        }
        return result;
    }

    public static boolean renderConduit(EntityPlayer player, IConduit con) {
        if (player == null || con == null) {
            return true;
        }
        return renderConduit(player, con.getBaseConduitType());
    }

    public static boolean renderConduit(EntityPlayer player, Class<? extends IConduit> conduitType) {
        if (player == null || conduitType == null) {
            return true;
        }
        ConduitDisplayMode mode = getDisplayMode(player);
        return mode.renderConduit(conduitType);
    }

    public static boolean shouldHeldItemHideFacades(EntityPlayer player) {
        player = player == null ? EnderIO.proxy.getClientPlayer() : player;
        if (player == null) {
            return false;
        }
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && held.getItem() instanceof IHideFacades) {
            return ((IHideFacades) held.getItem()).shouldHideFacades(held, player);
        }
        return ToolUtil.isToolEquipped(player);
    }

    public static boolean isConduitEquipped(EntityPlayer player) {
        player = player == null ? EnderIO.proxy.getClientPlayer() : player;
        if (player == null) {
            return false;
        }
        ItemStack equipped = player.getCurrentEquippedItem();
        if (equipped == null) {
            return false;
        }
        return equipped.getItem() instanceof IConduitItem;
    }

    public static boolean isProbeEquipped(EntityPlayer player) {
        player = player == null ? EnderIO.proxy.getClientPlayer() : player;
        if (player == null) {
            return false;
        }
        ItemStack equipped = player.getCurrentEquippedItem();
        if (equipped == null) {
            return false;
        }
        return equipped.getItem() == EnderIO.itemConduitProbe;
    }

    public static <T extends IConduit> T getConduit(World world, int x, int y, int z, Class<T> type) {
        if (world == null || !world.blockExists(x, y, z)) {
            return null;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof IConduitBundle) {
            IConduitBundle con = (IConduitBundle) te;
            return con.getConduit(type);
        }
        return null;
    }

    public static <T extends IConduit> T getConduit(World world, TileEntity te, ForgeDirection dir, Class<T> type) {
        return ConduitUtil
                .getConduit(world, te.xCoord + dir.offsetX, te.yCoord + dir.offsetY, te.zCoord + dir.offsetZ, type);
    }

    public static <T extends IConduit> Collection<T> getConnectedConduits(World world, int x, int y, int z,
            Class<T> type) {
        TileEntity te = world.blockExists(x, y, z) ? world.getTileEntity(x, y, z) : null;
        if (!(te instanceof IConduitBundle)) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<T>();
        IConduitBundle root = (IConduitBundle) te;
        T con = root.getConduit(type);
        if (con != null) {
            for (ForgeDirection dir : con.getConduitConnections()) {
                T connected = getConduit(world, root.getEntity(), dir, type);
                if (connected != null) {
                    result.add(connected);
                }
            }
        }
        return result;
    }

    public static void writeToNBT(IConduit conduit, NBTTagCompound conduitRoot) {
        if (conduit == null) {
            return;
        }

        NBTTagCompound conduitBody = new NBTTagCompound();
        conduit.writeToNBT(conduitBody);

        conduitRoot.setString("conduitType", conduit.getClass().getCanonicalName());
        conduitRoot.setTag("conduit", conduitBody);
    }

    public static IConduit readConduitFromNBT(NBTTagCompound conduitRoot, short nbtVersion) {
        String typeName = conduitRoot.getString("conduitType");
        NBTTagCompound conduitBody = conduitRoot.getCompoundTag("conduit");
        if (typeName == null || conduitBody == null) {
            return null;
        }
        if ((typeName.contains("conduit.oc") && !OCUtil.isOCEnabled())
                || (typeName.contains("conduit.me") && !MEUtil.isMEEnabled())
                || (typeName.contains("conduit.gas") && !GasUtil.isGasConduitEnabled())) {
            return null;
        }
        if (nbtVersion == 0 && "crazypants.enderio.conduit.liquid.LiquidConduit".equals(typeName)) {
            Log.debug("ConduitUtil.readConduitFromNBT: Converted pre 0.7.3 fluid conduit to advanced fluid conduit.");
            typeName = "crazypants.enderio.conduit.liquid.AdvancedLiquidConduit";
        } else if ("crazypants.enderio.conduit.liquid.AdvancedEnderLiquidConduit".equals(typeName)) {
            Log.debug(
                    "ConduitUtil.readConduitFromNBT: Converted advanced ender fluid conduit to crystalline ender fluid conduit.");
            typeName = "crazypants.enderio.conduit.liquid.CrystallineEnderLiquidConduit";
        }
        IConduit result;
        try {
            result = (IConduit) Class.forName(typeName).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create an instance of the conduit with name: " + typeName, e);
        }
        result.readFromNBT(conduitBody, nbtVersion);
        return result;
    }

    public static boolean isRedstoneControlModeMet(IConduitBundle bundle, RedstoneControlMode mode, DyeColor col) {

        if (mode == RedstoneControlMode.IGNORE) {
            return true;
        } else if (mode == RedstoneControlMode.NEVER) {
            return false;
        } else if (mode == null) {
            return false;
        }

        int signalStrength = getInternalSignalForColor(bundle, col);
        if (signalStrength < 15 && DyeColor.RED == col && bundle != null && bundle.getEntity() != null) {
            TileEntity te = bundle.getEntity();
            signalStrength = Math
                    .max(signalStrength, te.getWorldObj().getStrongestIndirectPower(te.xCoord, te.yCoord, te.zCoord));
        }
        return RedstoneControlMode.isConditionMet(mode, signalStrength);
    }

    public static int getInternalSignalForColor(IConduitBundle bundle, DyeColor col) {
        int signalStrength = 0;
        if (bundle == null) {
            return 0;
        }
        IRedstoneConduit rsCon = bundle.getConduit(IRedstoneConduit.class);
        if (rsCon != null) {
            Set<Signal> signals = rsCon.getNetworkOutputs(ForgeDirection.UNKNOWN);
            for (Signal sig : signals) {
                if (sig.color == col) {
                    if (sig.strength > signalStrength) {
                        signalStrength = sig.strength;
                    }
                }
            }
        }
        return signalStrength;
    }

    public static boolean isFluidValid(FluidStack fluidStack) {
        if (fluidStack != null) {
            String name = FluidRegistry.getFluidName(fluidStack);
            if (name != null && !name.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static void openConduitGui(World world, int x, int y, int z, EntityPlayer player) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileConduitBundle)) {
            return;
        }
        IConduitBundle cb = (IConduitBundle) te;
        Set<ForgeDirection> cons = new HashSet<ForgeDirection>();
        boolean hasInsulated = false;
        for (IConduit con : cb.getConduits()) {
            cons.addAll(con.getExternalConnections());
            if (con instanceof IInsulatedRedstoneConduit) {
                hasInsulated = true;
            }
        }
        if (cons.isEmpty() && !hasInsulated) {
            return;
        }
        if (cons.size() == 1) {
            player.openGui(
                    EnderIO.instance,
                    GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE + cons.iterator().next().ordinal(),
                    world,
                    x,
                    y,
                    z);
            return;
        }
        player.openGui(EnderIO.instance, GuiHandler.GUI_ID_EXTERNAL_CONNECTION_SELECTOR, world, x, y, z);
    }

    public static void playBreakSound(SoundType snd, World world, int x, int y, int z) {
        if (!world.isRemote) {
            world.playSoundEffect(
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    snd.getBreakSound(),
                    (snd.getVolume() + 1.0F) / 2.0F,
                    snd.getPitch() * 0.8F);
        } else {
            playClientBreakSound(snd);
        }
    }

    private static void playClientBreakSound(SoundType snd) {
        FMLClientHandler.instance().getClientPlayerEntity()
                .playSound(snd.getBreakSound(), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
    }

    public static void playHitSound(SoundType snd, World world, int x, int y, int z) {
        if (!world.isRemote) {
            world.playSoundEffect(
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    snd.getStepResourcePath(),
                    (snd.getVolume() + 1.0F) / 2.0F,
                    snd.getPitch() * 0.8F);
        } else {
            playClientHitSound(snd);
        }
    }

    private static void playClientHitSound(SoundType snd) {
        FMLClientHandler.instance().getClientPlayerEntity()
                .playSound(snd.getStepResourcePath(), (snd.getVolume() + 1.0F) / 8.0F, snd.getPitch() * 0.5F);
    }

    public static void playStepSound(SoundType snd, World world, int x, int y, int z) {
        if (!world.isRemote) {
            world.playSoundEffect(
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    snd.getStepResourcePath(),
                    (snd.getVolume() + 1.0F) / 2.0F,
                    snd.getPitch() * 0.8F);
        } else {
            playClientStepSound(snd);
        }
    }

    private static void playClientStepSound(SoundType snd) {
        FMLClientHandler.instance().getClientPlayerEntity()
                .playSound(snd.getStepResourcePath(), (snd.getVolume() + 1.0F) / 8.0F, snd.getPitch());
    }

    public static void playPlaceSound(SoundType snd, World world, int x, int y, int z) {
        if (!world.isRemote) {
            world.playSoundEffect(
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    snd.func_150496_b(),
                    (snd.getVolume() + 1.0F) / 2.0F,
                    snd.getPitch() * 0.8F);
        } else {
            playClientPlaceSound(snd);
        }
    }

    private static void playClientPlaceSound(SoundType snd) {
        FMLClientHandler.instance().getClientPlayerEntity()
                .playSound(snd.func_150496_b(), (snd.getVolume() + 1.0F) / 8.0F, snd.getPitch());
    }
}
