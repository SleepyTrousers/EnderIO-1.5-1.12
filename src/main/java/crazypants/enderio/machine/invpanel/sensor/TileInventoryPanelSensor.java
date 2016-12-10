package crazypants.enderio.machine.invpanel.sensor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.ModObject;
import crazypants.enderio.capacitor.CapacitorKey;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemConduitNetwork;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.ContinuousTask;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.invpanel.server.InventoryDatabaseServer;
import crazypants.enderio.machine.invpanel.server.ItemEntry;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable.IPaintableTileEntity;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class TileInventoryPanelSensor extends AbstractPoweredTaskEntity implements IPaintableTileEntity {

  // TODO: Copied from power monitor, look into this, it should be needed,
  // certainly not 5 seconds worth!
  private int slowstart = 10;

  @Store
  private int startCount = 32;

  @Store
  private int stopCount = 64;

  private int currentSignal = 0;

  @Store
  private ItemStack itemToCheck = null;

  //send client side for rendering
  private boolean active = false;

  public TileInventoryPanelSensor() {
    super(new SlotDefinition(0, 0, 0), CapacitorKey.INV_PANEL_SENSOR_POWER_INTAKE, CapacitorKey.INV_PANEL_SENSOR_POWER_BUFFER,
        CapacitorKey.INV_PANEL_SENSOR_POWER_USE);
  }

  @Override
  public @Nonnull String getMachineName() {
    return ModObject.blockInventoryPanelSensor.getUnlocalisedName();
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, @Nullable ItemStack item) {
    return false;
  }

  @Override
  public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
    return mode == IoMode.NONE || mode == IoMode.DISABLED;
  }

  @Override
  protected boolean checkProgress(boolean redstoneChecksPassed) {
    usePower();
    if (slowstart > 0) {
      // give the network a while to form after the chunk has loaded to prevent
      // bogus readings (all zeros)
      slowstart--;
      return false;
    }

    if (shouldDoWorkThisTick(10)) {
      if (itemToCheck != null) {
        
        InventoryDatabaseServer db = getInventoryDB();
        if (db != null) {
          int invHasCount = 0;
          ItemEntry entry = db.lookupItem(itemToCheck, null, false);
          if (entry != null) {
            invHasCount = entry.countItems();
          }
          updateRedstone(invHasCount);
        } else {
          setCurrentSignal(0);
        }
      } else if(isEmitting()) {
        setCurrentSignal(0);
      }
    }

    return false;
  }

  private void updateRedstone(int invHasCount) {
    if (itemToCheck == null) {
      setCurrentSignal(0);
      return;
    }
    if (isEmitting()) {
      
      if (invHasCount >= stopCount) {
        setCurrentSignal(0);
      }
    } else {
      if (invHasCount <= startCount) {
        setCurrentSignal(15);
      }
    }
  }

  private void setCurrentSignal(int newVal) {
    if (currentSignal == newVal) {
      return;
    }
    currentSignal = newVal;
    setActive(isEmitting());
    PacketHandler.sendToAllAround(new PacketActive(this), this);
    broadcastSignal();
    markDirty();
  }

  private void broadcastSignal() {
    worldObj.notifyNeighborsOfStateChange(getPos(), getBlockType());
  }

  @Override
  protected IPoweredTask createTask(IMachineRecipe nextRecipe, float chance) {
    return new ContinuousTask(getPowerUsePerTick());
  }

  public InventoryDatabaseServer getInventoryDB() {

    for (EnumFacing dir : EnumFacing.values()) {
      IItemConduit con = ConduitUtil.getConduit(worldObj, this, dir, IItemConduit.class);
      if (con != null) {
        AbstractConduitNetwork<?, ?> n = con.getNetwork();
        if (n instanceof ItemConduitNetwork) {
          InventoryDatabaseServer db = ((ItemConduitNetwork) n).getDatabase();
          if (db != null) {
            return db;
          }
        }
      }
    }
    return null;
  }

  @Override
  public boolean isActive() {
    return active;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public ItemStack getItemToCheck() {
    return itemToCheck;
  }

  public void setItemToCheck(ItemStack itemToCheck) {
    this.itemToCheck = itemToCheck;
    markDirty();
  }

  public int getStartCount() {
    return startCount;
  }

  public void setStartCount(int startCount) {
    this.startCount = startCount;
    markDirty();
  }

  public int getStopCount() {
    return stopCount;
  }

  public void setStopCount(int stopCount) {
    this.stopCount = stopCount;
    markDirty();
  }

  public boolean isEmitting() {
    return getRedstoneLevel() > 0;
  }
  
  public int getRedstoneLevel() {
    return currentSignal;
  }

}
