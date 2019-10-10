package crazypants.enderio.conduits.conduit.redstone;

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
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitNetwork;
import crazypants.enderio.base.conduit.IConduitTexture;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.redstone.ConnectivityTool;
import crazypants.enderio.base.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.base.conduit.redstone.signals.CombinedSignal;
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
import crazypants.enderio.base.filter.redstone.items.IItemInputSignalFilterUpgrade;
import crazypants.enderio.base.filter.redstone.items.IItemOutputSignalFilterUpgrade;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduits.conduit.AbstractConduit;
import crazypants.enderio.conduits.config.ConduitConfig;
import crazypants.enderio.conduits.gui.RedstoneSettings;
import crazypants.enderio.conduits.render.BlockStateWrapperConduitBundle;
import crazypants.enderio.conduits.render.ConduitTexture;
import crazypants.enderio.powertools.lang.Lang;
import crazypants.enderio.util.EnumReader;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.conduits.init.ConduitObject.item_redstone_conduit;

public class InsulatedRedstoneConduit extends AbstractConduit implements IRedstoneConduit, IFilterHolder<IRedstoneSignalFilter> {

  static final Map<String, IConduitTexture> ICONS = new HashMap<>();

  static {
    ICONS.put(KEY_INS_CORE_OFF_ICON, new ConduitTexture(TextureRegistry.registerTexture("blocks/conduit_core_1"), ConduitTexture.core(3)));
    ICONS.put(KEY_INS_CORE_ON_ICON, new ConduitTexture(TextureRegistry.registerTexture("blocks/conduit_core_0"), ConduitTexture.core(3)));
    ICONS.put(KEY_INS_CONDUIT_ICON, new ConduitTexture(TextureRegistry.registerTexture("blocks/conduit"), ConduitTexture.arm(1)));
    ICONS.put(KEY_CONDUIT_ICON, new ConduitTexture(TextureRegistry.registerTexture("blocks/conduit"), ConduitTexture.arm(3)));
    ICONS.put(KEY_TRANSMISSION_ICON, new ConduitTexture(TextureRegistry.registerTexture("blocks/conduit"), ConduitTexture.arm(2)));
  }

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

  private boolean connectionsDirty = false; // TODO: can this be merged with super.connectionsDirty?

  @SuppressWarnings("unused")
  public InsulatedRedstoneConduit() {
  }

  @Override
  public @Nullable RedstoneConduitNetwork getNetwork() {
    return network;
  }

  @FunctionalInterface
  private interface Callback<T> {
    void accept(@Nonnull T t);
  }

