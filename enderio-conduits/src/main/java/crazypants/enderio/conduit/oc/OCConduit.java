package crazypants.enderio.conduit.oc;

import static crazypants.enderio.base.ModObject.itemOCConduit;
import static crazypants.enderio.base.config.Config.enableOCConduitsAnimatedTexture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.base.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.IConduitComponent;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.gui.OCSettings;
import crazypants.enderio.conduit.render.BlockStateWrapperConduitBundle;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OCConduit extends AbstractConduit implements IOCConduit, IConduitComponent {

  protected OCConduitNetwork network;

  private final Map<EnumFacing, DyeColor> signalColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);

  private static final TextureSupplier coreTextureS = TextureRegistry.registerTexture("blocks/ocConduitCore");
  private static final TextureSupplier coreTextureA = TextureRegistry.registerTexture("blocks/ocConduitCoreAnim");
  private static final TextureSupplier longTextureS = TextureRegistry.registerTexture("blocks/ocConduit");
  private static final TextureSupplier longTextureA = TextureRegistry.registerTexture("blocks/ocConduitAnim");

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
  }

  public OCConduit() {
    super();
  }

  public OCConduit(int meta) {
    super();
  }

  @Override
  protected void readTypeSettings(EnumFacing dir, NBTTagCompound dataRoot) {
    setSignalColor(dir, DyeColor.values()[dataRoot.getShort("signalColor")]);
  }

  @Override
  protected void writeTypeSettingsToNbt(EnumFacing dir, NBTTagCompound dataRoot) {
    dataRoot.setShort("signalColor", (short) getSignalColor(dir).ordinal());
  }

  @Override
  public DyeColor getSignalColor(EnumFacing dir) {
    DyeColor res = signalColors.get(dir);
    if (res == null) {
      return DyeColor.SILVER;
    }
    return res;
  }

  @Override
  public Collection<CollidableComponent> createCollidables(CacheKey key) {
    Collection<CollidableComponent> baseCollidables = super.createCollidables(key);
    if (key.dir == null) {
      return baseCollidables;
    }

    BoundingBox bb = ConduitGeometryUtil.instance.createBoundsForConnectionController(key.dir, key.offset);
    CollidableComponent cc = new CollidableComponent(IOCConduit.class, bb, key.dir, COLOR_CONTROLLER_ID);

    List<CollidableComponent> result = new ArrayList<CollidableComponent>();
    result.addAll(baseCollidables);
    result.add(cc);

    return result;
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    if (signalColors.size() >= 0) {
      byte[] modes = new byte[6];
      int i = 0;
      for (EnumFacing dir : EnumFacing.values()) {
        DyeColor col = signalColors.get(dir);
        if (col != null) {
          modes[i] = (byte) col.ordinal();
        } else {
          modes[i] = -1;
        }
        i++;
      }
      nbtRoot.setByteArray("signalColors", modes);
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot, short nbtVersion) {
    super.readFromNBT(nbtRoot, nbtVersion);
    signalColors.clear();
    byte[] cols = nbtRoot.getByteArray("signalColors");
    if (cols != null && cols.length == 6) {
      int i = 0;
      for (EnumFacing dir : EnumFacing.values()) {
        if (cols[i] >= 0) {
          signalColors.put(dir, DyeColor.values()[cols[i]]);
        }
        i++;
      }
    }
  }

  @Override
  public void setSignalColor(EnumFacing dir, DyeColor col) {
    if (signalColors.get(dir) == col) {
      return;
    }
    disconnectNode(dir);
    signalColors.put(dir, col);
    addMissingNodeConnections();
    setClientStateDirty();
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IOCConduit.class;
  }

  @Override
  public ItemStack createItem() {
    return new ItemStack(itemOCConduit.getItem(), 1, 0);
  }

  @Override
  public AbstractConduitNetwork<?, ?> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(AbstractConduitNetwork<?, ?> network) {
    if (network == null) {
      for (EnumFacing dir : getExternalConnections()) {
        disconnectNode(dir);
      }
    }
    this.network = (OCConduitNetwork) network;
    addMissingNodeConnections();
    return true;
  }

  @Override
  public boolean shouldMirrorTexture() {
    return !enableOCConduitsAnimatedTexture;
  }

  private static String prettyNode(Node o) {
    String at = "";
    Environment host = o.host();
    if (host instanceof TileEntity) {
      BlockCoord bc = new BlockCoord((TileEntity) host);
      at = " at " + bc.x + "/" + bc.y + "/" + bc.z;
    }
    return host.getClass().getName().replaceFirst("^.*\\.", "") + at;
  }

  private static TextFormatting dye2chat(DyeColor dyeColor) {
    switch (dyeColor) {
    case BLACK:
      return TextFormatting.BLACK;
    case BLUE:
      return TextFormatting.DARK_BLUE;
    case BROWN:
      return TextFormatting.DARK_RED;
    case CYAN:
      return TextFormatting.DARK_AQUA;
    // return TextFormatting.AQUA;
    case GRAY:
      return TextFormatting.DARK_GRAY;
    case GREEN:
      return TextFormatting.DARK_GREEN;
    case LIGHT_BLUE:
      return TextFormatting.BLUE;
    case LIME:
      return TextFormatting.GREEN;
    case MAGENTA:
      return TextFormatting.LIGHT_PURPLE;
    case ORANGE:
      return TextFormatting.GOLD;
    case PINK:
      return TextFormatting.LIGHT_PURPLE;
    case PURPLE:
      return TextFormatting.DARK_PURPLE;
    case RED:
      return TextFormatting.RED;
    case SILVER:
      return TextFormatting.GRAY;
    case WHITE:
      return TextFormatting.WHITE;
    case YELLOW:
      return TextFormatting.YELLOW;
    default:
      return null;
    }
  }

  @Override
  public boolean onBlockActivated(EntityPlayer player, EnumHand hand, RaytraceResult res, List<RaytraceResult> all) {
    DyeColor col = DyeColor.getColorFromDye(player.getHeldItem(hand));
    if (col != null && res.component != null) {
      setSignalColor(res.component.dir, col);
      return true;
    } else if (ConduitUtil.isProbeEquipped(player, hand)) {
      if (!player.world.isRemote) {
        BlockCoord bc = getLocation();
        if (network != null) {
          boolean noconnections = true;
          for (DyeColor color : DyeColor.values()) {
            if (node(color).neighbors().iterator().hasNext()) {
              noconnections = false;
              TextComponentString coltxt = new TextComponentString(color.getLocalisedName());
              coltxt.getStyle().setColor(dye2chat(color));
              TextComponentString chantxt = new TextComponentString("Channel ");
              chantxt.appendSibling(coltxt);
              chantxt.appendText(" at " + bc.x + "/" + bc.y + "/" + bc.z);
              player.addChatMessage(chantxt);
              for (Node other : node(color).neighbors()) {
                player.addChatMessage(new TextComponentString("  Connected to: " + prettyNode(other)));
              }
            }
          }
          if (noconnections) {
            player.addChatMessage(new TextComponentString("No connections at " + bc.x + "/" + bc.y + "/" + bc.z));
          }
        } else {
          player.addChatMessage(new TextComponentString("No network at " + bc.x + "/" + bc.y + "/" + bc.z));
        }
      }
      return true;
    } else if (ToolUtil.isToolEquipped(player, hand)) {
      if (!getBundle().getEntity().getWorld().isRemote) {
        if (res != null && res.component != null) {
          EnumFacing connDir = res.component.dir;
          EnumFacing faceHit = res.movingObjectPosition.sideHit;
          if (all != null && containsExternalConnection(connDir)) {
            for (RaytraceResult rtr : all) {
              if (rtr != null && rtr.component != null && COLOR_CONTROLLER_ID.equals(rtr.component.data)) {
                setSignalColor(connDir, DyeColor.getNext(getSignalColor(connDir)));
                return true;
              }
            }
          }
          if (connDir == null || connDir == faceHit) {
            if (getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
              setConnectionMode(faceHit, ConnectionMode.IN_OUT);
              return true;
            }
            return ConduitUtil.joinConduits(this, faceHit);
          } else if (externalConnections.contains(connDir)) {
            setConnectionMode(connDir, getNextConnectionMode(connDir));
            return true;
          } else if (containsConduitConnection(connDir)) {
            ConduitUtil.disconectConduits(this, connDir);
            addMissingNodeConnections();
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public void setConnectionMode(EnumFacing dir, ConnectionMode mode) {
    if (mode == ConnectionMode.DISABLED) {
      disconnectNode(dir);
    }
    super.setConnectionMode(dir, mode);
  }

  @Override
  public void connectionsChanged() {
    super.connectionsChanged();
    addMissingNodeConnections();
  }

  private void addMissingNodeConnections() {
    BlockCoord loc = getLocation();
    if (loc != null && network != null) {
      World world = getBundle().getBundleworld();
      EnumSet<EnumFacing> conns = getConnections();
      for (DyeColor color : DyeColor.values()) {
        Set<Node> should = new HashSet<Node>();
        for (EnumFacing direction : conns) {
          if (getSignalColor(direction) == color) {
            TileEntity te = getLocation().getLocation(direction).getTileEntity(world);
            Node other = null;
            if (te instanceof SidedEnvironment) {
              other = ((SidedEnvironment) te).sidedNode(direction.getOpposite());
            } else if (te instanceof Environment) {
              other = ((Environment) te).node();
            } else {
              // We have a connection to something we cannot connect to. Should
              // not happen. Poke debugger in >here< if it does!
            }
            if (other != null && other != node(color)) {
              should.add(other);
            }
          }
        }
        for (Node other : should) {
          if (!node(color).isNeighborOf(other)) {
            node(color).connect(other);
          }
        }
      }
    }
  }

  private void disconnectNode(EnumFacing direction) {
    World world = getBundle().getBundleworld();
    TileEntity te = getLocation().getLocation(direction).getTileEntity(world);
    Node other = null;
    if (te instanceof SidedEnvironment) {
      other = ((SidedEnvironment) te).sidedNode(direction.getOpposite());
    } else if (te instanceof Environment) {
      other = ((Environment) te).node();
    }
    if (other != null) {
      disconnectNode(other, getSignalColor(direction));
    }
  }

  /**
   * This will disconnect a node from our network unless it has another
   * connection to our network. This only works if all the node's blocks are
   * adjacent to us. Connecting 2 ManagedEnvironments at different locations
   * won't work well.
   * 
   * @param other
   *          The node to disconnect from us
   */
  private void disconnectNode(Node other, DyeColor color) {

    // Um. No.
    if (other == node(color)) {
      return;
    }
    // Two conduit networks never connect to each other. They join instead.
    Environment otherHost = other.host();
    if (otherHost instanceof OCConduitNetwork && otherHost != network) {
      node(color).disconnect(other);
      return;
    }

    World world = getBundle().getBundleworld();
    EnumSet<EnumFacing> conns = getConnections();
    // we need to check if that node has another way of connecting to our
    // network. First find out which of our neighbor(s) it belongs to. May
    // be just one, may be many.
    List<TileEntity> toCheck = new ArrayList<TileEntity>();
    if (otherHost instanceof TileEntity) {
      TileEntity otherTe = (TileEntity) otherHost;
      toCheck.add(otherTe);
    } else {
      for (EnumFacing direction : conns) {
        if (getSignalColor(direction) == color) {
          TileEntity te = getLocation().getLocation(direction).getTileEntity(world);
          Node other2 = null;
          if (te instanceof SidedEnvironment) {
            other2 = ((SidedEnvironment) te).sidedNode(direction.getOpposite());
          } else if (te instanceof Environment) {
            other2 = ((Environment) te).node();
          }
          if (other2 == other) {
            toCheck.add(te);
          }
        }
      }
    }
    // Then see if it still has a connection to our node other than through us.
    boolean stayConnected = false;
    for (TileEntity otherTe : toCheck) {
      for (EnumFacing direction : EnumFacing.values()) {
        if (!stayConnected) {
          boolean checkThisSide = true;
          if (otherHost instanceof SidedEnvironment) {
            checkThisSide = ((SidedEnvironment) otherHost).sidedNode(direction) != null;
          }
          if (checkThisSide) {
            BlockCoord otherPos = new BlockCoord(otherTe);
            BlockCoord connTo = otherPos.getLocation(direction);
            if (!connTo.equals(getLocation())) {
              TileEntity connToTe = connTo.getTileEntity(world);
              if (connToTe instanceof SidedEnvironment) {
                stayConnected = ((SidedEnvironment) connToTe).sidedNode(direction.getOpposite()) == node(color);
              } else if (connToTe instanceof Environment) {
                stayConnected = ((Environment) connToTe).node() == node(color);
              }
            }
          }
        }
      }
    }
    if (!stayConnected) {
      node(color).disconnect(other);
    }
  }

  public EnumSet<EnumFacing> getConnections() {
    EnumSet<EnumFacing> cons = EnumSet.noneOf(EnumFacing.class);
    cons.addAll(getConduitConnections());
    for (EnumFacing dir : getExternalConnections()) {
      if (getConnectionMode(dir) != ConnectionMode.DISABLED) {
        cons.add(dir);
      }
    }
    return cons;
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
  public boolean canConnectToExternal(EnumFacing direction, boolean ignoreConnectionMode) {
    TileEntity te = getLocation().getLocation(direction).getTileEntity(getBundle().getBundleworld());
    if (te instanceof SidedEnvironment) {
      if (getBundle().getBundleworld().isRemote) {
        return ((SidedEnvironment) te).canConnect(direction.getOpposite());
      } else {
        return ((SidedEnvironment) te).sidedNode(direction.getOpposite()) != null;
      }
    } else if (te instanceof Environment) {
      return true;
    }
    return false;
  }

  @Override
  @Method(modid = "OpenComputersAPI|Network")
  public Node node() {
    return network != null ? network.node(DyeColor.SILVER) : null;
  }

  @Method(modid = "OpenComputersAPI|Network")
  public Node node(DyeColor subnet) {
    return network != null ? network.node(subnet) : null;
  }

  @Override
  @Method(modid = "OpenComputersAPI|Network")
  public void onConnect(Node node) {
  }

  @Override
  @Method(modid = "OpenComputersAPI|Network")
  public void onDisconnect(Node node) {
  }

  @Override
  @Method(modid = "OpenComputersAPI|Network")
  public void onMessage(Message message) {
  }

  @Override
  @Method(modid = "OpenComputersAPI|Network")
  public Node sidedNode(EnumFacing side) {
    return getConnections().contains(side) ? node(getSignalColor(side)) : null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Method(modid = "OpenComputersAPI|Network")
  public boolean canConnect(EnumFacing side) {
    return getConnections().contains(side);
  }

  @Override
  public TextureAtlasSprite getTextureForState(CollidableComponent component) {
    if (Config.enableOCConduitsAnimatedTexture) {
      if (component.dir == null) {
        return coreTextureA.get(TextureAtlasSprite.class);
      } else {
        return longTextureA.get(TextureAtlasSprite.class);
      }
    } else {
      if (component.dir == null) {
        return coreTextureS.get(TextureAtlasSprite.class);
      } else {
        return longTextureS.get(TextureAtlasSprite.class);
      }
    }
  }

  @Override
  public TextureAtlasSprite getTransmitionTextureForState(CollidableComponent component) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Vector4f getTransmitionTextureColorForState(CollidableComponent component) {
    return null;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void hashCodeForModelCaching(IBlockStateWrapper wrapper, BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
    super.hashCodeForModelCaching(wrapper, hashCodes);
    hashCodes.addEnum(signalColors);
  }

  @Override
  public OCConduitNetwork createNetworkForType() {
    return new OCConduitNetwork();
  }

  @SideOnly(Side.CLIENT)
  @Override
  public ITabPanel createPanelForConduit(GuiExternalConnection gui, IConduit con) {
    return new OCSettings(gui, con);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public int getTabOrderForConduit(IConduit con) {
    return 5;
  }

}
