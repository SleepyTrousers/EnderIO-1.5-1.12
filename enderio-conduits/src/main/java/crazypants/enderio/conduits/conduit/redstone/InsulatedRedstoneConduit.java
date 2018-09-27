package crazypants.enderio.conduits.conduit.redstone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.vecmath.Vector4f;
import com.google.common.collect.Lists;

import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitNetwork;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.redstone.ConnectivityTool;
import crazypants.enderio.base.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.base.conduit.redstone.signals.CombinedSignal;
import crazypants.enderio.base.conduit.redstone.signals.Signal;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.diagnostics.Prof;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.capability.CapabilityFilterHolder;
import crazypants.enderio.base.filter.capability.IFilterHolder;
import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.filter.redstone.DefaultInputSignalFilter;
import crazypants.enderio.base.filter.redstone.DefaultOutputSignalFilter;
import crazypants.enderio.base.filter.redstone.IInputSignalFilter;
import crazypants.enderio.base.filter.redstone.IOutputSignalFilter;
import crazypants.enderio.base.filter.redstone.IRedstoneSignalFilter;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduits.conduit.AbstractConduit;
import crazypants.enderio.conduits.conduit.IConduitComponent;
import crazypants.enderio.conduits.config.ConduitConfig;
import crazypants.enderio.conduits.gui.RedstoneSettings;
import crazypants.enderio.conduits.render.BlockStateWrapperConduitBundle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.conduits.init.ConduitObject.item_redstone_conduit;

public class InsulatedRedstoneConduit extends AbstractConduit implements IRedstoneConduit, IConduitComponent, IFilterHolder<IRedstoneSignalFilter> {

  static final Map<String, TextureSupplier> ICONS = new HashMap<>();

  static {
    ICONS.put(KEY_INS_CORE_OFF_ICON, TextureRegistry.registerTexture(KEY_INS_CORE_OFF_ICON));
    ICONS.put(KEY_INS_CORE_ON_ICON, TextureRegistry.registerTexture(KEY_INS_CORE_ON_ICON));
    ICONS.put(KEY_INS_CONDUIT_ICON, TextureRegistry.registerTexture(KEY_INS_CONDUIT_ICON));
    ICONS.put(KEY_CONDUIT_ICON, TextureRegistry.registerTexture(KEY_CONDUIT_ICON));
    ICONS.put(KEY_TRANSMISSION_ICON, TextureRegistry.registerTexture(KEY_TRANSMISSION_ICON));
  }

  public static final TextureSupplier ICON_IN_OUT_KEY = TextureRegistry.registerTexture("blocks/item_conduit_in_out");
  public static final TextureSupplier ICON_KEY_IN_OUT_BG = TextureRegistry.registerTexture("blocks/item_conduit_io_connector");
  public static final TextureSupplier ICON_KEY_INPUT = TextureRegistry.registerTexture("blocks/item_conduit_input");
  public static final TextureSupplier ICON_KEY_OUTPUT = TextureRegistry.registerTexture("blocks/item_conduit_output");
  public static final TextureSupplier ICON_KEY_IN_OUT_OUT = TextureRegistry.registerTexture("blocks/item_conduit_in_out_out");
  public static final TextureSupplier ICON_KEY_IN_OUT_IN = TextureRegistry.registerTexture("blocks/item_conduit_in_out_in");

  // --------------------------------- Class Start
  // -------------------------------------------

  private final EnumMap<EnumFacing, IRedstoneSignalFilter> outputFilters = new EnumMap<EnumFacing, IRedstoneSignalFilter>(EnumFacing.class);
  private final EnumMap<EnumFacing, IRedstoneSignalFilter> inputFilters = new EnumMap<EnumFacing, IRedstoneSignalFilter>(EnumFacing.class);
  private final EnumMap<EnumFacing, ItemStack> outputFilterUpgrades = new EnumMap<EnumFacing, ItemStack>(EnumFacing.class);
  private final EnumMap<EnumFacing, ItemStack> inputFilterUpgrades = new EnumMap<EnumFacing, ItemStack>(EnumFacing.class);

  private Map<EnumFacing, ConnectionMode> forcedConnections = new EnumMap<EnumFacing, ConnectionMode>(EnumFacing.class);

