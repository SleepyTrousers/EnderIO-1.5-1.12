package crazypants.enderio.conduit.redstone;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.render.IconUtil;
import crazypants.util.BlockCoord;
import crazypants.util.DyeColor;

public class RedstoneConduit extends AbstractConduit implements IRedstoneConduit {

  static final Map<String, Icon> ICONS = new HashMap<String, Icon>();

  @SideOnly(Side.CLIENT)
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

  protected boolean neighbourDirty = true;

  public RedstoneConduit() {
  }

  @Override
  public ItemStack createItem() {
    return new ItemStack(ModObject.itemRedstoneConduit.actualId, 1, 0);
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IRedstoneConduit.class;
  }

  @Override
  public AbstractConduitNetwork<IRedstoneConduit, IRedstoneConduit> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(AbstractConduitNetwork<?, ?> network) {
    this.network = (RedstoneConduitNetwork) network;
    return true;
  }

  @Override
  public boolean canConnectToExternal(ForgeDirection direction, boolean ignoreDisabled) {
    return false;
  }

  @Override
  public void updateNetwork() {
    World world = getBundle().getEntity().worldObj;
    if(world != null) {
      updateNetwork(world);
    }
  }

  protected boolean acceptSignalsForDir(ForgeDirection dir) {
    BlockCoord loc = getLocation().getLocation(dir);
    return ConduitUtil.getConduit(getBundle().getEntity().getWorldObj(), loc.x, loc.y, loc.z, IRedstoneConduit.class) == null;
  }

  @Override
  public Set<Signal> getNetworkInputs() {
    return getNetworkInputs(null);
  }

  @Override
  public Set<Signal> getNetworkInputs(ForgeDirection side) {
    if(network != null) {
      network.setNetworkEnabled(false);
    }

    Set<Signal> res = new HashSet<Signal>();
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      if((side == null || dir == side) && acceptSignalsForDir(dir)) {
        int input = getExternalPowerLevel(dir);
        if(input > 1) { // need to degrade external signals by one as they
                        // enter
          BlockCoord loc = getLocation().getLocation(dir);
          Signal signal = new Signal(loc.x, loc.y, loc.z, dir, input - 1, getSignalColor(dir));
          res.add(signal);
        }
      }
    }

    if(network != null) {
      network.setNetworkEnabled(true);
    }

    return res;
  }

  @Override
  public DyeColor getSignalColor(ForgeDirection dir) {
    return DyeColor.RED;
  }

  @Override
  public Set<Signal> getNetworkOutputs(ForgeDirection side) {
    if(network == null) {
      return Collections.emptySet();
    }
    return network.getSignals();
  }

  @Override
  public boolean onNeighborBlockChange(int blockId) {
    World world = getBundle().getEntity().worldObj;
    if(world.isRemote) {
      return false;
    }
    boolean res = super.onNeighborBlockChange(blockId);
    if(network == null || network.updatingNetwork) {
      return false;
    }
    neighbourDirty = res;
    return res;
  }

  @Override
  public void updateEntity(World world) {
    super.updateEntity(world);
    if(!world.isRemote && neighbourDirty) {
      network.destroyNetwork();
      updateNetwork(world);
      neighbourDirty = false;
    }
  }

  protected int getExternalPowerLevel(ForgeDirection dir) {
    World world = getBundle().getEntity().worldObj;
    BlockCoord loc = getLocation();
    loc = loc.getLocation(dir);
    return world.getIndirectPowerLevelTo(loc.x, loc.y, loc.z, dir.ordinal());
  }

  @Override
  public int isProvidingStrongPower(ForgeDirection toDirection) {
    return 0;
  }

  @Override
  public int isProvidingWeakPower(ForgeDirection toDirection) {
    if(network == null || !network.isNetworkEnabled()) {
      return 0;
    }
    int result = 0;
    for (Signal signal : getNetworkOutputs(toDirection.getOpposite())) {
      result = Math.max(result, signal.strength);
    }
    return result;
  }

  @Override
  public Icon getTextureForState(CollidableComponent component) {
    if(component.dir == ForgeDirection.UNKNOWN) {
      return isActive() ? ICONS.get(KEY_CORE_ON_ICON) : ICONS.get(KEY_CORE_OFF_ICON);
    }
    return isActive() ? ICONS.get(KEY_TRANSMISSION_ICON) : ICONS.get(KEY_CONDUIT_ICON);
  }

  @Override
  public Icon getTransmitionTextureForState(CollidableComponent component) {
    return null;
  }

  @Override
  public String toString() {
    return "RedstoneConduit [network=" + network + " connections=" + conduitConnections + " active=" + active + "]";
  }

  @Override
  public int[] getOutputValues(World world, int x, int y, int z, ForgeDirection side) {
    int[] result = new int[16];
    Set<Signal> outs = getNetworkOutputs(side);
    if(outs != null) {
      for (Signal s : outs) {
        result[s.color.ordinal()] = s.strength;
      }
    }
    return result;
  }

  @Override
  public int getOutputValue(World world, int x, int y, int z, ForgeDirection side, int subnet) {
    Set<Signal> outs = getNetworkOutputs(side);
    if(outs != null) {
      for (Signal s : outs) {
        if(subnet == s.color.ordinal()) {
          return s.strength;
        }
      }
    }
    return 0;
  }

}
