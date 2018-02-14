package crazypants.enderio.base.material.food;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.stackable.IProducer;

import crazypants.enderio.util.Prep;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.base.init.ModObject.itemEnderFood;

public enum EnderFood {
  ENDERIOS("enderios", new IProducer() {
    @Override
    public @Nullable Item getItem() {
      return Items.BOWL;
    }
  }, 10, 0.8f, true);

  final @Nonnull private String unlocalisedName;
  private final int hunger;
  private final float saturation;
  private final boolean doesTeleport;
  private final IProducer containerItem;

  public static final EnderFood[] VALUES = values();

  private EnderFood(@Nonnull String name, IProducer containerItem, int hunger, float saturation, boolean doesTeleport) {
    this.unlocalisedName = name;
    this.containerItem = containerItem;
    this.hunger = hunger;
    this.saturation = saturation;
    this.doesTeleport = doesTeleport;
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

  public @Nonnull ItemStack getContainerItem() {
    return containerItem != null ? new ItemStack(containerItem.getItemNN()) : Prep.getEmpty();
  }

  public int getHunger() {
    return hunger;
  }

  public float getSaturation() {
    return saturation;
  }

  public boolean doesTeleport() {
    return doesTeleport;
  }

}