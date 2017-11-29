package crazypants.enderio.machines.machine.obelisk.spawn;

import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.obelisk.AbstractBlockObelisk;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.entity.EntityLivingBase;

import javax.annotation.Nonnull;



@Storable
public abstract class TileEntityAbstractSpawningObelisk extends AbstractMobObelisk implements ISpawnCallback {

  private boolean registered = false;

  @Override
  public abstract Result isSpawnPrevented(EntityLivingBase mob);

  @Override
  public abstract @Nonnull String getMachineName();
  

  public TileEntityAbstractSpawningObelisk(SlotDefinition slotDefinition, ICapacitorKey maxEnergyRecieved, ICapacitorKey maxEnergyStored,
                                           ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  public TileEntityAbstractSpawningObelisk(SlotDefinition slotDefinition, MachineObject modObject) {
    super(slotDefinition, modObject);
  }

  @Override
  public void invalidate() {
    super.invalidate();    
    SpawningObeliskController.instance.deregisterGuard(this);
    registered = false;
  }

  @Override
  public float getRange() {
    return (float) AbstractBlockObelisk.DUMMY;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    if (redstoneCheck && hasPower()) {
      if(!registered) {
        SpawningObeliskController.instance.registerGuard(this);
        registered = true;
      }
      usePower();
    } 
    return false;    
  }

}