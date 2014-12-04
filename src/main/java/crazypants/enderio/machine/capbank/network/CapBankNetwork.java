package crazypants.enderio.machine.capbank.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConduitNetworkTickHandler;
import crazypants.enderio.conduit.ConduitNetworkTickHandler.TickListener;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.packet.PacketNetworkStateResponse;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.IPowerStorage;
import crazypants.enderio.power.PerTickIntAverageCalculator;
import crazypants.util.BlockCoord;
import crazypants.util.RoundRobinIterator;

public class CapBankNetwork implements ICapBankNetwork {

  private static final int IO_CAP = 2000000000;

  private final List<TileCapBank> capBanks = new ArrayList<TileCapBank>();

  private final Set<EnergyReceptor> receptors = new HashSet<EnergyReceptor>();
  private RoundRobinIterator<EnergyReceptor> receptorIterator;

  private final int id;

  private int maxIO;

  private int maxInput = -1;

  private int maxOutput = -1;

  private long timeAtLastApply;

  private long energyStored;
  private long prevEnergyStored = -1;

  private long maxEnergyStored;

  private CapBankType type;

  private Set<BlockCoord> redstoneRecievers = new HashSet<BlockCoord>();

  private RedstoneControlMode inputControlMode = RedstoneControlMode.IGNORE;
  private RedstoneControlMode outputControlMode = RedstoneControlMode.IGNORE;

  private boolean inputRedstoneConditionMet = true;
  private boolean outputRedstoneConditionMet = true;

  private TickListener tickListener;

  private PerTickIntAverageCalculator powerTracker = new PerTickIntAverageCalculator(2);

  private final InventoryImpl inventory = new InventoryImpl();

  public CapBankNetwork(int id) {
    this.id = id;
  }

  //--------- Network Management 

  public void init(TileCapBank cap, Collection<TileCapBank> neighbours, World world) {
    if(world.isRemote) {
      throw new UnsupportedOperationException();
    }

    tickListener = new TickReciever();

    type = cap.getType();
    inputControlMode = cap.getInputControlMode();
    outputControlMode = cap.getOutputControlMode();
    for (TileCapBank con : neighbours) {
      ICapBankNetwork network = con.getNetwork();
      if(network != null) {
        network.destroyNetwork();
      }
    }
    setNetwork(world, cap);
    EnderIO.packetPipeline.sendToAllAround(new PacketNetworkStateResponse(this), cap);
  }


  protected void setNetwork(World world, TileCapBank cap) {
    if(cap != null && cap.setNetwork(this)) {
      addMember(cap);
      Collection<TileCapBank> neighbours = NetworkUtil.getNeigbours(cap);
      for (TileCapBank neighbour : neighbours) {
        if(neighbour.getNetwork() == null) {
          setNetwork(world, neighbour);
        } else if(neighbour.getNetwork() != this) {
          neighbour.getNetwork().destroyNetwork();
          setNetwork(world, neighbour);
        }
      }
    }
  }

  @Override
  public void destroyNetwork() {
    distributeEnergyToBanks();
    TileCapBank cap = null;
    for (TileCapBank cb : capBanks) {
      cb.setNetwork(null);
      if(cap == null) {
        cap = cb;
      }
    }
    capBanks.clear();
    if(cap != null) {
      EnderIO.packetPipeline.INSTANCE.sendToAll(new PacketNetworkStateResponse(this, true));
    }
  }

  @Override
  public List<TileCapBank> getMembers() {
    return capBanks;
  }

  @Override
  public void addMember(TileCapBank cap) {
    if(!capBanks.contains(cap)) {
      capBanks.add(cap);
      long newIO = maxIO + cap.getType().getMaxIO();
      if(newIO > IO_CAP) {
        newIO = IO_CAP;
      }
      maxIO = (int) newIO;

      energyStored += cap.getEnergyStored();
      maxEnergyStored += cap.getMaxEnergyStored();
      if(maxInput == -1) {
        maxInput = cap.getMaxInputOverride();
      }
      if(maxOutput == -1) {
        maxOutput = cap.getMaxOutputOverride();
      }
      cap.setInputControlMode(inputControlMode);
      cap.setOutputControlMode(outputControlMode);

      List<EnergyReceptor> recs = cap.getReceptors();
      if(!recs.isEmpty()) {
        addReceptors(recs);
      }

      if(inventory.isEmtpy()) {
        inventory.setCapBank(cap);
      } else if(!InventoryImpl.isInventoryEmtpy(cap)) {
        if(inventory.isEmtpy()) {
          inventory.setCapBank(cap);
        } else {
          cap.dropItems();
        }
      }

    }
  }