  protected void withNetwork(@Nonnull Callback<RedstoneConduitNetwork> callback) {
    if (network != null) {
      callback.accept(network);
    }
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
    if (NullHelper.untrust(world) != null) {
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
    withNetwork(n -> n.removeConduit(this));
    super.onChunkUnload();
  }

  @Override
  public boolean onBlockActivated(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull RaytraceResult res, @Nonnull List<RaytraceResult> all) {

    World world = getBundle().getEntity().getWorld();

    DyeColor col = DyeColor.getColorFromDye(player.getHeldItem(hand));
    final CollidableComponent component = res.component;
    if (col != null && component != null && component.isDirectional()) {
      if (!world.isRemote) {
        if (getConnectionMode(component.getDirection()).acceptsInput()) {
          // Note: There's no way to set the input color in IN_OUT mode...
          setOutputSignalColor(component.getDirection(), col);
        } else {
          setInputSignalColor(component.getDirection(), col);
        }
      }
      return true;
    } else if (ToolUtil.isToolEquipped(player, hand)) {
      if (world.isRemote) {
        return true;
      }

      if (component != null) {
        EnumFacing faceHit = res.movingObjectPosition.sideHit;

        if (component.isCore()) {

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
          forceConnectionMode(faceHit, ConnectionMode.IN_OUT);
          return true;

        } else {
          EnumFacing connDir = component.getDirection();
          if (externalConnections.contains(connDir)) {
            withNetwork(n -> n.destroyNetwork());
            externalConnectionRemoved(connDir);
            forceConnectionMode(connDir, ConnectionMode.getNext(getConnectionMode(connDir)));
            return true;

          } else if (containsConduitConnection(connDir)) {
            BlockPos pos = getBundle().getLocation().offset(connDir);
            IRedstoneConduit neighbour = ConduitUtil.getConduit(getBundle().getEntity().getWorld(), pos.getX(), pos.getY(), pos.getZ(), IRedstoneConduit.class);
            if (neighbour != null) {
              withNetwork(n -> n.destroyNetwork());
              final RedstoneConduitNetwork neighbourNetwork = neighbour.getNetwork();
              if (neighbourNetwork != null) {
                neighbourNetwork.destroyNetwork();
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
    if (network == null || !network.isUpdatingNetwork()) {
      return 0;
    }
    toDirection = toDirection.getOpposite();
    if (!getConnectionMode(toDirection).acceptsInput()) {
      return 0;
    }
    int result = 0;
    CombinedSignal signal = getNetworkOutput(toDirection);
    result = Math.max(result, signal.getStrength());
    return result;
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

  protected int getExternalPowerLevelProtected(@Nonnull EnumFacing side) {
    if (network != null) {
      network.setNetworkEnabled(false);
    }

    int input = getExternalPowerLevel(side);

    if (network != null) {
      network.setNetworkEnabled(true);
    }

    return input;
  }

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
      if (stack != null && Prep.isValid(stack)) {
        res.add(stack);
      }
    }
    for (ItemStack stack : outputFilterUpgrades.values()) {
      if (stack != null && Prep.isValid(stack)) {
        res.add(stack);
      }
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

  // ---------------------
  // TEXTURES
  // ---------------------

  @Override
  public int getInternalNetworkSignalForColor(@Nonnull DyeColor col) {
    if (network != null) {
      return network.getSignalLevel(EnumDyeColor.byDyeDamage(col.ordinal()));
    }
    return 0;
  }

  @SuppressWarnings("null")
  @Override
  @Nonnull
  public IConduitTexture getTextureForState(@Nonnull CollidableComponent component) {
    if (component.isCore()) {
      return ConduitConfig.showState.get() && isActive() ? ICONS.get(KEY_INS_CORE_ON_ICON) : ICONS.get(KEY_INS_CORE_OFF_ICON);
    }
    return ICONS.get(KEY_INS_CONDUIT_ICON);
  }

  @SuppressWarnings("null")
  @Override
  @Nonnull
  public IConduitTexture getTransmitionTextureForState(@Nonnull CollidableComponent component) {
    return ConduitConfig.showState.get() && isActive() ? ICONS.get(KEY_TRANSMISSION_ICON) : ICONS.get(KEY_CONDUIT_ICON);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable Vector4f getTransmitionTextureColorForState(@Nonnull CollidableComponent component) {
    return null;
  }

  @Override
  protected void readTypeSettings(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    forceConnectionMode(dir, EnumReader.get(ConnectionMode.class, dataRoot.getShort("connectionMode")));
    setInputSignalColor(dir, EnumReader.get(DyeColor.class, dataRoot.getShort("inputSignalColor")));
    setOutputSignalColor(dir, EnumReader.get(DyeColor.class, dataRoot.getShort("outputSignalColor")));
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
        boolean isStrong = dir != null && isOutputStrong(dir);
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
      ItemStack up = entry.getValue();
      if (up != null && Prep.isValid(up)) {
        IRedstoneSignalFilter filter = getSignalFilter(entry.getKey(), true);
        FilterRegistry.writeFilterToStack(filter, up);

        NBTTagCompound itemRoot = new NBTTagCompound();
        up.writeToNBT(itemRoot);
        nbtRoot.setTag("inputSignalFilterUpgrades." + entry.getKey().name(), itemRoot);
      }
    }

    for (Entry<EnumFacing, ItemStack> entry : outputFilterUpgrades.entrySet()) {
      ItemStack up = entry.getValue();
      if (up != null && Prep.isValid(up)) {
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
    if (modes.length == 6) {
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
    if (cols.length == 6) {
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
    if (outCols.length == 6) {
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
    if (strengths.length == 6) {
      int i = 0;
      for (EnumFacing dir : EnumFacing.VALUES) {
        if (strengths[i] > 0) {
          signalStrengths.put(dir, true);
        }
        i++;
      }
    }

    inputFilters.clear();
    outputFilters.clear();
    inputFilterUpgrades.clear();
    outputFilterUpgrades.clear();
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
  public void onAddedToNetwork() {
    signalIdBase = id;
  }

  @Override
  @Nonnull
  public IRedstoneSignalFilter getSignalFilter(@Nonnull EnumFacing dir, boolean isOutput) {
    if (!isOutput) {
      return NullHelper.first(inputFilters.get(dir), DefaultInputSignalFilter.instance);
    } else {
      return NullHelper.first(outputFilters.get(dir), DefaultOutputSignalFilter.instance);
    }
  }

  public void setSignalFilter(@Nonnull EnumFacing dir, boolean isInput, @Nonnull IRedstoneSignalFilter filter) {
    if (!isInput) {
      inputFilters.put(dir, filter);
    } else {
      outputFilters.put(dir, filter);
    }
    setClientStateDirty();
    connectionsDirty = true;
  }

  @Override
  public @Nonnull IRedstoneSignalFilter getFilter(int filterIndex, int param1) {
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
      return NullHelper.first(inputFilterUpgrades.get(EnumFacing.getFront(param1)), Prep.getEmpty());
    } else if (filterIndex == getOutputFilterIndex()) {
      return NullHelper.first(outputFilterUpgrades.get(EnumFacing.getFront(param1)), Prep.getEmpty());
    }
    return Prep.getEmpty();
  }

  @Override
  public void setFilterStack(int filterIndex, int param1, @Nonnull ItemStack stack) {
    if (filterIndex == getInputFilterIndex()) {
      if (Prep.isValid(stack)) {
        inputFilterUpgrades.put(EnumFacing.getFront(param1), stack);
      } else {
        inputFilterUpgrades.remove(EnumFacing.getFront(param1));
      }
    } else if (filterIndex == getOutputFilterIndex()) {
      if (Prep.isValid(stack)) {
        outputFilterUpgrades.put(EnumFacing.getFront(param1), stack);
      } else {
        outputFilterUpgrades.remove(EnumFacing.getFront(param1));
      }
    }
    final IRedstoneSignalFilter filterForUpgrade = FilterRegistry.<IRedstoneSignalFilter> getFilterForUpgrade(stack);
    if (filterForUpgrade != null) {
      setFilter(filterIndex, param1, filterForUpgrade);
    }
  }

  @Override
  public int getInputFilterIndex() {
    return FilterGuiUtil.INDEX_INPUT_REDSTONE;
  }

  @Override
  public int getOutputFilterIndex() {
    return FilterGuiUtil.INDEX_OUTPUT_REDSTONE;
  }

  @Override
  public boolean isFilterUpgradeAccepted(@Nonnull ItemStack stack, boolean isInput) {
    if (!isInput) {
      return stack.getItem() instanceof IItemInputSignalFilterUpgrade;
    } else {
      return stack.getItem() instanceof IItemOutputSignalFilterUpgrade;
    }
  }

  @Override
  @Nonnull
  public NNList<ITextComponent> getConduitProbeInformation(@Nonnull EntityPlayer player) {
    final NNList<ITextComponent> result = super.getConduitProbeInformation(player);

    if (getExternalConnections().isEmpty()) {
      ITextComponent elem = Lang.GUI_CONDUIT_PROBE_REDSTONE_HEADING_NO_CONNECTIONS.toChatServer();
      elem.getStyle().setColor(TextFormatting.GOLD);
      result.add(elem);
    } else {
      for (EnumFacing dir : getExternalConnections()) {
        if (dir == null) {
          continue;
        }

        ITextComponent elem = Lang.GUI_CONDUIT_PROBE_REDSTONE_HEADING.toChatServer(new TextComponentTranslation(EnderIO.lang.addPrefix("facing." + dir)));
        elem.getStyle().setColor(TextFormatting.GREEN);
        result.add(elem);

        ConnectionMode mode = getConnectionMode(dir);
        if (mode.acceptsInput()) {
          elem = Lang.GUI_CONDUIT_PROBE_REDSTONE_STRONG.toChatServer(isProvidingStrongPower(dir));
          elem.getStyle().setColor(TextFormatting.BLUE);
          result.add(elem);

          elem = Lang.GUI_CONDUIT_PROBE_REDSTONE_WEAK.toChatServer(isProvidingWeakPower(dir));
          elem.getStyle().setColor(TextFormatting.BLUE);
          result.add(elem);
        }
        if (mode.acceptsOutput()) {
          elem = Lang.GUI_CONDUIT_PROBE_REDSTONE_EXTERNAL.toChatServer(getExternalPowerLevelProtected(dir));
          elem.getStyle().setColor(TextFormatting.BLUE);
          result.add(elem);
        }

      }
    }

    return result;
  }

}
