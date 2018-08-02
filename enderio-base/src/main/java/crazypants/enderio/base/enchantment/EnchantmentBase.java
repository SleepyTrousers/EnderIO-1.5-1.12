package crazypants.enderio.base.enchantment;

import javax.annotation.Nonnull;

import com.enderio.core.api.common.enchant.IAdvancedEnchant;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.factory.IValue;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public abstract class EnchantmentBase extends Enchantment implements IAdvancedEnchant {

  protected final @Nonnull IValue<Boolean> enableFlag;
  protected final @Nonnull IValue<Rarity> rarity;

  protected EnchantmentBase(@Nonnull String name, @Nonnull IValue<Rarity> rarityIn, @Nonnull EnumEnchantmentType typeIn, @Nonnull IValue<Boolean> enableFlag) {
    this(name, rarityIn, typeIn, EntityEquipmentSlot.values(), enableFlag);
  }

  protected EnchantmentBase(@Nonnull String name, @Nonnull IValue<Rarity> rarityIn, @Nonnull EnumEnchantmentType typeIn, @Nonnull EntityEquipmentSlot[] slots,
      @Nonnull IValue<Boolean> enableFlag) {
    super(rarityIn.get(), typeIn, slots);
    this.enableFlag = enableFlag;
    this.rarity = rarityIn;
    setName(EnderIO.DOMAIN + "." + name);
    setRegistryName(EnderIO.DOMAIN, name);
  }

  @Override
  public @Nonnull Rarity getRarity() {
    return rarity.get();
  }

  @Override
  public boolean canApply(@Nonnull ItemStack stack) {
    return enableFlag.get() && super.canApply(stack);
  }

  @Override
  public boolean canApplyAtEnchantingTable(@Nonnull ItemStack stack) {
    return enableFlag.get() && super.canApplyAtEnchantingTable(stack);
  }

  @Override
  public boolean isAllowedOnBooks() {
    return enableFlag.get() && super.isAllowedOnBooks();
  }

}
