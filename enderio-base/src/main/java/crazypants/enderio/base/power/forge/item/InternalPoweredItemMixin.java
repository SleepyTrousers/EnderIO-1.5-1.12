package crazypants.enderio.base.power.forge.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.MappedCapabilityProvider;
import com.enderio.core.common.transform.SimpleMixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

@SimpleMixin(IInternalPoweredItem.class)
public abstract class InternalPoweredItemMixin extends Item implements IInternalPoweredItem {

  // Capabilities (Energy)

  @Override
  public @Nullable ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
    return initCapabilities(stack, nbt, new MappedCapabilityProvider().add(CapabilityEnergy.ENERGY, new InternalPoweredItemCap(this, stack)));
  }

}
