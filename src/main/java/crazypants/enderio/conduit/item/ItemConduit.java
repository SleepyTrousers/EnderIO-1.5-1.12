package crazypants.enderio.conduit.item;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.inventory.IInventoryConnection;

import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.RaytraceResult;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.item.filter.IItemFilter;
import crazypants.enderio.conduit.item.filter.ItemFilter;
import crazypants.enderio.item.PacketConduitProbe;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.tool.ToolUtil;

public class ItemConduit extends AbstractConduit implements IItemConduit {

  public static final String EXTERNAL_INTERFACE_GEOM = "ExternalInterface";

  public static final String ICON_KEY = "enderio:itemConduit";

  public static final String ICON_KEY_CORE = "enderio:itemConduitCore";

  public static final String ICON_KEY_CORE_ADV = "enderio:itemConduitCoreAdvanced";

  public static final String ICON_KEY_INPUT = "enderio:itemConduitInput";

  public static final String ICON_KEY_OUTPUT = "enderio:itemConduitOutput";

  public static final String ICON_KEY_IN_OUT_OUT = "enderio:itemConduitInOut_Out";

  public static final String ICON_KEY_IN_OUT_IN = "enderio:itemConduitInOut_In";

  public static final String ICON_KEY_IN_OUT_BG = "enderio:itemConduitIoConnector";

  public static final String ICON_KEY_ENDER = "enderio:ender_still";

