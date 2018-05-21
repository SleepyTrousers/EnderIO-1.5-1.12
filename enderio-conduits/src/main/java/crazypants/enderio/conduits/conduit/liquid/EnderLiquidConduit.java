package crazypants.enderio.conduits.conduit.liquid;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitNetwork;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.capability.CapabilityFilterHolder;
import crazypants.enderio.base.filter.capability.IFilterHolder;
import crazypants.enderio.base.filter.fluid.FluidFilter;
import crazypants.enderio.base.filter.fluid.IFluidFilter;
import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduits.capability.CapabilityUpgradeHolder;
import crazypants.enderio.conduits.capability.IUpgradeHolder;
import crazypants.enderio.conduits.conduit.IConduitComponent;
import crazypants.enderio.conduits.conduit.IEnderConduit;
import crazypants.enderio.conduits.conduit.item.ItemConduit;
import crazypants.enderio.conduits.render.BlockStateWrapperConduitBundle;
import crazypants.enderio.util.Prep;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.conduits.init.ConduitObject.item_liquid_conduit;

public class EnderLiquidConduit extends AbstractLiquidConduit implements IConduitComponent, IFilterHolder<IFluidFilter>, IUpgradeHolder, IEnderConduit {

  public static final TextureSupplier ICON_KEY = TextureRegistry.registerTexture("blocks/liquid_conduit_ender");
  public static final TextureSupplier ICON_CORE_KEY = TextureRegistry.registerTexture("blocks/liquid_conduit_core_ender");
  public static final TextureSupplier ICON_IN_OUT_KEY = TextureRegistry.registerTexture("blocks/liquid_conduit_advanced_in_out");
  public static final TextureSupplier ICON_KEY_IN_OUT_BG = TextureRegistry.registerTexture("blocks/item_conduit_io_connector");
  public static final TextureSupplier ICON_KEY_INPUT = TextureRegistry.registerTexture("blocks/item_conduit_input");
  public static final TextureSupplier ICON_KEY_OUTPUT = TextureRegistry.registerTexture("blocks/item_conduit_output");
  public static final TextureSupplier ICON_KEY_IN_OUT_OUT = TextureRegistry.registerTexture("blocks/item_conduit_in_out_out");
  public static final TextureSupplier ICON_KEY_IN_OUT_IN = TextureRegistry.registerTexture("blocks/item_conduit_in_out_in");

  private EnderLiquidConduitNetwork network;
  private int ticksSinceFailedExtract;

  private final EnumMap<EnumFacing, IFluidFilter> outputFilters = new EnumMap<EnumFacing, IFluidFilter>(EnumFacing.class);
  private final EnumMap<EnumFacing, IFluidFilter> inputFilters = new EnumMap<EnumFacing, IFluidFilter>(EnumFacing.class);
  private final EnumMap<EnumFacing, ItemStack> outputFilterUpgrades = new EnumMap<EnumFacing, ItemStack>(EnumFacing.class);
  private final EnumMap<EnumFacing, ItemStack> inputFilterUpgrades = new EnumMap<EnumFacing, ItemStack>(EnumFacing.class);