  @Override
  public InventoryImpl getInventory() {
    return inventory;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public NetworkState getState() {
    return new NetworkState(this);
  }

  //--------- Tick Handling 

  @Override
  public void onUpdateEntity(TileCapBank tileCapBank) {
    World world = tileCapBank.getWorldObj();
    if(world == null) {
      return;
    }
    if(world.isRemote) {
      return;
    }
    long curTime = world.getTotalWorldTime();
    if(curTime != timeAtLastApply) {
      timeAtLastApply = curTime;
      ConduitNetworkTickHandler.instance.addListener(tickListener);
    }
  }

  private void doNetworkTick() {

    chargeItems(inventory.getStacks());
    transmitEnergy();

    if(energyStored != prevEnergyStored) {
      distributeEnergyToBanks();
    }
    if(prevEnergyStored != -1) {
      powerTracker.tick((int) (energyStored - prevEnergyStored));
    }
    prevEnergyStored = energyStored;

  }

  private void transmitEnergy() {

    if(!outputRedstoneConditionMet) {
      return;
    }

    if(receptors.isEmpty()) {
      return;
    }

    int available = getEnergyAvailableForTick(getMaxOutput());
    if(available <= 0) {
      return;
    }

    if(receptorIterator == null) {
      List<EnergyReceptor> rl = new ArrayList<EnergyReceptor>(receptors);
      receptorIterator = new RoundRobinIterator<EnergyReceptor>(rl);
    }

    int totalSent = 0;
    Iterator<EnergyReceptor> iter = receptorIterator.iterator();
    while (available > 0 && iter.hasNext()) {
      int sent = sendPowerTo(iter.next(), available);
      totalSent += sent;
      available -= sent;
    }
    if(!type.isCreative()) {
      addEnergy(-totalSent);
    }
  }

  protected int getEnergyAvailableForTick(int limit) {
    int available;
    if(energyStored > limit) {
      available = limit;
    } else {
      available = (int) energyStored;
    }
    return available;
  }

  private int sendPowerTo(EnergyReceptor next, int available) {
    //Can only send to power conduits if we are in push mode or the conduit is in pull mode
    //With default setting interaction between conduits and Cap Banks is handled by NetworkPowerManager
    IPowerConduit con = next.getConduit();
    if(con != null && next.getMode() == IoMode.NONE && con.getConnectionMode(next.getDir().getOpposite()) == ConnectionMode.IN_OUT) {
      return 0;
    }
    IPowerInterface inf = next.getReceptor();
    int result = inf.recieveEnergy(next.getDir().getOpposite(), available);
    if(result < 0) {
      result = 0;
    }
    return result;
  }

  public boolean chargeItems(ItemStack[] items) {
    if(items == null) {
      return false;
    }
    boolean chargedItem = false;
    int available = getEnergyAvailableForTick(getMaxIO());
    for (ItemStack item : items) {
      if(item != null && available > 0) {
        int used = 0;
        if(item.getItem() instanceof IEnergyContainerItem) {
          IEnergyContainerItem chargable = (IEnergyContainerItem) item.getItem();

          int max = chargable.getMaxEnergyStored(item);
          int cur = chargable.getEnergyStored(item);
          int canUse = Math.min(available, max - cur);
          if(cur < max) {
            used = chargable.receiveEnergy(item, canUse, false);
          }

        }
        if(used > 0) {
          if(!type.isCreative()) {
            addEnergy(-used);
          }
          chargedItem = true;
          available -= used;
        }
      }
    }
    return chargedItem;
  }

  private void distributeEnergyToBanks() {
    if(capBanks.isEmpty()) {
      return;
    }
    int energyPerCapBank = (int) (energyStored / capBanks.size());
    int remaining = (int) (energyStored % capBanks.size());
    for (TileCapBank cb : capBanks) {
      cb.setEnergyStored(energyPerCapBank);
    }
    TileCapBank cb = capBanks.get(0);
    cb.setEnergyStored(cb.getEnergyStored() + remaining);
  }

  //------ Power     

  @Override
  public float getAverageChangePerTick() {
    return powerTracker.getAverage();
  }

  @Override
  public int recieveEnergy(int maxReceive, boolean simulate) {
    if(maxReceive <= 0 || !inputRedstoneConditionMet) {
      return 0;
    }

    long spaceAvailable = maxEnergyStored - energyStored;
    if(spaceAvailable > Integer.MAX_VALUE) {
      spaceAvailable = Integer.MAX_VALUE;
    }
    int res = Math.min(maxReceive, (int) spaceAvailable);
    res = Math.min(maxReceive, getMaxInput());
    if(!simulate) {
      if(!type.isCreative()) {
        addEnergy(res);
      }
    }

    return res;
  }

  @Override
  public void addEnergy(int energy) {
    energyStored += energy;
    if(energyStored > maxEnergyStored) {
      energyStored = maxEnergyStored;
    } else if(energyStored < 0) {
      energyStored = 0;
    }
  }

  public void addEnergyReceptor(EnergyReceptor rec) {
    receptors.add(rec);
    receptorIterator = null;
  }

  @Override
  public void addReceptors(Collection<EnergyReceptor> rec) {
    if(rec.isEmpty()) {
      return;
    }
    receptors.addAll(rec);
    receptorIterator = null;
  }

  @Override
  public void removeReceptors(Collection<EnergyReceptor> rec) {
    if(rec.isEmpty()) {
      return;
    }
    receptors.removeAll(rec);
    receptorIterator = null;
  }

  public void removeReceptor(EnergyReceptor rec) {
    receptors.remove(rec);
    receptorIterator = null;
  }

  @Override
  public long getEnergyStoredL() {
    return energyStored;
  }

  @Override
  public long getMaxEnergyStoredL() {
    return maxEnergyStored;
  }

  @Override
  public int getMaxIO() {
    return maxIO;
  }

  //----- IO overrides

  @Override
  public int getMaxInput() {
    if(maxInput == -1) {
      return maxIO;
    }
    return Math.min(maxInput, maxIO);
  }

  @Override
  public int getMaxOutput() {
    if(maxOutput == -1) {
      return maxIO;
    }
    return Math.min(maxOutput, maxIO);
  }

  @Override
  public void setMaxInput(int max) {
    if(max >= maxIO) {
      maxInput = -1;
    } else if(max < 0) {
      maxInput = 0;
    } else {
      maxInput = max;
    }
    for (TileCapBank cb : capBanks) {
      cb.setMaxInput(maxInput);
    }
  }

  @Override
  public void setMaxOutput(int max) {
    if(max >= maxIO) {
      maxOutput = -1;
    } else if(max < 0) {
      maxOutput = 0;
    } else {
      maxOutput = max;
    }
    for (TileCapBank cb : capBanks) {
      cb.setMaxOutput(maxOutput);
    }
  }



  //----------- Redstone

  @Override
  public RedstoneControlMode getInputControlMode() {
    return inputControlMode;
  }

  @Override
  public void setInputControlMode(RedstoneControlMode inputControlMode) {
    if(this.inputControlMode == inputControlMode) {
      return;
    }
    this.inputControlMode = inputControlMode;
    for (TileCapBank capBank : capBanks) {
      capBank.setInputControlMode(inputControlMode);
    }
    updateRedstoneConditions();
  }

  @Override
  public RedstoneControlMode getOutputControlMode() {
    return outputControlMode;
  }

  @Override
  public void setOutputControlMode(RedstoneControlMode outputControlMode) {
    if(this.outputControlMode == outputControlMode) {
      return;
    }
    this.outputControlMode = outputControlMode;
    for (TileCapBank capBank : capBanks) {
      capBank.setOutputControlMode(outputControlMode);
    }
    updateRedstoneConditions();
  }

  @Override
  public void updateRedstoneSignal(TileCapBank tileCapBank, boolean recievingSignal) {
    if(recievingSignal) {
      redstoneRecievers.add(tileCapBank.getLocation());
    } else {
      redstoneRecievers.remove(tileCapBank.getLocation());
    }
    updateRedstoneConditions();
  }

  @Override
  public boolean isInputEnabled() {
    return inputRedstoneConditionMet;
  }

  @Override
  public boolean isOutputEnabled() {
    return outputRedstoneConditionMet;
  }

  private void updateRedstoneConditions() {
    int powerLevel = redstoneRecievers.isEmpty() ? 0 : 15;
    inputRedstoneConditionMet = RedstoneControlMode.isConditionMet(inputControlMode, powerLevel);
    outputRedstoneConditionMet = RedstoneControlMode.isConditionMet(outputControlMode, powerLevel);

  }

  private class TickReciever implements TickListener {

    @Override
    public void tickStart(ServerTickEvent evt) {
    }

    @Override
    public void tickEnd(ServerTickEvent evt) {
      doNetworkTick();
    }

  }

  @Override
  public IPowerStorage getController() {
    return this;
  }

  @Override
  public boolean isOutputEnabled(ForgeDirection direction) {
    return isOutputEnabled();
  }

  @Override
  public boolean isInputEnabled(ForgeDirection direction) {
    return isInputEnabled();
  }

  @Override
  public boolean isCreative() {
    return type.isCreative();
  }

}
