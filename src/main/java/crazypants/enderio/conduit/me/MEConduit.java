package crazypants.enderio.conduit.me;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.AEApi;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.util.AECableType;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import cpw.mods.fml.common.Optional.Method;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.RaytraceResult;
import crazypants.enderio.conduit.geom.CollidableComponent;
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
  public boolean canConnectToConduit(ForgeDirection direction, IConduit conduit) {
    return super.canConnectToConduit(direction, conduit);
  }

  @Override
  public boolean canConnectToExternal(ForgeDirection direction, boolean ignoreConnectionMode) {
    return canConnectTo(getBundle().getWorld(), direction, getBundle().getLocation());
  }
  
  @Method(modid = "appliedenergistics2")
  private boolean canConnectTo(World world, ForgeDirection dir, BlockCoord pos) {
    TileEntity te = world.getTileEntity(pos.x + dir.offsetX, pos.y + dir.offsetY, pos.z + dir.offsetZ);
    if (te instanceof IGridProxyable) {
      AENetworkProxy proxy = ((IGridProxyable)te).getProxy();
      return proxy != null && proxy.getConnectableSides().contains(dir.getOpposite());
    } else if (te instanceof IPartHost) {
      IPart part = ((IPartHost) te).getPart(dir.getOpposite());
      return part != null ? part.getExternalFacingNode() != null : ((IPartHost)te).getPart(ForgeDirection.UNKNOWN) != null;
    } else if (te instanceof IGridHost) {
      return ((IGridHost)te).getCableConnectionType(dir.getOpposite()) != AECableType.NONE;
    }
    return false;
  }
  
  @Override
  public IIcon getTextureForState(CollidableComponent component) {
    if (component.dir == ForgeDirection.UNKNOWN) {
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
    if (grid == null) {
      grid = new MEConduitGrid(this);
    }
    
    if(getNode() == null && !worldObj.isRemote) {
      IGridNode node = AEApi.instance().createGridNode(grid);
      if (node != null) {
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
    ConnectionMode mode = conectionModes.get(dir);
    return mode == null ? validConnections.contains(dir) ? ConnectionMode.IN_OUT : ConnectionMode.DISABLED : mode;
  }
  
  @Override
  public void setConnectionMode(ForgeDirection dir, ConnectionMode mode) {
    super.setConnectionMode(dir, mode);
    if (mode == ConnectionMode.DISABLED) {
      validConnections.remove(dir);
    } else {
      validConnections.add(dir);
    }
    getNode().updateState();
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
    if(ConduitUtil.isToolEquipped(player)) {
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
    }
    return false;
  }
  
  @Method(modid = "appliedenergistics2")
  public void onNodeChanged() {
    boolean foundConnection = false;

    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      TileEntity te = getBundle().getLocation().getLocation(dir).getTileEntity(getBundle().getWorld());
      if (te != null && te instanceof IGridHost) {
        IGridNode node = ((IGridHost)te).getGridNode(ForgeDirection.UNKNOWN);
        foundConnection |= validConnections.contains(dir);
        if (node == null) {
          node = ((IGridHost)te).getGridNode(dir.getOpposite());
        }
        if (node != null) {
          node.updateState();
        }
      }
    }
    if (!foundConnection && hasNode()) {
      getNode().destroy();
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
