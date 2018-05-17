package crazypants.enderio.conduits.refinedstorage.conduit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduits.conduit.AbstractConduit;
import crazypants.enderio.conduits.refinedstorage.RSHelper;
import crazypants.enderio.conduits.refinedstorage.conduit.gui.RefinedStorageSettings;
import crazypants.enderio.conduits.refinedstorage.init.ConduitRefinedStorageObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RefinedStorageConduit extends AbstractConduit implements IRefinedStorageConduit {

  static final Map<String, TextureSupplier> ICONS = new HashMap<>();

  static {
    ICONS.put(ICON_KEY, TextureRegistry.registerTexture(ICON_KEY));
    ICONS.put(ICON_CORE_KEY, TextureRegistry.registerTexture(ICON_CORE_KEY));
  }

  private ConduitRefinedStorageNode clientSideNode;

  protected RefinedStorageConduitNetwork network;

  public RefinedStorageConduit() {
  }

  @Override
  public boolean canConnectToExternal(@Nonnull EnumFacing direction, boolean ignoreConnectionMode) {
    TileEntity te = bundle.getEntity();
    World world = te.getWorld();
    TileEntity test = world.getTileEntity(te.getPos().offset(direction));
    if (test == null) {
      return false;
    }
    if (test.hasCapability(RSHelper.NETWORK_NODE_PROXY_CAPABILITY, direction.getOpposite())) {
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
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == RSHelper.NETWORK_NODE_PROXY_CAPABILITY) {
      return RSHelper.NETWORK_NODE_PROXY_CAPABILITY.cast(this);
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
  public void onBeforeRemovedFromBundle() {
    BlockPos pos = getBundle().getLocation();

    INetworkNodeManager manager = RSHelper.API.getNetworkNodeManager(getBundle().getBundleworld());

    INetworkNode node = manager.getNode(pos);

    manager.removeNode(pos);
    manager.markForSaving();

    if (node != null && node.getNetwork() != null) {
      node.getNetwork().getNodeGraph().rebuild();
    }

    super.onBeforeRemovedFromBundle();
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
  public @Nonnull ConnectionMode getNextConnectionMode(@Nonnull EnumFacing dir) {
    ConnectionMode mode = getConnectionMode(dir);
    mode = mode == ConnectionMode.IN_OUT ? ConnectionMode.DISABLED : ConnectionMode.IN_OUT;
    return mode;
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

}
