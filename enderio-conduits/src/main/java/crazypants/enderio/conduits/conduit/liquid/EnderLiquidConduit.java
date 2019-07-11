package crazypants.enderio.conduits.conduit.liquid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
import crazypants.enderio.base.conduit.item.ItemFunctionUpgrade;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.capability.CapabilityFilterHolder;
import crazypants.enderio.base.filter.capability.IFilterHolder;
import crazypants.enderio.base.filter.fluid.FluidFilter;
import crazypants.enderio.base.filter.fluid.IFluidFilter;
import crazypants.enderio.base.filter.fluid.items.IItemFilterFluidUpgrade;
import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduits.capability.CapabilityUpgradeHolder;
import crazypants.enderio.conduits.capability.IUpgradeHolder;
import crazypants.enderio.conduits.conduit.IEnderConduit;
import crazypants.enderio.conduits.conduit.item.ItemConduit;
import crazypants.enderio.conduits.conduit.power.IPowerConduit;
import crazypants.enderio.conduits.conduit.power.PowerConduit;
import crazypants.enderio.conduits.render.BlockStateWrapperConduitBundle;
import crazypants.enderio.conduits.render.ConduitTexture;
import crazypants.enderio.conduits.render.ConduitTextureWrapper;
import crazypants.enderio.util.EnumReader;
import crazypants.enderio.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.conduits.init.ConduitObject.item_liquid_conduit;

public class EnderLiquidConduit extends AbstractLiquidConduit implements IFilterHolder<IFluidFilter>, IUpgradeHolder, IEnderConduit {

  public static final IConduitTexture ICON_KEY = new ConduitTexture(TextureRegistry.registerTexture("blocks/liquid_conduit"), ConduitTexture.arm(3));
  public static final IConduitTexture ICON_CORE_KEY = new ConduitTexture(TextureRegistry.registerTexture("blocks/conduit_core_1"), ConduitTexture.core(2));

  private EnderLiquidConduitNetwork network;
  private int ticksSinceFailedExtract;

  private final @Nonnull EnumMap<EnumFacing, IFluidFilter> outputFilters = new EnumMap<EnumFacing, IFluidFilter>(EnumFacing.class);
  private final @Nonnull EnumMap<EnumFacing, IFluidFilter> inputFilters = new EnumMap<EnumFacing, IFluidFilter>(EnumFacing.class);
  private final @Nonnull EnumMap<EnumFacing, ItemStack> outputFilterUpgrades = new EnumMap<EnumFacing, ItemStack>(EnumFacing.class);
  private final @Nonnull EnumMap<EnumFacing, ItemStack> inputFilterUpgrades = new EnumMap<EnumFacing, ItemStack>(EnumFacing.class);

