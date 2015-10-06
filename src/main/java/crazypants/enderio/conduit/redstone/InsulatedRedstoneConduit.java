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
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.tileentity.IRedstoneControl;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.DyeColor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.api.redstone.IRedstoneConnectable;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.RaytraceResult;
import crazypants.enderio.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.tool.ToolUtil;

public class InsulatedRedstoneConduit extends RedstoneConduit implements IInsulatedRedstoneConduit {

  static final Map<String, IIcon> ICONS = new HashMap<String, IIcon>();

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IIconRegister register) {
        ICONS.put(KEY_INS_CORE_OFF_ICON, register.registerIcon(KEY_INS_CORE_OFF_ICON));
        ICONS.put(KEY_INS_CORE_ON_ICON, register.registerIcon(KEY_INS_CORE_ON_ICON));
        ICONS.put(KEY_INS_CONDUIT_ICON, register.registerIcon(KEY_INS_CONDUIT_ICON));
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }  

  private static final List<Block> CONECTABLE_BLOCKS = Arrays.asList(Blocks.redstone_lamp, Blocks.lit_redstone_lamp, Blocks.redstone_torch,
      Blocks.redstone_wire, Blocks.redstone_block, Blocks.dispenser, Blocks.lever, Blocks.wooden_button, Blocks.stone_button, Blocks.wooden_pressure_plate,
      Blocks.stone_pressure_plate, Blocks.dropper, Blocks.daylight_detector, Blocks.command_block, Blocks.golden_rail, Blocks.trapped_chest, Blocks.piston,
      Blocks.sticky_piston, Blocks.noteblock);

  private static Map<Class<?>, Boolean> CONNECTABLE_CLASSES = null;

  public static void addConnectableBlock(NBTTagCompound nbt) {
    if(nbt == null) {
      Log.warn("InsulatedRedstoneConduit: An attempt was made to register a redstone connectable using a null NBT");
      return;
    }
    boolean connectable = true;
    if(nbt.hasKey("isConnectable")) {
      connectable = nbt.getBoolean("isConnectable");
    }
    String className = nbt.getString("className");
    addConnectableInterface(className, connectable);
  }

  public static void addConnectableBlock(Block block) {
    if(block == null) {
      Log.warn("InsulatedRedstoneConduit: An attempt was made to register a redstone connectable using a null Block");
      return;
    }
    CONECTABLE_BLOCKS.add(block);
  }

  public static void addConnectableInterface(String className, boolean connectable) {
    try {
      Class<?> clz = Class.forName(className);
      addConnectableInterface(clz, connectable);
    } catch (Exception e) {
      Log.warn("InsulatedRedstoneConduit: An attempt was made to register " + className + " as connectable but it could not be loaded");
    }
  }

  public static void addConnectableInterface(Class<?> clazz, boolean connectable) {
    if(clazz == null) {
      Log.warn("InsulatedRedstoneConduit: An attempt was made to register a null class as a connectable");
      return;
    }
    getConnectableInterfaces().put(clazz, connectable);
  }

  private static Map<Class<?>, Boolean> getConnectableInterfaces() {
    if(CONNECTABLE_CLASSES == null) {
      CONNECTABLE_CLASSES = new HashMap<Class<?>, Boolean>();
      CONNECTABLE_CLASSES.put(IRedstoneControl.class, false);
      try {
        Class<?> conInterface = Class.forName("powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetConnection");
        CONNECTABLE_CLASSES.put(conInterface, false);
      } catch (Throwable e) {
        //NO-OP
      }
      try {
        Class<?> ccInterface = Class.forName("dan200.computercraft.shared.computer.blocks.IComputerTile");
        CONNECTABLE_CLASSES.put(ccInterface, true);
      } catch (Throwable e) {
        //NO-OP
      }
    }
    return CONNECTABLE_CLASSES;
  }

  private Map<ForgeDirection, ConnectionMode> forcedConnections = new HashMap<ForgeDirection, ConnectionMode>();

  private Map<ForgeDirection, DyeColor> signalColors = new HashMap<ForgeDirection, DyeColor>();
  
  private Map<ForgeDirection, Boolean> signalStrengths = new HashMap<ForgeDirection, Boolean>();

  @Override
  public boolean onBlockActivated(EntityPlayer player, RaytraceResult res, List<RaytraceResult> all) {

    World world = getBundle().getEntity().getWorldObj();
    if(!world.isRemote) {

      DyeColor col = DyeColor.getColorFromDye(player.getCurrentEquippedItem());
      if(col != null && res.component != null) {
        setSignalColor(res.component.dir, col);
        return true;
      } else if(ToolUtil.isToolEquipped(player)) {

        if(res != null && res.component != null) {
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
            setSignalColor(connDir, DyeColor.getNext(getSignalColor(connDir)));
            return true;

          } else if(connDir == ForgeDirection.UNKNOWN || connDir == faceHit) {

            BlockCoord loc = getLocation().getLocation(faceHit);
            Block id = world.getBlock(loc.x, loc.y, loc.z);
            if(id == EnderIO.blockConduitBundle) {
              IRedstoneConduit neighbour = ConduitUtil.getConduit(world, loc.x, loc.y, loc.z, IRedstoneConduit.class);
              if(neighbour != null && neighbour.getConnectionMode(faceHit.getOpposite()) == ConnectionMode.DISABLED) {
                neighbour.setConnectionMode(faceHit.getOpposite(), ConnectionMode.NOT_SET);
              }
              setConnectionMode(faceHit, ConnectionMode.NOT_SET);
              return ConduitUtil.joinConduits(this, faceHit);
            }
            forceConnectionMode(faceHit, ConnectionMode.IN_OUT);
            return true;

          } else if(externalConnections.contains(connDir)) {
            if(network != null) {
              network.destroyNetwork();
            }
            externalConnectionRemoved(connDir);
            forceConnectionMode(connDir, ConnectionMode.DISABLED);
            return true;

          } else if(containsConduitConnection(connDir)) {
            BlockCoord loc = getLocation().getLocation(connDir);
            IRedstoneConduit neighbour = ConduitUtil.getConduit(getBundle().getEntity().getWorldObj(), loc.x, loc.y, loc.z, IRedstoneConduit.class);
            if(neighbour != null) {
              if(network != null) {
                network.destroyNetwork();
              }
              if(neighbour.getNetwork() != null) {
                neighbour.getNetwork().destroyNetwork();
              }
              neighbour.conduitConnectionRemoved(connDir.getOpposite());
              conduitConnectionRemoved(connDir);
              neighbour.connectionsChanged();
              connectionsChanged();
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
  protected void readTypeSettings(ForgeDirection dir, NBTTagCompound dataRoot) {    
    setSignalColor(dir, DyeColor.values()[dataRoot.getShort("signalColor")]);
    setOutputStrength(dir, dataRoot.getBoolean("signalStrong"));
  }

  @Override
  protected void writeTypeSettingsToNbt(ForgeDirection dir, NBTTagCompound dataRoot) {
    dataRoot.setShort("signalColor", (short) getSignalColor(dir).ordinal());
    dataRoot.setBoolean("signalStrong", isOutputStrong(dir));
  }

  @Override
  public void forceConnectionMode(ForgeDirection dir, ConnectionMode mode) {
    if(mode == ConnectionMode.IN_OUT) {

      setConnectionMode(dir, mode);
      forcedConnections.put(dir, mode);
      onAddedToBundle();
      Set<Signal> newSignals = getNetworkInputs(dir);
      if(network != null) {
        network.addSignals(newSignals);
        network.notifyNeigborsOfSignals();
      }

    } else {

      Set<Signal> signals = getNetworkInputs(dir);
      setConnectionMode(dir, mode);
      forcedConnections.put(dir, mode);
      onAddedToBundle();
      if(network != null) {
        network.removeSignals(signals);
        network.notifyNeigborsOfSignals();
      }

    }
  }

  @Override
  public ConnectionMode getNextConnectionMode(ForgeDirection dir) {
    if(getConnectionMode(dir) == ConnectionMode.IN_OUT) {
      return ConnectionMode.DISABLED;
    }
    return ConnectionMode.IN_OUT;
  }

  @Override
  public ConnectionMode getPreviousConnectionMode(ForgeDirection dir) {
    if(getConnectionMode(dir) == ConnectionMode.IN_OUT) {
      return ConnectionMode.DISABLED;
    }
    return ConnectionMode.IN_OUT;
  }

  @Override
  public ItemStack createItem() {
    return new ItemStack(EnderIO.itemRedstoneConduit, 1, 2);
  }

  @Override
  public Class<? extends IConduit> getCollidableType() {
    return IInsulatedRedstoneConduit.class;
  }

  @Override
  public void onInputsChanged(ForgeDirection side, int[] inputValues) {
  }

  @Override
  public void onInputChanged(ForgeDirection side, int inputValue) {
  }

  @Override
  public DyeColor getSignalColor(ForgeDirection dir) {
    DyeColor res = signalColors.get(dir);
    if(res == null) {
      return DyeColor.RED;
    }
    return res;
  }

  @Override
  public void setSignalColor(ForgeDirection dir, DyeColor col) {
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
  public boolean isOutputStrong(ForgeDirection dir) {
    if(signalStrengths.containsKey(dir)) {
      return signalStrengths.get(dir);
    }
    return false;
  }

  @Override
  public void setOutputStrength(ForgeDirection dir, boolean isStrong) {
    if(isOutputStrong(dir) != isStrong) {
      if(isStrong) {
        signalStrengths.put(dir, isStrong);
      } else {
        signalStrengths.remove(dir);
      }
      if(network != null) {
        network.notifyNeigborsOfSignals();
      }
    }
  }

  @Override
  public boolean canConnectToExternal(ForgeDirection direction, boolean ignoreConnectionState) {
    if(ignoreConnectionState) { //you can always force an external connection
      return true;
    }
    if(forcedConnections.get(direction) == ConnectionMode.DISABLED) {
      return false;
    } else if(forcedConnections.get(direction) == ConnectionMode.IN_OUT) {
      return true;
    }
    //Not set so figure it out
    BlockCoord loc = getLocation().getLocation(direction);
    World world = getBundle().getEntity().getWorldObj();
    Block block = world.getBlock(loc.x, loc.y, loc.z);
    TileEntity te = world.getTileEntity(loc.x, loc.y, loc.z);

    if(block == null || block == EnderIO.blockConduitBundle) {
      return false;
    }
    
    if(CONECTABLE_BLOCKS.contains(block)) {
      return true;
    }
    
    if (block instanceof IRedstoneConnectable) {
      return ((IRedstoneConnectable) block).shouldRedstoneConduitConnect(world, loc.x, loc.y, loc.z, direction);
    }
    
    if (te instanceof IRedstoneConnectable) {
      return ((IRedstoneConnectable) te).shouldRedstoneConduitConnect(world, loc.x, loc.y, loc.z, direction);
    }

    Map<Class<?>, Boolean> connectableInterfaces = getConnectableInterfaces();
    for(Class<?> connectable : connectableInterfaces.keySet()) {
      if((te != null && connectable.isAssignableFrom(te.getClass())) || (connectable.isAssignableFrom(block.getClass()))) {
        return true;
      }
    }    

    return false;
  }

  @Override
  public boolean isSpecialConnection(ForgeDirection dir){
    BlockCoord loc = getLocation().getLocation(dir);
    Block block = getBundle().getEntity().getWorldObj().getBlock(loc.x, loc.y, loc.z);
    World world = getBundle().getEntity().getWorldObj();
    TileEntity te = world.getTileEntity(loc.x, loc.y, loc.z);

    Map<Class<?>, Boolean> connectableInterfaces = getConnectableInterfaces();
    for (Class<?> connectable : connectableInterfaces.keySet()) {
      if((te != null && connectable.isAssignableFrom(te.getClass())) || (block != null && connectable.isAssignableFrom(block.getClass()))) {
        return connectableInterfaces.get(connectable);
      }
    }

    return false;
  }

  @Override
  public int isProvidingWeakPower(ForgeDirection toDirection) {
    if(getConnectionMode(toDirection.getOpposite()) != ConnectionMode.IN_OUT) {
      return 0;
    }
    return super.isProvidingWeakPower(toDirection);    
  }

  @Override
  public int isProvidingStrongPower(ForgeDirection toDirection) {
    if(isOutputStrong(toDirection.getOpposite())) {
      return isProvidingWeakPower(toDirection);
    } else {
      return 0;
    }
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
    if(side == null || side == ForgeDirection.UNKNOWN) {
      return super.getNetworkOutputs(side);
    }

    ConnectionMode mode = getConnectionMode(side);
    if(network == null || mode != ConnectionMode.IN_OUT) {
      return Collections.emptySet();
    }
    Set<Signal> allSigs = network.getSignals();
    if(allSigs.isEmpty()) {
      return allSigs;
    }

    DyeColor col = getSignalColor(side);
    Set<Signal> result = new HashSet<Signal>();
    for (Signal signal : allSigs) {
      if(signal.color == col) {
        result.add(signal);
      }
    }

    return result;
  }

  @Override
  public ConnectionMode getConnectionMode(ForgeDirection dir) {
    ConnectionMode res = conectionModes.get(dir);
    if(res == null) {
      return ConnectionMode.NOT_SET;
    }
    return res;
  }

  @Override
  protected boolean acceptSignalsForDir(ForgeDirection dir) {
    return getConnectionMode(dir) == ConnectionMode.IN_OUT && super.acceptSignalsForDir(dir);
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
  public IIcon getTextureForState(CollidableComponent component) {
    if(component.dir == ForgeDirection.UNKNOWN) {
      return isActive() ? ICONS.get(KEY_INS_CORE_ON_ICON) : ICONS.get(KEY_INS_CORE_OFF_ICON);
    }
    if(COLOR_CONTROLLER_ID.equals(component.data)) {
      return IconUtil.whiteTexture;
    }
    return ICONS.get(KEY_INS_CONDUIT_ICON);
  }

  @Override
  public IIcon getTransmitionTextureForState(CollidableComponent component) {
    return isActive() ? RedstoneConduit.ICONS.get(KEY_TRANSMISSION_ICON) : RedstoneConduit.ICONS.get(KEY_CONDUIT_ICON);
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
        DyeColor col = signalColors.get(dir);
        if(col != null) {
          modes[i] = (byte) col.ordinal();
        } else {
          modes[i] = -1;
        }
        i++;
      }
      nbtRoot.setByteArray("signalColors", modes);
    }

    if(signalStrengths.size() >= 0) {
      byte[] modes = new byte[6];
      int i = 0;
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        boolean isStrong = isOutputStrong(dir);
        if(isStrong) {
          modes[i] = 1;
        } else {
          modes[i] = 0;
        }
        i++;
      }
      nbtRoot.setByteArray("signalStrengths", modes);
    }

  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot, short nbtVersion) {
    super.readFromNBT(nbtRoot, nbtVersion);

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
          signalColors.put(dir, DyeColor.values()[cols[i]]);
        }
        i++;
      }
    }

    signalStrengths.clear();
    byte[] strengths = nbtRoot.getByteArray("signalStrengths");
    if(strengths != null && strengths.length == 6) {
      int i = 0;
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if(strengths[i] > 0) {
          signalStrengths.put(dir, true);
        }
        i++;
      }
    }

  }

}