  private final EnumMap<EnumFacing, DyeColor> inputColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);
  private final EnumMap<EnumFacing, DyeColor> outputColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);

  protected final EnumMap<EnumFacing, Integer> priorities = new EnumMap<EnumFacing, Integer>(EnumFacing.class);

  protected final EnumMap<EnumFacing, Boolean> roundRobin = new EnumMap<EnumFacing, Boolean>(EnumFacing.class);

  protected final EnumMap<EnumFacing, Boolean> selfFeed = new EnumMap<EnumFacing, Boolean>(EnumFacing.class);

  protected final EnumMap<EnumFacing, ItemStack> functionUpgrades = new EnumMap<EnumFacing, ItemStack>(EnumFacing.class);

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

          EnumFacing connDir = component.dir;
          EnumFacing faceHit = res.movingObjectPosition.sideHit;

          if (connDir == null || connDir == faceHit) {

            if (getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
              setConnectionMode(faceHit, getNextConnectionMode(faceHit));
              return true;
            }

            BlockPos pos = getBundle().getLocation().offset(faceHit);
            ILiquidConduit n = ConduitUtil.getConduit(getBundle().getEntity().getWorld(), pos.getX(), pos.getY(), pos.getZ(), ILiquidConduit.class);
            if (n == null) {
              return false;
            }
            if (!(n instanceof EnderLiquidConduit)) {
              return false;
            }
            return ConduitUtil.connectConduits(this, faceHit);
          } else if (containsExternalConnection(connDir)) {
            // Toggle extraction mode
            setConnectionMode(connDir, getNextConnectionMode(connDir));
          } else if (containsConduitConnection(connDir)) {
            ConduitUtil.disconnectConduits(this, connDir);

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
      return inputFilterUpgrades.get(dir);
    } else {
      return outputFilterUpgrades.get(dir);
    }
  }

  public void setFilterStack(@Nonnull EnumFacing dir, @Nonnull ItemStack stack, boolean isInput) {
    if (isInput) {
      inputFilterUpgrades.put(dir, stack);
    } else {
      outputFilterUpgrades.put(dir, stack);
    }
    setFilter(dir, FilterRegistry.<IFluidFilter> getFilterForUpgrade(stack), isInput);
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

    return true;
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
  public TextureAtlasSprite getTextureForState(@Nonnull CollidableComponent component) {
    if (component.dir == null) {
      return ICON_CORE_KEY.get(TextureAtlasSprite.class);
    }
    return ICON_KEY.get(TextureAtlasSprite.class);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getTextureForInputMode() {
    return ICON_KEY_INPUT.get(TextureAtlasSprite.class);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getTextureForInOutMode(boolean input) {
    return input ? ICON_KEY_IN_OUT_IN.get(TextureAtlasSprite.class) : ICON_KEY_IN_OUT_OUT.get(TextureAtlasSprite.class);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getTextureForOutputMode() {
    return ICON_KEY_OUTPUT.get(TextureAtlasSprite.class);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getTextureForInOutMode() {
    return ICON_IN_OUT_KEY.get(TextureAtlasSprite.class);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getTextureForInOutBackground() {
    return ICON_KEY_IN_OUT_BG.get(TextureAtlasSprite.class);
  }

  @Override
  public @Nonnull TextureAtlasSprite getTransmitionTextureForState(@Nonnull CollidableComponent component) {
    return ItemConduit.ICON_KEY_ENDER.get(TextureAtlasSprite.class);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull Vector4f getTransmitionTextureColorForState(@Nonnull CollidableComponent component) {
    return null;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void hashCodeForModelCaching(BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
    super.hashCodeForModelCaching(hashCodes);
    hashCodes.addEnum(outputColors);
    hashCodes.addEnum(inputColors);
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
    if (network == null) {
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
    if (dataRoot.hasKey("outputFilters")) {
      FluidFilter out = new FluidFilter();
      out.readFromNBT(dataRoot.getCompoundTag("outputFilters"));
      outputFilters.put(dir, out);
    }
    if (dataRoot.hasKey("inputFilters")) {
      FluidFilter in = new FluidFilter();
      in.readFromNBT(dataRoot.getCompoundTag("inputFilters"));
      inputFilters.put(dir, in);
    }
  }

  @Override
  protected void writeTypeSettingsToNbt(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    super.writeTypeSettingsToNbt(dir, dataRoot);
    IFluidFilter out = outputFilters.get(dir);
    if (out != null) {
      NBTTagCompound outTag = new NBTTagCompound();
      out.writeToNBT(outTag);
      dataRoot.setTag("outputFilters", outTag);
    }
    IFluidFilter in = inputFilters.get(dir);
    if (in != null) {
      NBTTagCompound inTag = new NBTTagCompound();
      in.writeToNBT(inTag);
      dataRoot.setTag("inputFilters", inTag);
    }
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
      if (entry.getValue() != null) {
        ItemStack up = entry.getValue();
        IFluidFilter filter = getFilter(entry.getKey(), true);
        FilterRegistry.writeFilterToStack(filter, up);

        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("inputFluidFilterUpgrades." + entry.getKey().name(), itemRoot);
      }
    }

    for (Entry<EnumFacing, ItemStack> entry : outputFilterUpgrades.entrySet()) {
      if (entry.getValue() != null) {
        ItemStack up = entry.getValue();
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
      if (entry.getValue() != null) {
        ItemStack up = entry.getValue();
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
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY
        || capability == CapabilityUpgradeHolder.UPGRADE_HOLDER_CAPABILITY && containsExternalConnection(facing)) {
      return true;
    }
    return super.hasCapability(capability, facing);
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
    return functionUpgrades.get(dir);
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

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return (T) new ConnectionEnderLiquidSide(facing);
    } else if (capability == CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY || capability == CapabilityUpgradeHolder.UPGRADE_HOLDER_CAPABILITY) {
      return (T) this;
    }
    return null;
  }

  protected class ConnectionEnderLiquidSide extends ConnectionLiquidSide {
    public ConnectionEnderLiquidSide(EnumFacing side) {
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
}
