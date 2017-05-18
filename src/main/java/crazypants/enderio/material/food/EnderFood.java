package crazypants.enderio.material.food;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.EnderIO;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static crazypants.enderio.ModObject.itemEnderFood;

public enum EnderFood {
  ENDERIOS("enderios", 10, 0.8f);

  public static NNList<ResourceLocation> resources() {
    NNList<ResourceLocation> res = new NNList<ResourceLocation>();
    for (EnderFood c : values()) {
      res.add(new ResourceLocation(EnderIO.DOMAIN, c.unlocalisedName));
    }
    return res;
  }

  public final @Nonnull String unlocalisedName;
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
}