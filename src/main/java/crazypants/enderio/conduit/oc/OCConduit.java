package crazypants.enderio.conduit.oc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import appeng.api.networking.IGridNode;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.Log;

import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import li.cil.oc.api.network.Visibility;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.RaytraceResult;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.conduit.me.IMEConduit;
import crazypants.enderio.conduit.me.MEConduitNetwork;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.conduit.redstone.Signal;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.PacketConduitProbe;
import crazypants.enderio.tool.ToolUtil;

public class OCConduit extends AbstractConduit implements IOCConduit {

  protected OCConduitNetwork network;

  private Map<ForgeDirection, DyeColor> signalColors = new HashMap<ForgeDirection, DyeColor>();

  public static IIcon[] coreTextures;
  public static IIcon[] longTextures;

  public OCConduit() {
    super();
  }

  public OCConduit(int meta) {
    super();
  }

  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IIconRegister register) {
        coreTextures = new IIcon[2];
        longTextures = new IIcon[2];

        coreTextures[0] = register.registerIcon(EnderIO.DOMAIN + ":ocConduitCore");
        coreTextures[1] = register.registerIcon(EnderIO.DOMAIN + ":ocConduitCoreAnim");

        longTextures[0] = register.registerIcon(EnderIO.DOMAIN + ":ocConduit");
        longTextures[1] = register.registerIcon(EnderIO.DOMAIN + ":ocConduitAnim");
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }

  @Override
  protected void readTypeSettings(ForgeDirection dir, NBTTagCompound dataRoot) {
    setSignalColor(dir, DyeColor.values()[dataRoot.getShort("signalColor")]);
  }

  @Override
  protected void writeTypeSettingsToNbt(ForgeDirection dir, NBTTagCompound dataRoot) {
    dataRoot.setShort("signalColor", (short) getSignalColor(dir).ordinal());
  }

  @Override
  public DyeColor getSignalColor(ForgeDirection dir) {
    DyeColor res = signalColors.get(dir);
    if (res == null) {
      return DyeColor.SILVER;
    }
    return res;
  }

  @Override
  public Collection<CollidableComponent> createCollidables(CacheKey key) {
    Collection<CollidableComponent> baseCollidables = super.createCollidables(key);
    if (key.dir == ForgeDirection.UNKNOWN) {
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
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
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
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if (cols[i] >= 0) {
          signalColors.put(dir, DyeColor.values()[cols[i]]);
        }
        i++;
      }
    }
  }

  @Override
  public void setSignalColor(ForgeDirection dir, DyeColor col) {
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
    return new ItemStack(EnderIO.itemOCConduit, 1, 0);
  }

  @Override
  public AbstractConduitNetwork<?, ?> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(AbstractConduitNetwork<?, ?> network) {
    if (network == null) {
      for (ForgeDirection dir : getExternalConnections()) {
        disconnectNode(dir);
      }
    }
    this.network = (OCConduitNetwork) network;
    addMissingNodeConnections();
    return true;
  }

  @Override
  public IIcon getTextureForState(CollidableComponent component) {
    int state = Config.enableOCConduitsAnimatedTexture ? 1 : 0;
    if (component.dir == ForgeDirection.UNKNOWN) {
      return coreTextures[state];
    } else {
      return longTextures[state];
    }
  }

  @Override
  public boolean shouldMirrorTexture() {
    return !Config.enableOCConduitsAnimatedTexture;
  }

  @Override
  public IIcon getTransmitionTextureForState(CollidableComponent component) {
    return null;
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

  private static EnumChatFormatting dye2chat(DyeColor dyeColor) {
    switch (dyeColor) {
    case BLACK:
      return EnumChatFormatting.BLACK;
    case BLUE:
      return EnumChatFormatting.DARK_BLUE;
    case BROWN:
      return EnumChatFormatting.DARK_RED;
    case CYAN:
      return EnumChatFormatting.DARK_AQUA;
      // return EnumChatFormatting.AQUA;
    case GRAY:
      return EnumChatFormatting.DARK_GRAY;
    case GREEN:
      return EnumChatFormatting.DARK_GREEN;
    case LIGHT_BLUE:
      return EnumChatFormatting.BLUE;
    case LIME:
      return EnumChatFormatting.GREEN;
    case MAGENTA:
      return EnumChatFormatting.LIGHT_PURPLE;
    case ORANGE:
      return EnumChatFormatting.GOLD;
    case PINK:
      return EnumChatFormatting.LIGHT_PURPLE;
    case PURPLE:
      return EnumChatFormatting.DARK_PURPLE;
    case RED:
      return EnumChatFormatting.RED;
    case SILVER:
      return EnumChatFormatting.GRAY;
    case WHITE:
      return EnumChatFormatting.WHITE;
    case YELLOW:
      return EnumChatFormatting.YELLOW;
    default:
      return null;
    }
  }

  @Override
  public boolean onBlockActivated(EntityPlayer player, RaytraceResult res, List<RaytraceResult> all) {
    DyeColor col = DyeColor.getColorFromDye(player.getCurrentEquippedItem());
    if (col != null && res.component != null) {
      setSignalColor(res.component.dir, col);
      return true;
    } else if (ConduitUtil.isProbeEquipped(player)) {
      if (!player.worldObj.isRemote) {
        BlockCoord bc = getLocation();
        if (network != null) {
          boolean noconnections = true;
          for (DyeColor color : DyeColor.values()) {
            if (node(color).neighbors().iterator().hasNext()) {
              noconnections = false;
              ChatComponentText coltxt = new ChatComponentText(color.getLocalisedName());
              coltxt.getChatStyle().setColor(dye2chat(color));
              ChatComponentText chantxt = new ChatComponentText("Channel ");
              chantxt.appendSibling(coltxt);
              chantxt.appendText(" at " + bc.x + "/" + bc.y + "/" + bc.z);
              player.addChatMessage(chantxt);
              for (Node other : node(color).neighbors()) {
                player.addChatMessage(new ChatComponentText("  Connected to: " + prettyNode(other)));
              }
            }
          }
          if (noconnections) {
            player.addChatMessage(new ChatComponentText("No connections at " + bc.x + "/" + bc.y + "/" + bc.z));
          }
        } else {
          player.addChatMessage(new ChatComponentText("No network at " + bc.x + "/" + bc.y + "/" + bc.z));
        }
      }
      return true;
    } else if (ToolUtil.isToolEquipped(player)) {
      if (!getBundle().getEntity().getWorldObj().isRemote) {
        if (res != null && res.component != null) {
          ForgeDirection connDir = res.component.dir;
          ForgeDirection faceHit = ForgeDirection.getOrientation(res.movingObjectPosition.sideHit);
          if (all != null && containsExternalConnection(connDir)) {
            for (RaytraceResult rtr : all) {
              if (rtr != null && rtr.component != null && COLOR_CONTROLLER_ID.equals(rtr.component.data)) {
                setSignalColor(connDir, DyeColor.getNext(getSignalColor(connDir)));
                return true;
              }
            }
          }
          if (connDir == ForgeDirection.UNKNOWN || connDir == faceHit) {
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
  public void setConnectionMode(ForgeDirection dir, ConnectionMode mode) {
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
      World world = getBundle().getWorld();
      EnumSet<ForgeDirection> conns = getConnections();
      for (DyeColor color : DyeColor.values()) {
        Set<Node> should = new HashSet<Node>();
        for (ForgeDirection direction : conns) {
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

  private void disconnectNode(ForgeDirection direction) {
    World world = getBundle().getWorld();
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

    World world = getBundle().getWorld();
    EnumSet<ForgeDirection> conns = getConnections();
    // we need to check if that node has another way of connecting to our
    // network. First find out which of our neighbor(s) it belongs to. May
    // be just one, may be many.
    List<TileEntity> toCheck = new ArrayList<TileEntity>();
    if (otherHost instanceof TileEntity) {
      TileEntity otherTe = (TileEntity) otherHost;
      toCheck.add(otherTe);
    } else {
      for (ForgeDirection direction : conns) {
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
      for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
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

  public EnumSet<ForgeDirection> getConnections() {
    EnumSet<ForgeDirection> cons = EnumSet.noneOf(ForgeDirection.class);
    cons.addAll(getConduitConnections());
    for (ForgeDirection dir : getExternalConnections()) {
      if (getConnectionMode(dir) != ConnectionMode.DISABLED) {
        cons.add(dir);
      }
    }
    return cons;
  }

  @Override
  public ConnectionMode getNextConnectionMode(ForgeDirection dir) {
    ConnectionMode mode = getConnectionMode(dir);
    mode = mode == ConnectionMode.IN_OUT ? ConnectionMode.DISABLED : ConnectionMode.IN_OUT;
    return mode;
  }

  @Override
  public ConnectionMode getPreviousConnectionMode(ForgeDirection dir) {
    return getNextConnectionMode(dir);
  }

  @Override
  public boolean canConnectToExternal(ForgeDirection direction, boolean ignoreConnectionMode) {
    TileEntity te = getLocation().getLocation(direction).getTileEntity(getBundle().getWorld());
    if (te instanceof SidedEnvironment) {
      if (getBundle().getWorld().isRemote) {
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
  public Node sidedNode(ForgeDirection side) {
    return getConnections().contains(side) ? node(getSignalColor(side)) : null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Method(modid = "OpenComputersAPI|Network")
  public boolean canConnect(ForgeDirection side) {
    return getConnections().contains(side);
  }

  @Override
  public void invalidate() {
  }

}
