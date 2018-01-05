package crazypants.enderio.conduit.me;

import static crazypants.enderio.base.ModObject.itemMEConduit;

import java.util.EnumSet;
import java.util.List;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.vecmath.Vector4f;

import appeng.api.AEApi;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.util.AEPartLocation;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.gui.MESettings;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MEConduit extends AbstractConduit implements IMEConduit {

  protected MEConduitNetwork network;
  protected MEConduitGrid grid;

  public static TextureRegistry.TextureSupplier coreTextureN = TextureRegistry.registerTexture("blocks/meConduitCore");
  public static TextureRegistry.TextureSupplier coreTextureD = TextureRegistry.registerTexture("blocks/meConduitCoreDense");
  public static TextureRegistry.TextureSupplier longTextureN = TextureRegistry.registerTexture("blocks/meConduit");
  public static TextureRegistry.TextureSupplier longTextureD = TextureRegistry.registerTexture("blocks/meConduitDense");

  private boolean isDense;
  private int playerID = -1;

  public MEConduit() {
    this(0);
  }

  public MEConduit(int itemDamage) {
    isDense = itemDamage == 1;
  }

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
  }

  public static int getDamageForState(boolean isDense) {
    return isDense ? 1 : 0;
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IMEConduit.class;
  }

  @Override
  public ItemStack createItem() {
    return new ItemStack(itemMEConduit.getItem(), 1, getDamageForState(isDense));
  }

  @Override
  public AbstractConduitNetwork<?, ?> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(AbstractConduitNetwork<?, ?> network) {
    this.network = (MEConduitNetwork) network;
    return true;
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    nbtRoot.setBoolean("isDense", isDense);
    nbtRoot.setInteger("playerID", playerID);
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot, short nbtVersion) {
    super.readFromNBT(nbtRoot, nbtVersion);
    isDense = nbtRoot.getBoolean("isDense");
    if(nbtRoot.hasKey("playerID")) {
      playerID = nbtRoot.getInteger("playerID");
    } else {
      playerID = -1;
    }
  }

  public void setPlayerID(int playerID) {
    this.playerID = playerID;
  }

  @Override
  public int getChannelsInUse() {
    int channelsInUse = 0;
    IGridNode node = getNode();
    if(node != null) {
      for (IGridConnection gc : node.getConnections()) {
        channelsInUse = Math.max(channelsInUse, gc.getUsedChannels());
      }
    }
    return channelsInUse;
  }

  @Override
  @Method(modid = "appliedenergistics2")
  public boolean canConnectToExternal(EnumFacing dir, boolean ignoreDisabled) {
    World world = getBundle().getBundleworld();
    BlockCoord pos = getLocation();
    TileEntity te = BlockEnder.getAnyTileEntitySafe(world, pos.getLocation(dir).getBlockPos());

    if(te instanceof TileConduitBundle) {
      return false;
    }

    // because the AE2 API doesn't allow an easy query like "which side can connect to an ME cable" it needs this mess
    if(te instanceof IPartHost) {
      IPart part = ((IPartHost) te).getPart(dir.getOpposite());
      if(part == null) {
        part = ((IPartHost) te).getPart(AEPartLocation.INTERNAL);
        return part != null;
      }
      if(part.getExternalFacingNode() != null) {
        return true;
      }
      String name = part.getClass().getSimpleName();
      return "PartP2PTunnelME".equals(name) || "PartQuartzFiber".equals(name) ||
              "PartToggleBus".equals(name) || "PartInvertedToggleBus".equals(name);
    } else if(te instanceof IGridHost) {
      IGridNode node = ((IGridHost) te).getGridNode(AEPartLocation.fromFacing(dir.getOpposite()));
      if(node == null) {
        node = ((IGridHost) te).getGridNode(AEPartLocation.INTERNAL);
      }
      if(node != null) {
        return node.getGridBlock().getConnectableSides().contains(dir.getOpposite());
      }
    }
    return false;
  }

  @Override
  public TextureAtlasSprite getTextureForState(CollidableComponent component) {
    if(component.dir == null) {
      return (isDense ? coreTextureD : coreTextureN).get(TextureAtlasSprite.class);
    } else {
      return (isDense ? longTextureD : longTextureN).get(TextureAtlasSprite.class);
    }
  }

  @Override
  public TextureAtlasSprite getTransmitionTextureForState(CollidableComponent component) {
    return null;
  }

  @Override
  public Vector4f getTransmitionTextureColorForState(CollidableComponent component) {
    return null;
  }

  @Override
  @Method(modid = "appliedenergistics2")
  public void updateEntity(World world) {
    if(grid == null) {
      grid = new MEConduitGrid(this);
    }

    if(getNode() == null && !world.isRemote) {
      IGridNode node = AEApi.instance().createGridNode(grid);
      if(node != null) {
        node.setPlayerID(playerID);
        getBundle().setGridNode(node);
        getNode().updateState();
      }
    }

    super.updateEntity(world);
  }

  @Override
  public ConnectionMode getNextConnectionMode(EnumFacing dir) {
    ConnectionMode mode = getConnectionMode(dir);
    mode = mode == ConnectionMode.IN_OUT ? ConnectionMode.DISABLED : ConnectionMode.IN_OUT;
    return mode;
  }

  @Override
  public ConnectionMode getPreviousConnectionMode(EnumFacing dir) {
    return getNextConnectionMode(dir);
  }

  @Override
  public boolean canConnectToConduit(EnumFacing direction, IConduit conduit) {
    if(!super.canConnectToConduit(direction, conduit)) {
      return false;
    }
    return conduit instanceof IMEConduit;
  }

  @Override
  @Method(modid = "appliedenergistics2")
  public void connectionsChanged() {
    super.connectionsChanged();
    BlockCoord loc = getLocation();
    if(loc != null) {
      onNodeChanged(loc);
      IGridNode node = getNode();
      if(node != null) {
        node.updateState();
        World world = node.getWorld();
        if (!world.isRemote && world instanceof WorldServer)
          ((WorldServer) world).getPlayerChunkMap().markBlockForUpdate(loc.getBlockPos());
      }
    }
  }

  @Override
  public boolean onBlockActivated(EntityPlayer player, EnumHand hand, RaytraceResult res, List<RaytraceResult> all) {
    if(ToolUtil.isToolEquipped(player, hand)) {
      if(!getBundle().getEntity().getWorld().isRemote) {
        if(res != null && res.component != null) {
          EnumFacing connDir = res.component.dir;
          EnumFacing faceHit = res.movingObjectPosition.sideHit;
          if(connDir == null || connDir == faceHit) {
            if(getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
              setConnectionMode(faceHit, ConnectionMode.IN_OUT);
              return true;
            }
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
    }
    return false;
  }

  @Method(modid = "appliedenergistics2")
  private void onNodeChanged(BlockCoord location) {
    World world = getBundle().getBundleworld();
    for (EnumFacing dir : EnumFacing.VALUES) {
      TileEntity te = location.getLocation(dir).getTileEntity(world);
      if(te != null && te instanceof IGridHost && !(te instanceof IConduitBundle)) {
        IGridNode node = ((IGridHost) te).getGridNode(AEPartLocation.INTERNAL);
        if(node == null) {
          node = ((IGridHost) te).getGridNode(AEPartLocation.fromFacing(dir.getOpposite()));
        }
        if(node != null) {
          node.updateState();
        }
      }
    }
  }

  @Override
  public void onAddedToBundle() {
    for (EnumFacing dir : EnumFacing.VALUES) {
      TileEntity te = getLocation().getLocation(dir).getTileEntity(getBundle().getBundleworld());
      if(te instanceof TileConduitBundle) {
        IMEConduit cond = ((TileConduitBundle) te).getConduit(IMEConduit.class);
        if(cond != null) {
          cond.setConnectionMode(dir.getOpposite(), ConnectionMode.IN_OUT);
          ConduitUtil.joinConduits(cond, dir.getOpposite());
        }
      }
    }
  }

  @Override
  @Method(modid = "appliedenergistics2")
  public void onRemovedFromBundle() {
    super.onRemovedFromBundle();
    getNode().destroy();
    getBundle().setGridNode(null);
  }

  @Override
  @Method(modid = "appliedenergistics2")
  public void onChunkUnload(World world) {
    super.onChunkUnload(world);
    if(getNode() != null) {
      getNode().destroy();
      getBundle().setGridNode(null);
    }
  }

  @Override
  public MEConduitGrid getGrid() {
    return grid;
  }

  @Method(modid = "appliedenergistics2")
  private IGridNode getNode() {
    return getBundle().getGridNode(null);
  }

  @Override
  public EnumSet<EnumFacing> getConnections() {
    EnumSet<EnumFacing> cons = EnumSet.noneOf(EnumFacing.class);
    cons.addAll(getConduitConnections());
    for(EnumFacing dir : getExternalConnections()) {
      if(getConnectionMode(dir) != ConnectionMode.DISABLED) {
        cons.add(dir);
      }
    }
    return cons;
  }

  @Override
  public boolean isDense() {
    return isDense;
  }

  @Override
  public MEConduitNetwork createNetworkForType() {
    return new MEConduitNetwork();
  }

  @SideOnly(Side.CLIENT)
  @Override
  public ITabPanel createPanelForConduit(GuiExternalConnection gui, IConduit con) {
    return new MESettings(gui, con);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public int getTabOrderForConduit(IConduit con) {
    return 4;
  }

}
