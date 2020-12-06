package gg.galaxygaming.gasconduits.common.conduit.ender;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.vecmath.Vector4f;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitNetwork;
import crazypants.enderio.base.conduit.IConduitTexture;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.base.conduit.item.FunctionUpgrade;
import crazypants.enderio.base.conduit.item.ItemFunctionUpgrade;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.capability.CapabilityFilterHolder;
import crazypants.enderio.base.filter.capability.IFilterHolder;
import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduits.capability.CapabilityUpgradeHolder;
import crazypants.enderio.conduits.capability.IUpgradeHolder;
import crazypants.enderio.conduits.conduit.IEnderConduit;
import crazypants.enderio.conduits.conduit.item.ItemConduit;
import crazypants.enderio.conduits.conduit.power.IPowerConduit;
import crazypants.enderio.conduits.conduit.power.PowerConduit;
import crazypants.enderio.conduits.lang.Lang;
import crazypants.enderio.conduits.render.BlockStateWrapperConduitBundle;
import crazypants.enderio.conduits.render.ConduitTexture;
import crazypants.enderio.conduits.render.ConduitTextureWrapper;
import crazypants.enderio.util.EnumReader;
import crazypants.enderio.util.Prep;
import gg.galaxygaming.gasconduits.GasConduitsConstants;
import gg.galaxygaming.gasconduits.client.utils.GasFilterGuiUtil;
import gg.galaxygaming.gasconduits.common.conduit.AbstractGasConduit;
import gg.galaxygaming.gasconduits.common.conduit.GasConduitObject;
import gg.galaxygaming.gasconduits.common.conduit.IGasConduit;
import gg.galaxygaming.gasconduits.common.config.GasConduitConfig;
import gg.galaxygaming.gasconduits.common.filter.GasFilter;
import gg.galaxygaming.gasconduits.common.filter.IGasFilter;
import gg.galaxygaming.gasconduits.common.filter.IItemFilterGasUpgrade;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnderGasConduit extends AbstractGasConduit implements IFilterHolder<IGasFilter>, IUpgradeHolder, IEnderConduit {

    public static final IConduitTexture ICON_KEY = new ConduitTexture(TextureRegistry.registerTexture("gasconduits:blocks/gas_conduit", false), ConduitTexture.arm(3));
    public static final IConduitTexture ICON_CORE_KEY = new ConduitTexture(TextureRegistry.registerTexture("gasconduits:blocks/gas_conduit_core", false), ConduitTexture.core(2));

    private EnderGasConduitNetwork network;
    private int ticksSinceFailedExtract;

    @Nonnull
    private final EnumMap<EnumFacing, IGasFilter> outputFilters = new EnumMap<>(EnumFacing.class);
    @Nonnull
    private final EnumMap<EnumFacing, IGasFilter> inputFilters = new EnumMap<>(EnumFacing.class);
    @Nonnull
    private final EnumMap<EnumFacing, ItemStack> outputFilterUpgrades = new EnumMap<>(EnumFacing.class);
    @Nonnull
    private final EnumMap<EnumFacing, ItemStack> inputFilterUpgrades = new EnumMap<>(EnumFacing.class);

    @Nonnull
    private final EnumMap<EnumFacing, DyeColor> inputColors = new EnumMap<>(EnumFacing.class);
    @Nonnull
    private final EnumMap<EnumFacing, DyeColor> outputColors = new EnumMap<>(EnumFacing.class);

    @Nonnull
    protected final EnumMap<EnumFacing, Integer> priorities = new EnumMap<>(EnumFacing.class);

    @Nonnull
    protected final EnumMap<EnumFacing, Boolean> roundRobin = new EnumMap<>(EnumFacing.class);

    @Nonnull
    protected final EnumMap<EnumFacing, Boolean> selfFeed = new EnumMap<>(EnumFacing.class);

    @Nonnull
    protected final EnumMap<EnumFacing, ItemStack> functionUpgrades = new EnumMap<>(EnumFacing.class);

    public EnderGasConduit() {
        super();
        for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext(); ) {
            EnumFacing dir = itr.next();
            outputFilterUpgrades.put(dir, ItemStack.EMPTY);
            inputFilterUpgrades.put(dir, ItemStack.EMPTY);
            functionUpgrades.put(dir, ItemStack.EMPTY);
            roundRobin.put(dir, true);
        }
    }

    @Override
    @Nonnull
    public ItemStack createItem() {
        return new ItemStack(GasConduitObject.itemGasConduit.getItemNN(), 1, 2);
    }

    @Override
    @Nonnull
    public NNList<ItemStack> getDrops() {
        NNList<ItemStack> res = super.getDrops();
        res.addAll(functionUpgrades.values());
        res.addAll(inputFilterUpgrades.values());
        res.addAll(outputFilterUpgrades.values());
        return res;
    }

    @Override
    public boolean onBlockActivated(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull RaytraceResult res, @Nonnull List<RaytraceResult> all) {
        if (Prep.isInvalid(player.getHeldItem(hand)) || !ToolUtil.isToolEquipped(player, hand)) {
            return false;
        }

        if (!getBundle().getEntity().getWorld().isRemote) {
            CollidableComponent component = res.component;
            if (component != null) {
                EnumFacing faceHit = res.movingObjectPosition.sideHit;
                if (component.isCore()) {
                    if (getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
                        setConnectionMode(faceHit, getNextConnectionMode(faceHit));
                        return true;
                    }
                    // Attempt to join networks
                    return ConduitUtil.connectConduits(this, faceHit);
                } else {
                    EnumFacing connDir = component.getDirection();
                    if (containsExternalConnection(connDir)) {
                        setConnectionMode(connDir, getNextConnectionMode(connDir));
                    } else if (containsConduitConnection(connDir)) {
                        ConduitUtil.disconnectConduits(this, connDir);
                    }
                }
            }
        }
        return true;
    }

    @Override
    @Nullable
    public IConduitNetwork<?, ?> getNetwork() {
        return network;
    }

    public IGasFilter getFilter(@Nonnull EnumFacing dir, boolean isInput) {
        return isInput ? inputFilters.get(dir) : outputFilters.get(dir);
    }

    public void setFilter(@Nonnull EnumFacing dir, @Nonnull IGasFilter filter, boolean isInput) {
        if (isInput) {
            inputFilters.put(dir, filter);
        } else {
            outputFilters.put(dir, filter);
        }
        setClientStateDirty();
    }

    @Nonnull
    public ItemStack getFilterStack(@Nonnull EnumFacing dir, boolean isInput) {
        return NullHelper.first(isInput ? inputFilterUpgrades.get(dir) : outputFilterUpgrades.get(dir), Prep.getEmpty());
    }

    public void setFilterStack(@Nonnull EnumFacing dir, @Nonnull ItemStack stack, boolean isInput) {
        if (isInput) {
            inputFilterUpgrades.put(dir, stack);
        } else {
            outputFilterUpgrades.put(dir, stack);
        }
        IGasFilter filter = FilterRegistry.getFilterForUpgrade(stack);
        if (filter != null) {
            setFilter(dir, filter, isInput);
        }
        setClientStateDirty();
    }

    @Override
    public boolean setNetwork(@Nonnull IConduitNetwork<?, ?> network) {
        if (network instanceof EnderGasConduitNetwork) {
            this.network = (EnderGasConduitNetwork) network;
            externalConnections.forEach(dir -> this.network.connectionChanged(this, dir));
            return super.setNetwork(network);
        }
        return false;
    }

    @Override
    public void clearNetwork() {
        this.network = null;
    }

    // --------------------------------
    // TEXTURES
    // --------------------------------

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
        return ICON_KEY;
    }

    @Nonnull
    @Override
    public IConduitTexture getTransmitionTextureForState(@Nonnull CollidableComponent component) {
        return ItemConduit.ICON_KEY_ENDER;
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public Vector4f getTransmitionTextureColorForState(@Nonnull CollidableComponent component) {
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void hashCodeForModelCaching(BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
        super.hashCodeForModelCaching(hashCodes);
        hashCodes.addEnum(outputColors);
        hashCodes.addEnum(inputColors);
        hashCodes.addEnum(extractionColors);
        hashCodes.addEnum(extractionModes);
    }

    @Override
    public boolean canConnectToConduit(@Nonnull EnumFacing direction, @Nonnull IConduit con) {
        return super.canConnectToConduit(direction, con) && con instanceof EnderGasConduit;
    }

    @Override
    public void setConnectionMode(@Nonnull EnumFacing dir, @Nonnull ConnectionMode mode) {
        super.setConnectionMode(dir, mode);
        refreshConnection(dir);
    }

    @Override
    public void setExtractionRedstoneMode(@Nonnull RedstoneControlMode mode, @Nonnull EnumFacing dir) {
        super.setExtractionRedstoneMode(mode, dir);
        refreshConnection(dir);
    }

    @Override
    public void externalConnectionAdded(@Nonnull EnumFacing fromDirection) {
        super.externalConnectionAdded(fromDirection);
        refreshConnection(fromDirection);
    }

    @Override
    public void externalConnectionRemoved(@Nonnull EnumFacing fromDirection) {
        super.externalConnectionRemoved(fromDirection);
        refreshConnection(fromDirection);
    }

    @Override
    public void updateEntity(@Nonnull World world) {
        super.updateEntity(world);
        if (world.isRemote) {
            return;
        }
        doExtract();
    }

    private void doExtract() {
        if (!hasExtractableMode() || network == null) {
            return;
        }

        // assume failure, reset to 0 if we do extract
        ticksSinceFailedExtract++;
        if (ticksSinceFailedExtract > 25 && ticksSinceFailedExtract % 10 != 0) {
            // after 25 ticks of failing, only check every 10 ticks
            return;
        }

        for (EnumFacing dir : externalConnections) {
            if (autoExtractForDir(dir) && network.extractFrom(this, dir)) {
                ticksSinceFailedExtract = 0;
            }
        }
    }

    // ---------- Gas Capability -----------------

    // Fill and Tank properties are both sided, and are handled below
    @Override
    public int receiveGas(EnumFacing side, GasStack resource, boolean doFill) {
        return 0;
    }

    @Override
    @Nonnull
    public GasTankInfo[] getTankInfo() {
        return new GasTankInfo[0];
    }

    @Nullable
    @Override
    public GasStack drawGas(EnumFacing side, int maxDrain, boolean doDrain) {
        return null;
    }

    // ---------- End ------------------------------

    // Gas API

    @Override
    public boolean canReceiveGas(EnumFacing from, Gas gas) {
        return network != null && getConnectionMode(from).acceptsInput();
    }

    @Override
    public boolean canDrawGas(EnumFacing from, Gas gas) {
        return false;
    }

    @Override
    protected void readTypeSettings(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
        super.readTypeSettings(dir, dataRoot);
        setConnectionMode(dir, EnumReader.get(ConnectionMode.class, dataRoot.getShort("connectionMode")));
        setExtractionSignalColor(dir, EnumReader.get(DyeColor.class, dataRoot.getShort("extractionSignalColor")));
        setExtractionRedstoneMode(EnumReader.get(RedstoneControlMode.class, dataRoot.getShort("extractionRedstoneMode")), dir);
        setInputColor(dir, EnumReader.get(DyeColor.class, dataRoot.getShort("inputColor")));
        setOutputColor(dir, EnumReader.get(DyeColor.class, dataRoot.getShort("outputColor")));
        setSelfFeedEnabled(dir, dataRoot.getBoolean("selfFeed"));
        setRoundRobinEnabled(dir, dataRoot.getBoolean("roundRobin"));
        setOutputPriority(dir, dataRoot.getInteger("outputPriority"));
    }

    @Override
    protected void writeTypeSettingsToNbt(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
        super.writeTypeSettingsToNbt(dir, dataRoot);
        dataRoot.setShort("connectionMode", (short) getConnectionMode(dir).ordinal());
        dataRoot.setShort("extractionSignalColor", (short) getExtractionSignalColor(dir).ordinal());
        dataRoot.setShort("extractionRedstoneMode", (short) getExtractionRedstoneMode(dir).ordinal());
        dataRoot.setShort("inputColor", (short) getInputColor(dir).ordinal());
        dataRoot.setShort("outputColor", (short) getOutputColor(dir).ordinal());
        dataRoot.setBoolean("selfFeed", isSelfFeedEnabled(dir));
        dataRoot.setBoolean("roundRobin", isRoundRobinEnabled(dir));
        dataRoot.setInteger("outputPriority", getOutputPriority(dir));
    }

    private boolean isDefault(IGasFilter f) {
        return f instanceof GasFilter && f.isDefault();
    }

    @Override
    public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
        super.writeToNBT(nbtRoot);
        for (Entry<EnumFacing, IGasFilter> entry : inputFilters.entrySet()) {
            if (entry.getValue() != null) {
                IGasFilter g = entry.getValue();
                if (!isDefault(g)) {
                    NBTTagCompound itemRoot = new NBTTagCompound();
                    FilterRegistry.writeFilterToNbt(g, itemRoot);
                    nbtRoot.setTag("inGasFilts." + entry.getKey().name(), itemRoot);
                }
            }
        }
        for (Entry<EnumFacing, IGasFilter> entry : outputFilters.entrySet()) {
            if (entry.getValue() != null) {
                IGasFilter g = entry.getValue();
                if (!isDefault(g)) {
                    NBTTagCompound itemRoot = new NBTTagCompound();
                    FilterRegistry.writeFilterToNbt(g, itemRoot);
                    nbtRoot.setTag("outGasFilts." + entry.getKey().name(), itemRoot);
                }
            }
        }
        for (Entry<EnumFacing, ItemStack> entry : inputFilterUpgrades.entrySet()) {
            ItemStack up = entry.getValue();
            if (up != null && Prep.isValid(up)) {
                IGasFilter filter = getFilter(entry.getKey(), true);
                FilterRegistry.writeFilterToStack(filter, up);

                NBTTagCompound itemRoot = new NBTTagCompound();
                up.writeToNBT(itemRoot);
                nbtRoot.setTag("inputGasFilterUpgrades." + entry.getKey().name(), itemRoot);
            }
        }

        for (Entry<EnumFacing, ItemStack> entry : outputFilterUpgrades.entrySet()) {
            ItemStack up = entry.getValue();
            if (up != null && Prep.isValid(up)) {
                IGasFilter filter = getFilter(entry.getKey(), false);
                FilterRegistry.writeFilterToStack(filter, up);

                NBTTagCompound itemRoot = new NBTTagCompound();
                up.writeToNBT(itemRoot);
                nbtRoot.setTag("outputGasFilterUpgrades." + entry.getKey().name(), itemRoot);
            }
        }

        for (Entry<EnumFacing, DyeColor> entry : inputColors.entrySet()) {
            if (entry.getValue() != null) {
                short ord = (short) entry.getValue().ordinal();
                nbtRoot.setShort("inSC." + entry.getKey().name(), ord);
            }
        }

        for (Entry<EnumFacing, DyeColor> entry : outputColors.entrySet()) {
            if (entry.getValue() != null) {
                short ord = (short) entry.getValue().ordinal();
                nbtRoot.setShort("outSC." + entry.getKey().name(), ord);
            }
        }

        for (Entry<EnumFacing, Integer> entry : priorities.entrySet()) {
            if (entry.getValue() != null) {
                nbtRoot.setInteger("priority." + entry.getKey().name(), entry.getValue());
            }
        }

        for (Entry<EnumFacing, Boolean> entry : roundRobin.entrySet()) {
            if (entry.getValue() != null) {
                nbtRoot.setBoolean("roundRobin." + entry.getKey().name(), entry.getValue());
            }
        }

        for (Entry<EnumFacing, Boolean> entry : selfFeed.entrySet()) {
            if (entry.getValue() != null) {
                nbtRoot.setBoolean("selfFeed." + entry.getKey().name(), entry.getValue());
            }
        }

        for (Entry<EnumFacing, ItemStack> entry : functionUpgrades.entrySet()) {
            ItemStack up = entry.getValue();
            if (up != null && Prep.isValid(up)) {
                NBTTagCompound itemRoot = new NBTTagCompound();
                up.writeToNBT(itemRoot);
                nbtRoot.setTag("functionUpgrades." + entry.getKey().name(), itemRoot);
            }
        }
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
        super.readFromNBT(nbtRoot);
        for (EnumFacing dir : EnumFacing.VALUES) {
            String key = "inGasFilts." + dir.name();
            if (nbtRoot.hasKey(key)) {
                NBTTagCompound filterTag = (NBTTagCompound) nbtRoot.getTag(key);
                IGasFilter filter = (IGasFilter) FilterRegistry.loadFilterFromNbt(filterTag);
                inputFilters.put(dir, filter);
            }

            key = "inputGasFilterUpgrades." + dir.name();
            if (nbtRoot.hasKey(key)) {
                NBTTagCompound upTag = (NBTTagCompound) nbtRoot.getTag(key);
                ItemStack ups = new ItemStack(upTag);
                inputFilterUpgrades.put(dir, ups);
            }

            key = "outputGasFilterUpgrades." + dir.name();
            if (nbtRoot.hasKey(key)) {
                NBTTagCompound upTag = (NBTTagCompound) nbtRoot.getTag(key);
                ItemStack ups = new ItemStack(upTag);
                outputFilterUpgrades.put(dir, ups);
            }

            key = "outGasFilts." + dir.name();
            if (nbtRoot.hasKey(key)) {
                NBTTagCompound filterTag = (NBTTagCompound) nbtRoot.getTag(key);
                IGasFilter filter = (IGasFilter) FilterRegistry.loadFilterFromNbt(filterTag);
                outputFilters.put(dir, filter);
            }

            key = "inSC." + dir.name();
            if (nbtRoot.hasKey(key)) {
                short ord = nbtRoot.getShort(key);
                if (ord >= 0 && ord < DyeColor.values().length) {
                    inputColors.put(dir, EnumReader.get(DyeColor.class, ord));
                }
            }

            key = "outSC." + dir.name();
            if (nbtRoot.hasKey(key)) {
                short ord = nbtRoot.getShort(key);
                if (ord >= 0 && ord < DyeColor.values().length) {
                    outputColors.put(dir, EnumReader.get(DyeColor.class, ord));
                }
            }

            key = "priority." + dir.name();
            if (nbtRoot.hasKey(key)) {
                int val = nbtRoot.getInteger(key);
                priorities.put(dir, val);
            }

            key = "roundRobin." + dir.name();
            if (nbtRoot.hasKey(key)) {
                boolean val = nbtRoot.getBoolean(key);
                roundRobin.put(dir, val);
            } else {
                roundRobin.remove(dir);
            }

            key = "selfFeed." + dir.name();
            if (nbtRoot.hasKey(key)) {
                boolean val = nbtRoot.getBoolean(key);
                selfFeed.put(dir, val);
            }

            key = "functionUpgrades." + dir.name();
            if (nbtRoot.hasKey(key)) {
                NBTTagCompound upTag = (NBTTagCompound) nbtRoot.getTag(key);
                ItemStack ups = new ItemStack(upTag);
                functionUpgrades.put(dir, ups);
            }
        }
        connectionsDirty = true;
    }

    @Override
    @Nonnull
    public EnderGasConduitNetwork createNetworkForType() {
        return new EnderGasConduitNetwork();
    }

    @Override
    public boolean hasInternalCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY || capability == CapabilityUpgradeHolder.UPGRADE_HOLDER_CAPABILITY && facing != null
                                                                                && containsExternalConnection(facing);
    }

    // FILTERS

    @Override
    @Nonnull
    public ItemStack getFilterStack(int filterIndex, EnumFacing side) {
        if (filterIndex == getInputFilterIndex()) {
            return getFilterStack(side, true);
        } else if (filterIndex == getOutputFilterIndex()) {
            return getFilterStack(side, false);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public IGasFilter getFilter(int filterIndex, int param1) {
        if (filterIndex == getInputFilterIndex()) {
            return getFilter(EnumFacing.getFront(param1), true);
        } else if (filterIndex == getOutputFilterIndex()) {
            return getFilter(EnumFacing.getFront(param1), false);
        }
        return null;
    }

    @Override
    public void setFilter(int filterIndex, EnumFacing side, @Nonnull IGasFilter filter) {
        if (filterIndex == getInputFilterIndex()) {
            setFilter(side, filter, true);
        } else if (filterIndex == getOutputFilterIndex()) {
            setFilter(side, filter, false);
        }
    }

    @Override
    public void setFilterStack(int filterIndex, EnumFacing side, @Nonnull ItemStack stack) {
        if (filterIndex == getInputFilterIndex()) {
            setFilterStack(side, stack, true);
        } else if (filterIndex == getOutputFilterIndex()) {
            setFilterStack(side, stack, false);
        }
    }

    @Override
    public int getInputFilterIndex() {
        return GasFilterGuiUtil.INDEX_INPUT_GAS;
    }

    @Override
    public int getOutputFilterIndex() {
        return GasFilterGuiUtil.INDEX_OUTPUT_GAS;
    }

    @Override
    public boolean isFilterUpgradeAccepted(@Nonnull ItemStack stack, boolean isInput) {
        return stack.getItem() instanceof IItemFilterGasUpgrade;
    }

    // ------------------------------------------------
    // ENDER CONDUIT START
    // ------------------------------------------------

    @Override
    @Nonnull
    public Map<EnumFacing, DyeColor> getInputColors() {
        return inputColors;
    }

    @Override
    @Nonnull
    public Map<EnumFacing, DyeColor> getOutputColors() {
        return outputColors;
    }

    @Override
    @Nonnull
    public Map<EnumFacing, Boolean> getSelfFeed() {
        return selfFeed;
    }

    @Override
    @Nonnull
    public Map<EnumFacing, Boolean> getRoundRobin() {
        return roundRobin;
    }

    @Override
    @Nonnull
    public Map<EnumFacing, Integer> getOutputPriorities() {
        return priorities;
    }

    @Override
    public void setClientDirty() {
        setClientStateDirty();
        collidablesDirty = true;
    }

    @Override
    public void refreshConnection(@Nonnull EnumFacing dir) {
        if (network == null) {
            return;
        }
        network.connectionChanged(this, dir);
    }

    // -------------------------------
    // END
    // -------------------------------

    @Nonnull
    public ItemStack getFunctionUpgrade(@Nonnull EnumFacing dir) {
        return NullHelper.first(functionUpgrades.get(dir), Prep.getEmpty());
    }

    public void setFunctionUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack upgrade) {
        functionUpgrades.put(dir, upgrade);
        setClientStateDirty();
    }

    @Override
    @Nonnull
    public ItemStack getUpgradeStack(int param1) {
        return this.getFunctionUpgrade(EnumFacing.getFront(param1));
    }

    @Override
    public void setUpgradeStack(int param1, @Nonnull ItemStack stack) {
        this.setFunctionUpgrade(EnumFacing.getFront(param1), stack);
    }

    @Override
    public int getUpgradeSlotLimit(@Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemFunctionUpgrade ? ((ItemFunctionUpgrade) stack.getItem()).getUpgradeSlotLimit()
                                                              : IUpgradeHolder.super.getUpgradeSlotLimit(stack);
    }

    @Override
    @Nonnull
    public List<String> getFunctionUpgradeToolTipText(@Nonnull EnumFacing dir) {
        //TODO: At some point it may make sense to make this use custom lang entries
        return new NNList<>(Lang.GUI_LIQUID_FUNCTION_UPGRADE_DETAILS.get(), Lang.GUI_LIQUID_FUNCTION_UPGRADE_DETAILS2.get((int) (100 * getExtractSpeedMultiplier(dir)),
                    LangFluid.MB((int) (GasConduitConfig.tier3_extractRate.get() * getExtractSpeedMultiplier(dir)))));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getInternalCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY || capability == CapabilityUpgradeHolder.UPGRADE_HOLDER_CAPABILITY ? (T) this : null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return hasCapability(capability, facing) ? (T) new ConnectionEnderGasSide(facing) : null;
    }

    protected class ConnectionEnderGasSide extends ConnectionGasSide {

        public ConnectionEnderGasSide(EnumFacing side) {
            super(side);
        }

        @Override
        public int receiveGas(EnumFacing facing, GasStack resource, boolean doFill) {
            return canReceiveGas(facing, resource.getGas()) ? network == null ? 0 : network.fillFrom(EnderGasConduit.this, facing, resource, doFill) : 0;
        }

        @Override
        @Nonnull
        public GasTankInfo[] getTankInfo() {
            return network == null ? new GasTankInfo[0] : network.getTankProperties(EnderGasConduit.this, side);
        }
    }

    @Override
    @Nonnull
    public Collection<CollidableComponent> createCollidables(@Nonnull CacheKey key) {
        Collection<CollidableComponent> baseCollidables = super.createCollidables(key);
        EnumFacing keydir = key.dir;
        if (keydir == null) {
            return baseCollidables;
        }

        BoundingBox bb = ConduitGeometryUtil.getInstance().createBoundsForConnectionController(keydir, key.offset);
        CollidableComponent cc = new CollidableComponent(IGasConduit.class, bb, keydir, IPowerConduit.COLOR_CONTROLLER_ID);

        List<CollidableComponent> result = new ArrayList<>(baseCollidables);
        result.add(cc);

        return result;
    }

    public float getExtractSpeedMultiplier(@Nonnull EnumFacing dir) {
        int extractSpeedMultiplier = 2;
        ItemStack upgradeStack = getFunctionUpgrade(dir);
        if (!upgradeStack.isEmpty()) {
            FunctionUpgrade upgrade = ItemFunctionUpgrade.getFunctionUpgrade(upgradeStack);
            if (upgrade == FunctionUpgrade.EXTRACT_SPEED_UPGRADE) {
                extractSpeedMultiplier += GasConduitsConstants.GAS_MAX_EXTRACTED_SCALER * Math.min(upgrade.getMaxStackSize(), upgradeStack.getCount());
            } else if (upgrade == FunctionUpgrade.EXTRACT_SPEED_DOWNGRADE) {
                extractSpeedMultiplier = 1;
            }
        }
        return extractSpeedMultiplier / 2F;
    }
}