package crazypants.enderio.base.power;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.util.NbtValue;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IInternalPoweredItem {

  int getMaxEnergyStored(@Nonnull ItemStack container);

  int getMaxInput(@Nonnull ItemStack container);

  int getMaxOutput(@Nonnull ItemStack container);

  default int getEnergyStored(@Nonnull ItemStack container) {
    return NbtValue.ENERGY.getInt(container);
  }

  default void setEnergyStored(@Nonnull ItemStack container, int energy) {
    NbtValue.ENERGY.setInt(container, MathHelper.clamp(energy, 0, getMaxEnergyStored(container)));
  }

  default void setFull(@Nonnull ItemStack container) {
    setEnergyStored(container, getMaxEnergyStored(container));
  }

  default @Nonnull ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
    return new ItemPowerCapabilityBackend(stack);
  }

}
