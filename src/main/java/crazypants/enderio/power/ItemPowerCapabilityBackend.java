package crazypants.enderio.power;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemPowerCapabilityBackend implements ICapabilityProvider {

  private static final @Nonnull List<ItemPowerCapabilityProvider> providers = new ArrayList<ItemPowerCapabilityProvider>();

  public static void register(ItemPowerCapabilityProvider provider) {
    providers.add(provider);
  }

  private final ItemStack stack;

  public ItemPowerCapabilityBackend(ItemStack stack) {
    this.stack = stack;
  }

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
    for (ItemPowerCapabilityProvider itemPowerCapabilityProvider : providers) {
      if (itemPowerCapabilityProvider.hasCapability(stack, capability, facing)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
    for (ItemPowerCapabilityProvider itemPowerCapabilityProvider : providers) {
      T res = itemPowerCapabilityProvider.getCapability(stack, capability, facing);
      if (res != null) {
        return res;
      }
    }
    return null;
  }

}
