package crazypants.enderio.conduit.me;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.AEApi;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.me.helpers.AENetworkProxy;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.render.IconUtil;
import crazypants.util.BlockCoord;

public class MEConduit extends AbstractConduit implements IMEConduit {

  private IGridNode node;

  protected MEConduitNetwork network;

  protected MEConduitGrid grid;
  
  private static IIcon coreTexture;
  private static IIcon longTexture;

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
  
  public MEConduit() {
    grid = new MEConduitGrid(this);
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
    return canConnectTo(getBundle().getWorld(), direction, getBundle().getBlockCoord());
  }
  
  private boolean canConnectTo(World world, ForgeDirection dir, BlockCoord pos) {
    TileEntity te = world.getTileEntity(pos.x + dir.offsetX, pos.y + dir.offsetY, pos.z + dir.offsetZ);
    if (te instanceof IGridHost) {
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
  public IGridNode getGridNode(ForgeDirection dir) {
    return node;
  }

  @Override
  public void securityBreak() {
    ;
  }

  @Override
  public AECableType getCableConnectionType(ForgeDirection dir) {
    return AECableType.GLASS;
  }

  @Override
  public AENetworkProxy getProxy() {
    return grid.getProxy();
  }

  @Override
  public DimensionalCoord getLocation() {
    return new DimensionalCoord(getBundle().getEntity());
  }

  @Override
  public void gridChanged() {
    ;
  }

  @Override
  public void updateEntity(World worldObj) {
    if(node == null && !worldObj.isRemote) {
      node = AEApi.instance().createGridNode(grid);
      node.updateState();
    }
  }

  @Override
  public void onRemovedFromBundle() {
    connectionsChanged();
    node.destroy();
    node = null;
  }
}
