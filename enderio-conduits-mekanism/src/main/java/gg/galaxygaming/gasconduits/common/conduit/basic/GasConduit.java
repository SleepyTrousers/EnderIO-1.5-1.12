package gg.galaxygaming.gasconduits.common.conduit.basic;

import com.enderio.core.common.util.Log;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitNetwork;
import crazypants.enderio.base.conduit.IConduitTexture;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.conduits.render.ConduitTexture;
import gg.galaxygaming.gasconduits.GasConduitsConstants;
import gg.galaxygaming.gasconduits.common.conduit.AbstractGasTankConduit;
import gg.galaxygaming.gasconduits.common.conduit.AbstractGasTankConduitNetwork;
import gg.galaxygaming.gasconduits.common.conduit.GasConduitObject;
import gg.galaxygaming.gasconduits.common.conduit.IGasConduit;
import gg.galaxygaming.gasconduits.common.config.GasConduitConfig;
import gg.galaxygaming.gasconduits.common.network.PacketConduitGasLevel;
import gg.galaxygaming.gasconduits.common.utils.GasUtil;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GasConduit extends AbstractGasTankConduit {

    static final int VOLUME_PER_CONNECTION = GasConduitsConstants.GAS_VOLUME / 4;

    public static final IConduitTexture ICON_KEY = new ConduitTexture(TextureRegistry.registerTexture("gasconduits:blocks/gas_conduit", false), ConduitTexture.arm(0));
    public static final IConduitTexture ICON_KEY_LOCKED = new ConduitTexture(TextureRegistry.registerTexture("gasconduits:blocks/gas_conduit_locked", false));
    public static final IConduitTexture ICON_CORE_KEY = new ConduitTexture(TextureRegistry.registerTexture("gasconduits:blocks/gas_conduit_core", false), ConduitTexture.core(0));
    public static final IConduitTexture ICON_EXTRACT_KEY = new ConduitTexture(TextureRegistry.registerTexture("gasconduits:blocks/gas_conduit_extract", false));
    public static final IConduitTexture ICON_EMPTY_EXTRACT_KEY = new ConduitTexture(TextureRegistry.registerTexture("gasconduits:blocks/empty_gas_conduit_extract", false));
    public static final IConduitTexture ICON_INSERT_KEY = new ConduitTexture(TextureRegistry.registerTexture("gasconduits:blocks/gas_conduit_insert", false));
    public static final IConduitTexture ICON_EMPTY_INSERT_KEY = new ConduitTexture(TextureRegistry.registerTexture("gasconduits:blocks/empty_gas_conduit_insert", false));

    private GasConduitNetwork network;

    private float lastSyncRatio = -99;

    private int currentPushToken;

    // -----------------------------

    private EnumFacing startPushDir = EnumFacing.DOWN;

    private long ticksSinceFailedExtract = 0;

    @Override
    public void updateEntity(@Nonnull World world) {
        super.updateEntity(world);
        if (world.isRemote) {
            return;
        }
        updateStartPushDir();
        doExtract();

        if (stateDirty) {
            getBundle().dirty();
            stateDirty = false;
            lastSyncRatio = tank.getFilledRatio();
        } else if ((lastSyncRatio != tank.getFilledRatio() && world.getTotalWorldTime() % 2 == 0)) {
            // need to send a custom packet as we don't want want to trigger a full chunk update, just
            // need to get the required values to the entity renderer
            BlockPos pos = getBundle().getLocation();
            PacketHandler.INSTANCE.sendToAllAround(new PacketConduitGasLevel(this), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
            lastSyncRatio = tank.getFilledRatio();
        }
    }

    private void doExtract() {
        if (!hasExtractableMode()) {
            return;
        }

        // assume failure, reset to 0 if we do extract
        ticksSinceFailedExtract++;
        if (ticksSinceFailedExtract > 9 && ticksSinceFailedExtract % 10 != 0) {
            // after 10 ticks of failing, only check every 10 ticks
            return;
        }

        int tier1ExtractRate = GasConduitConfig.tier1_extractRate.get();
        for (EnumFacing dir : externalConnections) {
            if (autoExtractForDir(dir)) {
                IGasHandler extTank = getExternalHandler(dir);
                GasStack couldDrain = GasUtil.getGasStack(extTank, dir.getOpposite());
                if (couldDrain != null && canReceiveGas(dir, couldDrain.getGas()) && extTank.canDrawGas(dir.getOpposite(), couldDrain.getGas())) {
                    if (couldDrain.amount > tier1ExtractRate) {
                        couldDrain.amount = tier1ExtractRate;
                    }
                    int used = pushGas(dir, couldDrain, true, network == null ? -1 : network.getNextPushToken());
                    if (used > 0) {
                        couldDrain.amount = used;
                        extTank.drawGas(dir.getOpposite(), couldDrain.amount, true);
                        if (network != null && network.getGasType() == null) {
                            network.setGasType(couldDrain);
                        }
                        ticksSinceFailedExtract = 0;
                    }
                }
            }
        }
    }

    // --------------- Gas Capability ------------

    @Override
    @Nonnull
    public GasTankInfo[] getTankInfo() {
        return network == null ? new GasTankInfo[0] : new GasTankInfo[]{tank};
    }

    @Override
    public int receiveGas(EnumFacing side, GasStack resource, boolean doFill) {
        return 0;
    }

    @Nullable
    @Override
    public GasStack drawGas(EnumFacing side, int maxDrain, boolean doDrain) {
        return canDrawGas(side, tank.getGasType()) ? tank.draw(maxDrain, doDrain) : null;
    }

    // --------------- End -------------------------

    public int receiveGas(EnumFacing from, GasStack resource, boolean doFill, boolean doPush, int pushToken) {
        if (network == null) {
            Log.error("The network for this conduit was null when asked to receiveGas. Please report this to the GasConduits github");
            return 0;
        }

        if (!network.canAcceptGas(resource)) {
            return 0;
        }
        network.setGasType(resource);
        resource = resource.copy();
        resource.amount = Math.min(GasConduitConfig.tier1_maxIO.get(), resource.amount);

        if (doPush) {
            return pushGas(from, resource, doFill, pushToken);
        }
        return tank.receive(resource, doFill);
    }

    private void updateStartPushDir() {
        EnumFacing newVal = getNextDir(startPushDir);
        boolean foundNewStart = false;
        while (newVal != startPushDir && !foundNewStart) {
            foundNewStart = containsConduitConnection(newVal) || containsExternalConnection(newVal);
            newVal = getNextDir(newVal);
        }
        startPushDir = newVal;
    }

    private EnumFacing getNextDir(@Nonnull EnumFacing dir) {
        return dir.ordinal() >= EnumFacing.VALUES.length - 1 ? EnumFacing.VALUES[0] : EnumFacing.VALUES[dir.ordinal() + 1];
    }

    private int pushGas(@Nullable EnumFacing from, GasStack pushStack, boolean doPush, int token) {
        if (token == currentPushToken || pushStack == null || pushStack.amount <= 0 || network == null) {
            return 0;
        }
        currentPushToken = token;
        int pushed = 0;
        int total = pushStack.amount;

        EnumFacing dir = startPushDir;
        GasStack toPush = pushStack.copy();

        int filledLocal = tank.receive(toPush, doPush);
        toPush.amount -= filledLocal;
        pushed += filledLocal;

        do {
            if (dir != from && canOutputToDir(dir) && !autoExtractForDir(dir)) {
                if (containsConduitConnection(dir)) {
                    IGasConduit conduitCon = getGasConduit(dir);
                    if (conduitCon != null) {
                        int toCon = ((GasConduit) conduitCon).pushGas(dir.getOpposite(), toPush, doPush, token);
                        toPush.amount -= toCon;
                        pushed += toCon;
                    }
                } else if (containsExternalConnection(dir)) {
                    IGasHandler con = getExternalHandler(dir);
                    if (con != null && con.canReceiveGas(dir.getOpposite(), toPush.getGas())) {
                        int toExt = con.receiveGas(dir.getOpposite(), toPush, doPush);
                        toPush.amount -= toExt;
                        pushed += toExt;
                        if (doPush) {
                            network.outputedToExternal(toExt);
                        }
                    }
                }
            }
            dir = getNextDir(dir);
        } while (dir != startPushDir && pushed < total);
        return pushed;
    }

    private IGasConduit getGasConduit(@Nonnull EnumFacing dir) {
        TileEntity ent = getBundle().getEntity();
        return ConduitUtil.getConduit(ent.getWorld(), ent, dir, IGasConduit.class);
    }

    @Override
    public boolean canReceiveGas(EnumFacing from, Gas gas) {
        if (!getConnectionMode(from).acceptsInput() || network == null || gas == null) {
            return false;
        }
        return tank.getGas() == null || GasUtil.areGasesTheSame(gas, tank.getGasType());
    }

    @Override
    public boolean canDrawGas(EnumFacing from, Gas gas) {
        return getConnectionMode(from).acceptsOutput() && tank.getGasType() != null && gas != null && GasUtil.areGasesTheSame(tank.getGasType(), gas);
    }

    @Override
    public void connectionsChanged() {
        super.connectionsChanged();
        updateTank();
    }

    @Override
    @Nonnull
    public ItemStack createItem() {
        return new ItemStack(GasConduitObject.itemGasConduit.getItemNN(), 1, 0);
    }

    @Override
    @Nonnull
    public IConduitNetwork<?, ?> getNetwork() {
        return network;
    }

    @Override
    public boolean setNetwork(@Nonnull IConduitNetwork<?, ?> network) {
        if (!(network instanceof AbstractGasTankConduitNetwork)) {
            return false;
        }

        AbstractGasTankConduitNetwork<?> n = (AbstractGasTankConduitNetwork<?>) network;
        if (tank.getGas() == null) {
            tank.setGas(n.getGasType() == null ? null : n.getGasType().copy());
        } else if (n.getGasType() == null) {
            n.setGasType(tank.getGas());
        } else if (!tank.getGas().isGasEqual(n.getGasType())) {
            return false;
        }
        this.network = (GasConduitNetwork) network;
        return super.setNetwork(network);
    }

    @Override
    public void clearNetwork() {
        this.network = null;
    }

    @Override
    public boolean canConnectToConduit(@Nonnull EnumFacing direction, @Nonnull IConduit con) {
        if (!super.canConnectToConduit(direction, con) || !(con instanceof GasConduit)) {
            return false;
        }
        return GasConduitNetwork.areGasesCompatible(getGasType(), ((GasConduit) con).getGasType());
    }

    @SideOnly(Side.CLIENT)
    @Override
    @Nonnull
    public IConduitTexture getTextureForState(@Nonnull CollidableComponent component) {
        if (component.isCore()) {
            return ICON_CORE_KEY;
        }
        EnumFacing componentDirection = component.getDirection();
        if (getConnectionMode(componentDirection) == ConnectionMode.INPUT) {
            return getGasType() == null ? ICON_EMPTY_EXTRACT_KEY : ICON_EXTRACT_KEY;
        }
        if (getConnectionMode(componentDirection) == ConnectionMode.OUTPUT) {
            return getGasType() == null ? ICON_EMPTY_INSERT_KEY : ICON_INSERT_KEY;
        }
        return gasTypeLocked ? ICON_KEY_LOCKED : ICON_KEY;
    }

    @Override
    protected void updateTank() {
        int totalConnections = getConduitConnections().size() + getExternalConnections().size();
        tank.setMaxGas(totalConnections * VOLUME_PER_CONNECTION);
    }

    @Override
    protected boolean canJoinNeighbour(IGasConduit n) {
        return n instanceof GasConduit;
    }

    @Override
    public AbstractGasTankConduitNetwork<? extends AbstractGasTankConduit> getTankNetwork() {
        return network;
    }

    @Override
    public IGasHandler getGasDir(@Nullable EnumFacing dir) {
        return dir != null ? new ConnectionGasConduitSide(dir) : this;
    }

    @Override
    @Nonnull
    public GasConduitNetwork createNetworkForType() {
        return new GasConduitNetwork();
    }

    protected class ConnectionGasConduitSide extends ConnectionGasSide {

        public ConnectionGasConduitSide(EnumFacing side) {
            super(side);
        }

        @Override
        public int receiveGas(EnumFacing side, GasStack resource, boolean doFill) {
            if (canReceiveGas(side, resource.getGas()) && network != null && network.lockNetworkForFill()) {
                try {
                    int res = GasConduit.this.receiveGas(side, resource, doFill, true, network == null ? -1 : network.getNextPushToken());
                    if (doFill && externalConnections.contains(side) && network != null) {
                        network.addedFromExternal(res);
                    }
                    return res;
                } finally {
                    if (network != null) {
                        network.unlockNetworkFromFill();
                    }
                }
            }
            return 0;
        }
    }

    @Override
    @Nonnull
    public Class<? extends IConduit> getCollidableType() {
        return GasConduit.class;
    }
}