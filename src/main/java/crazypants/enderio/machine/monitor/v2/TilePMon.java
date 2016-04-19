package crazypants.enderio.machine.monitor.v2;

import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.power.NetworkPowerManager;
import crazypants.enderio.conduit.power.PowerConduitNetwork;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;

import static info.loenwind.autosave.annotations.Store.StoreFor.ITEM;
import static info.loenwind.autosave.annotations.Store.StoreFor.SAVE;

@Storable
public class TilePMon extends AbstractPoweredTaskEntity {

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

  public TilePMon() {
    super(new SlotDefinition(0, 0, 0), ModObject.blockPowerMonitorv2);
  }

  @Override
  public String getMachineName() {
    return ModObject.blockPowerMonitorv2.getUnlocalisedName();
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, @Nullable ItemStack item) {
    return false;
  }

  private int slowstart = 100;

  // tick goes in here
  @Override
  protected boolean checkProgress(boolean redstoneChecksPassed) {
    if (slowstart > 0) {
      // give the network a while to form after the chunk has loaded to prevent bogus readings (all zeros)
      slowstart--;
      return false;
    }
    usePower();
    NetworkPowerManager pm = getPowerManager();
    if (pm != null) {
      final int capPower = logSrqt2(pm.getPowerInCapacitorBanks());
      for (StatCollector statCollector : stats) {
        statCollector.addValue(capPower);
      }
    }
    if (shouldDoWorkThisTick(iconUpdateRate / 10)) {
      PacketHandler.sendToAllAround(PacketPMon.sendUpdate(this, stats.length - 1), this);
    }
    return false;
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

  private NetworkPowerManager getPowerManager() {
    for (EnumFacing dir : EnumFacing.values()) {
      IPowerConduit con = ConduitUtil.getConduit(worldObj, this, dir, IPowerConduit.class);
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
    // setCapacitor(new BasicCapacitor(100, 10000, 10));
    currentTask = createTask(null, 0);
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
      PacketHandler.INSTANCE.sendToServer(PacketPMon.requestUpdate(this, id));
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

}
