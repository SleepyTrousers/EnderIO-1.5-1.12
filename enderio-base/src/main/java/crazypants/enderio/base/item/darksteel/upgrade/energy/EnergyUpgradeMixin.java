package crazypants.enderio.base.item.darksteel.upgrade.energy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.MappedCapabilityProvider;
import com.enderio.core.common.transform.SimpleMixin;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

@SimpleMixin(IDarkSteelItem.class)
public abstract class EnergyUpgradeMixin extends Item implements IDarkSteelItem {

  @Override
  public @Nullable ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
    return initCapabilities(stack, nbt, new MappedCapabilityProvider().add(CapabilityEnergy.ENERGY, new EnergyUpgradeCap(stack)));
  }

}
