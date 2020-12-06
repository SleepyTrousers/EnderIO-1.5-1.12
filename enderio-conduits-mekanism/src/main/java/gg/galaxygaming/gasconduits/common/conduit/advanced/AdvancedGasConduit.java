package gg.galaxygaming.gasconduits.common.conduit.advanced;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.IconUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitNetwork;
import crazypants.enderio.base.conduit.IConduitTexture;
import crazypants.enderio.base.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.conduits.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduits.conduit.power.IPowerConduit;
import crazypants.enderio.conduits.conduit.power.PowerConduit;
import crazypants.enderio.conduits.render.BlockStateWrapperConduitBundle;
import crazypants.enderio.conduits.render.ConduitTexture;
import crazypants.enderio.conduits.render.ConduitTextureWrapper;
import gg.galaxygaming.gasconduits.GasConduitsConstants;
import gg.galaxygaming.gasconduits.common.conduit.AbstractGasTankConduit;
import gg.galaxygaming.gasconduits.common.conduit.AbstractGasTankConduitNetwork;
import gg.galaxygaming.gasconduits.common.conduit.GasConduitObject;
import gg.galaxygaming.gasconduits.common.conduit.GasOutput;
import gg.galaxygaming.gasconduits.common.conduit.IGasConduit;
import gg.galaxygaming.gasconduits.common.conduit.basic.GasConduitNetwork;
import gg.galaxygaming.gasconduits.common.config.GasConduitConfig;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AdvancedGasConduit extends AbstractGasTankConduit {

    public static final int CONDUIT_VOLUME = GasConduitsConstants.GAS_VOLUME;

    public static final IConduitTexture ICON_KEY = new ConduitTexture(TextureRegistry.registerTexture("gasconduits:blocks/gas_conduit", false), ConduitTexture.arm(1));
    public static final IConduitTexture ICON_KEY_LOCKED = new ConduitTexture(TextureRegistry.registerTexture("gasconduits:blocks/gas_conduit", false), ConduitTexture.arm(2));
    public static final IConduitTexture ICON_CORE_KEY = new ConduitTexture(TextureRegistry.registerTexture("gasconduits:blocks/gas_conduit_core", false), ConduitTexture.core(1));

    public static final TextureSupplier ICON_EMPTY_EDGE = TextureRegistry.registerTexture("gasconduits:blocks/gas_conduit_advanced_edge", false);

    private AdvancedGasConduitNetwork network;

    private long ticksSinceFailedExtract = 0;

    public AdvancedGasConduit() {
        updateTank();
    }

    @Override
    public void updateEntity(@Nonnull World world) {
        super.updateEntity(world);
        if (!world.isRemote) {
            doExtract();
            if (stateDirty) {
                getBundle().dirty();
                stateDirty = false;
            }
        }
    }

    private void doExtract() {
        // Extraction can happen on extract mode or in/out mode
        if (!hasExtractableMode()) {
            return;
        }
        if (network == null) {
            return;
        }

        // assume failure, reset to 0 if we do extract
        ticksSinceFailedExtract++;
        if (ticksSinceFailedExtract > 25 && ticksSinceFailedExtract % 10 != 0) {
            // after 25 ticks of failing, only check every 10 ticks
            return;
        }

        int tier2ExtractRate = GasConduitConfig.tier2_extractRate.get();
        for (EnumFacing dir : externalConnections) {
            if (autoExtractForDir(dir) && network.extractFrom(this, dir, tier2ExtractRate)) {
                ticksSinceFailedExtract = 0;
            }
        }
    }

    @Override
    protected void updateTank() {
        tank.setMaxGas(CONDUIT_VOLUME);
        if (network != null) {
            network.updateConduitVolumes();
        }
    }

    @Override
    @Nonnull
    public ItemStack createItem() {
        return new ItemStack(GasConduitObject.itemGasConduit.getItemNN(), 1, 1);
    }

    @Override
    public @Nullable
    AbstractConduitNetwork<?, ?> getNetwork() {
        return network;
    }

    @Override
    public boolean setNetwork(@Nonnull IConduitNetwork<?, ?> network) {
        if (!(network instanceof AdvancedGasConduitNetwork)) {
            return false;
        }

        AdvancedGasConduitNetwork n = (AdvancedGasConduitNetwork) network;
        if (tank.getGas() == null) {
            tank.setGas(n.getGasType() == null ? null : n.getGasType().copy());
        } else if (n.getGasType() == null) {
            n.setGasType(tank.getGas());
        } else if (!tank.getGas().isGasEqual(n.getGasType())) {
            return false;
        }
        this.network = n;
        return super.setNetwork(network);
    }

    @Override
    public void clearNetwork() {
        this.network = null;
    }

    @Override
    public boolean canConnectToConduit(@Nonnull EnumFacing direction, @Nonnull IConduit con) {
        if (!super.canConnectToConduit(direction, con) || !(con instanceof AdvancedGasConduit)) {
            return false;
        }
        return GasConduitNetwork.areGasesCompatible(getGasType(), ((AdvancedGasConduit) con).getGasType());
    }

    @Override
    public void setConnectionMode(@Nonnull EnumFacing dir, @Nonnull ConnectionMode mode) {
        super.setConnectionMode(dir, mode);
        refreshInputs(dir);
    }

    @Override
    public void setExtractionRedstoneMode(@Nonnull RedstoneControlMode mode, @Nonnull EnumFacing dir) {
        super.setExtractionRedstoneMode(mode, dir);
        refreshInputs(dir);
    }

    private void refreshInputs(@Nonnull EnumFacing dir) {
        if (network != null) {
            GasOutput lo = new GasOutput(getBundle().getLocation().offset(dir), dir.getOpposite());
            network.removeInput(lo);
            if (canInputToDir(dir) && containsExternalConnection(dir)) {
                network.addInput(lo);
            }
        }
    }

    @Override
    public void externalConnectionAdded(@Nonnull EnumFacing fromDirection) {
        super.externalConnectionAdded(fromDirection);
        refreshInputs(fromDirection);
    }

    @Override
    public void externalConnectionRemoved(@Nonnull EnumFacing fromDirection) {
        super.externalConnectionRemoved(fromDirection);
        refreshInputs(fromDirection);
    }

    // -------------------------------------
    // TEXTURES
    // -------------------------------------

    @SideOnly(Side.CLIENT)
    @Override
    @Nonnull
    public IConduitTexture getTextureForState(@Nonnull CollidableComponent component) {
        if (component.isCore()) {
            return ICON_CORE_KEY;
        }
        if (PowerConduit.COLOR_CONTROLLER_ID.equals(component.data)) {
            return new ConduitTextureWrapper(IconUtil.instance.whiteTexture);
        }
        return gasTypeLocked ? ICON_KEY_LOCKED : ICON_KEY;
    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getNotSetEdgeTexture() {
        return ICON_EMPTY_EDGE.get(TextureAtlasSprite.class);
    }

    // --------------- Gas Capability ------------

    @Nonnull
    @Override
    public GasTankInfo[] getTankInfo() {
        return network == null ? new GasTankInfo[0] : new GasTankInfo[]{tank};
    }

    @Override
    public int receiveGas(EnumFacing side, GasStack resource, boolean doFill) {
        return network == null || !canReceiveGas(side, resource.getGas()) ? 0 : network.receiveGas(resource, doFill);
    }

    @Nullable
    @Override
    public GasStack drawGas(EnumFacing side, int maxDrain, boolean doDrain) {
        return network == null || !canDrawGas(side, tank.getGasType()) ? null : network.drawGas(maxDrain, doDrain);
    }

    // --------------- End -------------------------

    @Override
    protected boolean canJoinNeighbour(IGasConduit n) {
        return n instanceof AdvancedGasConduit;
    }

    @Override
    public AbstractGasTankConduitNetwork<? extends AbstractGasTankConduit> getTankNetwork() {
        return network;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void hashCodeForModelCaching(BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
        super.hashCodeForModelCaching(hashCodes);
        GasStack gasType = getGasType();
        if (gasType != null && gasType.getGas() != null) {
            hashCodes.add(gasType.getGas());
        }
        hashCodes.addEnum(extractionColors);
        hashCodes.addEnum(extractionModes);
    }

    @Override
    @Nonnull
    public AdvancedGasConduitNetwork createNetworkForType() {
        return new AdvancedGasConduitNetwork();
    }

    @Override
    @Nonnull
    public Collection<CollidableComponent> createCollidables(@Nonnull CacheKey key) {
        Collection<CollidableComponent> baseCollidables = super.createCollidables(key);
        EnumFacing keyDir = key.dir;
        if (keyDir == null) {
            return baseCollidables;
        }

        BoundingBox bb = ConduitGeometryUtil.getInstance().createBoundsForConnectionController(keyDir, key.offset);
        CollidableComponent cc = new CollidableComponent(IGasConduit.class, bb, keyDir, IPowerConduit.COLOR_CONTROLLER_ID);

        List<CollidableComponent> result = new ArrayList<>(baseCollidables);
        result.add(cc);

        return result;
    }
}