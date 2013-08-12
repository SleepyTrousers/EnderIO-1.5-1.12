package crazypants.enderio.conduit.redstone;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.render.IconUtil;
import crazypants.util.BlockCoord;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class RedstoneConduit extends AbstractConduit implements IRedstoneConduit {

  
  static final  Map<String, Icon> ICONS = new HashMap<String, Icon>();

  
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {           

      @Override
      public void registerIcons(IconRegister register) {
        ICONS.put(KEY_CORE_OFF_ICON, register.registerIcon(KEY_CORE_OFF_ICON));
        ICONS.put(KEY_CORE_ON_ICON, register.registerIcon(KEY_CORE_ON_ICON));
        ICONS.put(KEY_CONDUIT_ICON, register.registerIcon(KEY_CONDUIT_ICON));
        ICONS.put(KEY_TRANSMISSION_ICON, register.registerIcon(KEY_TRANSMISSION_ICON));        
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }
  
  protected RedstoneConduitNetwork network;

  protected final Set<Signal> externalSignals = new HashSet<Signal>();

  public RedstoneConduit() {
  }   

  @Override
  public boolean isReplaceableByControl(Class<? extends IRedstoneConduit> replacenebtType) {
    return true;
  }
  
  @Override
  public ItemStack createItem() {
    return new ItemStack(ModObject.itemRedstoneConduit.actualId,1,0);
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IRedstoneConduit.class;
  }

  @Override
  public AbstractConduitNetwork<IRedstoneConduit> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(AbstractConduitNetwork<?> network) {
    this.network = (RedstoneConduitNetwork) network;
    return true;
  } 

  @Override
  public boolean canConnectToExternal(ForgeDirection direction) {
    TileEntity te = bundle.getEntity();
    World world = te.worldObj;
    if (world == null) {
      return false;
    }
    int id = world.getBlockId(te.xCoord + direction.offsetX, te.yCoord + direction.offsetY, te.zCoord + direction.offsetZ);
    if (id > 0 && id != EnderIO.blockConduitBundle.blockID) {
      
      //We can connect to a block if it can provide power or it is receiving a string signal (not emitted by us)
      
      BlockCoord loc = getLocation().getLocation(direction);
      
      boolean toggleNetwork = network != null && network.isNetworkEnabled();
      if(toggleNetwork ) {
        network.setNetworkEnabled(false);
      }
      boolean gettingStrongPower = world.getBlockPowerInput(loc.x, loc.y, loc.z) == 15;
      if(toggleNetwork) {
        network.setNetworkEnabled(true);
      }
      
      
      //We can get an input from the block when:
      return 
          Block.blocksList[id].canProvidePower() || //The block can provide power           
          //Or its getting a strong signal that we are not providing
          gettingStrongPower;
          //( world.getBlockPowerInput(loc.x, loc.y, loc.z) == 15 && isProvidingStrongPower(direction) != 15 ); 
    }
    return false;
  }

  @Override
  public Set<Signal> getNetworkInputs() {    
    Set<Signal> res = new HashSet<Signal>();
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      if (canConnectToExternal(dir)) {
        int input = getExternalPowerLevel(dir);
        if (input > 1) { //need to degrade external signals by one as they enter
          BlockCoord loc = getLocation().getLocation(dir);
          Signal signal = new Signal(loc.x, loc.y, loc.z, input - 1, SignalColor.RED);
          res.add(signal);
        }        
      }
    }
    return res;    
  }

  @Override
  public Set<Signal> getNetworkOutputs(ForgeDirection side) {
    if (network == null) {
      return Collections.emptySet();
    }
    return network.getSignals();
  }
  
  @Override
  public boolean onNeighborBlockChange(int blockId) {
    World world = getBundle().getEntity().worldObj;
    if (world.isRemote) {
      return false;
    }
    if (blockId == EnderIO.blockConduitBundle.blockID) {
      return false;
    }
    boolean res = super.onNeighborBlockChange(blockId);
        
    if (network == null || network.updatingNetwork) {      
      return false;
    }    
    
    if(blockId > 0 && Block.blocksList[blockId].canProvidePower() && network != null) {  
      //TODO: Just recalculate the signals, no need for a full rebuild
      network.destroyNetwork();      
      return false;
    }
    return res;
  }

  private int getExternalPowerLevel(ForgeDirection dir) {
    if (network != null) {
      network.setNetworkEnabled(false);
    }    
    World world = getBundle().getEntity().worldObj;
    BlockCoord loc = getLocation();   
    int  result = world.getStrongestIndirectPower(loc.x, loc.y, loc.z);    
    
    if (network != null) {
      network.setNetworkEnabled(true);
    }
    return result;

  }

  @Override
  public int isProvidingStrongPower(ForgeDirection toDirection) {
    return 0;
  }

  @Override
  public int isProvidingWeakPower(ForgeDirection toDirection) {
    if (network == null || !network.isNetworkEnabled()) {
      return 0;
    }
    int result = 0;
    for (Signal signal : network.getSignals()) {
      result = Math.max(result, signal.strength);
    }
    return result;
  }

  @Override
  public Icon getTextureForState(CollidableComponent component) {
    if (component.dir == ForgeDirection.UNKNOWN) {
      return isActive() ? ICONS.get(KEY_CORE_ON_ICON) : ICONS.get(KEY_CORE_OFF_ICON);
    }
    //return ICONS.get(KEY_CONDUIT_ICON);
    return isActive() ? ICONS.get(KEY_TRANSMISSION_ICON) : ICONS.get(KEY_CONDUIT_ICON);
  }

  @Override
  public Icon getTransmitionTextureForState(CollidableComponent component) {
//    if (component.id == ForgeDirection.UNKNOWN) {
//      return null;
//    }
//    return isActive() ? ICONS.get(KEY_TRANSMISSION_ICON) : null;
    return null;
        
  }

  @Override
  public String toString() {
    return "RedstoneConduit [network=" + network + " connections=" + conduitConnections + " active=" + active + "]";
  }

}