  static final Map<String, IIcon> ICONS = new HashMap<String, IIcon>();

  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IIconRegister register) {
        ICONS.put(ICON_KEY, register.registerIcon(ICON_KEY));
        ICONS.put(ICON_KEY_CORE, register.registerIcon(ICON_KEY_CORE));
        ICONS.put(ICON_KEY_CORE_ADV, register.registerIcon(ICON_KEY_CORE_ADV));
        ICONS.put(ICON_KEY_INPUT, register.registerIcon(ICON_KEY_INPUT));
        ICONS.put(ICON_KEY_OUTPUT, register.registerIcon(ICON_KEY_OUTPUT));
        ICONS.put(ICON_KEY_IN_OUT_OUT, register.registerIcon(ICON_KEY_IN_OUT_OUT));
        ICONS.put(ICON_KEY_IN_OUT_IN, register.registerIcon(ICON_KEY_IN_OUT_IN));
        ICONS.put(ICON_KEY_IN_OUT_BG, register.registerIcon(ICON_KEY_IN_OUT_BG));
        ICONS.put(ICON_KEY_ENDER, register.registerIcon(ICON_KEY_ENDER));
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }

  ItemConduitNetwork network;

  protected final EnumMap<ForgeDirection, RedstoneControlMode> extractionModes = new EnumMap<ForgeDirection, RedstoneControlMode>(ForgeDirection.class);
  protected final EnumMap<ForgeDirection, DyeColor> extractionColors = new EnumMap<ForgeDirection, DyeColor>(ForgeDirection.class);

  protected final EnumMap<ForgeDirection, IItemFilter> outputFilters = new EnumMap<ForgeDirection, IItemFilter>(ForgeDirection.class);
  protected final EnumMap<ForgeDirection, IItemFilter> inputFilters = new EnumMap<ForgeDirection, IItemFilter>(ForgeDirection.class);
  protected final EnumMap<ForgeDirection, ItemStack> outputFilterUpgrades = new EnumMap<ForgeDirection, ItemStack>(ForgeDirection.class);
  protected final EnumMap<ForgeDirection, ItemStack> inputFilterUpgrades = new EnumMap<ForgeDirection, ItemStack>(ForgeDirection.class);
  protected final EnumMap<ForgeDirection, ItemStack> speedUpgrades = new EnumMap<ForgeDirection, ItemStack>(ForgeDirection.class);
  protected final EnumMap<ForgeDirection, ItemStack> functionUpgrades = new EnumMap<ForgeDirection, ItemStack>(ForgeDirection.class);

  protected final EnumMap<ForgeDirection, Boolean> selfFeed = new EnumMap<ForgeDirection, Boolean>(ForgeDirection.class);

  protected final EnumMap<ForgeDirection, Boolean> roundRobin = new EnumMap<ForgeDirection, Boolean>(ForgeDirection.class);

  protected final EnumMap<ForgeDirection, Integer> priority = new EnumMap<ForgeDirection, Integer>(ForgeDirection.class);

  protected final EnumMap<ForgeDirection, DyeColor> outputColors = new EnumMap<ForgeDirection, DyeColor>(ForgeDirection.class);
  protected final EnumMap<ForgeDirection, DyeColor> inputColors = new EnumMap<ForgeDirection, DyeColor>(ForgeDirection.class);

  private int metaData;

  public ItemConduit() {
    this(0);
  }

  public ItemConduit(int itemDamage) {
    metaData = itemDamage;
    updateFromNonUpgradeableVersion();
  }

  private void updateFromNonUpgradeableVersion() {
    int filterMeta = metaData;
    if(metaData == 1) {

      for (Entry<ForgeDirection, IItemFilter> entry : inputFilters.entrySet()) {
        if(entry.getValue() != null) {
          IItemFilter f = entry.getValue();
          if(f != null) {
            setSpeedUpgrade(entry.getKey(), new ItemStack(EnderIO.itemExtractSpeedUpgrade, 15, 0));
          }
        }
      }

      metaData = 0;
    }

    Map<ForgeDirection, ItemStack> converted = new HashMap<ForgeDirection, ItemStack>();

    convertToItemUpgrades(filterMeta, converted, inputFilters);
    for (Entry<ForgeDirection, ItemStack> entry : converted.entrySet()) {
      setInputFilter(entry.getKey(), null);
      setInputFilterUpgrade(entry.getKey(), entry.getValue());
    }

    converted.clear();
    convertToItemUpgrades(filterMeta, converted, outputFilters);
    for (Entry<ForgeDirection, ItemStack> entry : converted.entrySet()) {
      setOutputFilter(entry.getKey(), null);
      setOutputFilterUpgrade(entry.getKey(), entry.getValue());
    }


  }

  @Override
  protected void readTypeSettings(ForgeDirection dir, NBTTagCompound dataRoot) {    
    setExtractionSignalColor(dir, DyeColor.values()[dataRoot.getShort("extractionSignalColor")]);
    setExtractionRedstoneMode(RedstoneControlMode.values()[dataRoot.getShort("extractionRedstoneMode")], dir);    
    setInputColor(dir, DyeColor.values()[dataRoot.getShort("inputColor")]);
    setOutputColor(dir, DyeColor.values()[dataRoot.getShort("outputColor")]);
    setSelfFeedEnabled(dir, dataRoot.getBoolean("selfFeed"));
    setRoundRobinEnabled(dir, dataRoot.getBoolean("roundRobin"));
    setOutputPriority(dir, dataRoot.getInteger("outputPriority"));
  }
  
  @Override
  protected void writeTypeSettingsToNbt(ForgeDirection dir, NBTTagCompound dataRoot) {
    dataRoot.setShort("extractionSignalColor", (short)getExtractionSignalColor(dir).ordinal());
    dataRoot.setShort("extractionRedstoneMode", (short)getExtractionRedstoneMode(dir).ordinal());
    dataRoot.setShort("inputColor", (short)getInputColor(dir).ordinal());
    dataRoot.setShort("outputColor", (short)getOutputColor(dir).ordinal());
    dataRoot.setBoolean("selfFeed", isSelfFeedEnabled(dir));
    dataRoot.setBoolean("roundRobin", isRoundRobinEnabled(dir));
    dataRoot.setInteger("outputPriority", getOutputPriority(dir));    
  }
  
  protected void convertToItemUpgrades(int filterMeta, Map<ForgeDirection, ItemStack> converted, EnumMap<ForgeDirection, IItemFilter> sourceFilters) {
    for (Entry<ForgeDirection, IItemFilter> entry : sourceFilters.entrySet()) {
      if(entry.getValue() != null) {
        IItemFilter f = entry.getValue();
        ItemStack up = new ItemStack(EnderIO.itemBasicFilterUpgrade, 1, filterMeta);
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
  public boolean onBlockActivated(EntityPlayer player, RaytraceResult res, List<RaytraceResult> all) {
    if(ConduitUtil.isProbeEquipped(player)) {
      if(!player.worldObj.isRemote) {
        PacketConduitProbe.sendInfoMessage(player, this, null);
      }
      return true;
    } else if(ToolUtil.isToolEquipped(player)) {
      if(!getBundle().getEntity().getWorldObj().isRemote) {
        if(res != null && res.component != null) {
          ForgeDirection connDir = res.component.dir;
          ForgeDirection faceHit = ForgeDirection.getOrientation(res.movingObjectPosition.sideHit);
          if(connDir == ForgeDirection.UNKNOWN || connDir == faceHit) {
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
        ForgeDirection connDir = res.component.dir;
        if(connDir != null && connDir != ForgeDirection.UNKNOWN && containsExternalConnection(connDir)) {
          if(!player.worldObj.isRemote) {
            PacketConduitProbe.sendInfoMessage(player, this, player.getCurrentEquippedItem());
          }
          return true;
        }
      }

    }
    return false;
  }

  @Override
  public ItemStack insertItem(ForgeDirection from, ItemStack item) {
    if(!externalConnections.contains(from)) {
      return item;
    } else if(!getConnectionMode(from).acceptsInput()) {
      return item;
    } else if(network == null) {
      return item;
    } else {
      IItemFilter filter = inputFilters.get(from);
      ItemConduitNetwork network = (ItemConduitNetwork) getNetwork();
      if(filter != null && !filter.doesItemPassFilter(network.getInventory(this, from.getOpposite()), item)) {
        return item;
      }
    }
    return network.sendItems(this, item, from);
  }

  @Override
  public void setInputFilter(ForgeDirection dir, IItemFilter filter) {
    inputFilters.put(dir, filter);
    if(network != null) {
      network.routesChanged();
    }
    setClientStateDirty();
  }

  @Override
  public void setOutputFilter(ForgeDirection dir, IItemFilter filter) {
    outputFilters.put(dir, filter);
    if(network != null) {
      network.routesChanged();
    }
    setClientStateDirty();
  }

  @Override
  public IItemFilter getInputFilter(ForgeDirection dir) {
    return inputFilters.get(dir);
  }

  @Override
  public IItemFilter getOutputFilter(ForgeDirection dir) {
    return outputFilters.get(dir);
  }

  @Override
  public void setInputFilterUpgrade(ForgeDirection dir, ItemStack stack) {
    inputFilterUpgrades.put(dir, stack);
    setInputFilter(dir, FilterRegister.getFilterForUpgrade(stack));
    setClientStateDirty();
  }

  @Override
  public void setOutputFilterUpgrade(ForgeDirection dir, ItemStack stack) {
    outputFilterUpgrades.put(dir, stack);
    setOutputFilter(dir, FilterRegister.getFilterForUpgrade(stack));
    setClientStateDirty();
  }

  @Override
  public ItemStack getInputFilterUpgrade(ForgeDirection dir) {
    return inputFilterUpgrades.get(dir);
  }

  @Override
  public ItemStack getOutputFilterUpgrade(ForgeDirection dir) {
    return outputFilterUpgrades.get(dir);
  }

  @Override
  public void setSpeedUpgrade(ForgeDirection dir, ItemStack upgrade) {
    if(upgrade != null) {
      speedUpgrades.put(dir, upgrade);
    } else {
      speedUpgrades.remove(dir);
    }
    setClientStateDirty();
  }

  @Override
  public ItemStack getSpeedUpgrade(ForgeDirection dir) {
    return speedUpgrades.get(dir);
  }

  @Override
  public void setFunctionUpgrade(ForgeDirection dir, ItemStack upgrade) {
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
  public ItemStack getFunctionUpgrade(ForgeDirection dir) {
    return functionUpgrades.get(dir);
  }

  @Override
  public boolean hasInventoryPanelUpgrade(ForgeDirection dir) {
    ItemStack upgrade = functionUpgrades.get(dir);
    return upgrade != null && EnderIO.itemFunctionUpgrade.getFunctionUpgrade(upgrade) == FunctionUpgrade.INVENTORY_PANEL;
  }

  @Override
  public int getMetaData() {
    return metaData;
  }

  @Override
  public void setExtractionRedstoneMode(RedstoneControlMode mode, ForgeDirection dir) {
    extractionModes.put(dir, mode);
  }

  @Override
  public RedstoneControlMode getExtractionRedstoneMode(ForgeDirection dir) {
    RedstoneControlMode res = extractionModes.get(dir);
    if(res == null) {
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
    if(result == null) {
      return DyeColor.RED;
    }
    return result;
  }

  @Override
  public boolean isExtractionRedstoneConditionMet(ForgeDirection dir) {
    RedstoneControlMode mode = getExtractionRedstoneMode(dir);
    if(mode == null) {
      return true;
    }
    return ConduitUtil.isRedstoneControlModeMet(getBundle(), mode, getExtractionSignalColor(dir));
  }

  @Override
  public DyeColor getInputColor(ForgeDirection dir) {
    DyeColor result = inputColors.get(dir);
    if(result == null) {
      return DyeColor.GREEN;
    }
    return result;
  }

  @Override
  public DyeColor getOutputColor(ForgeDirection dir) {
    DyeColor result = outputColors.get(dir);
    if(result == null) {
      return DyeColor.GREEN;
    }
    return result;
  }

  @Override
  public void setInputColor(ForgeDirection dir, DyeColor col) {
    inputColors.put(dir, col);
    if(network != null) {
      network.routesChanged();
    }
    setClientStateDirty();
    collidablesDirty = true;
  }

  @Override
  public void setOutputColor(ForgeDirection dir, DyeColor col) {
    outputColors.put(dir, col);
    if(network != null) {
      network.routesChanged();
    }
    setClientStateDirty();
    collidablesDirty = true;
  }

  @Override
  public int getMaximumExtracted(ForgeDirection dir) {
    ItemStack stack = speedUpgrades.get(dir);
    if(stack == null) {
      return SpeedUpgrade.BASE_MAX_EXTRACTED;
    }
    SpeedUpgrade speedUpgrade = EnderIO.itemExtractSpeedUpgrade.getSpeedUpgrade(stack);
    return speedUpgrade.getMaximumExtracted(stack.stackSize);
  }

  @Override
  public float getTickTimePerItem(ForgeDirection dir) {
    float maxExtract = 10f / getMaximumExtracted(dir);
    return maxExtract;
  }

  @Override
  public void itemsExtracted(int numExtracted, int slot) {
  }

  @Override
  public void externalConnectionAdded(ForgeDirection direction) {
    super.externalConnectionAdded(direction);
    if(network != null) {
      TileEntity te = bundle.getEntity();
      network.inventoryAdded(this, direction, te.xCoord + direction.offsetX, te.yCoord + direction.offsetY, te.zCoord + direction.offsetZ,
          getExternalInventory(direction));
    }
  }

  @Override
  public IInventory getExternalInventory(ForgeDirection direction) {
    World world = getBundle().getWorld();
    if(world == null) {
      return null;
    }
    BlockCoord loc = getLocation().getLocation(direction);
    TileEntity te = world.getTileEntity(loc.x, loc.y, loc.z);
    if(te instanceof IInventory && !(te instanceof IConduitBundle)) {
      return (IInventory) te;
    }
    return null;
  }

  @Override
  public void externalConnectionRemoved(ForgeDirection direction) {
    externalConnections.remove(direction);
    connectionsChanged();
    if(network != null) {
      TileEntity te = bundle.getEntity();
      network.inventoryRemoved(this, te.xCoord + direction.offsetX, te.yCoord + direction.offsetY, te.zCoord + direction.offsetZ);
    }
  }

  @Override
  public void setConnectionMode(ForgeDirection dir, ConnectionMode mode) {
    ConnectionMode oldVal = conectionModes.get(dir);
    if(oldVal == mode) {
      return;
    }
    super.setConnectionMode(dir, mode);
    if(network != null) {
      network.routesChanged();
    }
  }

  @Override
  public boolean isSelfFeedEnabled(ForgeDirection dir) {
    Boolean val = selfFeed.get(dir);
    if(val == null) {
      return false;
    }
    return val;
  }

  @Override
  public void setSelfFeedEnabled(ForgeDirection dir, boolean enabled) {
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
  public boolean isRoundRobinEnabled(ForgeDirection dir) {
    Boolean val = roundRobin.get(dir);
    if(val == null) {
      return false;
    }
    return val;
  }

  @Override
  public void setRoundRobinEnabled(ForgeDirection dir, boolean enabled) {
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
  public int getOutputPriority(ForgeDirection dir) {
    Integer res = priority.get(dir);
    if(res == null) {
      return 0;
    }
    return res.intValue();
  }

  @Override
  public void setOutputPriority(ForgeDirection dir, int priority) {
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
  public boolean canConnectToExternal(ForgeDirection direction, boolean ignoreDisabled) {
    IInventory inv = getExternalInventory(direction);
    if (inv==null) return false;
    else if (inv instanceof IInventoryConnection){
      return ((IInventoryConnection)inv).canConnectInventory(direction.getOpposite()).canConnect;
    }
    else if(inv instanceof ISidedInventory) {
      int[] slots = ((ISidedInventory) inv).getAccessibleSlotsFromSide(direction.getOpposite().ordinal());
      return slots!=null && slots.length>0;
    } else {
      return true;
    }
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
    ItemStack result = new ItemStack(EnderIO.itemItemConduit, 1, metaData);
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

  @Override
  public IIcon getTextureForInputMode() {
    return ICONS.get(ICON_KEY_INPUT);
  }

  @Override
  public IIcon getTextureForOutputMode() {
    return ICONS.get(ICON_KEY_OUTPUT);
  }

  @Override
  public IIcon getTextureForInOutMode(boolean input) {
    return input ? ICONS.get(ICON_KEY_IN_OUT_IN) : ICONS.get(ICON_KEY_IN_OUT_OUT);
  }

  @Override
  public IIcon getTextureForInOutBackground() {
    return ICONS.get(ICON_KEY_IN_OUT_BG);
  }

  @Override
  public IIcon getEnderIcon() {
    return ICONS.get(ICON_KEY_ENDER);
  }

  public IIcon getCoreIcon() {
    return metaData == 1 ? ICONS.get(ICON_KEY_CORE_ADV) : ICONS.get(ICON_KEY_CORE);
  }

  @Override
  public IIcon getTextureForState(CollidableComponent component) {
    if(component.dir == ForgeDirection.UNKNOWN) {
      return getCoreIcon();
    }
    if(EXTERNAL_INTERFACE_GEOM.equals(component.data)) {
      return getCoreIcon();
    }
    return ICONS.get(ICON_KEY);
  }

  @Override
  public IIcon getTransmitionTextureForState(CollidableComponent component) {
    return getEnderIcon();
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);

    for (Entry<ForgeDirection, IItemFilter> entry : inputFilters.entrySet()) {
      if(entry.getValue() != null) {
        IItemFilter f = entry.getValue();
        if(!isDefault(f)) {
          NBTTagCompound itemRoot = new NBTTagCompound();
          FilterRegister.writeFilterToNbt(f, itemRoot);
          nbtRoot.setTag("inFilts." + entry.getKey().name(), itemRoot);
        }
      }
    }

    for (Entry<ForgeDirection, ItemStack> entry : speedUpgrades.entrySet()) {
      if(entry.getValue() != null) {
        ItemStack up = entry.getValue();
        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("speedUpgrades." + entry.getKey().name(), itemRoot);
      }
    }

    for (Entry<ForgeDirection, ItemStack> entry : functionUpgrades.entrySet()) {
      if(entry.getValue() != null) {
        ItemStack up = entry.getValue();
        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("functionUpgrades." + entry.getKey().name(), itemRoot);
      }
    }

    for (Entry<ForgeDirection, IItemFilter> entry : outputFilters.entrySet()) {
      if(entry.getValue() != null) {
        IItemFilter f = entry.getValue();
        if(!isDefault(f)) {
          NBTTagCompound itemRoot = new NBTTagCompound();
          FilterRegister.writeFilterToNbt(f, itemRoot);
          nbtRoot.setTag("outFilts." + entry.getKey().name(), itemRoot);
        }
      }
    }

    for (Entry<ForgeDirection, ItemStack> entry : inputFilterUpgrades.entrySet()) {
      if(entry.getValue() != null) {
        ItemStack up = entry.getValue();
        IItemFilter filter = getInputFilter(entry.getKey());
        FilterRegister.writeFilterToStack(filter, up);

        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("inputFilterUpgrades." + entry.getKey().name(), itemRoot);
      }
    }

    for (Entry<ForgeDirection, ItemStack> entry : outputFilterUpgrades.entrySet()) {
      if(entry.getValue() != null) {
        ItemStack up = entry.getValue();
        IItemFilter filter = getOutputFilter(entry.getKey());
        FilterRegister.writeFilterToStack(filter, up);

        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("outputFilterUpgrades." + entry.getKey().name(), itemRoot);
      }
    }

    for (Entry<ForgeDirection, RedstoneControlMode> entry : extractionModes.entrySet()) {
      if(entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("extRM." + entry.getKey().name(), ord);
      }
    }

    for (Entry<ForgeDirection, DyeColor> entry : extractionColors.entrySet()) {
      if(entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("extSC." + entry.getKey().name(), ord);
      }
    }

    for (Entry<ForgeDirection, Boolean> entry : selfFeed.entrySet()) {
      if(entry.getValue() != null) {
        nbtRoot.setBoolean("selfFeed." + entry.getKey().name(), entry.getValue());
      }
    }

    for (Entry<ForgeDirection, Boolean> entry : roundRobin.entrySet()) {
      if(entry.getValue() != null) {
        nbtRoot.setBoolean("roundRobin." + entry.getKey().name(), entry.getValue());
      }
    }

    for (Entry<ForgeDirection, Integer> entry : priority.entrySet()) {
      if(entry.getValue() != null) {
        nbtRoot.setInteger("priority." + entry.getKey().name(), entry.getValue());
      }
    }

    for (Entry<ForgeDirection, DyeColor> entry : inputColors.entrySet()) {
      if(entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("inSC." + entry.getKey().name(), ord);
      }
    }

    for (Entry<ForgeDirection, DyeColor> entry : outputColors.entrySet()) {
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

    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {

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

    if(nbtRoot.hasKey("metaData")) {
      updateFromNonUpgradeableVersion();
    }

    if(nbtVersion == 0 && !nbtRoot.hasKey("conModes")) {
      //all externals where on default so need to switch them to the old default
      for (ForgeDirection dir : externalConnections) {
        conectionModes.put(dir, ConnectionMode.OUTPUT);
      }
    }
  }

}
