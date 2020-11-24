package crazypants.enderio.powertools.machine.monitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduitNetwork;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.machine.task.ContinuousTask;
import crazypants.enderio.base.paint.IPaintable.IPaintableTileEntity;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.render.util.DynaTextureProvider;
import crazypants.enderio.conduits.autosave.HandleStatCollector;
import crazypants.enderio.conduits.conduit.power.IPowerConduit;
import crazypants.enderio.conduits.conduit.power.NetworkPowerManager;
import crazypants.enderio.conduits.conduit.power.PowerConduitNetwork;
import crazypants.enderio.conduits.conduit.power.PowerTracker;
import crazypants.enderio.powertools.init.PowerToolObject;
import crazypants.enderio.powertools.network.PacketHandler;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.powertools.capacitor.CapacitorKey.POWER_MONITOR_POWER_BUFFER;
import static crazypants.enderio.powertools.capacitor.CapacitorKey.POWER_MONITOR_POWER_INTAKE;
import static crazypants.enderio.powertools.capacitor.CapacitorKey.POWER_MONITOR_POWER_USE;
import static info.loenwind.autosave.util.NBTAction.CLIENT;
import static info.loenwind.autosave.util.NBTAction.ITEM;
import static info.loenwind.autosave.util.NBTAction.SAVE;

@Storable
public class TilePowerMonitor extends AbstractPoweredTaskEntity implements IPaintableTileEntity, DynaTextureProviderPMon.IDataProvider {

  private static final int iconUpdateRate = 30 * 60 * 20 / 24; // ticks per pixel

  protected @Store(value = { SAVE, ITEM }, handler = HandleStatCollector.class) StatCollector stats10s = new StatCollector(2);
  protected @Store(value = { SAVE, ITEM }, handler = HandleStatCollector.class) StatCollector stats01m = new StatCollector(12);
  protected @Store(value = { SAVE, ITEM }, handler = HandleStatCollector.class) StatCollector stats10m = new StatCollector(120);
  protected @Store(value = { SAVE, ITEM }, handler = HandleStatCollector.class) StatCollector stats01h = new StatCollector(720);
  protected @Store(value = { SAVE, ITEM }, handler = HandleStatCollector.class) StatCollector stats06h = new StatCollector(7200);
  protected @Store(value = { SAVE, ITEM }, handler = HandleStatCollector.class) StatCollector stats24h = new StatCollector(17280);
  protected @Store(value = { SAVE, ITEM }, handler = HandleStatCollector.class) StatCollector stats07d = new StatCollector(120960);
  protected @Store(value = { SAVE, ITEM }, handler = HandleStatCollector.class) StatCollector statsIcn = new StatCollector(iconUpdateRate, 28);

  protected StatCollector[] stats = { stats10s, stats01m, stats10m, stats01h, stats06h, stats24h, stats07d, statsIcn };

  @Store({ SAVE, CLIENT })
  private boolean advanced;
  @Store
  private boolean engineControlEnabled = false;
  @Store
  private float startLevel = 0.75f;
  @Store
  private float stopLevel = 0.99f;
  @Store(CLIENT)
  private boolean redStoneOn;
  /**
   * Forces neighbors to be kicked regardless of power change. Used to make sure state is propagated correctly after being loaded in from the save.
   */
  private boolean initialized = false;

  public TilePowerMonitor() {
    super(new SlotDefinition(0, 0, 0), POWER_MONITOR_POWER_INTAKE, POWER_MONITOR_POWER_BUFFER, POWER_MONITOR_POWER_USE);
  }

  @Override
  public @Nonnull String getMachineName() {
    return PowerToolObject.block_power_monitor.getUnlocalisedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack item) {
    return false;
  }

