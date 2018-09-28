package crazypants.enderio.conduits.conduit.power;

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
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.IConduitNetwork;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.power.IPowerInterface;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduits.conduit.AbstractConduit;
import crazypants.enderio.conduits.conduit.IConduitComponent;
import crazypants.enderio.conduits.config.ConduitConfig;
import crazypants.enderio.conduits.gui.PowerSettings;
import crazypants.enderio.conduits.render.BlockStateWrapperConduitBundle;
import crazypants.enderio.powertools.lang.Lang;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.base.conduit.ConnectionMode.INPUT;
import static crazypants.enderio.base.conduit.ConnectionMode.OUTPUT;

public class PowerConduit extends AbstractConduit implements IPowerConduit, IConduitComponent {

  static final Map<String, TextureSupplier> ICONS = new HashMap<>();

  static final String[] POSTFIX = new String[] { "", "_enhanced", "_ender" };

  static {
    for (String pf : POSTFIX) {
      ICONS.put(ICON_KEY + pf, TextureRegistry.registerTexture(ICON_KEY + pf));
      ICONS.put(ICON_CORE_KEY + pf, TextureRegistry.registerTexture(ICON_CORE_KEY + pf));
    }
    ICONS.put(ICON_KEY_INPUT, TextureRegistry.registerTexture(ICON_KEY_INPUT));
    ICONS.put(ICON_KEY_OUTPUT, TextureRegistry.registerTexture(ICON_KEY_OUTPUT));
    ICONS.put(ICON_TRANSMISSION_KEY, TextureRegistry.registerTexture(ICON_TRANSMISSION_KEY));
  }

  public static final float WIDTH = 0.075f;
  public static final float HEIGHT = 0.075f;

  public static final @Nonnull Vector3d MIN = new Vector3d(0.5f - WIDTH, 0.5 - HEIGHT, 0.5 - WIDTH);
  public static final @Nonnull Vector3d MAX = new Vector3d(MIN.x + WIDTH, MIN.y + HEIGHT, MIN.z + WIDTH);

  public static final @Nonnull BoundingBox BOUNDS = new BoundingBox(MIN, MAX);

  protected PowerConduitNetwork network;

  private int energyStoredRF;

  private @Nonnull IPowerConduitData subtype = IPowerConduitData.Registry.fromID(0);

