package crazypants.enderio.conduit.me;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.AEApi;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;

import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.util.BlockCoord;

import cpw.mods.fml.common.Optional.Method;
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
import crazypants.enderio.tool.ToolUtil;

public class MEConduit extends AbstractConduit implements IMEConduit {

  protected MEConduitNetwork network;
  protected MEConduitGrid grid;

  public static IIcon[] coreTextures;
  public static IIcon[] longTextures;
  
  private boolean isDense;
  private int playerID = -1;

  public MEConduit() {
    this(0);
  }
  
  public MEConduit(int itemDamage) {
    isDense = itemDamage == 1;
  }

  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IIconRegister register) {
        coreTextures = new IIcon[2];
        longTextures = new IIcon[2];
        
        coreTextures[0] = register.registerIcon(EnderIO.DOMAIN + ":meConduitCore");
        coreTextures[1] = register.registerIcon(EnderIO.DOMAIN + ":meConduitCoreDense");
        
        longTextures[0] = register.registerIcon(EnderIO.DOMAIN + ":meConduit");
        longTextures[1] = register.registerIcon(EnderIO.DOMAIN + ":meConduitDense");
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
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
    return new ItemStack(EnderIO.itemMEConduit, 1, getDamageForState(isDense));
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
  public boolean canConnectToExternal(ForgeDirection dir, boolean ignoreDisabled) {
    World world = getBundle().getWorld();
    BlockCoord pos = getLocation();
    TileEntity te = world.getTileEntity(pos.x + dir.offsetX, pos.y + dir.offsetY, pos.z + dir.offsetZ);

    if(te instanceof TileConduitBundle) {
      return false;
    }

    // because the AE2 API doesn't allow an easy query like "which side can connect to an ME cable" it needs this mess
    if(te instanceof IPartHost) {
      IPart part = ((IPartHost) te).getPart(dir.getOpposite());
      if(part == null) {
        part = ((IPartHost) te).getPart(ForgeDirection.UNKNOWN);
        return part != null;
      }
      if(part.getExternalFacingNode() != null) {
        return true;
      }
      String name = part.getClass().getSimpleName();
      return "PartP2PTunnelME".equals(name) || "PartQuartzFiber".endsWith(name) ||
              "PartToggleBus".equals(name) || "PartInvertedToggleBus".equals(name);
    } else if(te instanceof IGridHost) {
      IGridNode node = ((IGridHost) te).getGridNode(dir.getOpposite());
      if(node == null) {
        node = ((IGridHost) te).getGridNode(ForgeDirection.UNKNOWN);
      }
      if(node != null) {
        return node.getGridBlock().getConnectableSides().contains(dir.getOpposite());
      }
    }
    return false;
  }

  @Override
  public IIcon getTextureForState(CollidableComponent component) {
    int state = getDamageForState(isDense);
    if(component.dir == ForgeDirection.UNKNOWN) {
      return coreTextures[state];
    } else {
      return longTextures[state];
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
        node.setPlayerID(playerID);
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
  public ConnectionMode getPreviousConnectionMode(ForgeDirection dir) {
    return getNextConnectionMode(dir);
  }

  @Override
  public boolean canConnectToConduit(ForgeDirection direction, IConduit conduit) {
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
        node.getWorld().markBlockForUpdate(loc.x, loc.y, loc.z);
      }
    }
  }

  @Override
  public boolean onBlockActivated(EntityPlayer player, RaytraceResult res, List<RaytraceResult> all) {
    if(ToolUtil.isToolEquipped(player)) {
      if(!getBundle().getEntity().getWorldObj().isRemote) {
        if(res != null && res.component != null) {
          ForgeDirection connDir = res.component.dir;
          ForgeDirection faceHit = ForgeDirection.getOrientation(res.movingObjectPosition.sideHit);
          if(connDir == ForgeDirection.UNKNOWN || connDir == faceHit) {
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
    World world = getBundle().getWorld();
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      TileEntity te = location.getLocation(dir).getTileEntity(world);
      if(te != null && te instanceof IGridHost && !(te instanceof IConduitBundle)) {
        IGridNode node = ((IGridHost) te).getGridNode(ForgeDirection.UNKNOWN);
        if(node == null) {
          node = ((IGridHost) te).getGridNode(dir.getOpposite());
        }
        if(node != null) {
          node.updateState();
        }
      }
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
  @Method(modid = "appliedenergistics2")
  public void onChunkUnload(World worldObj) {
    super.onChunkUnload(worldObj);
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
  public EnumSet<ForgeDirection> getConnections() {
    EnumSet<ForgeDirection> cons = EnumSet.noneOf(ForgeDirection.class);
    cons.addAll(getConduitConnections());
    for(ForgeDirection dir : getExternalConnections()) {
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
}
