package crazypants.enderio.conduit.me;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import appeng.api.events.GridTileLoadEvent;
import appeng.api.events.GridTileUnloadEvent;
import appeng.api.me.tiles.IGridTileEntity;
import appeng.api.me.util.IGridInterface;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.render.IconUtil;
import crazypants.util.BlockCoord;

public class MeConduit extends AbstractConduit implements IMeConduit {

  static final Map<String, Icon> ICONS = new HashMap<String, Icon>();

  private static final String KEY_CORE_ICON = "enderio:meConduitCore";
  private static final String KEY_CONDUIT_ICON = "enderio:meConduit";

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IconRegister register) {
        ICONS.put(KEY_CORE_ICON, register.registerIcon(KEY_CORE_ICON));
        ICONS.put(KEY_CONDUIT_ICON, register.registerIcon(KEY_CONDUIT_ICON));
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }

  private boolean doInit = true;

  private IGridInterface myGrid = null;
  private boolean hasPower = false;
  private boolean networkReady = false;

  @Override
  public boolean isMachineActive() {
    return networkReady && hasPower;
  }

  @Override
  public void setNetworkReady(boolean isReady) {
    this.networkReady = isReady;
  }

  @Override
  public float getPowerDrainPerTick() {
    return 1 / 16f;
  }

  @Override
  public void setPoweredStatus(boolean hasPower) {
    this.hasPower = hasPower;
  }

  @Override
  public boolean isPowered() {
    return hasPower;
  }

  @Override
  public IGridInterface getGrid() {
    return myGrid == null ? null : myGrid.isValid() ? myGrid : null;
  }

  @Override
  public void setGrid(IGridInterface gi) {
    if(gi != myGrid) {
      myGrid = gi;
    }
  }

  @Override
  public void onChunkUnload(World worldObj) {
    super.onChunkUnload(worldObj);
    if(!doInit && !worldObj.isRemote) {
      MinecraftForge.EVENT_BUS.post(new GridTileUnloadEvent(getBundle(), worldObj, getBundle().getLocation()));
      doInit = true;
    }
  }

  @Override
  public void updateEntity(World world) {
    super.updateEntity(world);
    if(doInit && !world.isRemote) {
      MinecraftForge.EVENT_BUS.post(new GridTileLoadEvent(getBundle(), world, getBundle().getLocation()));
      doInit = false;
    }
  }

  @Override
  public void onRemovedFromBundle() {
    super.onRemovedFromBundle();
    if(!doInit) {
      MinecraftForge.EVENT_BUS.post(new GridTileUnloadEvent(getBundle(), getBundle().getWorld(), getBundle().getLocation()));
      doInit = true;
    }
  }

  @Override
  public boolean canConnectToConduit(ForgeDirection direction, IConduit conduit) {
    return true;
  }

  @Override
  public boolean canConnectToExternal(ForgeDirection direction, boolean ignoreConnectionMode) {
    BlockCoord bc = getLocation().getLocation(direction);
    TileEntity te = getBundle().getWorld().getBlockTileEntity(bc.x, bc.y, bc.z);
    return te instanceof IGridTileEntity && !(te instanceof IConduitBundle);
  }

  @Override
  protected void updateNetwork(World world) {
    //No network managed by us 
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IMeConduit.class;
  }

  @Override
  public ItemStack createItem() {
    return new ItemStack(ModObject.itemMeConduit.actualId, 1, 0);
  }

  @Override
  public AbstractConduitNetwork<?, ?> getNetwork() {
    return null;
  }

  @Override
  public boolean setNetwork(AbstractConduitNetwork<?, ?> network) {
    return true;
  }

  @Override
  public Icon getTextureForState(CollidableComponent component) {
    if(component.dir == ForgeDirection.UNKNOWN) {
      return ICONS.get(KEY_CORE_ICON);
    }
    return ICONS.get(KEY_CONDUIT_ICON);
  }

  @Override
  public Icon getTransmitionTextureForState(CollidableComponent component) {
    return null;
  }

}