  protected final EnumMap<EnumFacing, RedstoneControlMode> rsModes = new EnumMap<EnumFacing, RedstoneControlMode>(EnumFacing.class);
  protected final EnumMap<EnumFacing, DyeColor> rsColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);

  protected EnumMap<EnumFacing, Long> recievedTicks;

  public PowerConduit() {
  }

  public PowerConduit(@Nonnull IPowerConduitData subtype) {
    this.subtype = subtype;
  }

  @Override
  public boolean getConnectionsDirty() {
    return connectionsDirty;
  }

  @Override
  @Nonnull
  public String getConduitProbeInfo(@Nonnull EntityPlayer player) {
    PowerConduitNetwork pcn = (PowerConduitNetwork) getNetwork();
    if (pcn == null) {
      return super.getConduitProbeInfo(player);
    }
    NetworkPowerManager pm = pcn.getPowerManager();
    PowerTracker tracker = pm.getTracker(this);

    if (tracker != null) {
      String color = "\u00A7a ";
      StringBuilder sb = new StringBuilder(super.getConduitProbeInfo(player));
      sb.append(color);
      sb.append(Lang.GUI_ENERGY_CONDUIT.get());

      color = "\u00A79 ";
      sb.append(color);
      sb.append(Lang.GUI_CONDUIT_PROBE_CONDUIT_BUFFER.get());
      sb.append(" ");
      sb.append(LangPower.RF(getEnergyStored()));
      sb.append(" ");
      sb.append(Lang.GUI_POWER_MONITOR_OF.get());
      sb.append(" ");
      sb.append(LangPower.RF(getMaxEnergyStored()));
      sb.append("\n");
      sb.append(Lang.GUI_POWER_MONITOR_AVERAGE_OUTPUT.get());
      sb.append(" ");
      sb.append(LangPower.RF(tracker.getAverageRfTickSent()));
      sb.append("\n");
      sb.append(Lang.GUI_POWER_MONITOR_AVERAGE_INPUT.get());
      sb.append(" ");
      sb.append(LangPower.RF(tracker.getAverageRfTickRecieved()));

      return sb.toString();
    } else {
      tracker = pm.getNetworkPowerTracker();
      String color = "\u00A7a ";
      StringBuilder sb = new StringBuilder(super.getConduitProbeInfo(player));
      sb.append(color);
      sb.append(Lang.GUI_CONDUIT_PROBE_NETWORK_HEADING.get());
      sb.append("\n");

      color = "\u00A79 ";
      sb.append(color);
      sb.append(Lang.GUI_POWER_MONITOR_CONDUIT_STORAGE.get());
      sb.append(" ");
      sb.append(LangPower.RF(pm.getPowerInConduits()));
      sb.append(" ");
      sb.append(Lang.GUI_POWER_MONITOR_OF.get());
      sb.append(" ");
      sb.append(LangPower.RF(pm.getMaxPowerInConduits()));
      sb.append("\n");
      sb.append(Lang.GUI_POWER_MONITOR_CAPBANK_STORAGE.get());
      sb.append(" ");
      sb.append(LangPower.RF(pm.getPowerInCapacitorBanks()));
      sb.append(" ");
      sb.append(Lang.GUI_POWER_MONITOR_OF.get());
      sb.append(" ");
      sb.append(LangPower.RF(pm.getMaxPowerInCapacitorBanks()));
      sb.append("\n");
      sb.append(Lang.GUI_POWER_MONITOR_MACHINE_BUFFER.get());
      sb.append(" ");
      sb.append(LangPower.RF(pm.getPowerInReceptors()));
      sb.append(" ");
      sb.append(Lang.GUI_POWER_MONITOR_OF.get());
      sb.append(" ");
      sb.append(LangPower.RF(pm.getMaxPowerInReceptors()));
      sb.append("\n");
      sb.append(Lang.GUI_POWER_MONITOR_AVERAGE_OUTPUT.get());
      sb.append(" ");
      sb.append(LangPower.RF(tracker.getAverageRfTickSent()));
      sb.append("\n");
      sb.append(Lang.GUI_POWER_MONITOR_AVERAGE_INPUT.get());
      sb.append(" ");
      sb.append(LangPower.RF(tracker.getAverageRfTickRecieved()));

      return sb.toString();
    }
  }

  @Override
  public boolean onBlockActivated(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull RaytraceResult res, @Nonnull List<RaytraceResult> all) {
    if (ConduitUtil.isProbeEquipped(player, hand)) {
      return false;
    } else {
      final CollidableComponent component = res.component;
      DyeColor col = DyeColor.getColorFromDye(player.getHeldItemMainhand());
      if (col != null && component != null && component.isDirectional() && isColorBandRendered(component.getDirection())) {
        setExtractionSignalColor(component.getDirection(), col);
        return true;
      } else if (ToolUtil.isToolEquipped(player, hand)) {
        if (!getBundle().getEntity().getWorld().isRemote) {
          if (component != null) {
            EnumFacing faceHit = res.movingObjectPosition.sideHit;
            if (component.isCore()) {
              if (getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
                setConnectionMode(faceHit, getNextConnectionMode(faceHit));
                return true;
              }
              // Attempt to join networks
              return ConduitUtil.connectConduits(this, faceHit);
            } else {
              EnumFacing connDir = component.getDirection();
              if (externalConnections.contains(connDir)) {
                setConnectionMode(connDir, getNextConnectionMode(connDir));
              } else if (containsConduitConnection(connDir)) {
                ConduitUtil.disconnectConduits(this, connDir);
              }
            }
          }
        }
        return true;
      }
    }
    return false;
  }

  private boolean isColorBandRendered(@Nonnull EnumFacing dir) {
    return getConnectionMode(dir) != ConnectionMode.DISABLED && getExtractionRedstoneMode(dir) != RedstoneControlMode.IGNORE;
  }

  @Override
  public void setExtractionRedstoneMode(@Nonnull RedstoneControlMode mode, @Nonnull EnumFacing dir) {
    rsModes.put(dir, mode);
    setClientStateDirty();
  }

  @Override
  @Nonnull
  public RedstoneControlMode getExtractionRedstoneMode(@Nonnull EnumFacing dir) {
    RedstoneControlMode res = rsModes.get(dir);
    if (res == null) {
      res = RedstoneControlMode.IGNORE;
    }
    return res;
  }

  @Override
  public void setExtractionSignalColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col) {
    rsColors.put(dir, col);
    setClientStateDirty();
  }

  @Override
  @Nonnull
  public DyeColor getExtractionSignalColor(@Nonnull EnumFacing dir) {
    DyeColor res = rsColors.get(dir);
    if (res == null) {
      res = DyeColor.RED;
    }
    return res;
  }

  @Override
  protected void readTypeSettings(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    setConnectionMode(dir, ConnectionMode.values()[dataRoot.getShort("connectionMode")]);
    setExtractionSignalColor(dir, DyeColor.values()[dataRoot.getShort("extractionSignalColor")]);
    setExtractionRedstoneMode(RedstoneControlMode.fromOrdinal(dataRoot.getShort("extractionRedstoneMode")), dir);
  }

  @Override
  protected void writeTypeSettingsToNbt(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    dataRoot.setShort("connectionMode", (short) getConnectionMode(dir).ordinal());
    dataRoot.setShort("extractionSignalColor", (short) getExtractionSignalColor(dir).ordinal());
    dataRoot.setShort("extractionRedstoneMode", (short) getExtractionRedstoneMode(dir).ordinal());
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    nbtRoot.setShort("subtype", (short) subtype.getID());
    nbtRoot.setInteger("energyStoredRF", energyStoredRF);

    for (Entry<EnumFacing, RedstoneControlMode> entry : rsModes.entrySet()) {
      if (entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("pRsMode." + entry.getKey().name(), ord);
      }
    }

    for (Entry<EnumFacing, DyeColor> entry : rsColors.entrySet()) {
      if (entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("pRsCol." + entry.getKey().name(), ord);
      }
    }
  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    subtype = IPowerConduitData.Registry.fromID(nbtRoot.getShort("subtype"));

    if (nbtRoot.hasKey("energyStored")) {
      nbtRoot.setInteger("energyStoredRF", (int) (nbtRoot.getFloat("energyStored") * 10));

    }
    setEnergyStored(nbtRoot.getInteger("energyStoredRF"));

    for (EnumFacing dir : EnumFacing.VALUES) {
      String key = "pRsMode." + dir.name();
      if (nbtRoot.hasKey(key)) {
        short ord = nbtRoot.getShort(key);
        if (ord >= 0 && ord < RedstoneControlMode.values().length) {
          rsModes.put(dir, RedstoneControlMode.values()[ord]);
        }
      }
      key = "pRsCol." + dir.name();
      if (nbtRoot.hasKey(key)) {
        short ord = nbtRoot.getShort(key);
        if (ord >= 0 && ord < DyeColor.values().length) {
          rsColors.put(dir, DyeColor.values()[ord]);
        }
      }
    }
  }

  @Override
  public void onTick() {
  }

  public static int getMaxEnergyIO(IPowerConduitData subtype) {
    return subtype.getMaxEnergyIO();
  }

  // ----------- IEnergyStorage ---------------

  @Override
  public int getMaxEnergyStored() {
    return getMaxEnergyIO(subtype);
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    if (getMaxEnergyIO(subtype) == 0 || maxReceive <= 0) {
      return 0;
    }
    int freeSpace = getMaxEnergyStored() - getEnergyStored();
    int result = Math.min(maxReceive, freeSpace);
    if (!simulate && result > 0) {
      setEnergyStored(getEnergyStored() + result);
    }
    return result;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    // Only support a push based model
    return 0;
  }

  @Override
  public boolean canExtract() {
    return true;
  }

  @Override
  public boolean canReceive() {
    return true;
  }

  @Override
  public int getEnergyStored() {
    return energyStoredRF;
  }

  // ----------- END --------------------------

  @Override
  public void setEnergyStored(int energyStored) {
    energyStoredRF = MathHelper.clamp(energyStored, 0, getMaxEnergyStored());
  }

  /**
   * Used to get the capability of the conduit for the given direction
   *
   * @param dir
   *          side for the capability
   * @return returns the connection with reference to the relevant side
   */
  @Nullable
  private IEnergyStorage getEnergyDir(EnumFacing dir) {
    if (dir != null)
      return new ConnectionSide(dir);
    return null;
  }

  private boolean isRedstoneEnabled(@Nonnull EnumFacing dir) {
    RedstoneControlMode mode = getExtractionRedstoneMode(dir);
    return ConduitUtil.isRedstoneControlModeMet(this, mode, getExtractionSignalColor(dir));
  }

  @Override
  public int getMaxEnergyRecieved(@Nonnull EnumFacing dir) {
    ConnectionMode mode = getConnectionMode(dir);
    if (mode == OUTPUT || mode == ConnectionMode.DISABLED || !isRedstoneEnabled(dir)) {
      return 0;
    }
    return getMaxEnergyIO(subtype);
  }

  @Override
  public int getMaxEnergyExtracted(@Nonnull EnumFacing dir) {
    ConnectionMode mode = getConnectionMode(dir);
    if (mode == INPUT || mode == ConnectionMode.DISABLED || !isRedstoneEnabled(dir)) {
      return 0;
    }
    return getMaxEnergyIO(subtype);
  }

  @Override
  public boolean onNeighborBlockChange(@Nonnull Block blockId) {
    if (network != null && network.powerManager != null) {
      network.powerManager.receptorsChanged();
    }
    return super.onNeighborBlockChange(blockId);
  }

  @Override
  public void setConnectionMode(@Nonnull EnumFacing dir, @Nonnull ConnectionMode mode) {
    super.setConnectionMode(dir, mode);
    recievedTicks = null;
  }

  @Override
  public @Nullable IConduitNetwork<?, ?> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(@Nonnull IConduitNetwork<?, ?> network) {
    this.network = (PowerConduitNetwork) network;
    return super.setNetwork(network);
  }

  @Override
  public void clearNetwork() {
    this.network = null;
  }

  @Override
  public boolean canConnectToExternal(@Nonnull EnumFacing direction, boolean ignoreDisabled) {
    IPowerInterface rec = getExternalPowerReceptor(direction);
    return rec != null;
  }

  @Override
  public boolean canConnectToConduit(@Nonnull EnumFacing direction, @Nonnull IConduit conduit) {
    boolean res = super.canConnectToConduit(direction, conduit);
    if (!res) {
      return false;
    }
    if (ConduitConfig.canDifferentTiersConnect.get()) {
      return res;
    }
    if (!(conduit instanceof IPowerConduit)) {
      return false;
    }
    IPowerConduit pc = (IPowerConduit) conduit;
    return pc.getMaxEnergyStored() == getMaxEnergyStored();
  }

  @Override
  public void externalConnectionAdded(@Nonnull EnumFacing direction) {
    super.externalConnectionAdded(direction);
    if (network != null) {
      TileEntity te = getBundle().getEntity();
      BlockPos p = te.getPos().offset(direction);
      network.powerReceptorAdded(this, direction, p.getX(), p.getY(), p.getZ(), getExternalPowerReceptor(direction));
    }
  }

  @Override
  public void externalConnectionRemoved(@Nonnull EnumFacing direction) {
    super.externalConnectionRemoved(direction);
    if (network != null) {
      TileEntity te = getBundle().getEntity();
      BlockPos p = te.getPos().offset(direction);
      network.powerReceptorRemoved(p.getX(), p.getY(), p.getZ());
    }
  }

  @Override
  public IPowerInterface getExternalPowerReceptor(@Nonnull EnumFacing direction) {
    TileEntity te = getBundle().getEntity();
    World world = te.getWorld();
    TileEntity test = world.getTileEntity(te.getPos().offset(direction));
    if (test == null) {
      return null;
    }
    if (test instanceof IConduitBundle) {
      return null;
    }
    return PowerHandlerUtil.getPowerInterface(test, direction.getOpposite());
  }

  @Override
  @Nonnull
  public ItemStack createItem() {
    return subtype.createItemStackForSubtype();
  }

  @Override
  @Nonnull
  public Class<? extends IConduit> getBaseConduitType() {
    return IPowerConduit.class;
  }

  // Rendering
  @Override
  @Nonnull
  public TextureAtlasSprite getTextureForState(@Nonnull CollidableComponent component) {
    return subtype.getTextureForState(component);
  }

  @Override
  public TextureAtlasSprite getTextureForInputMode() {
    return ICONS.get(PowerConduit.ICON_KEY_INPUT).get(TextureAtlasSprite.class);
  }

  @Override
  public TextureAtlasSprite getTextureForOutputMode() {
    return ICONS.get(PowerConduit.ICON_KEY_OUTPUT).get(TextureAtlasSprite.class);
  }

  @Override
  public @Nullable TextureAtlasSprite getTransmitionTextureForState(@Nonnull CollidableComponent component) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable Vector4f getTransmitionTextureColorForState(@Nonnull CollidableComponent component) {
    return null;
  }

  @Override
  @Nonnull
  public Collection<CollidableComponent> createCollidables(@Nonnull CacheKey key) {
    Collection<CollidableComponent> baseCollidables = super.createCollidables(key);
    if (key.dir == null) {
      return baseCollidables;
    }

    BoundingBox bb = ConduitGeometryUtil.instance.createBoundsForConnectionController(key.dir, key.offset);
    CollidableComponent cc = new CollidableComponent(IPowerConduit.class, bb, key.dir, COLOR_CONTROLLER_ID);

    List<CollidableComponent> result = new ArrayList<CollidableComponent>();
    result.addAll(baseCollidables);
    result.add(cc);

    return result;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void hashCodeForModelCaching(BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
    super.hashCodeForModelCaching(hashCodes);
    if (subtype.getID() != 1) {
      hashCodes.add(subtype.getID());
    }
    hashCodes.addEnum(rsModes);
    hashCodes.addEnum(rsColors);
  }

  @Override
  public @Nonnull PowerConduitNetwork createNetworkForType() {
    return new PowerConduitNetwork();
  }

  // @SideOnly(Side.CLIENT)
  // @Override
  // public ITabPanel createPanelForConduit(GuiExternalConnection gui, IConduit con) {
  // return new PowerSettings(gui, con);
  // }

  @SideOnly(Side.CLIENT)
  @Nonnull
  @Override
  public ITabPanel createGuiPanel(@Nonnull IGuiExternalConnection gui, @Nonnull IClientConduit con) {
    return new PowerSettings(gui, con);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean updateGuiPanel(@Nonnull ITabPanel panel) {
    if (panel instanceof PowerSettings) {
      return ((PowerSettings) panel).updateConduit(this);
    }
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public int getGuiPanelTabOrder() {
    return 3;
  }

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityEnergy.ENERGY)
      return getExternalConnections().contains(facing);
    return false;
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityEnergy.ENERGY)
      return (T) getEnergyDir(facing);
    return null;
  }

  /**
   * Class for determining the handling conduit energy in each direction
   */
  public class ConnectionSide implements IEnergyStorage {

    private final @Nonnull EnumFacing side;

    public ConnectionSide(@Nonnull EnumFacing side) {
      this.side = side;
    }

    private boolean recievedRfThisTick() {
      if (recievedTicks == null || recievedTicks.get(side) == null || bundle == null) {
        return false;
      }

      long curTick = getBundle().getBundleworld().getTotalWorldTime();
      long recT = recievedTicks.get(side);
      if (curTick - recT <= 5) {
        return true;
      }
      return false;
    }

    private boolean isEnabled() {
      if (getConnectionMode(side) == ConnectionMode.DISABLED || isRedstoneEnabled(side))
        return false;
      return true;
    }

    private boolean isExtract() {
      switch (getConnectionMode(side)) {
      case OUTPUT:
      case IN_OUT:
        return isRedstoneEnabled(side);
      default:
        return false;
      }
    }

    private boolean isReceive() {
      switch (getConnectionMode(side)) {
      case INPUT:
      case IN_OUT:
        return isRedstoneEnabled(side);
      default:
        return false;
      }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
      if (isReceive()) {
        int energyFinal = PowerConduit.this.receiveEnergy(maxReceive, simulate);
        if (bundle != null) {
          if (recievedTicks == null) {
            recievedTicks = new EnumMap<EnumFacing, Long>(EnumFacing.class);
          }
          recievedTicks.put(side, getBundle().getBundleworld().getTotalWorldTime());
        }
        return energyFinal;
      }
      return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
      if (isExtract())
        return PowerConduit.this.extractEnergy(maxExtract, simulate);
      return 0;
    }

    @Override
    public int getEnergyStored() {
      if (isEnabled())
        return PowerConduit.this.getEnergyStored();
      return 0;
    }

    @Override
    public int getMaxEnergyStored() {
      if (isEnabled())
        return PowerConduit.this.getMaxEnergyStored();
      return 0;
    }

    @Override
    public boolean canExtract() {
      return isExtract() && PowerConduit.this.canExtract() && !recievedRfThisTick();
    }

    @Override
    public boolean canReceive() {
      return isReceive() && PowerConduit.this.canReceive();
    }
  }
}
