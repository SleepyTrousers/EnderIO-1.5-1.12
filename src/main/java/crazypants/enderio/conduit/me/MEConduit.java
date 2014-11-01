package crazypants.enderio.conduit.me;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.AEApi;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkEvent;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.me.helpers.AENetworkProxy;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;

public class MEConduit extends AbstractConduit implements IMEConduit {

  private IGridNode node;

  protected MEConduitNetwork network;

  protected MEConduitGrid grid;

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
  public IIcon getTextureForState(CollidableComponent component) {
    return null;
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
    node.destroy();
    node = null;
  }
}
