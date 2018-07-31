package crazypants.enderio.conduits.refinedstorage.conduit;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.common.vecmath.Vector4f;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeManager;

import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitNetwork;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.capability.CapabilityFilterHolder;
import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.filter.item.IItemFilter;
import crazypants.enderio.base.filter.item.ItemFilter;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduits.capability.CapabilityUpgradeHolder;
import crazypants.enderio.conduits.conduit.AbstractConduit;
import crazypants.enderio.conduits.refinedstorage.RSHelper;
import crazypants.enderio.conduits.refinedstorage.conduit.gui.RefinedStorageSettings;
import crazypants.enderio.conduits.refinedstorage.init.ConduitRefinedStorageObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class RefinedStorageConduit extends AbstractConduit implements IRefinedStorageConduit {

  static final Map<String, TextureSupplier> ICONS = new HashMap<>();

  static {
    ICONS.put(ICON_KEY, TextureRegistry.registerTexture(ICON_KEY));
    ICONS.put(ICON_CORE_KEY, TextureRegistry.registerTexture(ICON_CORE_KEY));
  }

  private Map<EnumFacing, ItemStack> upgrades = new EnumMap<EnumFacing, ItemStack>(EnumFacing.class);
  protected final EnumMap<EnumFacing, IFilter> outputFilters = new EnumMap<>(EnumFacing.class);
  protected final EnumMap<EnumFacing, IFilter> inputFilters = new EnumMap<>(EnumFacing.class);
  protected final EnumMap<EnumFacing, ItemStack> outputFilterUpgrades = new EnumMap<>(EnumFacing.class);
  protected final EnumMap<EnumFacing, ItemStack> inputFilterUpgrades = new EnumMap<>(EnumFacing.class);

  private ConduitRefinedStorageNode clientSideNode;

  protected RefinedStorageConduitNetwork network;

  public RefinedStorageConduit() {
    for (EnumFacing dir : EnumFacing.VALUES) {
      upgrades.put(dir, ItemStack.EMPTY);
      outputFilterUpgrades.put(dir, ItemStack.EMPTY);
      inputFilterUpgrades.put(dir, ItemStack.EMPTY);
    }
  }

  @Override
  public boolean canConnectToExternal(@Nonnull EnumFacing direction, boolean ignoreConnectionMode) {
    TileEntity te = bundle.getEntity();
    World world = te.getWorld();
    TileEntity test = world.getTileEntity(te.getPos().offset(direction));
    if (test == null) {
      return false;
    }
    if (test.hasCapability(RSHelper.NETWORK_NODE_PROXY_CAPABILITY, direction.getOpposite()) || test
        .hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()) || test
        .hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite())) {
      return true;
    }

    return super.canConnectToExternal(direction, ignoreConnectionMode);
  }

  @Override
  @Nonnull
  public ITabPanel createGuiPanel(@Nonnull IGuiExternalConnection gui, @Nonnull IClientConduit con) {
    return new RefinedStorageSettings(gui, con);
  }

  @Override
  public boolean updateGuiPanel(@Nonnull ITabPanel panel) {
    if (panel instanceof RefinedStorageSettings) {
      return ((RefinedStorageSettings) panel).updateConduit(this);
    }
    return false;
  }

  @Override
  public int getGuiPanelTabOrder() {
    return 4;
  }

  @Override
  @Nonnull
  public Class<? extends IConduit> getBaseConduitType() {
    return IRefinedStorageConduit.class;
  }

  @Override
  @Nonnull
  public ItemStack createItem() {
    return new ItemStack(ConduitRefinedStorageObject.item_refined_storage_conduit.getItemNN(), 1);
  }

  @Override
  @Nonnull
  public String getConduitProbeInfo(@Nonnull EntityPlayer player) {
    return "";
  }

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    if (capability == RSHelper.NETWORK_NODE_PROXY_CAPABILITY) {
      return true;
    }
    return false;
  }

  @Override
  @Nullable
  @SuppressWarnings("unchecked")
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == RSHelper.NETWORK_NODE_PROXY_CAPABILITY) {
      return RSHelper.NETWORK_NODE_PROXY_CAPABILITY.cast(this);
    }
    return null;
  }

  @Override
  public boolean hasClientCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    if (capability == RSHelper.NETWORK_NODE_PROXY_CAPABILITY) {
      return true;
    }
    return false;
  }

  @Override
  @Nullable
  public <T> T getClientCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == RSHelper.NETWORK_NODE_PROXY_CAPABILITY) {
      return RSHelper.NETWORK_NODE_PROXY_CAPABILITY.cast(this);
    }
    return null;
  }

  @Override
  public boolean hasInternalCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityUpgradeHolder.UPGRADE_HOLDER_CAPABILITY || capability == CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY) {
      return true;
    }
    return false;
  }

  @Nullable
  @Override
  public <T> T getInternalCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityUpgradeHolder.UPGRADE_HOLDER_CAPABILITY || capability == CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY) {
      return (T) this;
    }
    return null;
  }

  @Override
  @Nonnull
  public RefinedStorageConduitNetwork createNetworkForType() {
    return new RefinedStorageConduitNetwork();
  }

  @Override
  @Nullable
  public RefinedStorageConduitNetwork getNetwork() throws NullPointerException {
    return network;
  }

  @Override
  public boolean setNetwork(@Nonnull IConduitNetwork<?, ?> network) {
    this.network = (RefinedStorageConduitNetwork) network;
    return true;
  }

  @Override
  public void clearNetwork() {
    network = null;
  }

  @Override
  @Nonnull
  public ConduitRefinedStorageNode getNode() {
    World world = getBundle().getBundleworld();
    BlockPos pos = getBundle().getLocation();
    if (world.isRemote) {
      if (clientSideNode == null) {
        clientSideNode = new ConduitRefinedStorageNode(this);
      }

      return clientSideNode;
    }

    INetworkNodeManager manager = RSHelper.API.getNetworkNodeManager(world);

    ConduitRefinedStorageNode node = (ConduitRefinedStorageNode) manager.getNode(pos);

    if (node == null || !node.getId().equals(ConduitRefinedStorageNode.ID)) {
      manager.setNode(pos, node = new ConduitRefinedStorageNode(this));
      manager.markForSaving();
    }

    return node;
  }

  @Override
  public void onAfterRemovedFromBundle() {
    super.onAfterRemovedFromBundle();
    BlockPos pos = getBundle().getLocation();

    INetworkNodeManager manager = RSHelper.API.getNetworkNodeManager(getBundle().getBundleworld());

    INetworkNode node = manager.getNode(pos);

    manager.removeNode(pos);
    manager.markForSaving();

    if (node != null && node.getNetwork() != null) {
      node.getNetwork().getNodeGraph().rebuild();
    }

  }

  @Override
  public boolean onBlockActivated(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull RaytraceResult res, @Nonnull List<RaytraceResult> all) {
    if (ToolUtil.isToolEquipped(player, hand)) {
      if (!getBundle().getEntity().getWorld().isRemote) {
        if (res != null && res.component != null) {
          EnumFacing connDir = res.component.dir;
          EnumFacing faceHit = res.movingObjectPosition.sideHit;
          if (connDir == null || connDir == faceHit) {
            if (getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
              setConnectionMode(faceHit, ConnectionMode.IN_OUT);
              return true;
            }
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
    }
    return false;
  }

  @Override
  public void onAddedToBundle() {
    super.onAddedToBundle();

    World world = getBundle().getBundleworld();

    if (!world.isRemote) {
      RSHelper.API.discoverNode(world, getBundle().getLocation());
    }
  }

  @Override
  public void connectionsChanged() {
    super.connectionsChanged();
    getNode().onConduitConnectionChange();
  }

  @Override
  public @Nonnull
  ConnectionMode getNextConnectionMode(@Nonnull EnumFacing dir) {
    ConnectionMode mode = getConnectionMode(dir);
    mode = mode == ConnectionMode.IN_OUT ? ConnectionMode.DISABLED : ConnectionMode.IN_OUT;
    return mode;
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);

    for (Entry<EnumFacing, ItemStack> entry : upgrades.entrySet()) {
      if (entry.getValue() != null) {
        ItemStack up = entry.getValue();
        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("upgrades." + entry.getKey().name(), itemRoot);
      }
    }

    for (Entry<EnumFacing, IFilter> entry : inputFilters.entrySet()) {
      if (entry.getValue() != null) {
        IFilter f = entry.getValue();
        if (!isDefault(f)) {
          NBTTagCompound itemRoot = new NBTTagCompound();
          FilterRegistry.writeFilterToNbt(f, itemRoot);
          nbtRoot.setTag("inFilts." + entry.getKey().name(), itemRoot);
        }
      }
    }

    for (Entry<EnumFacing, IFilter> entry : outputFilters.entrySet()) {
      if (entry.getValue() != null) {
        IFilter f = entry.getValue();
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
        IFilter filter = getInputFilter(entry.getKey());
        FilterRegistry.writeFilterToStack(filter, up);

        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("inputFilterUpgrades." + entry.getKey().name(), itemRoot);
      }
    }

    for (Entry<EnumFacing, ItemStack> entry : outputFilterUpgrades.entrySet()) {
      if (entry.getValue() != null) {
        ItemStack up = entry.getValue();
        IFilter filter = getOutputFilter(entry.getKey());
        FilterRegistry.writeFilterToStack(filter, up);

        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("outputFilterUpgrades." + entry.getKey().name(), itemRoot);
      }

    }
  }

  private boolean isDefault(IFilter f) {
    if (f instanceof ItemFilter) {
      return ((ItemFilter) f).isDefault();
    }
    return false;
  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);

    for (EnumFacing dir : EnumFacing.VALUES) {
      String key = "upgrades." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound upTag = (NBTTagCompound) nbtRoot.getTag(key);
        ItemStack ups = new ItemStack(upTag);
        upgrades.put(dir, ups);
      }

      key = "inFilts." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound filterTag = (NBTTagCompound) nbtRoot.getTag(key);
        IFilter filter = FilterRegistry.loadFilterFromNbt(filterTag);
        inputFilters.put(dir, filter);
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
        IFilter filter = FilterRegistry.loadFilterFromNbt(filterTag);
        outputFilters.put(dir, filter);
      }
    }
  }

  // ---------------------------------------------------------
  // TEXTURES
  // ---------------------------------------------------------

  @Override
  @Nonnull
  public TextureAtlasSprite getTextureForState(@Nonnull CollidableComponent component) {
    if (component.dir == null) {
      return ICONS.get(ICON_CORE_KEY).get(TextureAtlasSprite.class);
    }
    return ICONS.get(ICON_KEY).get(TextureAtlasSprite.class);
  }

  @Override
  public TextureAtlasSprite getTransmitionTextureForState(@Nonnull CollidableComponent component) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Vector4f getTransmitionTextureColorForState(@Nonnull CollidableComponent component) {
    return null;
  }

  // Upgrade Capability

  @Nonnull
  @Override
  public ItemStack getUpgradeStack(int param1) {
    return upgrades.get(EnumFacing.getFront(param1));
  }

  @Override
  public void setUpgradeStack(int param1, @Nonnull ItemStack stack) {
    upgrades.put(EnumFacing.getFront(param1), stack);
  }

  // Filter Capability
  @Override
  public IFilter getFilter(int filterId, int param1) {
    if (filterId == FilterGuiUtil.INDEX_INPUT_ITEM) {
      return getInputFilter(EnumFacing.getFront(param1));
    } else if (filterId == FilterGuiUtil.INDEX_OUTPUT_ITEM) {
      return getOutputFilter(EnumFacing.getFront(param1));
    }
    return null;
  }

  @Override
  public void setFilter(int filterId, int param1, @Nonnull IFilter filter) {
    if (filterId == FilterGuiUtil.INDEX_INPUT_ITEM) {
      setInputFilter(EnumFacing.getFront(param1), filter);
    } else if (filterId == FilterGuiUtil.INDEX_OUTPUT_ITEM) {
      setOutputFilter(EnumFacing.getFront(param1), filter);
    }
  }

  @Override
  @Nullable
  public IItemHandler getInventoryForSnapshot(int filterId, int param1) {
    //    ItemConduitNetwork icn = getNetwork();
    //    if (icn != null) {
    //      return icn.getInventory(this, EnumFacing.getFront(param1)).getInventory();
    //    }
    return null;
  }

  @Override
  @Nonnull
  public ItemStack getFilterStack(int filterIndex, int param1) {
    if (filterIndex == FilterGuiUtil.INDEX_INPUT_ITEM) {
      return getInputFilterUpgrade(EnumFacing.getFront(param1));
    } else if (filterIndex == FilterGuiUtil.INDEX_OUTPUT_ITEM) {
      return getOutputFilterUpgrade(EnumFacing.getFront(param1));
    }
    return ItemStack.EMPTY;
  }

  @Override
  public void setFilterStack(int filterIndex, int param1, @Nonnull ItemStack stack) {
    if (filterIndex == FilterGuiUtil.INDEX_INPUT_ITEM) {
      setInputFilterUpgrade(EnumFacing.getFront(param1), stack);
    } else if (filterIndex == FilterGuiUtil.INDEX_OUTPUT_ITEM) {
      setOutputFilterUpgrade(EnumFacing.getFront(param1), stack);
    }
  }

  @Override
  public void setInputFilter(@Nonnull EnumFacing dir, @Nonnull IFilter filter) {
    inputFilters.put(dir, filter);
    setClientStateDirty();
  }

  @Override
  public void setOutputFilter(@Nonnull EnumFacing dir, @Nonnull IFilter filter) {
    outputFilters.put(dir, filter);
    setClientStateDirty();
  }

  @Override
  public IFilter getInputFilter(@Nonnull EnumFacing dir) {
    return inputFilters.get(dir);
  }

  @Override
  public IFilter getOutputFilter(@Nonnull EnumFacing dir) {
    return outputFilters.get(dir);
  }

  @Override
  public void setInputFilterUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack stack) {
    inputFilterUpgrades.put(dir, stack);
    setInputFilter(dir, FilterRegistry.<IItemFilter>getFilterForUpgrade(stack));
    setClientStateDirty();
  }

  @Override
  public void setOutputFilterUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack stack) {
    outputFilterUpgrades.put(dir, stack);
    setOutputFilter(dir, FilterRegistry.<IItemFilter>getFilterForUpgrade(stack));
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
  public int getInputFilterIndex() {
    return FilterGuiUtil.INDEX_INPUT_ITEM;
  }

  @Override
  public int getOutputFilterIndex() {
    return FilterGuiUtil.INDEX_OUTPUT_ITEM;
  }
}
