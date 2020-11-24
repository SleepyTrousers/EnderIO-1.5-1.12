package crazypants.enderio.invpanel.sensor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.invpanel.capability.CapabilityDatabaseHandler;
import crazypants.enderio.base.invpanel.capability.IDatabaseHandler;
import crazypants.enderio.base.invpanel.database.IInventoryDatabaseServer;
import crazypants.enderio.base.invpanel.database.IServerItemEntry;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.machine.task.ContinuousTask;
import crazypants.enderio.base.paint.IPaintable.IPaintableTileEntity;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.invpanel.init.InvpanelObject;
import crazypants.enderio.invpanel.network.PacketHandler;
import crazypants.enderio.invpanel.network.sensor.PacketActive;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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
  @Nonnull
  private ItemStack itemToCheck = ItemStack.EMPTY;

  // send client side for rendering
  private boolean active = false;

  public TileInventoryPanelSensor() {
    super(new SlotDefinition(0, 0, 0), CapacitorKey.LEGACY_ENERGY_INTAKE, CapacitorKey.LEGACY_ENERGY_BUFFER, CapacitorKey.LEGACY_ENERGY_USE);
  }

  @Override
  @Nonnull
  public String getMachineName() {
    return InvpanelObject.blockInventoryPanelSensor.getUnlocalisedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack item) {
    return false;
  }

  @Override
  public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
    return mode == IoMode.NONE || mode == IoMode.DISABLED;
  }

  @Override
  protected void checkProgress(boolean redstoneChecksPassed) {
    usePower();
    if (slowstart > 0) {
      // give the network a while to form after the chunk has loaded to prevent
      // bogus readings (all zeros)
      slowstart--;
      return;
    }

    if (shouldDoWorkThisTick(10)) {
      if (!itemToCheck.isEmpty()) {

        IInventoryDatabaseServer db = getInventoryDB();
        if (db != null) {
          int invHasCount = 0;
          IServerItemEntry entry = db.lookupItem(itemToCheck, null, false);
          if (entry != null) {
            invHasCount = entry.countItems();
          }
          updateRedstone(invHasCount);
        } else {
          setCurrentSignal(0);
        }
      } else if (isEmitting()) {
        setCurrentSignal(0);
      }
    }
  }

  private void updateRedstone(int invHasCount) {
    if (itemToCheck.isEmpty()) {
      setCurrentSignal(0);
      return;
    }
    if (isEmitting()) {

      if (invHasCount >= stopCount) {
        setCurrentSignal(0);
      }
    } else {
      if (invHasCount < startCount) {
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
    world.notifyNeighborsOfStateChange(getPos(), getBlockType(), true);
  }

  @Override
  protected IPoweredTask createTask(@Nonnull IMachineRecipe nextRecipe, long nextSeed) {
    return new ContinuousTask(getPowerUsePerTick());
  }

  @Nullable
  public IInventoryDatabaseServer getInventoryDB() {

    for (EnumFacing dir : EnumFacing.values()) {
      TileEntity te = world.getTileEntity(getPos().offset(dir));
      if (te != null) {
        if (te.hasCapability(CapabilityDatabaseHandler.DATABASE_HANDLER_CAPABILITY, dir.getOpposite())) {
          IDatabaseHandler dbHandler = te.getCapability(CapabilityDatabaseHandler.DATABASE_HANDLER_CAPABILITY, dir.getOpposite());
          if (dbHandler != null) {
            return dbHandler.getDatabase();
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

  @Nonnull
  public ItemStack getItemToCheck() {
    return itemToCheck;
  }

  public void setItemToCheck(@Nonnull ItemStack itemToCheck) {
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
