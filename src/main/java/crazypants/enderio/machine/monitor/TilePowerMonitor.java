package crazypants.enderio.machine.monitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.power.NetworkPowerManager;
import crazypants.enderio.conduit.power.PowerConduitNetwork;
import crazypants.enderio.conduit.power.PowerTracker;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable.IPaintableTileEntity;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.capacitor.CapacitorKey.POWER_MONITOR_POWER_BUFFER;
import static crazypants.enderio.capacitor.CapacitorKey.POWER_MONITOR_POWER_INTAKE;
import static crazypants.enderio.capacitor.CapacitorKey.POWER_MONITOR_POWER_USE;
import static info.loenwind.autosave.annotations.Store.StoreFor.CLIENT;
import static info.loenwind.autosave.annotations.Store.StoreFor.ITEM;
import static info.loenwind.autosave.annotations.Store.StoreFor.SAVE;

@Storable
public class TilePowerMonitor extends AbstractPoweredTaskEntity implements IPaintableTileEntity {

  private static final int iconUpdateRate = 30 * 60 * 20 / 24; // ticks per pixel

  protected @Store({ SAVE, ITEM }) StatCollector stats10s = new StatCollector(2);
  protected @Store({ SAVE, ITEM }) StatCollector stats01m = new StatCollector(12);
  protected @Store({ SAVE, ITEM }) StatCollector stats10m = new StatCollector(120);
  protected @Store({ SAVE, ITEM }) StatCollector stats01h = new StatCollector(720);
  protected @Store({ SAVE, ITEM }) StatCollector stats06h = new StatCollector(7200);
  protected @Store({ SAVE, ITEM }) StatCollector stats24h = new StatCollector(17280);
  protected @Store({ SAVE, ITEM }) StatCollector stats07d = new StatCollector(120960);
  protected @Store({ SAVE, ITEM }) StatCollector statsIcn = new StatCollector(iconUpdateRate, 28);

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
    return ModObject.blockPowerMonitorv2.getUnlocalisedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nullable ItemStack item) {
    return false;
  }

  @Override
  public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
    return mode == IoMode.NONE;
  }

  private int slowstart = 100;

  // tick goes in here
  @Override
  protected boolean checkProgress(boolean redstoneChecksPassed) {
    usePower();
    if (!advanced && !engineControlEnabled) {
      return false;
    }
    if (slowstart > 0) {
      // give the network a while to form after the chunk has loaded to prevent bogus readings (all zeros)
      slowstart--;
      return false;
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
        float level = getPercentFull(pm);
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
    return false;
  }
  
  private float getPercentFull(NetworkPowerManager pm) {
    return (float) (pm.getPowerInConduits() + pm.getPowerInCapacitorBanks()) / (pm.getMaxPowerInConduits() + pm.getMaxPowerInCapacitorBanks());
  }

  private void broadcastSignal() {
    initialized = true;
    world.notifyNeighborsOfStateChange(getPos(), getBlockType());
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

  public NetworkPowerManager getPowerManager() {
    for (EnumFacing dir : EnumFacing.values()) {
      IPowerConduit con = ConduitUtil.getConduit(world, this, dir, IPowerConduit.class);
      if (con != null) {
        AbstractConduitNetwork<?, ?> n = con.getNetwork();
        if (n instanceof PowerConduitNetwork) {
          NetworkPowerManager pm = ((PowerConduitNetwork) n).getPowerManager();
          if (pm != null) {
            return pm;
          }
        }
      }
    }
    return null;
  }

  @Override
  protected IPoweredTask createTask(IMachineRecipe nextRecipe, float chance) {
    return new ContinuousTask(getPowerUsePerTick());
  }

  @Override
  public void onCapacitorDataChange() {
    // TODO setCapacitor(new BasicCapacitor(100, 10000, 10));
    currentTask = createTask(null, 0);
    initialized = false;
  }

  // Side.CLIENT
  private long[] lastUpdateRequest = new long[stats.length];

  @SideOnly(Side.CLIENT)
  public StatCollector getStatCollector(int id) {
    if (id < 0 || id >= stats.length) {
      return null;
    }
    long now = EnderIO.proxy.getTickCount();
    if (lastUpdateRequest[id] < now) {
      lastUpdateRequest[id] = now + 10;
      PacketHandler.INSTANCE.sendToServer(PacketPowerMonitorGraph.requestUpdate(this, id));
    }
    return stats[id];
  }

  // Side.CLIENT
  protected Object dynaTextureProvider = null;

  @SideOnly(Side.CLIENT)
  public void bindTexture() {
    if (dynaTextureProvider == null) {
      dynaTextureProvider = new DynaTextureProvider(this);
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
  protected int[] iconMins = new int[DynaTextureProvider.TEXSIZE];
  // Side.CLIENT
  protected int[] iconMaxs = new int[DynaTextureProvider.TEXSIZE];

  @SideOnly(Side.CLIENT)
  public int[][] getIconValues() {
    return statsIcn.getValues();
  }

  // Side.CLIENT
  protected StatData statData = null;

  // Side.CLIENT
  private long lastUpdateRequestStatData = -1;

  @SideOnly(Side.CLIENT)
  public StatData getStatData() {
    long now = EnderIO.proxy.getTickCount();
    if (lastUpdateRequestStatData < now) {
      lastUpdateRequestStatData = now + 10;
      PacketHandler.INSTANCE.sendToServer(PacketPowerMonitorStatData.requestUpdate(this));
    }
    return statData;
  }

  static class StatData {
    int powerInConduits;
    int maxPowerInConduits;
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
