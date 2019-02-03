package crazypants.enderio.base.power.forge.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.MappedCapabilityProvider;

import crazypants.enderio.util.NbtValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

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

  /**
   * This allows you to add more capabilities to your item in addition to the energy capability you get automatically.
   * 
   * @param stack
   *          See {@link Item#initCapabilities(ItemStack, NBTTagCompound)}
   * @param nbt
   *          See {@link Item#initCapabilities(ItemStack, NBTTagCompound)}
   * @param capProv
   *          A map that already contains the energy capability
   * @return the third parameter (for chaining the call)
   */
  default @Nonnull MappedCapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt,
      @Nonnull MappedCapabilityProvider capProv) {
    return capProv;
  }

}
