package crazypants.enderio.power;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public interface ItemPowerCapabilityProvider {

  boolean hasCapability(ItemStack stack, Capability<?> capability, @Nullable EnumFacing facing);

  <T> T getCapability(ItemStack stack, Capability<T> capability, @Nullable EnumFacing facing);

}