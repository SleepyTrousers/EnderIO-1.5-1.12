package crazypants.enderio.conduit.item;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.capability.ItemTools;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitNetwork;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.INetworkedInventory;
import crazypants.enderio.base.filter.filters.ItemFilter;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.IConduitComponent;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.gui.item.ItemSettings;
import crazypants.enderio.conduit.render.BlockStateWrapperConduitBundle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import static crazypants.enderio.base.init.ModObject.itemItemFilter;
import static crazypants.enderio.conduit.init.ConduitObject.item_item_conduit;

public class ItemConduit extends AbstractConduit implements IItemConduit, IConduitComponent {

  public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

  public static final String EXTERNAL_INTERFACE_GEOM = "ExternalInterface";

  public static final String ICON_KEY = "enderio:blocks/item_conduit";

  public static final String ICON_KEY_CORE = "enderio:blocks/item_conduit_core";

  public static final String ICON_KEY_INPUT = "enderio:blocks/item_conduit_input";

  public static final String ICON_KEY_OUTPUT = "enderio:blocks/item_conduit_output";

  public static final String ICON_KEY_IN_OUT_OUT = "enderio:blocks/item_conduit_in_out_out";

  public static final String ICON_KEY_IN_OUT_IN = "enderio:blocks/item_conduit_in_out_in";

  public static final String ICON_KEY_IN_OUT_BG = "enderio:blocks/item_conduit_io_connector";

  public static final String ICON_KEY_ENDER = "enderio:blocks/ender_still";

