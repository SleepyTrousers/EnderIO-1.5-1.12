package crazypants.enderio.conduits.oc.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitNetwork;
import crazypants.enderio.base.conduit.IConduitTexture;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduits.conduit.AbstractConduit;
import crazypants.enderio.conduits.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduits.oc.gui.OCSettings;
import crazypants.enderio.conduits.render.BlockStateWrapperConduitBundle.ConduitCacheKey;
import crazypants.enderio.conduits.render.ConduitTexture;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.conduits.oc.init.ConduitOpenComputersObject.item_opencomputers_conduit;

public class OCConduit extends AbstractConduit implements IOCConduit {

  protected OCConduitNetwork network;

  private final Map<EnumFacing, DyeColor> signalColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);

  private static final @Nonnull IConduitTexture coreTextureA = new ConduitTexture(TextureRegistry.registerTexture("blocks/oc_conduit_core_anim"),
      ConduitTexture.core());
  private static final @Nonnull IConduitTexture longTextureA = new ConduitTexture(TextureRegistry.registerTexture("blocks/oc_conduit_anim"),
      ConduitTexture.arm(0));

  public OCConduit() {
    super();
  }

  public OCConduit(int meta) {
    super();
  }

  @Override
  protected void readTypeSettings(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    setSignalColor(dir, NullHelper.first(DyeColor.values()[dataRoot.getShort("signalColor")], DyeColor.SILVER));
  }

  @Override
  protected void writeTypeSettingsToNbt(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    dataRoot.setShort("signalColor", (short) getSignalColor(dir).ordinal());
  }

  @Override
  public @Nonnull DyeColor getSignalColor(@Nonnull EnumFacing dir) {
    DyeColor res = signalColors.get(dir);
    if (res == null) {
      return DyeColor.SILVER;
    }
    return res;
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
    CollidableComponent cc = new CollidableComponent(IOCConduit.class, bb, keydir, COLOR_CONTROLLER_ID);

    List<CollidableComponent> result = new ArrayList<CollidableComponent>();
    result.addAll(baseCollidables);
    result.add(cc);

    return result;
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
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
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    signalColors.clear();
    byte[] cols = nbtRoot.getByteArray("signalColors");
    if (cols.length == 6) {
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
  public void setSignalColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col) {
    if (signalColors.get(dir) == col) {
      return;
    }
    disconnectNode(dir);
    signalColors.put(dir, col);
    addMissingNodeConnections();
    setClientStateDirty();
  }

  @Override
  @Nonnull
  public Class<? extends IConduit> getBaseConduitType() {
    return IOCConduit.class;
  }

  @Override
  @Nonnull
  public ItemStack createItem() {
    return new ItemStack(item_opencomputers_conduit.getItemNN(), 1, 0);
  }

  @Override
  public AbstractConduitNetwork<?, ?> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(@Nonnull IConduitNetwork<?, ?> network) {
    this.network = (OCConduitNetwork) network;
    addMissingNodeConnections();
    return super.setNetwork(network);
  }

  @Override
  public boolean shouldMirrorTexture() {
    return false;// TODO !enableOCConduitsAnimatedTexture;
  }

  private static String prettyNode(Node o) {
    String at = "";
    Environment host = o.host();
    if (host instanceof TileEntity) {
      BlockPos bc = ((TileEntity) host).getPos();
      at = " at " + bc.getX() + "/" + bc.getY() + "/" + bc.getZ();
    }
    return host.getClass().getName().replaceFirst("^.*\\.", "") + at;
  }

  private static @Nonnull TextFormatting dye2chat(@Nonnull DyeColor dyeColor) {
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
      throw new RuntimeException("Unknown 17th DyeColor " + dyeColor);
    }
  }

  @Override
  public boolean onBlockActivated(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull RaytraceResult res, @Nonnull List<RaytraceResult> all) {
    DyeColor col = DyeColor.getColorFromDye(player.getHeldItem(hand));
    final CollidableComponent component = res.component;
    if (col != null && component != null && component.isDirectional()) {
      setSignalColor(component.getDirection(), col);
      return true;
    } else if (ConduitUtil.isProbeEquipped(player, hand)) {
      // FIXME: This belongs in the probe callback, not here
      if (!player.world.isRemote) {
        BlockPos bc = getBundle().getLocation();
        if (network != null) {
          boolean noconnections = true;
          for (DyeColor color : DyeColor.values()) {
            if (node(color).neighbors().iterator().hasNext()) {
              noconnections = false;
              TextComponentString coltxt = new TextComponentString(color.getLocalisedName());
              coltxt.getStyle().setColor(dye2chat(color));
              TextComponentString chantxt = new TextComponentString("Channel ");
              chantxt.appendSibling(coltxt);
              chantxt.appendText(" at " + bc.getX() + "/" + bc.getY() + "/" + bc.getZ());
              player.sendMessage(chantxt);
              for (Node other : node(color).neighbors()) {
                player.sendMessage(new TextComponentString("  Connected to: " + prettyNode(other)));
              }
            }
          }
          if (noconnections) {
            player.sendMessage(new TextComponentString("No connections at " + bc.getX() + "/" + bc.getY() + "/" + bc.getZ()));
          }
        } else {
          player.sendMessage(new TextComponentString("No network at " + bc.getX() + "/" + bc.getY() + "/" + bc.getZ()));
        }
      }
      return true;
    } else if (ToolUtil.isToolEquipped(player, hand)) {
      if (!getBundle().getEntity().getWorld().isRemote) {
        if (component != null) {
          EnumFacing faceHit = res.movingObjectPosition.sideHit;
          if (component.isCore()) {
            if (getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
              setConnectionMode(faceHit, ConnectionMode.IN_OUT);
              return true;
            }
            return ConduitUtil.connectConduits(this, faceHit);
          } else {
            EnumFacing connDir = component.getDirection();
            if (containsExternalConnection(connDir)) {
              for (RaytraceResult rtr : all) {
                if (rtr != null && rtr.component != null && COLOR_CONTROLLER_ID.equals(rtr.component.data)) {
                  setSignalColor(connDir, DyeColor.getNext(getSignalColor(connDir)));
                  return true;
                }
              }
              setConnectionMode(connDir, getNextConnectionMode(connDir));
            } else if (containsConduitConnection(connDir)) {
              ConduitUtil.disconnectConduits(this, connDir);
              addMissingNodeConnections();
            }
          }
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public void setConnectionMode(@Nonnull EnumFacing dir, @Nonnull ConnectionMode mode) {
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
    if (network != null) {
      World world = getBundle().getBundleworld();
      EnumSet<EnumFacing> conns = getConnections();
      for (DyeColor color : DyeColor.values()) {
        Set<Node> should = new HashSet<Node>();
        for (EnumFacing direction : conns) {
          if (direction != null && getSignalColor(direction) == color) {
            TileEntity te = world.getTileEntity(getBundle().getLocation().offset(direction));
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

  private void disconnectNode(@Nonnull EnumFacing direction) {
    World world = getBundle().getBundleworld();
    TileEntity te = world.getTileEntity(getBundle().getLocation().offset(direction));
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
   * This will disconnect a node from our network unless it has another connection to our network. This only works if all the node's blocks are adjacent to us.
   * Connecting 2 ManagedEnvironments at different locations won't work well.
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
        if (direction != null && getSignalColor(direction) == color) {
          TileEntity te = world.getTileEntity(getBundle().getLocation().offset(direction));
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
        if (direction != null && !stayConnected) {
          boolean checkThisSide = true;
          if (otherHost instanceof SidedEnvironment) {
            checkThisSide = ((SidedEnvironment) otherHost).sidedNode(direction) != null;
          }
          if (checkThisSide) {
            BlockPos otherPos = otherTe.getPos();
            BlockPos connTo = otherPos.offset(direction);
            if (!connTo.equals(getBundle().getLocation())) {
              TileEntity connToTe = world.getTileEntity(connTo);
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
      if (dir != null && getConnectionMode(dir) != ConnectionMode.DISABLED) {
        cons.add(dir);
      }
    }
    return cons;
  }

  @Override
  @Nonnull
  public ConnectionMode getNextConnectionMode(@Nonnull EnumFacing dir) {
    ConnectionMode mode = getConnectionMode(dir);
    mode = mode == ConnectionMode.IN_OUT ? ConnectionMode.DISABLED : ConnectionMode.IN_OUT;
    return mode;
  }

  @Override
  @Nonnull
  public ConnectionMode getPreviousConnectionMode(@Nonnull EnumFacing dir) {
    return getNextConnectionMode(dir);
  }

  @Override
  public boolean canConnectToExternal(@Nonnull EnumFacing direction, boolean ignoreConnectionMode) {
    TileEntity te = getBundle().getBundleworld().getTileEntity(getBundle().getLocation().offset(direction));
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
  @Method(modid = "opencomputersapi|network")
  public Node node() {
    return network != null ? network.node(DyeColor.SILVER) : null;
  }

  @Method(modid = "opencomputersapi|network")
  public Node node(DyeColor subnet) {
    return network != null ? network.node(subnet) : null;
  }

  @Override
  @Method(modid = "opencomputersapi|network")
  public void onConnect(Node node) {
  }

  @Override
  @Method(modid = "opencomputersapi|network")
  public void onDisconnect(Node node) {
  }

  @Override
  @Method(modid = "opencomputersapi|network")
  public void onMessage(Message message) {
  }

  @Override
  @Method(modid = "opencomputersapi|network")
  public Node sidedNode(EnumFacing side) {
    return side != null && getConnections().contains(side) ? node(getSignalColor(side)) : null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Method(modid = "opencomputersapi|network")
  public boolean canConnect(EnumFacing side) {
    return side != null && getConnections().contains(side);
  }

  @Override
  @Nonnull
  public IConduitTexture getTextureForState(@Nonnull CollidableComponent component) {
    // TODO
    // if (Config.enableOCConduitsAnimatedTexture) {
    if (component.isCore()) {
      return coreTextureA;
    } else {
      return longTextureA;
    }
    // } else {
    // if (component.dir == null) {
    // return coreTextureS.get(TextureAtlasSprite.class);
    // } else {
    // return longTextureS.get(TextureAtlasSprite.class);
    // }
    // }
  }

  @Override
  public @Nullable IConduitTexture getTransmitionTextureForState(@Nonnull CollidableComponent component) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable Vector4f getTransmitionTextureColorForState(@Nonnull CollidableComponent component) {
    return null;
  }

  @Override
  public void hashCodeForModelCaching(ConduitCacheKey hashCodes) {
    super.hashCodeForModelCaching(hashCodes);
    hashCodes.addEnum(signalColors);
  }

  @Override
  @Nonnull
  public IConduitNetwork<?, ?> createNetworkForType() {
    return new OCConduitNetwork();
  }

  @SideOnly(Side.CLIENT)
  @Override
  @Nonnull
  public ITabPanel createGuiPanel(@Nonnull IGuiExternalConnection gui, @Nonnull IClientConduit con) {
    return new OCSettings(gui, con);
  }

  @Override
  public boolean updateGuiPanel(@Nonnull ITabPanel panel) {
    if (panel instanceof OCSettings) {
      return ((OCSettings) panel).updateConduit(this);
    }
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public int getGuiPanelTabOrder() {
    return 5;
  }

  @Override
  public void clearNetwork() {
    this.network = null;
  }

}
