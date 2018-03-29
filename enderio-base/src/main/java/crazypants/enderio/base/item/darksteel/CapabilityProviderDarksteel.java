package crazypants.enderio.base.item.darksteel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.CompoundCapabilityProvider;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade.EnergyUpgradeHolder;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;

public class CapabilityProviderDarksteel extends CompoundCapabilityProvider implements ICapabilityProvider {
  
  private final @Nonnull ItemStack stack;
  private final @Nonnull IDarkSteelItem dsItem;
  
  public CapabilityProviderDarksteel(@Nonnull ItemStack stack, ICapabilityProvider... parents) {
    super(parents);
    if (!(stack.getItem() instanceof IDarkSteelItem)) {
      throw new IllegalArgumentException("Cannot use DS capability provider on non DS item " + stack.getItem().getClass().getName());
    }
    this.stack = stack;
    this.dsItem = (IDarkSteelItem) stack.getItem();
  }

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    return getCapability(capability, facing) != null || super.hasCapability(capability, facing);
  }

  @Override
  @Nullable
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityEnergy.ENERGY) {
      final EnergyUpgradeHolder holder = EnergyUpgradeManager.loadFromItem(stack);
      if (holder != null) {
        return CapabilityEnergy.ENERGY.cast(new EnergyStorage(holder.getCapacity()) {

          @Override
          public int extractEnergy(int maxExtract, boolean simulate) {
            int removed = holder.extractEnergy(maxExtract, simulate);
            if (!simulate && removed > 0) {
              holder.writeToItem(stack, dsItem);
            }
            return removed;
          }

          @Override
          public int receiveEnergy(int maxReceive, boolean simulate) {
            int accepted = holder.receiveEnergy(maxReceive, simulate);
            if (!simulate && accepted > 0) {
              holder.writeToItem(stack, dsItem);
            }
            return accepted;
          }

          @Override
          public int getEnergyStored() {
            return holder.getEnergy();
          }
        });
      }
    }
    return super.getCapability(capability, facing);
  }

}
