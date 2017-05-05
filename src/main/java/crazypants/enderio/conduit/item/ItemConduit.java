package crazypants.enderio.conduit.item;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.capability.ItemTools;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitComponent;
import crazypants.enderio.conduit.RaytraceResult;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.gui.item.ItemSettings;
import crazypants.enderio.conduit.item.filter.IItemFilter;
import crazypants.enderio.conduit.item.filter.ItemFilter;
import crazypants.enderio.conduit.render.BlockStateWrapperConduitBundle;
import crazypants.enderio.item.PacketConduitProbe;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.invpanel.chest.TileInventoryChest;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.tool.ToolUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import static crazypants.enderio.ModObject.itemBasicFilterUpgrade;
import static crazypants.enderio.ModObject.itemItemConduit;

public class ItemConduit extends AbstractConduit implements IItemConduit, IConduitComponent {

  public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
  
  public static final String EXTERNAL_INTERFACE_GEOM = "ExternalInterface";

  public static final String ICON_KEY = "enderio:blocks/itemConduit";

  public static final String ICON_KEY_CORE = "enderio:blocks/itemConduitCore";

  public static final String ICON_KEY_INPUT = "enderio:blocks/itemConduitInput";

  public static final String ICON_KEY_OUTPUT = "enderio:blocks/itemConduitOutput";

  public static final String ICON_KEY_IN_OUT_OUT = "enderio:blocks/itemConduitInOut_Out";

  public static final String ICON_KEY_IN_OUT_IN = "enderio:blocks/itemConduitInOut_In";

  public static final String ICON_KEY_IN_OUT_BG = "enderio:blocks/itemConduitIoConnector";

  public static final String ICON_KEY_ENDER = "enderio:blocks/ender_still";

