package crazypants.enderio.power;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public interface ItemPowerCapabilityProvider {

  boolean hasCapability(@Nonnull ItemStack stack, Capability<?> capability, @Nullable EnumFacing facing);

  <T> T getCapability(@Nonnull ItemStack stack, Capability<T> capability, @Nullable EnumFacing facing);

}