  private final @Nonnull EnumMap<EnumFacing, DyeColor> inputColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);
  private final @Nonnull EnumMap<EnumFacing, DyeColor> outputColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);

  protected final @Nonnull EnumMap<EnumFacing, Integer> priorities = new EnumMap<EnumFacing, Integer>(EnumFacing.class);

  protected final @Nonnull EnumMap<EnumFacing, Boolean> roundRobin = new EnumMap<EnumFacing, Boolean>(EnumFacing.class);

  protected final @Nonnull EnumMap<EnumFacing, Boolean> selfFeed = new EnumMap<EnumFacing, Boolean>(EnumFacing.class);

  protected final @Nonnull EnumMap<EnumFacing, ItemStack> functionUpgrades = new EnumMap<EnumFacing, ItemStack>(EnumFacing.class);

  public EnderLiquidConduit() {
    super();
    for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
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
    return new ItemStack(item_liquid_conduit.getItemNN(), 1, 2);
  }

  @Override
  public @Nonnull NNList<ItemStack> getDrops() {
    NNList<ItemStack> res = super.getDrops();
    for (ItemStack stack : functionUpgrades.values()) {
      res.add(stack);
    }
    for (ItemStack stack : inputFilterUpgrades.values()) {
      res.add(stack);
    }
    for (ItemStack stack : outputFilterUpgrades.values()) {
      res.add(stack);
    }
    return res;
  }

  @Override
  public boolean onBlockActivated(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull RaytraceResult res, @Nonnull List<RaytraceResult> all) {
    if (Prep.isInvalid(player.getHeldItem(hand))) {
      return false;
    }

    if (ToolUtil.isToolEquipped(player, hand)) {
      if (!getBundle().getEntity().getWorld().isRemote) {
        final CollidableComponent component = res.component;
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
    return false;
  }

  @Override
  public @Nullable IConduitNetwork<?, ?> getNetwork() {
    return network;
  }

  public IFluidFilter getFilter(@Nonnull EnumFacing dir, boolean isInput) {
    if (isInput) {
      return inputFilters.get(dir);
    }
    return outputFilters.get(dir);
  }

  public void setFilter(@Nonnull EnumFacing dir, @Nonnull IFluidFilter filter, boolean isInput) {
    if (isInput) {
      inputFilters.put(dir, filter);
    } else {
      outputFilters.put(dir, filter);
    }
    setClientStateDirty();
  }

  @Nonnull
  public ItemStack getFilterStack(@Nonnull EnumFacing dir, boolean isInput) {
    if (isInput) {
      return NullHelper.first(inputFilterUpgrades.get(dir), Prep.getEmpty());
    } else {
      return NullHelper.first(outputFilterUpgrades.get(dir), Prep.getEmpty());
    }
  }

  public void setFilterStack(@Nonnull EnumFacing dir, @Nonnull ItemStack stack, boolean isInput) {
    if (isInput) {
      inputFilterUpgrades.put(dir, stack);
    } else {
      outputFilterUpgrades.put(dir, stack);
    }
    final IFluidFilter filterForUpgrade = FilterRegistry.<IFluidFilter> getFilterForUpgrade(stack);
    if (filterForUpgrade != null) {
      setFilter(dir, filterForUpgrade, isInput);
    }
    setClientStateDirty();
  }

  @Override
  public boolean setNetwork(@Nonnull IConduitNetwork<?, ?> network) {
    if (!(network instanceof EnderLiquidConduitNetwork)) {
      return false;
    }
    this.network = (EnderLiquidConduitNetwork) network;
    for (EnumFacing dir : externalConnections) {
      this.network.connectionChanged(this, dir);
    }

    return super.setNetwork(network);
  }

  @Override
  public void clearNetwork() {
    this.network = null;
  }

  // --------------------------------
  // TEXTURES
  // --------------------------------

  @SuppressWarnings("null")
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

  @Override
  public @Nonnull IConduitTexture getTransmitionTextureForState(@Nonnull CollidableComponent component) {
    return ItemConduit.ICON_KEY_ENDER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable Vector4f getTransmitionTextureColorForState(@Nonnull CollidableComponent component) {
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
    if (!super.canConnectToConduit(direction, con)) {
      return false;
    }
    if (!(con instanceof EnderLiquidConduit)) {
      return false;
    }
    return true;
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

    for (EnumFacing dir : externalConnections) {
      if (autoExtractForDir(dir)) {
        if (network.extractFrom(this, dir)) {
          ticksSinceFailedExtract = 0;
        }
      }
    }

  }

  // ---------- Fluid Capability -----------------

  // Fill and Tank properties are both sided, and are handled below
  @Override
  public int fill(FluidStack resource, boolean doFill) {
    return 0;
  }

  @Override
  public IFluidTankProperties[] getTankProperties() {
    return new IFluidTankProperties[0];
  }

  @Nullable
  @Override
  public FluidStack drain(FluidStack resource, boolean doDrain) {
    return null;
  }

  @Nullable
  @Override
  public FluidStack drain(int maxDrain, boolean doDrain) {
    return null;
  }

  // ---------- End ------------------------------

  // Fluid API

  @Override
  public boolean canFill(EnumFacing from, FluidStack fluid) {
    if (network == null || from == null) {
      return false;
    }
    return getConnectionMode(from).acceptsInput();
  }

  @Override
  public boolean canDrain(EnumFacing from, FluidStack fluid) {
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

  private boolean isDefault(IFluidFilter f) {
    if (f instanceof FluidFilter) {
      return ((FluidFilter) f).isDefault();
    }
    return false;
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    for (Entry<EnumFacing, IFluidFilter> entry : inputFilters.entrySet()) {
      if (entry.getValue() != null) {
        IFluidFilter f = entry.getValue();
        if (!isDefault(f)) {
          NBTTagCompound itemRoot = new NBTTagCompound();
          FilterRegistry.writeFilterToNbt(f, itemRoot);
          nbtRoot.setTag("inFluidFilts." + entry.getKey().name(), itemRoot);
        }
      }
    }
    for (Entry<EnumFacing, IFluidFilter> entry : outputFilters.entrySet()) {
      if (entry.getValue() != null) {
        IFluidFilter f = entry.getValue();
        if (!isDefault(f)) {
          NBTTagCompound itemRoot = new NBTTagCompound();
          FilterRegistry.writeFilterToNbt(f, itemRoot);
          nbtRoot.setTag("outFluidFilts." + entry.getKey().name(), itemRoot);
        }
      }
    }
    for (Entry<EnumFacing, ItemStack> entry : inputFilterUpgrades.entrySet()) {
      ItemStack up = entry.getValue();
      if (up != null && Prep.isValid(up)) {
        IFluidFilter filter = getFilter(entry.getKey(), true);
        FilterRegistry.writeFilterToStack(filter, up);

        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("inputFluidFilterUpgrades." + entry.getKey().name(), itemRoot);
      }
    }

    for (Entry<EnumFacing, ItemStack> entry : outputFilterUpgrades.entrySet()) {
      ItemStack up = entry.getValue();
      if (up != null && Prep.isValid(up)) {
        IFluidFilter filter = getFilter(entry.getKey(), false);
        FilterRegistry.writeFilterToStack(filter, up);

        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("outputFluidFilterUpgrades." + entry.getKey().name(), itemRoot);
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
      String key = "inFluidFilts." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound filterTag = (NBTTagCompound) nbtRoot.getTag(key);
        IFluidFilter filter = (IFluidFilter) FilterRegistry.loadFilterFromNbt(filterTag);
        inputFilters.put(dir, filter);
      }

      key = "inputFluidFilterUpgrades." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound upTag = (NBTTagCompound) nbtRoot.getTag(key);
        ItemStack ups = new ItemStack(upTag);
        inputFilterUpgrades.put(dir, ups);
      }

      key = "outputFluidFilterUpgrades." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound upTag = (NBTTagCompound) nbtRoot.getTag(key);
        ItemStack ups = new ItemStack(upTag);
        outputFilterUpgrades.put(dir, ups);
      }

      key = "outFluidFilts." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound filterTag = (NBTTagCompound) nbtRoot.getTag(key);
        IFluidFilter filter = (IFluidFilter) FilterRegistry.loadFilterFromNbt(filterTag);
        outputFilters.put(dir, filter);
      }

      key = "inSC." + dir.name();
      if (nbtRoot.hasKey(key)) {
        short ord = nbtRoot.getShort(key);
        if (ord >= 0 && ord < DyeColor.values().length) {
          inputColors.put(dir, DyeColor.values()[ord]);
        }
      }

      key = "outSC." + dir.name();
      if (nbtRoot.hasKey(key)) {
        short ord = nbtRoot.getShort(key);
        if (ord >= 0 && ord < DyeColor.values().length) {
          outputColors.put(dir, DyeColor.values()[ord]);
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
  public EnderLiquidConduitNetwork createNetworkForType() {
    return new EnderLiquidConduitNetwork();
  }

  @Override
  public boolean hasInternalCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY
        || capability == CapabilityUpgradeHolder.UPGRADE_HOLDER_CAPABILITY && facing != null && containsExternalConnection(facing)) {
      return true;
    }
    return false;
  }

  // FILTERS

  @Override
  @Nonnull
  public ItemStack getFilterStack(int filterIndex, int param1) {
    if (filterIndex == FilterGuiUtil.INDEX_INPUT_FLUID) {
      return getFilterStack(EnumFacing.getFront(param1), true);
    } else if (filterIndex == FilterGuiUtil.INDEX_OUTPUT_FLUID) {
      return getFilterStack(EnumFacing.getFront(param1), false);
    }
    return ItemStack.EMPTY;
  }

  @Override
  public IFluidFilter getFilter(int filterIndex, int param1) {
    if (filterIndex == FilterGuiUtil.INDEX_INPUT_FLUID) {
      return getFilter(EnumFacing.getFront(param1), true);
    } else if (filterIndex == FilterGuiUtil.INDEX_OUTPUT_FLUID) {
      return getFilter(EnumFacing.getFront(param1), false);
    }
    return null;
  }

  @Override
  public void setFilter(int filterIndex, int param1, @Nonnull IFluidFilter filter) {
    if (filterIndex == FilterGuiUtil.INDEX_INPUT_FLUID) {
      setFilter(EnumFacing.getFront(param1), filter, true);
    } else if (filterIndex == FilterGuiUtil.INDEX_OUTPUT_FLUID) {
      setFilter(EnumFacing.getFront(param1), filter, false);
    }
  }

  @Override
  public void setFilterStack(int filterIndex, int param1, @Nonnull ItemStack stack) {
    if (filterIndex == FilterGuiUtil.INDEX_INPUT_FLUID) {
      setFilterStack(EnumFacing.getFront(param1), stack, true);
    } else if (filterIndex == FilterGuiUtil.INDEX_OUTPUT_FLUID) {
      setFilterStack(EnumFacing.getFront(param1), stack, false);
    }
  }

  @Override
  public int getInputFilterIndex() {
    return FilterGuiUtil.INDEX_INPUT_FLUID;
  }

  @Override
  public int getOutputFilterIndex() {
    return FilterGuiUtil.INDEX_OUTPUT_FLUID;
  }

  @Override
  public boolean isFilterUpgradeAccepted(@Nonnull ItemStack stack, boolean isInput) {
    return stack.getItem() instanceof IItemFilterFluidUpgrade;
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

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T getInternalCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY || capability == CapabilityUpgradeHolder.UPGRADE_HOLDER_CAPABILITY) {
      return (T) this;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != null) {
      return (T) new ConnectionEnderLiquidSide(facing);
    }
    return null;
  }

  protected class ConnectionEnderLiquidSide extends ConnectionLiquidSide {
    public ConnectionEnderLiquidSide(@Nonnull EnumFacing side) {
      super(side);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
      if (canFill(side, resource)) {
        return network.fillFrom(EnderLiquidConduit.this, side, resource, doFill);
      }
      return 0;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
      if (network == null) {
        return new FluidTankProperties[0];
      }
      return network.getTankProperties(EnderLiquidConduit.this, side);
    }
  }

  @Override
  @Nonnull
  public Collection<CollidableComponent> createCollidables(@Nonnull CacheKey key) {
    Collection<CollidableComponent> baseCollidables = super.createCollidables(key);
    final EnumFacing keydir = key.dir;
    if (keydir == null) {
      return baseCollidables;
    }

    BoundingBox bb = ConduitGeometryUtil.getInstance().createBoundsForConnectionController(keydir, key.offset);
    CollidableComponent cc = new CollidableComponent(ILiquidConduit.class, bb, keydir, IPowerConduit.COLOR_CONTROLLER_ID);

    List<CollidableComponent> result = new ArrayList<CollidableComponent>();
    result.addAll(baseCollidables);
    result.add(cc);

    return result;
  }

}
