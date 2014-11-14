package crazypants.enderio.conduit.me;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.AEApi;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.util.AECableType;
import cpw.mods.fml.common.Optional.Method;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.RaytraceResult;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.tool.ToolUtil;
import crazypants.render.IconUtil;
import crazypants.util.BlockCoord;

public class MEConduit extends AbstractConduit implements IMEConduit {

  protected MEConduitNetwork network;
  protected MEConduitGrid grid;

  public static IIcon coreTexture;
  public static IIcon longTexture;

  EnumSet<ForgeDirection> validConnections = EnumSet.copyOf(Arrays.asList(ForgeDirection.VALID_DIRECTIONS));

  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IIconRegister register) {
        coreTexture = register.registerIcon(EnderIO.MODID + ":meConduitCore");
        longTexture = register.registerIcon(EnderIO.MODID + ":meConduit");
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IMEConduit.class;
  }

  @Override
  public ItemStack createItem() {
    return new ItemStack(EnderIO.itemMEConduit);
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
    NBTTagList list = new NBTTagList();
    for (ForgeDirection dir : validConnections) {
      NBTTagString name = new NBTTagString(dir.name());
      list.appendTag(name);
    }
    nbtRoot.setTag("validConnections", list);
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot, short nbtVersion) {
    super.readFromNBT(nbtRoot, nbtVersion);
    if(nbtRoot.hasKey("validConnections")) {
      validConnections.clear();
      NBTTagList connections = nbtRoot.getTagList("validConnections", Constants.NBT.TAG_STRING);
      for (int i = 0; i < connections.tagCount(); i++) {
        validConnections.add(ForgeDirection.valueOf(connections.getStringTagAt(i)));
      }
    }
  }

  @Override
  @Method(modid = "appliedenergistics2")
  public boolean canConnectToExternal(ForgeDirection dir, boolean ignoreConnectionMode) {
    World world = getBundle().getWorld();
    BlockCoord pos = getLocation();
    TileEntity te = world.getTileEntity(pos.x + dir.offsetX, pos.y + dir.offsetY, pos.z + dir.offsetZ);

    if(!ignoreConnectionMode && getConnectionMode(dir) == ConnectionMode.DISABLED) {
      return false;
    }

    if(te instanceof IPartHost) {
      IPart part = ((IPartHost) te).getPart(dir.getOpposite());
      if(part == null) {
        part = ((IPartHost) te).getPart(ForgeDirection.UNKNOWN);
        return part != null;
      }
      return part.getExternalFacingNode() != null;
    } else if(te instanceof IGridHost) {
      return !(te instanceof TileConduitBundle) && ((IGridHost) te).getCableConnectionType(dir.getOpposite()) != AECableType.NONE;
    }
    return false;
  }

  @Override
  public IIcon getTextureForState(CollidableComponent component) {
    if(component.dir == ForgeDirection.UNKNOWN) {
      return coreTexture;
    } else {
      return longTexture;
    }
  }

  @Override
  public IIcon getTransmitionTextureForState(CollidableComponent component) {
    return null;
  }

  @Override
  @Method(modid = "appliedenergistics2")
  public void updateEntity(World worldObj) {
    if(grid == null) {
      grid = new MEConduitGrid(this);
    }

    if(getNode() == null && !worldObj.isRemote) {
      IGridNode node = AEApi.instance().createGridNode(grid);
      if(node != null) {
        getBundle().setGridNode(node);
        getNode().updateState();
      }
    }

    super.updateEntity(worldObj);
  }

  @Override
  public ConnectionMode getNextConnectionMode(ForgeDirection dir) {
    ConnectionMode mode = getConnectionMode(dir);
    mode = mode == ConnectionMode.IN_OUT ? ConnectionMode.DISABLED : ConnectionMode.IN_OUT;
    return mode;
  }

  @Override
  public ConnectionMode getConnectionMode(ForgeDirection dir) {
    return validConnections.contains(dir) ? ConnectionMode.IN_OUT : ConnectionMode.DISABLED;
  }

  @Override
  public void setConnectionMode(ForgeDirection dir, ConnectionMode mode) {
    super.setConnectionMode(dir, mode);
    if(mode == ConnectionMode.DISABLED) {
      validConnections.remove(dir);
    } else {
      validConnections.add(dir);
    }
    if(hasNode()) {
      getNode().updateState();
      getNode().getWorld().markBlockForUpdate(getLocation().x, getLocation().y, getLocation().z);
    }
  }

  @Override
  public boolean canConnectToConduit(ForgeDirection direction, IConduit conduit) {
    if(conduit == null) {
      return false;
    }
    return conduit instanceof IMEConduit;
  }

  @Override
  public void conduitConnectionAdded(ForgeDirection fromDirection) {
    super.conduitConnectionAdded(fromDirection);
    validConnections.add(fromDirection);
  }

  @Override
  public void conduitConnectionRemoved(ForgeDirection fromDirection) {
    super.conduitConnectionRemoved(fromDirection);
    validConnections.remove(fromDirection);
  }

  @Override
  @Method(modid = "appliedenergistics2")
  protected void connectionsChanged() {
    super.connectionsChanged();
    onNodeChanged();
  }

  @Override
  public boolean onBlockActivated(EntityPlayer player, RaytraceResult res, List<RaytraceResult> all) {
    super.onBlockActivated(player, res, all);
    if(ToolUtil.isToolEquipped(player)) {
      if(!getBundle().getEntity().getWorldObj().isRemote) {
        if(res != null && res.component != null) {
          ForgeDirection connDir = res.component.dir;
          ForgeDirection faceHit = ForgeDirection.getOrientation(res.movingObjectPosition.sideHit);
          if(connDir == ForgeDirection.UNKNOWN || connDir == faceHit) {
            if(getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
              setConnectionMode(faceHit, getNextConnectionMode(faceHit));
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
    }
    return false;
  }

  @Method(modid = "appliedenergistics2")
  public void onNodeChanged() {
    boolean foundConnection = false;

    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      TileEntity te = getBundle().getLocation().getLocation(dir).getTileEntity(getBundle().getWorld());
      if(te != null && te instanceof IGridHost) {
        IGridNode node = ((IGridHost) te).getGridNode(ForgeDirection.UNKNOWN);
        foundConnection |= validConnections.contains(dir);
        if(node == null) {
          node = ((IGridHost) te).getGridNode(dir.getOpposite());
        }
        if(node != null) {
          node.updateState();
        }
      }
    }
    if(!foundConnection && hasNode()) {
      getNode().destroy();
    }
  }

  @Override
  public void onAddedToBundle() {
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      TileEntity te = getLocation().getLocation(dir).getTileEntity(getBundle().getWorld());
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
  public MEConduitGrid getGrid() {
    return grid;
  }

  @Method(modid = "appliedenergistics2")
  private IGridNode getNode() {
    return getBundle().getGridNode(null);
  }

  private boolean hasNode() {
    return getNode() != null;
  }

  @Override
  public EnumSet<ForgeDirection> getConnections() {
    return validConnections;
  }
}