  static final Map<String, TextureAtlasSprite> ICONS = new HashMap<String, TextureAtlasSprite>();

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(@Nonnull TextureMap register) {
        ICONS.put(ICON_KEY, register.registerSprite(new ResourceLocation(ICON_KEY)));
        ICONS.put(ICON_KEY_CORE, register.registerSprite(new ResourceLocation(ICON_KEY_CORE)));
        ICONS.put(ICON_KEY_INPUT, register.registerSprite(new ResourceLocation(ICON_KEY_INPUT)));
        ICONS.put(ICON_KEY_OUTPUT, register.registerSprite(new ResourceLocation(ICON_KEY_OUTPUT)));
        ICONS.put(ICON_KEY_IN_OUT_OUT, register.registerSprite(new ResourceLocation(ICON_KEY_IN_OUT_OUT)));
        ICONS.put(ICON_KEY_IN_OUT_IN, register.registerSprite(new ResourceLocation(ICON_KEY_IN_OUT_IN)));
        ICONS.put(ICON_KEY_IN_OUT_BG, register.registerSprite(new ResourceLocation(ICON_KEY_IN_OUT_BG)));
        ICONS.put(ICON_KEY_ENDER, register.registerSprite(new ResourceLocation(ICON_KEY_ENDER)));
      }

    });
  }

  ItemConduitNetwork network;

  protected final EnumMap<EnumFacing, RedstoneControlMode> extractionModes = new EnumMap<EnumFacing, RedstoneControlMode>(EnumFacing.class);
  protected final EnumMap<EnumFacing, DyeColor> extractionColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);

  protected final EnumMap<EnumFacing, IItemFilter> outputFilters = new EnumMap<EnumFacing, IItemFilter>(EnumFacing.class);
  protected final EnumMap<EnumFacing, IItemFilter> inputFilters = new EnumMap<EnumFacing, IItemFilter>(EnumFacing.class);
  protected final EnumMap<EnumFacing, ItemStack> outputFilterUpgrades = new EnumMap<EnumFacing, ItemStack>(EnumFacing.class);
  protected final EnumMap<EnumFacing, ItemStack> inputFilterUpgrades = new EnumMap<EnumFacing, ItemStack>(EnumFacing.class);
  protected final EnumMap<EnumFacing, ItemStack> speedUpgrades = new EnumMap<EnumFacing, ItemStack>(EnumFacing.class);
  protected final EnumMap<EnumFacing, ItemStack> functionUpgrades = new EnumMap<EnumFacing, ItemStack>(EnumFacing.class);

  protected final EnumMap<EnumFacing, Boolean> selfFeed = new EnumMap<EnumFacing, Boolean>(EnumFacing.class);

  protected final EnumMap<EnumFacing, Boolean> roundRobin = new EnumMap<EnumFacing, Boolean>(EnumFacing.class);

  protected final EnumMap<EnumFacing, Integer> priority = new EnumMap<EnumFacing, Integer>(EnumFacing.class);

  protected final EnumMap<EnumFacing, DyeColor> outputColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);
  protected final EnumMap<EnumFacing, DyeColor> inputColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);

  private int metaData;

  public ItemConduit() {
    this(0);
  }

  public ItemConduit(int itemDamage) {
    metaData = itemDamage;
    for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
      EnumFacing dir = itr.next();
      outputFilterUpgrades.put(dir, ItemStack.EMPTY);
      inputFilterUpgrades.put(dir, ItemStack.EMPTY);
      speedUpgrades.put(dir, ItemStack.EMPTY);
      functionUpgrades.put(dir, ItemStack.EMPTY);
    }
  }

  @Override
  protected void readTypeSettings(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    setExtractionSignalColor(dir, DyeColor.values()[dataRoot.getShort("extractionSignalColor")]);
    setExtractionRedstoneMode(RedstoneControlMode.values()[dataRoot.getShort("extractionRedstoneMode")], dir);
    setInputColor(dir, DyeColor.values()[dataRoot.getShort("inputColor")]);
    setOutputColor(dir, DyeColor.values()[dataRoot.getShort("outputColor")]);
    setSelfFeedEnabled(dir, dataRoot.getBoolean("selfFeed"));
    setRoundRobinEnabled(dir, dataRoot.getBoolean("roundRobin"));
    setOutputPriority(dir, dataRoot.getInteger("outputPriority"));
  }

  @Override
  protected void writeTypeSettingsToNbt(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    dataRoot.setShort("extractionSignalColor", (short) getExtractionSignalColor(dir).ordinal());
    dataRoot.setShort("extractionRedstoneMode", (short) getExtractionRedstoneMode(dir).ordinal());
    dataRoot.setShort("inputColor", (short) getInputColor(dir).ordinal());
    dataRoot.setShort("outputColor", (short) getOutputColor(dir).ordinal());
    dataRoot.setBoolean("selfFeed", isSelfFeedEnabled(dir));
    dataRoot.setBoolean("roundRobin", isRoundRobinEnabled(dir));
    dataRoot.setInteger("outputPriority", getOutputPriority(dir));
  }

  protected void convertToItemUpgrades(int filterMeta, Map<EnumFacing, ItemStack> converted, EnumMap<EnumFacing, IItemFilter> sourceFilters) {
    for (Entry<EnumFacing, IItemFilter> entry : sourceFilters.entrySet()) {
      if (entry.getValue() != null) {
        IItemFilter f = entry.getValue();
        ItemStack up = new ItemStack(itemItemFilter.getItem(), 1, filterMeta);
        FilterRegistry.writeFilterToStack(f, up);
        converted.put(entry.getKey(), up);
      }
    }
  }

  @Override
  public @Nonnull NNList<ItemStack> getDrops() {
    NNList<ItemStack> res = new NNList<>();
    res.add(createItem());
    for (ItemStack stack : speedUpgrades.values()) {
      res.add(stack);
    }
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
    if (ConduitUtil.isProbeEquipped(player, hand)) {
      if (!player.world.isRemote) {
        // TODO PacketConduitProbe.sendInfoMessage(player, this, null);
      }
      return true;
    } else if (ToolUtil.isToolEquipped(player, hand)) {
      if (!getBundle().getEntity().getWorld().isRemote) {
        if (res != null && res.component != null) {
          EnumFacing connDir = res.component.dir;
          EnumFacing faceHit = res.movingObjectPosition.sideHit;
          if (connDir == null || connDir == faceHit) {
            if (getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
              setConnectionMode(faceHit, getNextConnectionMode(faceHit));
              return true;
            }
            // Attempt to join networks
            return ConduitUtil.connectConduits(this, faceHit);
          } else if (externalConnections.contains(connDir)) {
            setConnectionMode(connDir, getNextConnectionMode(connDir));
            return true;
          } else if (containsConduitConnection(connDir)) {
            ConduitUtil.disconnectConduits(this, connDir);
            return true;
          }
        }
      }
    } else {

      if (res != null && res.component != null) {
        EnumFacing connDir = res.component.dir;
        if (connDir != null && containsExternalConnection(connDir)) {
          if (!player.world.isRemote) {
            // TODO PacketConduitProbe.sendInfoMessage(player, this, player.getHeldItem(hand));
          }
          return true;
        }
      }

    }
    return false;
  }

  @Override
  public void setInputFilter(@Nonnull EnumFacing dir, @Nonnull IItemFilter filter) {
    inputFilters.put(dir, filter);
    if (network != null) {
      network.routesChanged();
    }
    setClientStateDirty();
  }

  @Override
  public void setOutputFilter(@Nonnull EnumFacing dir, @Nonnull IItemFilter filter) {
    outputFilters.put(dir, filter);
    if (network != null) {
      network.routesChanged();
    }
    setClientStateDirty();
  }

  @Override
  public IItemFilter getInputFilter(@Nonnull EnumFacing dir) {
    return inputFilters.get(dir);
  }

  @Override
  public IItemFilter getOutputFilter(@Nonnull EnumFacing dir) {
    return outputFilters.get(dir);
  }

  @Override
  public void setInputFilterUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack stack) {
    inputFilterUpgrades.put(dir, stack);
    setInputFilter(dir, FilterRegistry.getFilterForUpgrade(stack));
    setClientStateDirty();
  }

  @Override
  public void setOutputFilterUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack stack) {
    outputFilterUpgrades.put(dir, stack);
    setOutputFilter(dir, FilterRegistry.getFilterForUpgrade(stack));
    setClientStateDirty();
  }

  @Override
  @Nonnull
  public ItemStack getInputFilterUpgrade(@Nonnull EnumFacing dir) {
    return inputFilterUpgrades.get(dir);
  }

  @Override
  @Nonnull
  public ItemStack getOutputFilterUpgrade(@Nonnull EnumFacing dir) {
    return outputFilterUpgrades.get(dir);
  }

  @Override
  public void setSpeedUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack upgrade) {
    speedUpgrades.put(dir, upgrade);
    setClientStateDirty();
  }

  @Override
  @Nonnull
  public ItemStack getSpeedUpgrade(@Nonnull EnumFacing dir) {
    return speedUpgrades.get(dir);
  }
  //
  // @Override
  // public void setFunctionUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack upgrade) {
  // boolean hadIPU = hasInventoryPanelUpgrade(dir);
  // if(!upgrade.isEmpty()) {
  // functionUpgrades.put(dir, upgrade);
  // } else {
  // functionUpgrades.remove(dir);
  // }
  // setClientStateDirty();
  // if(network != null && hadIPU != hasInventoryPanelUpgrade(dir)) {
  // network.inventoryPanelSourcesChanged();
  // }
  // }
  //
  // @Override
  // @Nonnull
  // public ItemStack getFunctionUpgrade(@Nonnull EnumFacing dir) {
  // return functionUpgrades.get(dir);
  // }

  // @Override
  // public boolean hasInventoryPanelUpgrade(@Nonnull EnumFacing dir) {
  // ItemStack upgrade = functionUpgrades.get(dir);
  // return (!upgrade.isEmpty() && ItemFunctionUpgrade.getFunctionUpgrade(upgrade) == FunctionUpgrade.INVENTORY_PANEL) || isConnectedToNetworkAwareBlock(dir);
  // }
  //
  // @Override
  // public boolean isConnectedToNetworkAwareBlock(@Nonnull EnumFacing dir) {
  // if (!externalConnections.contains(dir)) {
  // return false;
  // }
  // World world = getBundle().getBundleworld();
  // if (world == null) {
  // return false;
  // }
  // BlockPos loc = getBundle().getLocation().offset(dir);
  // if (!world.isBlockLoaded(loc)) {
  // return false;
  // }
  // TileEntity tileEntity = world.getTileEntity(loc);
  // return tileEntity instanceof TileInventoryChest;
  // }

  @Override
  public int getMetaData() {
    return metaData;
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
  public boolean isExtractionRedstoneConditionMet(@Nonnull EnumFacing dir) {
    RedstoneControlMode mode = getExtractionRedstoneMode(dir);
    return ConduitUtil.isRedstoneControlModeMet(this, mode, getExtractionSignalColor(dir));
  }

  @Override
  @Nonnull
  public DyeColor getInputColor(@Nonnull EnumFacing dir) {
    DyeColor result = inputColors.get(dir);
    if (result == null) {
      return DyeColor.GREEN;
    }
    return result;
  }

  @Override
  @Nonnull
  public DyeColor getOutputColor(@Nonnull EnumFacing dir) {
    DyeColor result = outputColors.get(dir);
    if (result == null) {
      return DyeColor.GREEN;
    }
    return result;
  }

  @Override
  public void setInputColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col) {
    inputColors.put(dir, col);
    if (network != null) {
      network.routesChanged();
    }
    setClientStateDirty();
    collidablesDirty = true;
  }

  @Override
  public void setOutputColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col) {
    outputColors.put(dir, col);
    if (network != null) {
      network.routesChanged();
    }
    setClientStateDirty();
    collidablesDirty = true;
  }

  @Override
  public int getMaximumExtracted(@Nonnull EnumFacing dir) {
    ItemStack stack = speedUpgrades.get(dir);
    if (stack.isEmpty()) {
      return SpeedUpgrade.BASE_MAX_EXTRACTED;
    }
    SpeedUpgrade speedUpgrade = ItemExtractSpeedUpgrade.getSpeedUpgrade(stack);
    return speedUpgrade.getMaximumExtracted(stack.getCount());
  }

  @Override
  public float getTickTimePerItem(@Nonnull EnumFacing dir) {
    float maxExtract = 10f / getMaximumExtracted(dir);
    return maxExtract;
  }

  @Override
  public void itemsExtracted(int numExtracted, int slot) {
  }

  @Override
  public void externalConnectionAdded(@Nonnull EnumFacing direction) {
    super.externalConnectionAdded(direction);
    checkInventoryConnections(direction);
  }

  @Override
  public IItemHandler getExternalInventory(@Nonnull EnumFacing direction) {
    World world = getBundle().getBundleworld();
    if (world == null) {
      return null;
    }
    BlockPos loc = getBundle().getLocation().offset(direction);
    return ItemTools.getExternalInventory(world, loc, direction.getOpposite());
  }

  @Override
  public void externalConnectionRemoved(@Nonnull EnumFacing direction) {
    externalConnections.remove(direction);
    connectionsChanged();
    checkInventoryConnections(direction);
  }

  private void checkInventoryConnections(@Nonnull EnumFacing direction) {
    if (network != null) {
      BlockPos p = bundle.getEntity().getPos().offset(direction);
      INetworkedInventory networkedInventory = network.getInventory(this, direction);
      if (externalConnections.contains(direction) && getConnectionMode(direction) != ConnectionMode.DISABLED) {
        if (networkedInventory == null) {
          network.inventoryAdded(this, direction, p, getExternalInventory(direction));
        }
      } else {
        if (networkedInventory != null) {
          network.inventoryRemoved(this, p);
        }
      }
    }
  }

  @Override
  public void setConnectionMode(@Nonnull EnumFacing dir, @Nonnull ConnectionMode mode) {
    ConnectionMode oldVal = conectionModes.get(dir);
    if (oldVal == mode) {
      return;
    }
    super.setConnectionMode(dir, mode);
    checkInventoryConnections(dir);
    if (network != null) {
      network.routesChanged();
    }
  }

  @Override
  public boolean isSelfFeedEnabled(@Nonnull EnumFacing dir) {
    Boolean val = selfFeed.get(dir);
    if (val == null) {
      return false;
    }
    return val;
  }

  @Override
  public void setSelfFeedEnabled(@Nonnull EnumFacing dir, boolean enabled) {
    if (!enabled) {
      selfFeed.remove(dir);
    } else {
      selfFeed.put(dir, enabled);
    }
    if (network != null) {
      network.routesChanged();
    }
  }

  @Override
  public boolean isRoundRobinEnabled(@Nonnull EnumFacing dir) {
    Boolean val = roundRobin.get(dir);
    if (val == null) {
      return false;
    }
    return val;
  }

  @Override
  public void setRoundRobinEnabled(@Nonnull EnumFacing dir, boolean enabled) {
    if (!enabled) {
      roundRobin.remove(dir);
    } else {
      roundRobin.put(dir, enabled);
    }
    if (network != null) {
      network.routesChanged();
    }
  }

  @Override
  public int getOutputPriority(@Nonnull EnumFacing dir) {
    Integer res = priority.get(dir);
    if (res == null) {
      return 0;
    }
    return res.intValue();
  }

  @Override
  public void setOutputPriority(@Nonnull EnumFacing dir, int priority) {
    if (priority == 0) {
      this.priority.remove(dir);
    } else {
      this.priority.put(dir, priority);
    }
    if (network != null) {
      network.routesChanged();
    }

  }

  @Override
  public boolean canConnectToExternal(@Nonnull EnumFacing direction, boolean ignoreDisabled) {
    return getExternalInventory(direction) != null;
  }

  @Override
  @Nonnull
  protected ConnectionMode getDefaultConnectionMode() {
    return ConnectionMode.INPUT;
  }

  @Override
  @Nonnull
  public Class<? extends IConduit> getBaseConduitType() {
    return IItemConduit.class;
  }

  @Override
  @Nonnull
  public ItemStack createItem() {
    ItemStack result = new ItemStack(item_item_conduit.getItem(), 1, metaData);
    return result;
  }

  @Override
  public AbstractConduitNetwork<?, ?> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(@Nonnull IConduitNetwork<?, ?> network) {
    this.network = (ItemConduitNetwork) network;
    return true;
  }

  // -------------------------------------------
  // Textures
  // ------------------------------------------

  @SideOnly(Side.CLIENT)
  @Override
  public TextureAtlasSprite getTextureForInputMode() {
    return ICONS.get(ICON_KEY_INPUT);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public TextureAtlasSprite getTextureForOutputMode() {
    return ICONS.get(ICON_KEY_OUTPUT);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public TextureAtlasSprite getTextureForInOutMode(boolean input) {
    return input ? ICONS.get(ICON_KEY_IN_OUT_IN) : ICONS.get(ICON_KEY_IN_OUT_OUT);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public TextureAtlasSprite getTextureForInOutBackground() {
    return ICONS.get(ICON_KEY_IN_OUT_BG);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public TextureAtlasSprite getEnderIcon() {
    return ICONS.get(ICON_KEY_ENDER);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getCoreIcon() {
    return ICONS.get(ICON_KEY_CORE);
  }

  @SideOnly(Side.CLIENT)
  @Override
  @Nonnull
  public TextureAtlasSprite getTextureForState(@Nonnull CollidableComponent component) {
    if (component.dir == null) {
      return getCoreIcon();
    }
    if (EXTERNAL_INTERFACE_GEOM.equals(component.data)) {
      return getCoreIcon();
    }
    return ICONS.get(ICON_KEY);
  }

  @SideOnly(Side.CLIENT)
  @Override
  @Nonnull
  public TextureAtlasSprite getTransmitionTextureForState(@Nonnull CollidableComponent component) {
    return getEnderIcon();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Vector4f getTransmitionTextureColorForState(@Nonnull CollidableComponent component) {
    return null;
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);

    for (Entry<EnumFacing, IItemFilter> entry : inputFilters.entrySet()) {
      if (entry.getValue() != null) {
        IItemFilter f = entry.getValue();
        if (!isDefault(f)) {
          NBTTagCompound itemRoot = new NBTTagCompound();
          FilterRegistry.writeFilterToNbt(f, itemRoot);
          nbtRoot.setTag("inFilts." + entry.getKey().name(), itemRoot);
        }
      }
    }

    for (Entry<EnumFacing, ItemStack> entry : speedUpgrades.entrySet()) {
      if (entry.getValue() != null) {
        ItemStack up = entry.getValue();
        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("speedUpgrades." + entry.getKey().name(), itemRoot);
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

    for (Entry<EnumFacing, IItemFilter> entry : outputFilters.entrySet()) {
      if (entry.getValue() != null) {
        IItemFilter f = entry.getValue();
        if (!isDefault(f)) {
          NBTTagCompound itemRoot = new NBTTagCompound();
          FilterRegistry.writeFilterToNbt(f, itemRoot);
          nbtRoot.setTag("outFilts." + entry.getKey().name(), itemRoot);
        }
      }
    }

    for (Entry<EnumFacing, ItemStack> entry : inputFilterUpgrades.entrySet()) {
      if (entry.getValue() != null) {
        ItemStack up = entry.getValue();
        IItemFilter filter = getInputFilter(entry.getKey());
        FilterRegistry.writeFilterToStack(filter, up);

        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("inputFilterUpgrades." + entry.getKey().name(), itemRoot);
      }
    }

    for (Entry<EnumFacing, ItemStack> entry : outputFilterUpgrades.entrySet()) {
      if (entry.getValue() != null) {
        ItemStack up = entry.getValue();
        IItemFilter filter = getOutputFilter(entry.getKey());
        FilterRegistry.writeFilterToStack(filter, up);

        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("outputFilterUpgrades." + entry.getKey().name(), itemRoot);
      }
    }

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

    for (Entry<EnumFacing, Boolean> entry : selfFeed.entrySet()) {
      if (entry.getValue() != null) {
        nbtRoot.setBoolean("selfFeed." + entry.getKey().name(), entry.getValue());
      }
    }

    for (Entry<EnumFacing, Boolean> entry : roundRobin.entrySet()) {
      if (entry.getValue() != null) {
        nbtRoot.setBoolean("roundRobin." + entry.getKey().name(), entry.getValue());
      }
    }

    for (Entry<EnumFacing, Integer> entry : priority.entrySet()) {
      if (entry.getValue() != null) {
        nbtRoot.setInteger("priority." + entry.getKey().name(), entry.getValue());
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

  }

  private boolean isDefault(IItemFilter f) {
    if (f instanceof ItemFilter) {
      return ((ItemFilter) f).isDefault();
    }
    return false;
  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);

    if (nbtRoot.hasKey("metaData")) {
      metaData = nbtRoot.getShort("metaData");
    } else {
      metaData = 0;
    }

    for (EnumFacing dir : EnumFacing.VALUES) {

      String key = "inFilts." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound filterTag = (NBTTagCompound) nbtRoot.getTag(key);
        IItemFilter filter = FilterRegistry.loadFilterFromNbt(filterTag);
        inputFilters.put(dir, filter);
      }

      key = "speedUpgrades." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound upTag = (NBTTagCompound) nbtRoot.getTag(key);
        ItemStack ups = new ItemStack(upTag);
        speedUpgrades.put(dir, ups);
      }

      key = "functionUpgrades." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound upTag = (NBTTagCompound) nbtRoot.getTag(key);
        ItemStack ups = new ItemStack(upTag);
        functionUpgrades.put(dir, ups);
      }

      key = "inputFilterUpgrades." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound upTag = (NBTTagCompound) nbtRoot.getTag(key);
        ItemStack ups = new ItemStack(upTag);
        inputFilterUpgrades.put(dir, ups);
      }

      key = "outputFilterUpgrades." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound upTag = (NBTTagCompound) nbtRoot.getTag(key);
        ItemStack ups = new ItemStack(upTag);
        outputFilterUpgrades.put(dir, ups);
      }

      key = "outFilts." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound filterTag = (NBTTagCompound) nbtRoot.getTag(key);
        IItemFilter filter = FilterRegistry.loadFilterFromNbt(filterTag);
        outputFilters.put(dir, filter);
      }

      key = "extRM." + dir.name();
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
      key = "selfFeed." + dir.name();
      if (nbtRoot.hasKey(key)) {
        boolean val = nbtRoot.getBoolean(key);
        selfFeed.put(dir, val);
      }

      key = "roundRobin." + dir.name();
      if (nbtRoot.hasKey(key)) {
        boolean val = nbtRoot.getBoolean(key);
        roundRobin.put(dir, val);
      }

      key = "priority." + dir.name();
      if (nbtRoot.hasKey(key)) {
        int val = nbtRoot.getInteger(key);
        priority.put(dir, val);
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
    }

    if (!nbtRoot.hasKey("conModes")) {
      // all externals where on default so need to switch them to the old default
      for (EnumFacing dir : externalConnections) {
        conectionModes.put(dir, ConnectionMode.OUTPUT);
      }
    }
    connectionsDirty = true;
  }

  // TODO Inventory
  // @Override
  // public boolean onNeighborChange(@Nonnull BlockPos neighbourPos) {
  // if (neighbourPos != null && network != null && network.hasDatabase()) {
  // network.getDatabase().onNeighborChange(neighbourPos);
  // }
  // return super.onNeighborChange(neighbourPos);
  // }

  @SideOnly(Side.CLIENT)
  @Override
  public void hashCodeForModelCaching(IBlockStateWrapper wrapper, BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
    super.hashCodeForModelCaching(wrapper, hashCodes);
    hashCodes.addEnum(outputColors);
    hashCodes.addEnum(inputColors);
  }

  @Override
  public void invalidate() {
    super.invalidate();
    if (network != null) {
      final BlockPos pos = bundle.getEntity().getPos();
      for (EnumFacing direction : externalConnections) {
        try {
          BlockPos p = pos.offset(direction);
          network.inventoryRemoved(this, p);
        } catch (Throwable t) {
          // silent
        }
      }
    }
  }

  @Override
  @Nonnull
  public ItemConduitNetwork createNetworkForType() {
    return new ItemConduitNetwork();
  }

  // @SideOnly(Side.CLIENT)
  // @Override
  // public ITabPanel createPanelForConduit(GuiExternalConnection gui, IConduit con) {
  // return new ItemSettings(gui, con);
  // }

  @SideOnly(Side.CLIENT)
  @Nonnull
  @Override
  public ITabPanel createGuiPanel(@Nonnull IGuiExternalConnection gui, @Nonnull IConduit con) {
    return new ItemSettings((GuiExternalConnection) gui, con); // TODO abstract this better for base
  }

  @SideOnly(Side.CLIENT)
  @Override
  public int getGuiPanelTabOrder() {
    return 0;
  }

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    return false;
  }

  @Nullable
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    return null;
  }
}
