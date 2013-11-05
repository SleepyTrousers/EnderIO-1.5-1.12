package crazypants.enderio.conduit.redstone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import powercrystals.minefactoryreloaded.api.rednet.IConnectableRedNet;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.api.rednet.RedNetConnectionType;
import buildcraft.api.power.IPowerEmitter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.RaytraceResult;
import crazypants.enderio.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.render.BoundingBox;
import crazypants.render.IconUtil;
import crazypants.util.BlockCoord;

public class InsulatedRedstoneConduit extends RedstoneConduit implements IInsulatedRedstoneConduit {

  static final Map<String, Icon> ICONS = new HashMap<String, Icon>();

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IconRegister register) {
        ICONS.put(KEY_INS_CORE_OFF_ICON, register.registerIcon(KEY_INS_CORE_OFF_ICON));
        ICONS.put(KEY_INS_CORE_ON_ICON, register.registerIcon(KEY_INS_CORE_ON_ICON));
        ICONS.put(KEY_INS_CONDUIT_ICON, register.registerIcon(KEY_INS_CONDUIT_ICON));
        ICONS.put(KEY_INS_TRANSMISSION_ICON, register.registerIcon(KEY_INS_TRANSMISSION_ICON));
        ICONS.put(KEY_COLOR_CONTROLLER, register.registerIcon(KEY_COLOR_CONTROLLER));
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }

  private static List<Integer> VANILLA_CONECTABLES = Arrays.asList(
      23, 25, 27, 28, 29, 33, 46, 55, 64, 69, 70, 71, 72, 75, 76, 77, 93, 94,
      96, 107, 123, 124, 131, 143, 147, 148, 149, 150, 151, 152, 154, 157, 158);

  private Map<ForgeDirection, ConnectionMode> forcedConnections = new HashMap<ForgeDirection, ConnectionMode>();

  private Map<ForgeDirection, SignalColor> signalColors = new HashMap<ForgeDirection, SignalColor>();

  @Override
  public boolean onBlockActivated(EntityPlayer player, RaytraceResult res, List<RaytraceResult> all) {

    World world = getBundle().getEntity().worldObj;
    if(!world.isRemote) {

      SignalColor col = getSignalColorFromEquippedItem(player.getCurrentEquippedItem());
      if(col != null && res.component != null) {
        setSignalColor(res.component.dir, col);
        return true;
      } else if(ConduitUtil.isToolEquipped(player)) {

        if(res.component != null) {
          ForgeDirection connDir = res.component.dir;
          ForgeDirection faceHit = ForgeDirection.getOrientation(res.movingObjectPosition.sideHit);

          boolean colorHit = false;
          if(all != null && containsExternalConnection(connDir)) {
            for (RaytraceResult rtr : all) {
              if(rtr != null && rtr.component != null && COLOR_CONTROLLER_ID.equals(rtr.component.data)) {
                colorHit = true;
              }
            }
          }

          if(colorHit) {
            setSignalColor(connDir, SignalColor.getNext(getSignalColor(connDir)));
            return true;

          } else if(connDir == ForgeDirection.UNKNOWN || connDir == faceHit) {
            // Attempt to join networks
            BlockCoord loc = getLocation().getLocation(faceHit);
            IRedstoneConduit neighbour = ConduitUtil.getConduit(getBundle().getEntity().worldObj, loc.x, loc.y, loc.z, IRedstoneConduit.class);
            if(neighbour != null) {
              if(network != null) {
                network.destroyNetwork();
              }
              if(neighbour.getNetwork() != null) {
                neighbour.getNetwork().destroyNetwork();
              }
              onAddedToBundle();

            } else {

              setConnectionMode(faceHit, ConnectionMode.IN_OUT);
              forcedConnections.put(faceHit, ConnectionMode.IN_OUT);
              onAddedToBundle();
              Set<Signal> newSignals = getNetworkInputs(faceHit);
              network.addSignals(newSignals);
              network.notifyNeigborsOfSignals();

            }
            return true;

          } else if(externalConnections.contains(connDir)) {

            Set<Signal> signals = getNetworkInputs(connDir);
            setConnectionMode(connDir, ConnectionMode.DISABLED);
            forcedConnections.put(connDir, ConnectionMode.DISABLED);
            onAddedToBundle();
            network.removeSignals(signals);
            network.notifyNeigborsOfSignals();
            return true;

          } else if(containsConduitConnection(connDir)) {
            BlockCoord loc = getLocation().getLocation(connDir);
            IRedstoneConduit neighbour = ConduitUtil.getConduit(getBundle().getEntity().worldObj, loc.x, loc.y, loc.z, IRedstoneConduit.class);
            if(neighbour != null) {
              if(network != null) {
                network.destroyNetwork();
              }
              if(neighbour.getNetwork() != null) {
                neighbour.getNetwork().destroyNetwork();
              }
              neighbour.conduitConnectionRemoved(connDir.getOpposite());
              conduitConnectionRemoved(connDir);
              updateNetwork();
              neighbour.updateNetwork();
              return true;

            }

          }
        }
      }
    }
    return false;
  }

  @Override
  public ItemStack createItem() {
    return new ItemStack(ModObject.itemRedstoneConduit.actualId, 1, 2);
  }

  @Override
  public Class<? extends IConduit> getCollidableType() {
    return InsulatedRedstoneConduit.class;
  }

  @Override
  public void onInputsChanged(ForgeDirection side, int[] inputValues) {
    //System.out.println("InsulatedRedstoneConduit.onInputsChanged: ");
  }

  @Override
  public void onInputChanged(ForgeDirection side, int inputValue) {
    //System.out.println("InsulatedRedstoneConduit.onInputChanged: ");
  }

  private SignalColor getSignalColorFromEquippedItem(ItemStack currentEquippedItem) {
    if(currentEquippedItem == null) {
      return null;
    }
    int oreId = OreDictionary.getOreID(currentEquippedItem);
    if(oreId < 0) {
      return null;
    }
    for (int i = 0; i < DYE_ORE_NAMES.length; i++) {
      String dyeName = DYE_ORE_NAMES[i];
      if(OreDictionary.getOreID(dyeName) == oreId) {
        return SignalColor.values()[i];
      }
    }
    return null;
  }

  @Override
  public SignalColor getSignalColor(ForgeDirection dir) {
    SignalColor res = signalColors.get(dir);
    if(res == null) {
      return SignalColor.RED;
    }
    return res;
  }

  public void setSignalColor(ForgeDirection dir, SignalColor col) {
    Set<Signal> toRemove = getNetworkInputs(dir);
    signalColors.put(dir, col);
    Set<Signal> toAdd = getNetworkInputs(dir);
    if(network != null) {
      network.removeSignals(toRemove);
      network.addSignals(toAdd);
      network.notifyNeigborsOfSignals();
    }
    setClientStateDirty();
  }

  @Override
  public boolean canConnectToExternal(ForgeDirection direction) {
    if(forcedConnections.get(direction) == ConnectionMode.DISABLED) {
      return false;
    } else if(forcedConnections.get(direction) == ConnectionMode.IN_OUT) {
      return true;
    }
    //Not set so figure it out
    BlockCoord loc = getLocation().getLocation(direction);
    int id = getBundle().getEntity().worldObj.getBlockId(loc.x, loc.y, loc.z);

    if(VANILLA_CONECTABLES.contains(Integer.valueOf(id))) {
      return true;
    }

    if(id == ModObject.blockConduitBundle.actualId) {
      return false;
    }

    if(id == ModObject.blockCapacitorBank.actualId) {
      return true;
    }

    if(id == ModObject.blockElectricLight.actualId) {
      return true;
    }

    Block block = Block.blocksList[id];
    if(block == null) {
      return false;
    }

    if(block instanceof IRedNetNoConnection) {
      return false;
    }

    World world = getBundle().getEntity().worldObj;
    if(block instanceof IConnectableRedNet) {
      RedNetConnectionType conType = ((IConnectableRedNet) block).getConnectionType(world, loc.x, loc.y, loc.z, direction.getOpposite());
      try {
        return conType != null && (conType.isSingleSubnet || conType.isAllSubnets);
      } catch (NoSuchFieldError ex) {
        Log.error("InsulatedRedstoneConduit: An outdated version of the Rednet API has been found.");
        return true;
      }
    }

    if(world.getBlockTileEntity(loc.x, loc.y, loc.z) instanceof IPowerEmitter) {
      return true;
    }

    return false;
  }

  @Override
  public boolean onNeighborBlockChange(int blockId) {
    if(super.onNeighborBlockChange(blockId)) {
      BlockCoord loc = getLocation();
      getBundle().getEntity().worldObj.notifyBlocksOfNeighborChange(loc.x, loc.y, loc.z, 2);
      return true;
    }
    return false;
  }

  @Override
  public int isProvidingWeakPower(ForgeDirection toDirection) {
    if(getConectionMode(toDirection.getOpposite()) != ConnectionMode.IN_OUT) {
      return 0;
    }
    return super.isProvidingWeakPower(toDirection);
  }

  @Override
  public void externalConnectionAdded(ForgeDirection fromDirection) {
    super.externalConnectionAdded(fromDirection);
    setConnectionMode(fromDirection, ConnectionMode.IN_OUT);
  }

  @Override
  public void externalConnectionRemoved(ForgeDirection fromDirection) {
    super.externalConnectionRemoved(fromDirection);
    setConnectionMode(fromDirection, ConnectionMode.NOT_SET);
  }

  @Override
  public Set<Signal> getNetworkOutputs(ForgeDirection side) {
    ConnectionMode mode = getConectionMode(side);
    if(network == null || mode != ConnectionMode.IN_OUT) {
      return Collections.emptySet();
    }
    Set<Signal> allSigs = network.getSignals();
    if(allSigs.isEmpty()) {
      return allSigs;
    }

    SignalColor col = getSignalColor(side);
    Set<Signal> result = new HashSet<Signal>();
    for (Signal signal : allSigs) {
      if(signal.color == col) {
        result.add(signal);
      }
    }

    return result;
  }

  @Override
  public ConnectionMode getConectionMode(ForgeDirection dir) {
    ConnectionMode res = conectionModes.get(dir);
    if(res == null) {
      return ConnectionMode.NOT_SET;
    }
    return res;
  }

  @Override
  protected boolean acceptSignalsForDir(ForgeDirection dir) {
    return getConectionMode(dir) == ConnectionMode.IN_OUT && super.acceptSignalsForDir(dir);
  }

  @Override
  public Collection<CollidableComponent> createCollidables(CacheKey key) {
    Collection<CollidableComponent> baseCollidables = super.createCollidables(key);
    if(key.dir == ForgeDirection.UNKNOWN) {
      return baseCollidables;
    }

    BoundingBox bb = ConduitGeometryUtil.instance.createBoundsForConnectionController(key.dir, key.offset);
    CollidableComponent cc = new CollidableComponent(IRedstoneConduit.class, bb, key.dir, COLOR_CONTROLLER_ID);

    List<CollidableComponent> result = new ArrayList<CollidableComponent>();
    result.addAll(baseCollidables);
    result.add(cc);

    return result;
  }

  @Override
  public Icon getTextureForState(CollidableComponent component) {
    if(component.dir == ForgeDirection.UNKNOWN) {
      return isActive() ? ICONS.get(KEY_INS_CORE_ON_ICON) : ICONS.get(KEY_INS_CORE_OFF_ICON);
    }
    if(COLOR_CONTROLLER_ID.equals(component.data)) {
      return ICONS.get(KEY_COLOR_CONTROLLER);
    }
    return isActive() ? ICONS.get(KEY_INS_TRANSMISSION_ICON) : ICONS.get(KEY_INS_CONDUIT_ICON);
  }

  @Override
  protected boolean renderStub(ForgeDirection dir) {
    return false;
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);

    if(forcedConnections.size() >= 0) {
      byte[] modes = new byte[6];
      int i = 0;
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        ConnectionMode mode = forcedConnections.get(dir);
        if(mode != null) {
          modes[i] = (byte) mode.ordinal();
        } else {
          modes[i] = -1;
        }
        i++;
      }
      nbtRoot.setByteArray("forcedConnections", modes);
    }

    if(signalColors.size() >= 0) {
      byte[] modes = new byte[6];
      int i = 0;
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        SignalColor col = signalColors.get(dir);
        if(col != null) {
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
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);

    forcedConnections.clear();
    byte[] modes = nbtRoot.getByteArray("forcedConnections");
    if(modes != null && modes.length == 6) {
      int i = 0;
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if(modes[i] >= 0) {
          forcedConnections.put(dir, ConnectionMode.values()[modes[i]]);
        }
        i++;
      }
    }

    signalColors.clear();
    byte[] cols = nbtRoot.getByteArray("signalColors");
    if(cols != null && cols.length == 6) {
      int i = 0;
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if(cols[i] >= 0) {
          signalColors.put(dir, SignalColor.values()[cols[i]]);
        }
        i++;
      }
    }
  }

}
