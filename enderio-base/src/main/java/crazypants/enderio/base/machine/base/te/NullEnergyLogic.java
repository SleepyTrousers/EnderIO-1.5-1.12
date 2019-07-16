package crazypants.enderio.base.machine.base.te;

import javax.annotation.Nonnull;

import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.base.power.IEnergyTank;
import crazypants.enderio.base.power.NullEnergyTank;
import net.minecraft.item.ItemStack;

public final class NullEnergyLogic implements IEnergyLogic {

  public static final @Nonnull NullEnergyLogic INSTANCE = new NullEnergyLogic();

  private NullEnergyLogic() {
  }

  @Override
  public void serverTick() {
  }

  @Override
  public void processTasks(boolean redstoneCheck) {
  }

  @Override
  public int getScaledPower() {
    return 0;
  }

  @Override
  public boolean displayPower() {
    return false;
  }

  @Override
  public boolean hasPower() {
    return false;
  }

  @Override
  @Nonnull
  public ICapacitorData getCapacitorData() {
    return NullEnergyTank.getInstance().getCapacitorData();
  }

  @Override
  @Nonnull
  public IEnergyTank getEnergy() {
    return NullEnergyTank.getInstance();
  }

  @Override
  public void updateCapacitorFromSlot() {
  }

  @Override
  public void readCustomNBT(@Nonnull ItemStack stack) {
  }

  @Override
  public void writeCustomNBT(@Nonnull ItemStack stack) {
  }

  @Override
  public void damageCapacitor() {
  }

}