  private Map<EnumFacing, DyeColor> inputSignalColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);

  private Map<EnumFacing, DyeColor> outputSignalColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);

  private Map<EnumFacing, Boolean> signalStrengths = new EnumMap<EnumFacing, Boolean>(EnumFacing.class);

  private RedstoneConduitNetwork network;

  private int activeUpdateCooldown = 0;

  private boolean activeDirty = false;

  private boolean connectionsDirty = false;

  private int signalIdBase = 0;

  @SuppressWarnings("unused")
  public InsulatedRedstoneConduit() {
    super();
    for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
      EnumFacing dir = itr.next();
      outputFilters.put(dir, new DefaultOutputSignalFilter());
      inputFilters.put(dir, new DefaultInputSignalFilter());
      outputFilterUpgrades.put(dir, ItemStack.EMPTY);
      inputFilterUpgrades.put(dir, ItemStack.EMPTY);
    }

  }

  @Override
  public @Nullable RedstoneConduitNetwork getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(@Nonnull IConduitNetwork<?, ?> network) {
    this.network = (RedstoneConduitNetwork) network;
    return super.setNetwork(network);
  }

  @Override
  public void clearNetwork() {
    this.network = null;
  }

  @Override
  @Nonnull
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
  public void updateEntity(@Nonnull World world) {
    super.updateEntity(world);

    if (!world.isRemote) {
      if (activeUpdateCooldown > 0) {
        --activeUpdateCooldown;
        Prof.start(world, "updateActiveState");
        updateActiveState();
        Prof.stop(world);
      }

      if (connectionsDirty) {
        if (hasExternalConnections()) {
          network.updateInputsFromConduit(this, false);
        }
        connectionsDirty = false;
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
    if (ConduitConfig.showState.get() && activeDirty && activeUpdateCooldown == 0) {
      setClientStateDirty();
      activeDirty = false;
      activeUpdateCooldown = 4;
    }
  }

  @Override
  public void onChunkUnload() {
    RedstoneConduitNetwork networkR = (RedstoneConduitNetwork) getNetwork();
    if (networkR != null) {
      BundledSignal oldSignals = networkR.getBundledSignal();
      List<IRedstoneConduit> conduits = Lists.newArrayList(networkR.getConduits());
      super.onChunkUnload();
      networkR.afterChunkUnload(conduits, oldSignals);
    }
  }

  @Override
  public boolean onBlockActivated(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull RaytraceResult res, @Nonnull List<RaytraceResult> all) {

    World world = getBundle().getEntity().getWorld();
    if (!world.isRemote) {

      DyeColor col = DyeColor.getColorFromDye(player.getHeldItem(hand));
      if (col != null && res.component != null) {
        setInputSignalColor(res.component.dir, col);
        return true;
      } else if (ToolUtil.isToolEquipped(player, hand)) {

        if (res != null && res.component != null) {
          EnumFacing connDir = res.component.dir;
          EnumFacing faceHit = res.movingObjectPosition.sideHit;

          if (connDir == null || connDir == faceHit) {

            BlockPos pos = getBundle().getLocation().offset(faceHit);
            Block id = world.getBlockState(pos).getBlock();
            if (id == ConduitRegistry.getConduitModObjectNN().getBlock()) {
              IRedstoneConduit neighbour = ConduitUtil.getConduit(world, pos.getX(), pos.getY(), pos.getZ(), IRedstoneConduit.class);
              if (neighbour != null && neighbour.getConnectionMode(faceHit.getOpposite()) == ConnectionMode.DISABLED) {
                neighbour.setConnectionMode(faceHit.getOpposite(), ConnectionMode.NOT_SET);
              }
              setConnectionMode(faceHit, ConnectionMode.NOT_SET);
              return ConduitUtil.connectConduits(this, faceHit);
            }
            forceConnectionMode(faceHit, ConnectionMode.INPUT);
            return true;

          } else if (externalConnections.contains(connDir)) {
            if (network != null) {
              network.destroyNetwork();
            }
            externalConnectionRemoved(connDir);
            forceConnectionMode(connDir, ConnectionMode.DISABLED);
            return true;

          } else if (containsConduitConnection(connDir)) {
            BlockPos pos = getBundle().getLocation().offset(connDir);
            IRedstoneConduit neighbour = ConduitUtil.getConduit(getBundle().getEntity().getWorld(), pos.getX(), pos.getY(), pos.getZ(), IRedstoneConduit.class);
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
  public void forceConnectionMode(@Nonnull EnumFacing dir, @Nonnull ConnectionMode mode) {
    setConnectionMode(dir, mode);
    forcedConnections.put(dir, mode);
    onAddedToBundle();
    if (network != null) {
      network.updateInputsFromConduit(this, false);
    }
  }

  @Override
  @Nonnull
  public ItemStack createItem() {
    return new ItemStack(item_redstone_conduit.getItemNN(), 1, 0);
  }

  @Override
  public void onInputsChanged(@Nonnull EnumFacing side, int[] inputValues) {
  }

  @Override
  public void onInputChanged(@Nonnull EnumFacing side, int inputValue) {
  }

  @Override
  @Nonnull
  public DyeColor getInputSignalColor(@Nonnull EnumFacing dir) {
    DyeColor res = inputSignalColors.get(dir);
    if (res == null) {
      return DyeColor.RED;
    }
    return res;
  }

  @Override
  public void setInputSignalColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col) {
    inputSignalColors.put(dir, col);
    if (network != null) {
      network.updateInputsFromConduit(this, false);
    }
    setClientStateDirty();
    collidablesDirty = true;
  }

  @Override
  @Nonnull
  public DyeColor getOutputSignalColor(@Nonnull EnumFacing dir) {
    DyeColor res = outputSignalColors.get(dir);
    if (res == null) {
      return DyeColor.GREEN;
    }
    return res;
  }

  @Override
  public void setOutputSignalColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col) {
    outputSignalColors.put(dir, col);
    if (network != null) {
      network.updateInputsFromConduit(this, false);
    }
    setClientStateDirty();
    collidablesDirty = true;
  }

  @Override
  public boolean isOutputStrong(@Nonnull EnumFacing dir) {
    if (signalStrengths.containsKey(dir)) {
      return signalStrengths.get(dir);
    }
    return false;
  }

  @Override
  public void setOutputStrength(@Nonnull EnumFacing dir, boolean isStrong) {
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
  public boolean canConnectToExternal(@Nonnull EnumFacing direction, boolean ignoreConnectionState) {
    if (ignoreConnectionState) { // you can always force an external connection
      return true;
    }
    ConnectionMode forcedConnection = forcedConnections.get(direction);
    if (forcedConnection == ConnectionMode.DISABLED) {
      return false;
    } else if (forcedConnection == ConnectionMode.IN_OUT || forcedConnection == ConnectionMode.OUTPUT || forcedConnection == ConnectionMode.INPUT) {
      return true;
    }
    // Not set so figure it out
    World world = getBundle().getBundleworld();
    BlockPos pos = getBundle().getLocation().offset(direction);
    IBlockState bs = world.getBlockState(pos);
    if (bs.getBlock() == ConduitRegistry.getConduitModObjectNN().getBlock()) {
      return false;
    }
    return ConnectivityTool.shouldAutoConnectRedstone(world, bs, pos, direction.getOpposite());
  }

  @Override
  public int isProvidingWeakPower(@Nonnull EnumFacing toDirection) {
    toDirection = toDirection.getOpposite();
    if (!getConnectionMode(toDirection).acceptsInput()) {
      return 0;
    }
    if (network == null || !network.isNetworkEnabled()) {
      return 0;
    }
    int result = 0;
    CombinedSignal signal = getNetworkOutput(toDirection);
    result = Math.max(result, signal.getStrength());
    return result;
  }

  @Nonnull
  private BlockPos getPos() {
    return getBundle().getLocation();
  }

  @Override
  public int isProvidingStrongPower(@Nonnull EnumFacing toDirection) {
    if (isOutputStrong(toDirection.getOpposite())) {
      return isProvidingWeakPower(toDirection);
    } else {
      return 0;
    }
  }

  @Override
  @Nonnull
  public CombinedSignal getNetworkOutput(@Nonnull EnumFacing side) {
    ConnectionMode mode = getConnectionMode(side);
    if (network == null || !mode.acceptsInput()) {
      return CombinedSignal.NONE;
    }
    DyeColor col = getOutputSignalColor(side);
    BundledSignal bundledSignal = network.getBundledSignal();
    return bundledSignal.getFilteredSignal(col, (IOutputSignalFilter) getSignalFilter(side, true));
  }

  @Override
  @Nonnull
  public Signal getNetworkInput(@Nonnull EnumFacing side) {
    if (network != null) {
      network.setNetworkEnabled(false);
    }

    CombinedSignal result = CombinedSignal.NONE;
    if (acceptSignalsForDir(side)) {
      int input = getExternalPowerLevel(side);
      result = new CombinedSignal(input);
      IInputSignalFilter filter = (IInputSignalFilter) getSignalFilter(side, false);

      result = filter.apply(result, getBundle().getBundleworld(), getBundle().getLocation().offset(side));
    }

    if (network != null) {
      network.setNetworkEnabled(true);
    }

    return new Signal(result, signalIdBase + side.ordinal());
  }

  protected int getExternalPowerLevel(@Nonnull EnumFacing dir) {
    World world = getBundle().getBundleworld();
    BlockPos loc = getBundle().getLocation().offset(dir);
    int res = 0;

    if (world.isBlockLoaded(loc)) {
      int strong = world.getStrongPower(loc, dir);
      if (strong > 0) {
        return strong;
      }

      res = world.getRedstonePower(loc, dir);
      IBlockState bs = world.getBlockState(loc);
      Block block = bs.getBlock();
      if (res <= 15 && block == Blocks.REDSTONE_WIRE) {
        int wireIn = bs.getValue(BlockRedstoneWire.POWER);
        res = Math.max(res, wireIn);
      }
    }

    return res;
  }

  // @Optional.Method(modid = "computercraft")
  // @Override
  // @Nonnull
  // public Map<DyeColor, Signal> getComputerCraftSignals(@Nonnull EnumFacing side) {
  // Map<DyeColor, Signal> ccSignals = new EnumMap<DyeColor, Signal>(DyeColor.class);
  //
  // int bundledInput = getComputerCraftBundledPowerLevel(side);
  // if (bundledInput >= 0) {
  // for (int i = 0; i < 16; i++) {
  // int color = bundledInput >>> i & 1;
  // Signal signal = new Signal(color == 1 ? 16 : 0, signalIdBase + side.ordinal());
  // ccSignals.put(DyeColor.fromIndex(Math.max(0, 15 - i)), signal);
  // }
  // }
  //
  // return ccSignals;
  // }

  // @Optional.Method(modid = "computercraft")
  // private int getComputerCraftBundledPowerLevel(EnumFacing dir) {
  // World world = getBundle().getBundleworld();
  // BlockPos pos = getBundle().getLocation().offset(dir);
  //
  // if (world.isBlockLoaded(pos)) {
  // return ComputerCraftAPI.getBundledRedstoneOutput(world, pos, dir.getOpposite());
  // } else {
  // return -1;
  // }
  // }

  @Override
  @Nonnull
  public ConnectionMode getConnectionMode(@Nonnull EnumFacing dir) {
    ConnectionMode res = forcedConnections.get(dir);
    if (res == null) {
      return getDefaultConnectionMode();
    }
    return res;
  }

  @Override
  @Nonnull
  public ConnectionMode getDefaultConnectionMode() {
    return ConnectionMode.OUTPUT;
  }

  @Override
  @Nonnull
  public NNList<ItemStack> getDrops() {
    NNList<ItemStack> res = super.getDrops();
    for (ItemStack stack : inputFilterUpgrades.values()) {
      res.add(stack);
    }
    for (ItemStack stack : outputFilterUpgrades.values()) {
      res.add(stack);
    }
    return res;
  }

  @Override
  public boolean onNeighborBlockChange(@Nonnull Block blockId) {
    World world = getBundle().getBundleworld();
    if (world.isRemote) {
      return false;
    }
    boolean res = super.onNeighborBlockChange(blockId);
    if (network == null || network.updatingNetwork) {
      return false;
    }
    if (blockId != ConduitRegistry.getConduitModObjectNN().getBlock()) {
      connectionsDirty = true;
    }
    return res;
  }

  private boolean acceptSignalsForDir(@Nonnull EnumFacing dir) {
    if (!getConnectionMode(dir).acceptsOutput()) {
      return false;
    }
    BlockPos loc = getBundle().getLocation().offset(dir);
    return ConduitUtil.getConduit(getBundle().getEntity().getWorld(), loc.getX(), loc.getY(), loc.getZ(), IRedstoneConduit.class) == null;
  }

  @Override
  @Nonnull
  public Collection<CollidableComponent> createCollidables(@Nonnull CacheKey key) {
    Collection<CollidableComponent> baseCollidables = super.createCollidables(key);
    if (key.dir == null) {
      return baseCollidables;
    }

    List<CollidableComponent> result = new ArrayList<CollidableComponent>();
    result.addAll(baseCollidables);

    return result;
  }

  // ---------------------
  // TEXTURES
  // ---------------------

  @Override
  public int getRedstoneSignalForColor(@Nonnull DyeColor col) {
    if (network != null) {
      return network.getSignalStrengthForColor(col);
    }
    return 0;
  }

  @Override
  @Nonnull
  public TextureAtlasSprite getTextureForState(@Nonnull CollidableComponent component) {
    if (component.dir == null) {
      return ConduitConfig.showState.get() && isActive() ? ICONS.get(KEY_INS_CORE_ON_ICON).get(TextureAtlasSprite.class)
          : ICONS.get(KEY_INS_CORE_OFF_ICON).get(TextureAtlasSprite.class);
    }
    return ICONS.get(KEY_INS_CONDUIT_ICON).get(TextureAtlasSprite.class);
  }

  @Override
  @Nonnull
  public TextureAtlasSprite getTransmitionTextureForState(@Nonnull CollidableComponent component) {
    return ConduitConfig.showState.get() && isActive() ? ICONS.get(KEY_TRANSMISSION_ICON).get(TextureAtlasSprite.class)
        : ICONS.get(KEY_CONDUIT_ICON).get(TextureAtlasSprite.class);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull Vector4f getTransmitionTextureColorForState(@Nonnull CollidableComponent component) {
    return null;
  }

  @SideOnly(Side.CLIENT)
  @Override
  @Nonnull
  public TextureAtlasSprite getTextureForInputMode() {
    return ICON_KEY_INPUT.get(TextureAtlasSprite.class);
  }

  @SideOnly(Side.CLIENT)
  @Override
  @Nonnull
  public TextureAtlasSprite getTextureForInOutMode(boolean input) {
    return input ? ICON_KEY_IN_OUT_IN.get(TextureAtlasSprite.class) : ICON_KEY_IN_OUT_OUT.get(TextureAtlasSprite.class);
  }

  @SideOnly(Side.CLIENT)
  @Override
  @Nonnull
  public TextureAtlasSprite getTextureForOutputMode() {
    return ICON_KEY_OUTPUT.get(TextureAtlasSprite.class);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getTextureForInOutMode() {
    return ICON_IN_OUT_KEY.get(TextureAtlasSprite.class);
  }

  @SideOnly(Side.CLIENT)
  @Override
  @Nonnull
  public TextureAtlasSprite getTextureForInOutBackground() {
    return ICON_KEY_IN_OUT_BG.get(TextureAtlasSprite.class);
  }

  @Override
  protected void readTypeSettings(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    forceConnectionMode(dir, ConnectionMode.values()[dataRoot.getShort("connectionMode")]);
    setInputSignalColor(dir, DyeColor.values()[dataRoot.getShort("inputSignalColor")]);
    setOutputSignalColor(dir, DyeColor.values()[dataRoot.getShort("outputSignalColor")]);
    setOutputStrength(dir, dataRoot.getBoolean("signalStrong"));
  }

  @Override
  protected void writeTypeSettingsToNbt(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    dataRoot.setShort("connectionMode", (short) forcedConnections.get(dir).ordinal());
    dataRoot.setShort("inputSignalColor", (short) getInputSignalColor(dir).ordinal());
    dataRoot.setShort("outputSignalColor", (short) getOutputSignalColor(dir).ordinal());
    dataRoot.setBoolean("signalStrong", isOutputStrong(dir));
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
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

    if (inputSignalColors.size() >= 0) {
      byte[] modes = new byte[6];
      int i = 0;
      for (EnumFacing dir : EnumFacing.VALUES) {
        DyeColor col = inputSignalColors.get(dir);
        if (col != null) {
          modes[i] = (byte) col.ordinal();
        } else {
          modes[i] = -1;
        }
        i++;
      }
      nbtRoot.setByteArray("signalColors", modes);
    }

    if (outputSignalColors.size() >= 0) {
      byte[] modes = new byte[6];
      int i = 0;
      for (EnumFacing dir : EnumFacing.VALUES) {
        DyeColor col = outputSignalColors.get(dir);
        if (col != null) {
          modes[i] = (byte) col.ordinal();
        } else {
          modes[i] = -1;
        }
        i++;
      }
      nbtRoot.setByteArray("outputSignalColors", modes);
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

    for (Entry<EnumFacing, IRedstoneSignalFilter> entry : inputFilters.entrySet()) {
      if (entry.getValue() != null) {
        IRedstoneSignalFilter f = entry.getValue();
        NBTTagCompound itemRoot = new NBTTagCompound();
        FilterRegistry.writeFilterToNbt(f, itemRoot);
        nbtRoot.setTag("inSignalFilts." + entry.getKey().name(), itemRoot);
      }
    }
    for (Entry<EnumFacing, IRedstoneSignalFilter> entry : outputFilters.entrySet()) {
      if (entry.getValue() != null) {
        IRedstoneSignalFilter f = entry.getValue();
        NBTTagCompound itemRoot = new NBTTagCompound();
        FilterRegistry.writeFilterToNbt(f, itemRoot);
        nbtRoot.setTag("outSignalFilts." + entry.getKey().name(), itemRoot);
      }
    }
    for (Entry<EnumFacing, ItemStack> entry : inputFilterUpgrades.entrySet()) {
      if (entry.getValue() != null) {
        ItemStack up = entry.getValue();
        IRedstoneSignalFilter filter = getSignalFilter(entry.getKey(), true);
        FilterRegistry.writeFilterToStack(filter, up);

        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("inputSignalFilterUpgrades." + entry.getKey().name(), itemRoot);
      }
    }

    for (Entry<EnumFacing, ItemStack> entry : outputFilterUpgrades.entrySet()) {
      if (entry.getValue() != null) {
        ItemStack up = entry.getValue();
        IRedstoneSignalFilter filter = getSignalFilter(entry.getKey(), false);
        FilterRegistry.writeFilterToStack(filter, up);

        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("outputSignalFilterUpgrades." + entry.getKey().name(), itemRoot);
      }
    }

  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);

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

    inputSignalColors.clear();
    byte[] cols = nbtRoot.getByteArray("signalColors");
    if (cols != null && cols.length == 6) {
      int i = 0;
      for (EnumFacing dir : EnumFacing.VALUES) {
        if (cols[i] >= 0) {
          inputSignalColors.put(dir, DyeColor.values()[cols[i]]);
        }
        i++;
      }
    }

    outputSignalColors.clear();
    byte[] outCols = nbtRoot.getByteArray("outputSignalColors");
    if (outCols != null && outCols.length == 6) {
      int i = 0;
      for (EnumFacing dir : EnumFacing.VALUES) {
        if (outCols[i] >= 0) {
          outputSignalColors.put(dir, DyeColor.values()[outCols[i]]);
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

    for (EnumFacing dir : EnumFacing.VALUES) {
      String key = "inSignalFilts." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound filterTag = (NBTTagCompound) nbtRoot.getTag(key);
        IRedstoneSignalFilter filter = (IRedstoneSignalFilter) FilterRegistry.loadFilterFromNbt(filterTag);
        inputFilters.put(dir, filter);
      }

      key = "inputSignalFilterUpgrades." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound upTag = (NBTTagCompound) nbtRoot.getTag(key);
        ItemStack ups = new ItemStack(upTag);
        inputFilterUpgrades.put(dir, ups);
      }

      key = "outputSignalFilterUpgrades." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound upTag = (NBTTagCompound) nbtRoot.getTag(key);
        ItemStack ups = new ItemStack(upTag);
        outputFilterUpgrades.put(dir, ups);
      }

      key = "outSignalFilts." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound filterTag = (NBTTagCompound) nbtRoot.getTag(key);
        IRedstoneSignalFilter filter = (IRedstoneSignalFilter) FilterRegistry.loadFilterFromNbt(filterTag);
        outputFilters.put(dir, filter);
      }
    }

  }

  @Override
  public String toString() {
    return "RedstoneConduit [network=" + network + " connections=" + conduitConnections + " active=" + active + "]";
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void hashCodeForModelCaching(BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
    super.hashCodeForModelCaching(hashCodes);
    hashCodes.addEnum(inputSignalColors);
    hashCodes.addEnum(outputSignalColors);
    if (ConduitConfig.showState.get() && isActive()) {
      hashCodes.add(1);
    }
  }

  @Override
  public @Nonnull RedstoneConduitNetwork createNetworkForType() {
    return new RedstoneConduitNetwork();
  }

  @SideOnly(Side.CLIENT)
  @Nonnull
  @Override
  public ITabPanel createGuiPanel(@Nonnull IGuiExternalConnection gui, @Nonnull IClientConduit con) {
    return new RedstoneSettings(gui, con);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean updateGuiPanel(@Nonnull ITabPanel panel) {
    if (panel instanceof RedstoneSettings) {
      return ((RedstoneSettings) panel).updateConduit(this);
    }
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public int getGuiPanelTabOrder() {
    return 2;
  }

  // ----------------- CAPABILITIES ------------

  @Override
  public boolean hasInternalCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY) {
      return true;
    }
    return super.hasInternalCapability(capability, facing);
  }

  @Override
  @Nullable
  public <T> T getInternalCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY) {
      return CapabilityFilterHolder.FILTER_HOLDER_CAPABILITY.cast(this);
    }
    return super.getInternalCapability(capability, facing);
  }

  // -------------------------------------------
  // FILTERS
  // -------------------------------------------

  @Override
  public void setSignalIdBase(int id) {
    signalIdBase = id;
  }

  @Override
  @Nonnull
  public IRedstoneSignalFilter getSignalFilter(@Nonnull EnumFacing dir, boolean isOutput) {
    if (!isOutput) {
      return inputFilters.get(dir);
    } else {
      return outputFilters.get(dir);
    }
  }

  public void setSignalFilter(@Nonnull EnumFacing dir, boolean isInput, @Nonnull IRedstoneSignalFilter filter) {
    if (!isInput) {
      inputFilters.put(dir, filter != null ? filter : new DefaultInputSignalFilter());
    } else {
      outputFilters.put(dir, filter != null ? filter : new DefaultOutputSignalFilter());
    }
    setClientStateDirty();
    connectionsDirty = true;
  }

  @Override
  public IRedstoneSignalFilter getFilter(int filterIndex, int param1) {
    return getSignalFilter(EnumFacing.getFront(param1), filterIndex == getInputFilterIndex() ? true : !(filterIndex == getOutputFilterIndex()));
  }

  @Override
  public void setFilter(int filterIndex, int param1, @Nonnull IRedstoneSignalFilter filter) {
    setSignalFilter(EnumFacing.getFront(param1), filterIndex == getInputFilterIndex() ? true : !(filterIndex == getOutputFilterIndex()), filter);
  }

  @Override
  @Nonnull
  public ItemStack getFilterStack(int filterIndex, int param1) {
    if (filterIndex == getInputFilterIndex()) {
      return inputFilterUpgrades.get(EnumFacing.getFront(param1));
    } else if (filterIndex == getOutputFilterIndex()) {
      return outputFilterUpgrades.get(EnumFacing.getFront(param1));
    }
    return ItemStack.EMPTY;
  }

  @Override
  public void setFilterStack(int filterIndex, int param1, @Nonnull ItemStack stack) {
    if (filterIndex == getInputFilterIndex()) {
      inputFilterUpgrades.put(EnumFacing.getFront(param1), stack);
    } else if (filterIndex == getOutputFilterIndex()) {
      outputFilterUpgrades.put(EnumFacing.getFront(param1), stack);
    }
    setFilter(filterIndex, param1, FilterRegistry.<IRedstoneSignalFilter> getFilterForUpgrade(stack));
  }

  @Override
  public int getInputFilterIndex() {
    return FilterGuiUtil.INDEX_INPUT_REDSTONE;
  }

  @Override
  public int getOutputFilterIndex() {
    return FilterGuiUtil.INDEX_OUTPUT_REDSTONE;
  }
}
