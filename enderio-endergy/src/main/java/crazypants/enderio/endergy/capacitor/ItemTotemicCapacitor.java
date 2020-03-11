package crazypants.enderio.endergy.capacitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.CompoundCapabilityProvider;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.capacitor.CapabilityCapacitorData;
import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.base.capacitor.CapacitorHelper;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemTotemicCapacitor extends ItemEndergyCapacitor {

  public static ItemTotemicCapacitor create(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemTotemicCapacitor(modObject, EndergyCapacitorData.TOTEMIC_CAPACITOR, 512);
  }

  public ItemTotemicCapacitor(@Nonnull IModObject modObject, @Nonnull ICapacitorData data, int damage) {
    super(modObject, data, damage);
  }

  @Override
  public boolean canApplyAtEnchantingTable(@Nonnull ItemStack stack, @Nonnull Enchantment enchantment) {
    if (enchantment == Enchantments.EFFICIENCY) {
      return true;
    }
    final EnumEnchantmentType type = enchantment.type;
    return type != null && type.canEnchantItem(stack.getItem());
  }

  @Override
  @Nullable
  public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
    ICapabilityProvider capProvider = new ICapabilityProvider() {

      @Override
      public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityCapacitorData.getCapNN();
      }

      @Override
      @Nullable
      public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityCapacitorData.getCapNN()) {
          return CapabilityCapacitorData.getCapNN().cast(getDataFromStack(stack));
        }
        return null;
      }
    };
    return new CompoundCapabilityProvider(super.initCapabilities(stack, nbt), capProvider);
  }

  @Override
  @Nonnull
  protected ICapacitorData getDataFromStack(@Nonnull ItemStack stack) {
    int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
    if (level > 0) {
      return CapacitorHelper.increaseCapacitorLevel(getData(), level / 2f);
    }
    return getData();
  }

}