  @Override
  public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
    return mode == IoMode.NONE;
  }

  private int slowstart = 100;

  // tick goes in here
  @Override
  protected void checkProgress(boolean redstoneChecksPassed) {
    usePower();
    if (!advanced && !engineControlEnabled) {
      return;
    }
    if (slowstart > 0) {
      // give the network a while to form after the chunk has loaded to prevent bogus readings (all zeros)
      slowstart--;
      return;
    }
    NetworkPowerManager pm = getPowerManager();
    if (pm != null) {
      if (advanced) {
        final int capPower = logSrqt2(pm.getPowerInCapacitorBanks());
        for (StatCollector statCollector : stats) {
          statCollector.addValue(capPower);
        }
      }
      if (engineControlEnabled) {
        double level = getPercentFull(pm);
        if (level < startLevel) {
          if (!redStoneOn) {
            redStoneOn = true;
            broadcastSignal();
          }
        } else if (level >= stopLevel) {
          if (redStoneOn) {
            redStoneOn = false;
            broadcastSignal();
          }
        }
        if (!initialized) {
          broadcastSignal();
        }
      }
    }
    if (advanced && shouldDoWorkThisTick(iconUpdateRate / 10)) {
      PacketHandler.sendToAllAround(PacketPowerMonitorGraph.sendUpdate(this, stats.length - 1), this);
    }
  }

  private double getPercentFull(NetworkPowerManager pm) {
    return ((double) pm.getPowerInConduits() + (double) pm.getPowerInCapacitorBanks())
        / ((double) pm.getMaxPowerInConduits() + (double) pm.getMaxPowerInCapacitorBanks());
  }

  private void broadcastSignal() {
    initialized = true;
    world.notifyNeighborsOfStateChange(getPos(), getBlockType(), true);
  }

  private static final long bit62 = Integer.MAX_VALUE;
  private static final long bit63 = bit62 * 2;

  private static int logSrqt2(long value) {
    if (value <= 0) {
      return 0;
    } else if (value >= bit63) {
      return 63;
    } else if (value >= bit62) {
      return 62;
    }
    for (int i = 30; i >= 0; i--) {
      if ((value & (1 << i)) != 0) {
        if (i == 0) {
          return 1;
        }
        if ((value & (1 << (i - 1))) != 0) {
          return i * 2 + 1;
        }
        return i * 2;
      }
    }
    return 0;
  }

  @Store(SAVE)
  private EnumFacing lastConduitConnection = null;
  private boolean lastConduitConnectionDirty = true;

  protected void onNeighbor() {
    lastConduitConnectionDirty = true;
  }

  // Side.SERVER
  public NetworkPowerManager getPowerManager() {
    class Finder {
      NetworkPowerManager pmFound = null;
      EnumFacing found = null;

      void find(EnumFacing dir) {
        if (dir != null && dir != found) {
          IPowerConduit con = ConduitUtil.getConduit(world, TilePowerMonitor.this, dir, IPowerConduit.class);
          if (con != null && con.getEffectiveConnectionMode(dir.getOpposite()).isActive()) {
            IConduitNetwork<?, ?> n = con.getNetwork();
            if (n instanceof PowerConduitNetwork) {
              NetworkPowerManager pm = ((PowerConduitNetwork) n).getPowerManager();
              if (pm != null) {
                if (pmFound == null) {
                  pmFound = pm;
                  found = dir;
                } else {
                  con.setConnectionMode(dir.getOpposite(), ConnectionMode.DISABLED);
                }
              }
            }
          }
        }
      }
    }
    Finder f = new Finder();

    // prio 1: existing connection
    f.find(lastConduitConnection);
    if (!lastConduitConnectionDirty && f.pmFound != null) {
      // stop wasting time if no neighbor was updated since the last full check
      return f.pmFound;
    }

    // prio 2: backside connection
    f.find(getFacing().getOpposite());

    // prio 3: enum order
    NNList.FACING.apply(f::find);

    lastConduitConnection = f.found;
    lastConduitConnectionDirty = false;
    return f.pmFound;
  }

  @Override
  protected IPoweredTask createTask(@Nullable IMachineRecipe nextRecipe, long nextSeed) {
    return new ContinuousTask(getPowerUsePerTick());
  }

  @Override
  public void onCapacitorDataChange() {
    currentTask = createTask(null, 0);
    initialized = false;
  }

  // Side.CLIENT
  private long[] nextUpdateRequest = new long[stats.length];

  @SideOnly(Side.CLIENT)
  public StatCollector getStatCollector(int id) {
    if (id < 0 || id >= stats.length) {
      return null;
    }
    long now = EnderIO.proxy.getTickCount();
    if (nextUpdateRequest[id] < now) {
      nextUpdateRequest[id] = now + 10;
      PacketHandler.INSTANCE.sendToServer(PacketPowerMonitorGraph.requestUpdate(this, id));
    }
    return stats[id];
  }

  // Side.CLIENT
  protected Object dynaTextureProvider = null;

  @SideOnly(Side.CLIENT)
  public void bindTexture() {
    if (dynaTextureProvider == null) {
      dynaTextureProvider = new DynaTextureProviderPMon(this);
    }
    ((DynaTextureProvider) dynaTextureProvider).bindTexture();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void invalidate() {
    super.invalidate();
    if (dynaTextureProvider != null) {
      ((DynaTextureProvider) dynaTextureProvider).free();
      dynaTextureProvider = null;
    }
  }

  // Side.CLIENT
  protected int[] iconMins = new int[DynaTextureProviderPMon.TEXSIZE];
  // Side.CLIENT
  protected int[] iconMaxs = new int[DynaTextureProviderPMon.TEXSIZE];

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull int[][] getIconValues() {
    return statsIcn.getValues();
  }

  // Side.CLIENT
  protected StatData statData = null;

  // Side.CLIENT
  private long nextUpdateRequestStatData = -1;

  @SideOnly(Side.CLIENT)
  public StatData getStatData() {
    long now = EnderIO.proxy.getTickCount();
    if (nextUpdateRequestStatData < now) {
      nextUpdateRequestStatData = now + 10;
      PacketHandler.INSTANCE.sendToServer(PacketPowerMonitorStatData.requestUpdate(this));
    }
    return statData;
  }

  static class StatData {
    long powerInConduits;
    long maxPowerInConduits;
    long powerInCapBanks;
    long maxPowerInCapBanks;
    long powerInMachines;
    long maxPowerInMachines;
    float aveRfSent;
    float aveRfReceived;

    StatData(NetworkPowerManager pm) {
      powerInConduits = pm.getPowerInConduits();
      maxPowerInConduits = pm.getMaxPowerInConduits();
      powerInCapBanks = pm.getPowerInCapacitorBanks();
      maxPowerInCapBanks = pm.getMaxPowerInCapacitorBanks();
      powerInMachines = pm.getPowerInReceptors();
      maxPowerInMachines = pm.getMaxPowerInReceptors();
      PowerTracker tracker = pm.getNetworkPowerTracker();
      aveRfSent = tracker.getAverageRfTickSent();
      aveRfReceived = tracker.getAverageRfTickRecieved();
    }

    StatData() {
    }
  }

  public boolean isAdvanced() {
    return advanced;
  }

  public void setAdvanced(boolean advanced) {
    this.advanced = advanced;
    markDirty();
  }

  public boolean isEngineControlEnabled() {
    return engineControlEnabled;
  }

  public void setEngineControlEnabled(boolean engineControlEnabled) {
    this.engineControlEnabled = engineControlEnabled;
    if (!engineControlEnabled && redStoneOn) {
      redStoneOn = false;
      broadcastSignal();
    }
    markDirty();
  }

  public float getStartLevel() {
    return startLevel;
  }

  public void setStartLevel(float startLevel) {
    this.startLevel = startLevel;
    markDirty();
  }

  public float getStopLevel() {
    return stopLevel;
  }

  public void setStopLevel(float stopLevel) {
    this.stopLevel = stopLevel;
    markDirty();
  }

  public int getRedstoneLevel() {
    return redStoneOn ? 15 : 0;
  }

  @Override
  public boolean isActive() {
    return true;
  }

}
