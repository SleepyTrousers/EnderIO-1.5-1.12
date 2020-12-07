package crazypants.enderio.base.power;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.inventory.InventorySlot;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.machine.gui.IPowerBarData;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

public interface IEnergyTank extends IEnergyStorage, IPowerBarData {

  boolean canUseEnergy(@Nonnull ICapacitorKey key);

  IEnergyStorage get(@Nullable EnumFacing side);

  int getMaxUsage(@Nonnull ICapacitorKey key);

  boolean hasCapacitor();

  boolean isFull();

  void loseEnergy();

  void setEnergyLoss(ICapacitorKey energyLoss);

  void setEnergyStored(int stored);

  boolean updateCapacitorFromSlot(@Nonnull InventorySlot slot);

  /**
   * Tries to use the energy given be the "use" capacitor key.
   * 
   * @return <code>true</code> if the energy was used
   */
  boolean useEnergy();

  /**
   * Tries to use the energy given be the given capacitor key.
   * 
   * @return <code>true</code> if the energy was used
   */
  boolean useEnergy(@Nonnull ICapacitorKey key);

}
