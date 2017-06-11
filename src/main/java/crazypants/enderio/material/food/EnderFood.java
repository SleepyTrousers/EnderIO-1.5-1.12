package crazypants.enderio.material.food;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import static crazypants.enderio.init.ModObject.itemEnderFood;

public enum EnderFood {
  ENDERIOS("enderios", 10, 0.8f);

  final @Nonnull private String unlocalisedName;
  public final int hunger;
  public final float saturation;

  public static final EnderFood[] VALUES = values();

  private EnderFood(@Nonnull String name, int hunger, float saturation) {
    this.unlocalisedName = name;
    this.hunger = hunger;
    this.saturation = saturation;
  }

  public @Nonnull ItemStack getStack() {
    return getStack(1);
  }

  public @Nonnull ItemStack getStack(int size) {
    return new ItemStack(itemEnderFood.getItemNN(), size, ordinal());
  }

  public static EnderFood get(@Nonnull ItemStack stack) {
    return VALUES[stack.getItemDamage() % VALUES.length];
  }

  public String getUnlocalisedName() {
    return unlocalisedName;
  }

}