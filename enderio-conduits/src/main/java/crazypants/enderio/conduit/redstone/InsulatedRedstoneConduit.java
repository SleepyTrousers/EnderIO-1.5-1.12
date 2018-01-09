package crazypants.enderio.conduit.redstone;

import static crazypants.enderio.base.ModObject.blockConduitBundle;
import static crazypants.enderio.base.ModObject.itemRedstoneConduit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.vecmath.Vector4f;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import crazypants.enderio.api.redstone.IRedstoneConnectable;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.base.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.IConduitComponent;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.gui.RedstoneSettings;
import crazypants.enderio.conduit.render.BlockStateWrapperConduitBundle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InsulatedRedstoneConduit extends AbstractConduit implements IRedstoneConduit, IConduitComponent {

  static final Map<String, TextureAtlasSprite> ICONS = new HashMap<String, TextureAtlasSprite>();

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(TextureMap register) {
        ICONS.put(KEY_INS_CORE_OFF_ICON, register.registerSprite(new ResourceLocation(KEY_INS_CORE_OFF_ICON)));
        ICONS.put(KEY_INS_CORE_ON_ICON, register.registerSprite(new ResourceLocation(KEY_INS_CORE_ON_ICON)));
        ICONS.put(KEY_INS_CONDUIT_ICON, register.registerSprite(new ResourceLocation(KEY_INS_CONDUIT_ICON)));
        ICONS.put(KEY_CONDUIT_ICON, register.registerSprite(new ResourceLocation(KEY_CONDUIT_ICON)));
        ICONS.put(KEY_TRANSMISSION_ICON, register.registerSprite(new ResourceLocation(KEY_TRANSMISSION_ICON)));
      }

    });
  }

  private static final List<Block> CONECTABLE_BLOCKS = Arrays.asList(Blocks.REDSTONE_LAMP, Blocks.LIT_REDSTONE_LAMP, Blocks.REDSTONE_TORCH,
      Blocks.REDSTONE_WIRE, Blocks.REDSTONE_BLOCK, Blocks.DISPENSER, Blocks.LEVER, Blocks.WOODEN_BUTTON, Blocks.STONE_BUTTON, Blocks.WOODEN_PRESSURE_PLATE,
      Blocks.STONE_PRESSURE_PLATE, Blocks.DROPPER, Blocks.DAYLIGHT_DETECTOR, Blocks.DAYLIGHT_DETECTOR_INVERTED, Blocks.COMMAND_BLOCK, Blocks.GOLDEN_RAIL,
      Blocks.TRAPPED_CHEST, Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.NOTEBLOCK);

  private static Map<Class<?>, Boolean> CONNECTABLE_CLASSES = null;
  private static final List<ISignalProvider> SIGNAL_PROVIDERS = new ArrayList<ISignalProvider>();

  public static void addConnectableBlock(NBTTagCompound nbt) {
    if (nbt == null) {
      Log.warn("InsulatedRedstoneConduit: An attempt was made to register a redstone connectable using a null NBT");
      return;
    }
    boolean connectable = true;
    if (nbt.hasKey("isConnectable")) {
      connectable = nbt.getBoolean("isConnectable");
    }
    String className = nbt.getString("className");
    addConnectableInterface(className, connectable);
  }

  public static void addConnectableBlock(Block block) {
    if (block == null) {
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
    if (clazz == null) {
      Log.warn("InsulatedRedstoneConduit: An attempt was made to register a null class as a connectable");
      return;
    }
    getConnectableInterfaces().put(clazz, connectable);
  }

  private static Map<Class<?>, Boolean> getConnectableInterfaces() {
    if (CONNECTABLE_CLASSES == null) {
      CONNECTABLE_CLASSES = new HashMap<Class<?>, Boolean>();
      // CONNECTABLE_CLASSES.put(IRedstoneControl.class, false);
      try {
        Class<?> conInterface = Class.forName("powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetConnection");
        CONNECTABLE_CLASSES.put(conInterface, false);
      } catch (Throwable e) {
        // NO-OP
      }
      try {
        Class<?> ccInterface = Class.forName("dan200.computercraft.shared.computer.blocks.IComputerTile");
        CONNECTABLE_CLASSES.put(ccInterface, true);
      } catch (Throwable e) {
        // NO-OP
      }
    }
    return CONNECTABLE_CLASSES;
  }

  public static void addSignalProvider(ISignalProvider provider) {
    SIGNAL_PROVIDERS.add(provider);
  }

  // --------------------------------- Class Start
  // -------------------------------------------

  private Map<EnumFacing, ConnectionMode> forcedConnections = new EnumMap<EnumFacing, ConnectionMode>(EnumFacing.class);

  private Map<EnumFacing, DyeColor> signalColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);

  private Map<EnumFacing, Boolean> signalStrengths = new EnumMap<EnumFacing, Boolean>(EnumFacing.class);

  private volatile Map<EnumFacing, Boolean> specialConnections = null;

  private final List<Set<Signal>> externalSignals = new ArrayList<Set<Signal>>();

  private RedstoneConduitNetwork network;
  
  private int activeUpdateCooldown = 0;
  
  private boolean activeDirty = false;

  @SuppressWarnings("unused")
  public InsulatedRedstoneConduit() {
    for (EnumFacing ignored : EnumFacing.VALUES) {
      externalSignals.add(new HashSet<Signal>());
    }
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
  public Class<? extends IConduit> getBaseConduitType() {
    return IRedstoneConduit.class;
  }

  @Override
  public void updateNetwork() {
    World world = getBundle().getEntity().getWorld();
    if (world != null) {
      updateNetwork(world);
    }
  }
  
  @Override
  public void updateEntity(World world) {
    super.updateEntity(world);

    if (!world.isRemote) {     
      if (activeUpdateCooldown > 0) {
        --activeUpdateCooldown;
        updateActiveState();
      }

    }
  }

  @Override
  public void setActive(boolean active) {
    if (active != this.active) {
      activeDirty = true;
    }
    this.active = active;

    updateActiveState();
  }

  private void updateActiveState() {
    if (Config.redstoneConduitsShowState && activeDirty && activeUpdateCooldown == 0) {      
      setClientStateDirty();
      activeDirty = false;
      activeUpdateCooldown = 4;
    }
  }

  @Override
  public void onChunkUnload(World world) {
    RedstoneConduitNetwork network = (RedstoneConduitNetwork) getNetwork();
    if (network != null) {
      Multimap<SignalSource, Signal> oldSignals = ArrayListMultimap.create(network.getSignals());
      List<IRedstoneConduit> conduits = Lists.newArrayList(network.getConduits());
      super.onChunkUnload(world);
      network.afterChunkUnload(conduits, oldSignals);
    }
  }

  @Override
  public boolean onBlockActivated(EntityPlayer player, EnumHand hand, RaytraceResult res, List<RaytraceResult> all) {

    World world = getBundle().getEntity().getWorld();
    if (!world.isRemote) {

      DyeColor col = DyeColor.getColorFromDye(player.getHeldItem(hand));
      if (col != null && res.component != null) {
        setSignalColor(res.component.dir, col);
        return true;
      } else if (ToolUtil.isToolEquipped(player, hand)) {

        if (res != null && res.component != null) {
          EnumFacing connDir = res.component.dir;
          EnumFacing faceHit = res.movingObjectPosition.sideHit;

          boolean colorHit = false;
          if (all != null && containsExternalConnection(connDir)) {
            for (RaytraceResult rtr : all) {
              if (rtr != null && rtr.component != null && COLOR_CONTROLLER_ID.equals(rtr.component.data)) {
                colorHit = true;
              }
            }
          }

          if (colorHit) {
            setSignalColor(connDir, DyeColor.getNext(getSignalColor(connDir)));
            return true;

          } else if (connDir == null || connDir == faceHit) {

            BlockCoord loc = getLocation().getLocation(faceHit);
            Block id = world.getBlockState(loc.getBlockPos()).getBlock();
            if (id == blockConduitBundle.getBlock()) {
              IRedstoneConduit neighbour = ConduitUtil.getConduit(world, loc.x, loc.y, loc.z, IRedstoneConduit.class);
              if (neighbour != null && neighbour.getConnectionMode(faceHit.getOpposite()) == ConnectionMode.DISABLED) {
                neighbour.setConnectionMode(faceHit.getOpposite(), ConnectionMode.NOT_SET);
              }
              setConnectionMode(faceHit, ConnectionMode.NOT_SET);
              return ConduitUtil.connectConduits(this, faceHit);
            }
            forceConnectionMode(faceHit, ConnectionMode.IN_OUT);
            return true;

          } else if (externalConnections.contains(connDir)) {
            if (network != null) {
              network.destroyNetwork();
            }
            externalConnectionRemoved(connDir);
            forceConnectionMode(connDir, ConnectionMode.DISABLED);
            return true;

          } else if (containsConduitConnection(connDir)) {
            BlockCoord loc = getLocation().getLocation(connDir);
            IRedstoneConduit neighbour = ConduitUtil.getConduit(getBundle().getEntity().getWorld(), loc.x, loc.y, loc.z, IRedstoneConduit.class);
            if (neighbour != null) {
              if (network != null) {
                network.destroyNetwork();
              }
              if (neighbour.getNetwork() != null) {
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
  public void forceConnectionMode(EnumFacing dir, ConnectionMode mode) {
    if (mode == ConnectionMode.IN_OUT) {

      setConnectionMode(dir, mode);
      forcedConnections.put(dir, mode);
      onAddedToBundle();
      if (network != null) {
        network.updateInputsFromConduit(this, false);
      }

    } else {
      setConnectionMode(dir, mode);
      forcedConnections.put(dir, mode);
      onAddedToBundle();
      if (network != null) {
        network.updateInputsFromConduit(this, false);
      }

    }
  }

  @Override
  public ConnectionMode getNextConnectionMode(EnumFacing dir) {
    if (getConnectionMode(dir) == ConnectionMode.IN_OUT) {
      return ConnectionMode.DISABLED;
    }
    return ConnectionMode.IN_OUT;
  }

  @Override
  public ConnectionMode getPreviousConnectionMode(EnumFacing dir) {
    if (getConnectionMode(dir) == ConnectionMode.IN_OUT) {
      return ConnectionMode.DISABLED;
    }
    return ConnectionMode.IN_OUT;
  }

  @Override
  public ItemStack createItem() {
    return new ItemStack(itemRedstoneConduit.getItem(), 1, 0);
  }

  @Override
  public void onInputsChanged(EnumFacing side, int[] inputValues) {
  }

  @Override
  public void onInputChanged(EnumFacing side, int inputValue) {
  }

  @Override
  public DyeColor getSignalColor(EnumFacing dir) {
    DyeColor res = signalColors.get(dir);
    if (res == null) {
      return DyeColor.RED;
    }
    return res;
  }

  @Override
  public void setSignalColor(EnumFacing dir, DyeColor col) {
    signalColors.put(dir, col);
    if (network != null) {
      network.updateInputsFromConduit(this, false);
    }
    setClientStateDirty();
  }

  @Override
  public boolean isOutputStrong(EnumFacing dir) {
    if (signalStrengths.containsKey(dir)) {
      return signalStrengths.get(dir);
    }
    return false;
  }

  @Override
  public void setOutputStrength(EnumFacing dir, boolean isStrong) {
    if (isOutputStrong(dir) != isStrong) {
      if (isStrong) {
        signalStrengths.put(dir, isStrong);
      } else {
        signalStrengths.remove(dir);
      }
      if (network != null) {
        network.notifyNeigborsOfSignalUpdate();
      }
    }
  }

  @Override
  public boolean canConnectToExternal(EnumFacing direction, boolean ignoreConnectionState) {
    if (ignoreConnectionState) { // you can always force an external connection
      return true;
    }
    if (forcedConnections.get(direction) == ConnectionMode.DISABLED) {
      return false;
    } else if (forcedConnections.get(direction) == ConnectionMode.IN_OUT) {
      return true;
    }
    // Not set so figure it out
    BlockCoord loc = getLocation().getLocation(direction);
    World world = getBundle().getEntity().getWorld();
    IBlockState bs = world.getBlockState(loc.getBlockPos());
    Block block = bs.getBlock();
    TileEntity te = world.getTileEntity(loc.getBlockPos());

    if (block == null || block == blockConduitBundle.getBlock()) {
      return false;
    }

    if (block instanceof IRedstoneConnectable) {
      return ((IRedstoneConnectable) block).shouldRedstoneConduitConnect(world, loc.x, loc.y, loc.z, direction);
    }

    if (te instanceof IRedstoneConnectable) {
      return ((IRedstoneConnectable) te).shouldRedstoneConduitConnect(world, loc.x, loc.y, loc.z, direction);
    }

    if (block.canConnectRedstone(bs, world, loc.getBlockPos(), direction.getOpposite()) || CONECTABLE_BLOCKS.contains(block)) {
      return true;
    }

    if (bs.canProvidePower()) {
      return true;
    }

    Map<Class<?>, Boolean> connectableInterfaces = getConnectableInterfaces();
    for (Class<?> connectable : connectableInterfaces.keySet()) {
      if ((te != null && connectable.isAssignableFrom(te.getClass())) || (connectable.isAssignableFrom(block.getClass()))) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean isSpecialConnection(EnumFacing dir) {
    if (specialConnections == null) {
      computeSpecialConnections();
    }
    return specialConnections.get(dir);
  }

  protected void computeSpecialConnections() {
    Map<EnumFacing, Boolean> temp = new EnumMap<EnumFacing, Boolean>(EnumFacing.class);
    SIDE: for (EnumFacing dir : EnumFacing.values()) {
      BlockCoord loc = getLocation().getLocation(dir);
      Block block = getBundle().getEntity().getWorld().getBlockState(loc.getBlockPos()).getBlock();
      World world = getBundle().getEntity().getWorld();
      TileEntity te = world.getTileEntity(loc.getBlockPos());

      Map<Class<?>, Boolean> connectableInterfaces = getConnectableInterfaces();
      for (Class<?> connectable : connectableInterfaces.keySet()) {
        if ((te != null && connectable.isAssignableFrom(te.getClass())) || (block != null && connectable.isAssignableFrom(block.getClass()))) {
          temp.put(dir, connectableInterfaces.get(connectable));
          continue SIDE;
        }
      }
      for(ISignalProvider provider : SIGNAL_PROVIDERS) {
        if(provider != null && provider.connectsToNetwork(world, loc.getBlockPos(), dir.getOpposite())) {
          temp.put(dir, true);
		  continue SIDE;
        }
      }
      temp.put(dir, false);
    }
    specialConnections = temp; // atomic assign for threading so no thread ever sees a half-filled map
  }

  @Override
  public int isProvidingWeakPower(EnumFacing toDirection) {
    toDirection = toDirection.getOpposite();
    if (getConnectionMode(toDirection) != ConnectionMode.IN_OUT) {
      return 0;
    }
    if (network == null || !network.isNetworkEnabled()) {
      return 0;
    }
    int result = 0;    
    for (Signal signal : getNetworkOutputs(toDirection)) {
      // don't return signals back to where they came from
      if (!signal.getPos().equals(getPos().offset(toDirection))) {
        result = Math.max(result, signal.strength);
      } 
    }
    return result;
  }

  private BlockPos getPos() {
    return bundle.getEntity().getPos();
  }

  @Override
  public int isProvidingStrongPower(EnumFacing toDirection) {
    if (isOutputStrong(toDirection.getOpposite())) {
      return isProvidingWeakPower(toDirection);
    } else {
      return 0;
    }
  }

  @Override
  public void externalConnectionAdded(EnumFacing fromDirection) {
    super.externalConnectionAdded(fromDirection);
    setConnectionMode(fromDirection, ConnectionMode.IN_OUT);
  }

  @Override
  public void externalConnectionRemoved(EnumFacing fromDirection) {
    super.externalConnectionRemoved(fromDirection);
    setConnectionMode(fromDirection, ConnectionMode.NOT_SET);
  }

  @Override
  public Collection<Signal> getNetworkOutputs(EnumFacing side) {
    if (side == null) {
      if (network == null) {
        return Collections.emptySet();
      }
      return network.getSignals().values();
    }

    ConnectionMode mode = getConnectionMode(side);
    if (network == null || mode != ConnectionMode.IN_OUT) {
      return Collections.emptySet();
    }
    Collection<Signal> allSigs = network.getSignals().values();
    if (allSigs.isEmpty() || isSpecialConnection(side)) {
      return allSigs;
    }

    DyeColor col = getSignalColor(side);
    Set<Signal> result = new HashSet<Signal>();
    for (Signal signal : allSigs) {
      if (signal.color == col) {
        result.add(signal);
      }
    }

    return result;
  }

  @Override
  public Set<Signal> getNetworkInputs(EnumFacing side) {
    if (network != null) {
      network.setNetworkEnabled(false);
    }

    HashSet<Signal> signals = new HashSet<Signal>();
    if(acceptSignalsForDir(side)) {
      if(isSpecialConnection(side)) {
        BlockCoord loc = getLocation().getLocation(side);
        World world = getBundle().getEntity().getWorld();
        for(ISignalProvider provider : SIGNAL_PROVIDERS) {
          Set<Signal> inputs = provider.getNetworkInputs(world, loc.getBlockPos(), side.getOpposite());
          if(inputs != null) {
            signals.addAll(inputs);
          }
        }
      } else {
        int input = getExternalPowerLevel(side);
        if(input > 1) { // need to degrade external signals by one as they
                        // enter
          BlockCoord loc = getLocation().getLocation(side);
          Signal signal = new Signal(loc.x, loc.y, loc.z, side, input - 1, getSignalColor(side));
          signals.add(signal);
        }
      }
    }

    if (network != null) {
      network.setNetworkEnabled(true);
    }

    Map<DyeColor, Signal> res = new HashMap<DyeColor, Signal>();
    for(Signal signal : signals) {
      if(signal != null && (!res.containsKey(signal.color) || signal.strength > res.get(signal.color).strength)) {
        res.put(signal.color, signal);
      }
    }

    return new HashSet<Signal>(res.values());
  }

  protected int getExternalPowerLevel(EnumFacing dir) {
    World world = getBundle().getEntity().getWorld();
    BlockPos loc = getLocation().getBlockPos().offset(dir);
    int res = 0;

    if (world.isBlockLoaded(loc)) {
      int strong = world.getStrongPower(loc, dir);
      if (strong > 0) {
        return strong;
      }

      res = world.getRedstonePower(loc, dir);
      IBlockState bs = world.getBlockState(loc);
      Block block = bs.getBlock();
      if (res < 15 && block == Blocks.REDSTONE_WIRE) {
        int wireIn = bs.getValue(BlockRedstoneWire.POWER);
        res = Math.max(res, wireIn);
      }
    }

    return res;
  }

  @Override
  public ConnectionMode getConnectionMode(EnumFacing dir) {
    ConnectionMode res = conectionModes.get(dir);
    if (res == null) {
      return ConnectionMode.NOT_SET;
    }
    return res;
  }

  
  @Override
  public boolean onNeighborBlockChange(Block blockId) {
    World world = getBundle().getEntity().getWorld();
    if (world.isRemote) {
      return false;
    }
    boolean res = super.onNeighborBlockChange(blockId);
    if (network == null || network.updatingNetwork) {
      return false;
    }
    if (blockId != blockConduitBundle.getBlock()) {
      computeSpecialConnections();
      if (hasExternalConnections()) {
        network.updateInputsFromConduit(this, false);
      }
    }
    return res;
  }

  private boolean acceptSignalsForDir(EnumFacing dir) {
    if (getConnectionMode(dir) != ConnectionMode.IN_OUT) {
      return false;
    }
    BlockCoord loc = getLocation().getLocation(dir);
    return ConduitUtil.getConduit(getBundle().getEntity().getWorld(), loc.x, loc.y, loc.z, IRedstoneConduit.class) == null;
  }

  @Override
  public Collection<CollidableComponent> createCollidables(CacheKey key) {
    Collection<CollidableComponent> baseCollidables = super.createCollidables(key);
    if (key.dir == null) {
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
  public TextureAtlasSprite getTextureForState(CollidableComponent component) {
    if (component.dir == null) {
      return Config.redstoneConduitsShowState && isActive() ? ICONS.get(KEY_INS_CORE_ON_ICON) : ICONS.get(KEY_INS_CORE_OFF_ICON);
    }
    if (COLOR_CONTROLLER_ID.equals(component.data)) {
      return IconUtil.instance.whiteTexture;
    }
    return ICONS.get(KEY_INS_CONDUIT_ICON);
  }

  @Override
  public TextureAtlasSprite getTransmitionTextureForState(CollidableComponent component) {
    return Config.redstoneConduitsShowState && isActive() ? ICONS.get(KEY_TRANSMISSION_ICON) : ICONS.get(KEY_CONDUIT_ICON);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Vector4f getTransmitionTextureColorForState(CollidableComponent component) {
    return null;
  }

  @Override
  protected boolean renderStub(EnumFacing dir) {
    return false;
  }

  @Override
  protected void readTypeSettings(EnumFacing dir, NBTTagCompound dataRoot) {
    setSignalColor(dir, DyeColor.values()[dataRoot.getShort("signalColor")]);
    setOutputStrength(dir, dataRoot.getBoolean("signalStrong"));
  }

  @Override
  protected void writeTypeSettingsToNbt(EnumFacing dir, NBTTagCompound dataRoot) {
    dataRoot.setShort("signalColor", (short) getSignalColor(dir).ordinal());
    dataRoot.setBoolean("signalStrong", isOutputStrong(dir));
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);

    if (forcedConnections.size() >= 0) {
      byte[] modes = new byte[6];
      int i = 0;
      for (EnumFacing dir : EnumFacing.VALUES) {
        ConnectionMode mode = forcedConnections.get(dir);
        if (mode != null) {
          modes[i] = (byte) mode.ordinal();
        } else {
          modes[i] = -1;
        }
        i++;
      }
      nbtRoot.setByteArray("forcedConnections", modes);
    }

    if (signalColors.size() >= 0) {
      byte[] modes = new byte[6];
      int i = 0;
      for (EnumFacing dir : EnumFacing.VALUES) {
        DyeColor col = signalColors.get(dir);
        if (col != null) {
          modes[i] = (byte) col.ordinal();
        } else {
          modes[i] = -1;
        }
        i++;
      }
      nbtRoot.setByteArray("signalColors", modes);
    }

    if (signalStrengths.size() >= 0) {
      byte[] modes = new byte[6];
      int i = 0;
      for (EnumFacing dir : EnumFacing.VALUES) {
        boolean isStrong = isOutputStrong(dir);
        if (isStrong) {
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
    if (modes != null && modes.length == 6) {
      int i = 0;
      for (EnumFacing dir : EnumFacing.VALUES) {
        if (modes[i] >= 0) {
          forcedConnections.put(dir, ConnectionMode.values()[modes[i]]);
        }
        i++;
      }
    }

    signalColors.clear();
    byte[] cols = nbtRoot.getByteArray("signalColors");
    if (cols != null && cols.length == 6) {
      int i = 0;
      for (EnumFacing dir : EnumFacing.VALUES) {
        if (cols[i] >= 0) {
          signalColors.put(dir, DyeColor.values()[cols[i]]);
        }
        i++;
      }
    }

    signalStrengths.clear();
    byte[] strengths = nbtRoot.getByteArray("signalStrengths");
    if (strengths != null && strengths.length == 6) {
      int i = 0;
      for (EnumFacing dir : EnumFacing.VALUES) {
        if (strengths[i] > 0) {
          signalStrengths.put(dir, true);
        }
        i++;
      }
    }

  }

  @Override
  public String toString() {
    return "RedstoneConduit [network=" + network + " connections=" + conduitConnections + " active=" + active + "]";
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void hashCodeForModelCaching(IBlockStateWrapper wrapper, BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
    super.hashCodeForModelCaching(wrapper, hashCodes);
    hashCodes.addEnum(signalColors);
    if (specialConnections == null) {
      computeSpecialConnections();
    }
    hashCodes.addBoolean(specialConnections);
    if (Config.redstoneConduitsShowState && isActive()) {
      hashCodes.add(1);
    }
  }

  @Override
  public RedstoneConduitNetwork createNetworkForType() {
    return new RedstoneConduitNetwork();
  }

  @SideOnly(Side.CLIENT)
  @Override
  public ITabPanel createPanelForConduit(GuiExternalConnection gui, IConduit con) {
    return new RedstoneSettings(gui, con);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public int getTabOrderForConduit(IConduit con) {
    return 2;
  }

}