  static final Map<String, TextureAtlasSprite> ICONS = new HashMap<String, TextureAtlasSprite>();

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {
      
      @Override
      public void registerIcons(TextureMap register) {
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
  }


  @Override
  protected void readTypeSettings(EnumFacing dir, NBTTagCompound dataRoot) {    
    setExtractionSignalColor(dir, DyeColor.values()[dataRoot.getShort("extractionSignalColor")]);
    setExtractionRedstoneMode(RedstoneControlMode.values()[dataRoot.getShort("extractionRedstoneMode")], dir);    
    setInputColor(dir, DyeColor.values()[dataRoot.getShort("inputColor")]);
    setOutputColor(dir, DyeColor.values()[dataRoot.getShort("outputColor")]);
    setSelfFeedEnabled(dir, dataRoot.getBoolean("selfFeed"));
    setRoundRobinEnabled(dir, dataRoot.getBoolean("roundRobin"));
    setOutputPriority(dir, dataRoot.getInteger("outputPriority"));
  }
  
  @Override
  protected void writeTypeSettingsToNbt(EnumFacing dir, NBTTagCompound dataRoot) {
    dataRoot.setShort("extractionSignalColor", (short)getExtractionSignalColor(dir).ordinal());
    dataRoot.setShort("extractionRedstoneMode", (short)getExtractionRedstoneMode(dir).ordinal());
    dataRoot.setShort("inputColor", (short)getInputColor(dir).ordinal());
    dataRoot.setShort("outputColor", (short)getOutputColor(dir).ordinal());
    dataRoot.setBoolean("selfFeed", isSelfFeedEnabled(dir));
    dataRoot.setBoolean("roundRobin", isRoundRobinEnabled(dir));
    dataRoot.setInteger("outputPriority", getOutputPriority(dir));    
  }
  
  protected void convertToItemUpgrades(int filterMeta, Map<EnumFacing, ItemStack> converted, EnumMap<EnumFacing, IItemFilter> sourceFilters) {
    for (Entry<EnumFacing, IItemFilter> entry : sourceFilters.entrySet()) {
      if(entry.getValue() != null) {
        IItemFilter f = entry.getValue();
        ItemStack up = new ItemStack(itemBasicFilterUpgrade.getItem(), 1, filterMeta);
        FilterRegister.writeFilterToStack(f, up);
        converted.put(entry.getKey(), up);
      }
    }
  }

  @Override
  public List<ItemStack> getDrops() {
    List<ItemStack> res = new ArrayList<ItemStack>();
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
  public boolean onBlockActivated(EntityPlayer player, EnumHand hand, RaytraceResult res, List<RaytraceResult> all) {
    if(ConduitUtil.isProbeEquipped(player, hand)) {
      if(!player.worldObj.isRemote) {
        PacketConduitProbe.sendInfoMessage(player, this, null);
      }
      return true;
    } else if(ToolUtil.isToolEquipped(player, hand)) {
      if(!getBundle().getEntity().getWorld().isRemote) {
        if(res != null && res.component != null) {
          EnumFacing connDir = res.component.dir;
          EnumFacing faceHit = res.movingObjectPosition.sideHit;
          if(connDir == null || connDir == faceHit) {
            if(getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
              setConnectionMode(faceHit, getNextConnectionMode(faceHit));
              return true;
            }
            // Attempt to join networks
            return ConduitUtil.joinConduits(this, faceHit);
          } else if(externalConnections.contains(connDir)) {
            setConnectionMode(connDir, getNextConnectionMode(connDir));
            return true;
          } else if(containsConduitConnection(connDir)) {
            ConduitUtil.disconectConduits(this, connDir);
            return true;
          }
        }
      }
    } else {

      if(res != null && res.component != null) {
        EnumFacing connDir = res.component.dir;
        if (connDir != null && containsExternalConnection(connDir)) {
          if(!player.worldObj.isRemote) {
            PacketConduitProbe.sendInfoMessage(player, this, player.getHeldItem(hand));
          }
          return true;
        }
      }

    }
    return false;
  }

  @Override
  public void setInputFilter(EnumFacing dir, IItemFilter filter) {
    inputFilters.put(dir, filter);
    if(network != null) {
      network.routesChanged();
    }
    setClientStateDirty();
  }

  @Override
  public void setOutputFilter(EnumFacing dir, IItemFilter filter) {
    outputFilters.put(dir, filter);
    if(network != null) {
      network.routesChanged();
    }
    setClientStateDirty();
  }

  @Override
  public IItemFilter getInputFilter(EnumFacing dir) {
    return inputFilters.get(dir);
  }

  @Override
  public IItemFilter getOutputFilter(EnumFacing dir) {
    return outputFilters.get(dir);
  }

  @Override
  public void setInputFilterUpgrade(EnumFacing dir, ItemStack stack) {
    inputFilterUpgrades.put(dir, stack);
    setInputFilter(dir, FilterRegister.getFilterForUpgrade(stack));
    setClientStateDirty();
  }

  @Override
  public void setOutputFilterUpgrade(EnumFacing dir, ItemStack stack) {
    outputFilterUpgrades.put(dir, stack);
    setOutputFilter(dir, FilterRegister.getFilterForUpgrade(stack));
    setClientStateDirty();
  }

  @Override
  public ItemStack getInputFilterUpgrade(EnumFacing dir) {
    return inputFilterUpgrades.get(dir);
  }

  @Override
  public ItemStack getOutputFilterUpgrade(EnumFacing dir) {
    return outputFilterUpgrades.get(dir);
  }

  @Override
  public void setSpeedUpgrade(EnumFacing dir, ItemStack upgrade) {
    if(upgrade != null) {
      speedUpgrades.put(dir, upgrade);
    } else {
      speedUpgrades.remove(dir);
    }
    setClientStateDirty();
  }

  @Override
  public ItemStack getSpeedUpgrade(EnumFacing dir) {
    return speedUpgrades.get(dir);
  }

  @Override
  public void setFunctionUpgrade(EnumFacing dir, ItemStack upgrade) {
    boolean hadIPU = hasInventoryPanelUpgrade(dir);
    if(upgrade != null) {
      functionUpgrades.put(dir, upgrade);
    } else {
      functionUpgrades.remove(dir);
    }
    setClientStateDirty();
    if(network != null && hadIPU != hasInventoryPanelUpgrade(dir)) {
      network.inventoryPanelSourcesChanged();
    }
  }

  @Override
  public ItemStack getFunctionUpgrade(EnumFacing dir) {
    return functionUpgrades.get(dir);
  }

  @Override
  public boolean hasInventoryPanelUpgrade(EnumFacing dir) {
    ItemStack upgrade = functionUpgrades.get(dir);
    return (upgrade != null && ItemFunctionUpgrade.getFunctionUpgrade(upgrade) == FunctionUpgrade.INVENTORY_PANEL) || isConnectedToNetworkAwareBlock(dir);
  }

  @Override
  public boolean isConnectedToNetworkAwareBlock(EnumFacing dir) {
    if (!externalConnections.contains(dir)) {
      return false;
    }
    World world = getBundle().getBundleWorldObj();
    if (world == null) {
      return false;
    }
    BlockPos loc = getLocation().getLocation(dir).getBlockPos();
    if (!world.isBlockLoaded(loc)) {
      return false;
    }
    TileEntity tileEntity = world.getTileEntity(loc);
    return tileEntity instanceof TileInventoryChest;
  }

  @Override
  public int getMetaData() {
    return metaData;
  }

  @Override
  public void setExtractionRedstoneMode(RedstoneControlMode mode, EnumFacing dir) {
    extractionModes.put(dir, mode);
  }

  @Override
  public RedstoneControlMode getExtractionRedstoneMode(EnumFacing dir) {
    RedstoneControlMode res = extractionModes.get(dir);
    if(res == null) {
      res = RedstoneControlMode.NEVER;
    }
    return res;
  }

  @Override
  public void setExtractionSignalColor(EnumFacing dir, DyeColor col) {
    extractionColors.put(dir, col);
  }

  @Override
  public DyeColor getExtractionSignalColor(EnumFacing dir) {
    DyeColor result = extractionColors.get(dir);
    if(result == null) {
      return DyeColor.RED;
    }
    return result;
  }

  @Override
  public boolean isExtractionRedstoneConditionMet(EnumFacing dir) {
    RedstoneControlMode mode = getExtractionRedstoneMode(dir);
    return ConduitUtil.isRedstoneControlModeMet(this, mode, getExtractionSignalColor(dir));
  }

  @Override
  public DyeColor getInputColor(EnumFacing dir) {
    DyeColor result = inputColors.get(dir);
    if(result == null) {
      return DyeColor.GREEN;
    }
    return result;
  }

  @Override
  public DyeColor getOutputColor(EnumFacing dir) {
    DyeColor result = outputColors.get(dir);
    if(result == null) {
      return DyeColor.GREEN;
    }
    return result;
  }

  @Override
  public void setInputColor(EnumFacing dir, DyeColor col) {
    inputColors.put(dir, col);
    if(network != null) {
      network.routesChanged();
    }
    setClientStateDirty();
    collidablesDirty = true;
  }

  @Override
  public void setOutputColor(EnumFacing dir, DyeColor col) {
    outputColors.put(dir, col);
    if(network != null) {
      network.routesChanged();
    }
    setClientStateDirty();
    collidablesDirty = true;
  }

  @Override
  public int getMaximumExtracted(EnumFacing dir) {
    ItemStack stack = speedUpgrades.get(dir);
    if(stack == null) {
      return SpeedUpgrade.BASE_MAX_EXTRACTED;
    }
    SpeedUpgrade speedUpgrade = ItemExtractSpeedUpgrade.getSpeedUpgrade(stack);
    return speedUpgrade.getMaximumExtracted(stack.stackSize);
  }

  @Override
  public float getTickTimePerItem(EnumFacing dir) {
    float maxExtract = 10f / getMaximumExtracted(dir);
    return maxExtract;
  }

  @Override
  public void itemsExtracted(int numExtracted, int slot) {
  }

  @Override
  public void externalConnectionAdded(EnumFacing direction) {
    super.externalConnectionAdded(direction);
    checkInventoryConnections(direction);
  }

  @Override
  public IItemHandler getExternalInventory(EnumFacing direction) {
    World world = getBundle().getBundleWorldObj();
    if(world == null) {
      return null;
    }
    BlockCoord loc = getLocation().getLocation(direction);
    return ItemTools.getExternalInventory(world, loc.getBlockPos(), direction.getOpposite());
  }

  @Override
  public void externalConnectionRemoved(EnumFacing direction) {
    externalConnections.remove(direction);
    connectionsChanged();
    checkInventoryConnections(direction);
  }

  private void checkInventoryConnections(EnumFacing direction) {
    if(network != null) {
      BlockPos p = bundle.getEntity().getPos().offset(direction);
      NetworkedInventory networkedInventory = network.getInventory(this, direction);
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
  public void setConnectionMode(EnumFacing dir, ConnectionMode mode) {
    ConnectionMode oldVal = conectionModes.get(dir);
    if(oldVal == mode) {
      return;
    }
    super.setConnectionMode(dir, mode);
    checkInventoryConnections(dir);
    if(network != null) {
      network.routesChanged();
    }
  }

  @Override
  public boolean isSelfFeedEnabled(EnumFacing dir) {
    Boolean val = selfFeed.get(dir);
    if(val == null) {
      return false;
    }
    return val;
  }

  @Override
  public void setSelfFeedEnabled(EnumFacing dir, boolean enabled) {
    if(!enabled) {
      selfFeed.remove(dir);
    } else {
      selfFeed.put(dir, enabled);
    }
    if(network != null) {
      network.routesChanged();
    }
  }

  @Override
  public boolean isRoundRobinEnabled(EnumFacing dir) {
    Boolean val = roundRobin.get(dir);
    if(val == null) {
      return false;
    }
    return val;
  }

  @Override
  public void setRoundRobinEnabled(EnumFacing dir, boolean enabled) {
    if(!enabled) {
      roundRobin.remove(dir);
    } else {
      roundRobin.put(dir, enabled);
    }
    if(network != null) {
      network.routesChanged();
    }
  }

  @Override
  public int getOutputPriority(EnumFacing dir) {
    Integer res = priority.get(dir);
    if(res == null) {
      return 0;
    }
    return res.intValue();
  }

  @Override
  public void setOutputPriority(EnumFacing dir, int priority) {
    if(priority == 0) {
      this.priority.remove(dir);
    } else {
      this.priority.put(dir, priority);
    }
    if(network != null) {
      network.routesChanged();
    }

  }

  @Override
  public boolean canConnectToExternal(EnumFacing direction, boolean ignoreDisabled) {
    return getExternalInventory(direction) != null;    
  }

  @Override
  protected ConnectionMode getDefaultConnectionMode() {
    return ConnectionMode.INPUT;
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IItemConduit.class;
  }

  @Override
  public ItemStack createItem() {
    ItemStack result = new ItemStack(itemItemConduit.getItem(), 1, metaData);
    return result;
  }

  @Override
  public AbstractConduitNetwork<?, ?> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(AbstractConduitNetwork<?, ?> network) {
    this.network = (ItemConduitNetwork) network;
    return true;
  }

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
  public TextureAtlasSprite getTextureForState(CollidableComponent component) {
    if(component.dir == null) {
      return getCoreIcon();
    }
    if(EXTERNAL_INTERFACE_GEOM.equals(component.data)) {
      return getCoreIcon();
    }
    return ICONS.get(ICON_KEY);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public TextureAtlasSprite getTransmitionTextureForState(CollidableComponent component) {
    return getEnderIcon();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Vector4f getTransmitionTextureColorForState(CollidableComponent component) {
    return null;
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);

    for (Entry<EnumFacing, IItemFilter> entry : inputFilters.entrySet()) {
      if(entry.getValue() != null) {
        IItemFilter f = entry.getValue();
        if(!isDefault(f)) {
          NBTTagCompound itemRoot = new NBTTagCompound();
          FilterRegister.writeFilterToNbt(f, itemRoot);
          nbtRoot.setTag("inFilts." + entry.getKey().name(), itemRoot);
        }
      }
    }

    for (Entry<EnumFacing, ItemStack> entry : speedUpgrades.entrySet()) {
      if(entry.getValue() != null) {
        ItemStack up = entry.getValue();
        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("speedUpgrades." + entry.getKey().name(), itemRoot);
      }
    }

    for (Entry<EnumFacing, ItemStack> entry : functionUpgrades.entrySet()) {
      if(entry.getValue() != null) {
        ItemStack up = entry.getValue();
        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("functionUpgrades." + entry.getKey().name(), itemRoot);
      }
    }

    for (Entry<EnumFacing, IItemFilter> entry : outputFilters.entrySet()) {
      if(entry.getValue() != null) {
        IItemFilter f = entry.getValue();
        if(!isDefault(f)) {
          NBTTagCompound itemRoot = new NBTTagCompound();
          FilterRegister.writeFilterToNbt(f, itemRoot);
          nbtRoot.setTag("outFilts." + entry.getKey().name(), itemRoot);
        }
      }
    }

    for (Entry<EnumFacing, ItemStack> entry : inputFilterUpgrades.entrySet()) {
      if(entry.getValue() != null) {
        ItemStack up = entry.getValue();
        IItemFilter filter = getInputFilter(entry.getKey());
        FilterRegister.writeFilterToStack(filter, up);

        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("inputFilterUpgrades." + entry.getKey().name(), itemRoot);
      }
    }

    for (Entry<EnumFacing, ItemStack> entry : outputFilterUpgrades.entrySet()) {
      if(entry.getValue() != null) {
        ItemStack up = entry.getValue();
        IItemFilter filter = getOutputFilter(entry.getKey());
        FilterRegister.writeFilterToStack(filter, up);

        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("outputFilterUpgrades." + entry.getKey().name(), itemRoot);
      }
    }

    for (Entry<EnumFacing, RedstoneControlMode> entry : extractionModes.entrySet()) {
      if(entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("extRM." + entry.getKey().name(), ord);
      }
    }

    for (Entry<EnumFacing, DyeColor> entry : extractionColors.entrySet()) {
      if(entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("extSC." + entry.getKey().name(), ord);
      }
    }

    for (Entry<EnumFacing, Boolean> entry : selfFeed.entrySet()) {
      if(entry.getValue() != null) {
        nbtRoot.setBoolean("selfFeed." + entry.getKey().name(), entry.getValue());
      }
    }

    for (Entry<EnumFacing, Boolean> entry : roundRobin.entrySet()) {
      if(entry.getValue() != null) {
        nbtRoot.setBoolean("roundRobin." + entry.getKey().name(), entry.getValue());
      }
    }

    for (Entry<EnumFacing, Integer> entry : priority.entrySet()) {
      if(entry.getValue() != null) {
        nbtRoot.setInteger("priority." + entry.getKey().name(), entry.getValue());
      }
    }

    for (Entry<EnumFacing, DyeColor> entry : inputColors.entrySet()) {
      if(entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("inSC." + entry.getKey().name(), ord);
      }
    }

    for (Entry<EnumFacing, DyeColor> entry : outputColors.entrySet()) {
      if(entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("outSC." + entry.getKey().name(), ord);
      }
    }

  }

  private boolean isDefault(IItemFilter f) {
    if(f instanceof ItemFilter) {
      return ((ItemFilter) f).isDefault();
    }
    return false;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot, short nbtVersion) {
    super.readFromNBT(nbtRoot, nbtVersion);

    if(nbtRoot.hasKey("metaData")) {
      metaData = nbtRoot.getShort("metaData");
    } else {
      metaData = 0;
    }

    for (EnumFacing dir : EnumFacing.VALUES) {

      String key = "inFilts." + dir.name();
      if(nbtRoot.hasKey(key)) {
        NBTTagCompound filterTag = (NBTTagCompound) nbtRoot.getTag(key);
        IItemFilter filter = FilterRegister.loadFilterFromNbt(filterTag);
        inputFilters.put(dir, filter);
      }

      key = "speedUpgrades." + dir.name();
      if(nbtRoot.hasKey(key)) {
        NBTTagCompound upTag = (NBTTagCompound) nbtRoot.getTag(key);
        ItemStack ups = ItemStack.loadItemStackFromNBT(upTag);
        speedUpgrades.put(dir, ups);
      }

      key = "functionUpgrades." + dir.name();
      if(nbtRoot.hasKey(key)) {
        NBTTagCompound upTag = (NBTTagCompound) nbtRoot.getTag(key);
        ItemStack ups = ItemStack.loadItemStackFromNBT(upTag);
        functionUpgrades.put(dir, ups);
      }

      key = "inputFilterUpgrades." + dir.name();
      if(nbtRoot.hasKey(key)) {
        NBTTagCompound upTag = (NBTTagCompound) nbtRoot.getTag(key);
        ItemStack ups = ItemStack.loadItemStackFromNBT(upTag);
        inputFilterUpgrades.put(dir, ups);
      }

      key = "outputFilterUpgrades." + dir.name();
      if(nbtRoot.hasKey(key)) {
        NBTTagCompound upTag = (NBTTagCompound) nbtRoot.getTag(key);
        ItemStack ups = ItemStack.loadItemStackFromNBT(upTag);
        outputFilterUpgrades.put(dir, ups);
      }

      key = "outFilts." + dir.name();
      if(nbtRoot.hasKey(key)) {
        NBTTagCompound filterTag = (NBTTagCompound) nbtRoot.getTag(key);
        IItemFilter filter = FilterRegister.loadFilterFromNbt(filterTag);
        outputFilters.put(dir, filter);
      }

      key = "extRM." + dir.name();
      if(nbtRoot.hasKey(key)) {
        short ord = nbtRoot.getShort(key);
        if(ord >= 0 && ord < RedstoneControlMode.values().length) {
          extractionModes.put(dir, RedstoneControlMode.values()[ord]);
        }
      }
      key = "extSC." + dir.name();
      if(nbtRoot.hasKey(key)) {
        short ord = nbtRoot.getShort(key);
        if(ord >= 0 && ord < DyeColor.values().length) {
          extractionColors.put(dir, DyeColor.values()[ord]);
        }
      }
      key = "selfFeed." + dir.name();
      if(nbtRoot.hasKey(key)) {
        boolean val = nbtRoot.getBoolean(key);
        selfFeed.put(dir, val);
      }

      key = "roundRobin." + dir.name();
      if(nbtRoot.hasKey(key)) {
        boolean val = nbtRoot.getBoolean(key);
        roundRobin.put(dir, val);
      }

      key = "priority." + dir.name();
      if(nbtRoot.hasKey(key)) {
        int val = nbtRoot.getInteger(key);
        priority.put(dir, val);
      }

      key = "inSC." + dir.name();
      if(nbtRoot.hasKey(key)) {
        short ord = nbtRoot.getShort(key);
        if(ord >= 0 && ord < DyeColor.values().length) {
          inputColors.put(dir, DyeColor.values()[ord]);
        }
      }

      key = "outSC." + dir.name();
      if(nbtRoot.hasKey(key)) {
        short ord = nbtRoot.getShort(key);
        if(ord >= 0 && ord < DyeColor.values().length) {
          outputColors.put(dir, DyeColor.values()[ord]);
        }
      }
    }

    if(nbtVersion == 0 && !nbtRoot.hasKey("conModes")) {
      //all externals where on default so need to switch them to the old default
      for (EnumFacing dir : externalConnections) {
        conectionModes.put(dir, ConnectionMode.OUTPUT);
      }
    }
    connectionsDirty = true;
  }

  @Override
  public boolean onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbourPos) {
    if (neighbourPos != null && network != null && network.hasDatabase()) {
      network.getDatabase().onNeighborChange(neighbourPos);
    }
    return super.onNeighborChange(world, pos, neighbourPos);
  }

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
  public ItemConduitNetwork createNetworkForType() {
    return new ItemConduitNetwork();
  }

  @SideOnly(Side.CLIENT)
  @Override
  public ITabPanel createPanelForConduit(GuiExternalConnection gui, IConduit con) {
    return new ItemSettings(gui, con);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public int getTabOrderForConduit(IConduit con) {
    return 0;
  }

}
