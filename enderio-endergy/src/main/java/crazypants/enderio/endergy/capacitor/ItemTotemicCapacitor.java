package crazypants.enderio.endergy.capacitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.CompoundCapabilityProvider;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.capacitor.CapabilityCapacitorData;
import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.base.capacitor.CapacitorHelper;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemTotemicCapacitor extends ItemEndergyCapacitor {

  private static final int ENCHANT_ID_EFFICIENCY = 32;

  public static ItemTotemicCapacitor create(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemTotemicCapacitor(modObject, EndergyCapacitorData.TOTEMIC_CAPACITOR, 512);
  }

  public ItemTotemicCapacitor(@Nonnull IModObject modObject, @Nonnull ICapacitorData data, int damage) {
    super(modObject, data, damage);
  }

  @Override
  public boolean canApplyAtEnchantingTable(@Nonnull ItemStack stack, @Nonnull Enchantment enchantment) {
    if (enchantment.getName().equals("efficiency")) {
      return true;
    }
    final EnumEnchantmentType type = enchantment.type;
    return type == null ? false : type.canEnchantItem(stack.getItem());
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
          return CapabilityCapacitorData.getCapNN().cast(NullHelper.notnullJ(getDataFromStack(stack), "Enum.values() has a null"));
        }
        return null;
      }
    };
    return new CompoundCapabilityProvider(super.initCapabilities(stack, nbt), capProvider);
  }

  @Override
  @Nonnull
  protected ICapacitorData getDataFromStack(@Nonnull ItemStack stack) {
    NBTTagList enchantments = stack.getEnchantmentTagList();

    for (int i = 0; i < enchantments.tagCount(); i++) {
      NBTTagCompound ench = enchantments.getCompoundTagAt(i);
      if (ench.hasKey("id") && ench.getInteger("id") == ENCHANT_ID_EFFICIENCY) {
        return CapacitorHelper.increaseCapacitorLevel(EndergyCapacitorData.TOTEMIC_CAPACITOR, ench.getInteger("lvl") / 2);
      }
    }
    return getData();
  }

}
