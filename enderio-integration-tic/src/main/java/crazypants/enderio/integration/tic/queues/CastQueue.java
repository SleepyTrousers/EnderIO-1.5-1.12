package crazypants.enderio.integration.tic.queues;

import javax.annotation.Nonnull;

import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class CastQueue {
  final @Nonnull private ItemStack result;
  final @Nonnull private ItemStack cast;
  final @Nonnull private ItemStack item;
  final private boolean consumeCast;
  private Fluid fluid;
  private float amount;

  public CastQueue(@Nonnull ItemStack result, @Nonnull ItemStack cast, @Nonnull ItemStack item, float amount, boolean consumeCast) {
    this.result = result;
    this.cast = cast;
    this.item = item;
    this.setFluid(null);
    this.setAmount(amount);
    this.consumeCast = consumeCast;
  }

  public CastQueue(@Nonnull ItemStack result, @Nonnull ItemStack cast, @Nonnull Fluid fluid, float amount, boolean consumeCast) {
    this.result = result;
    this.cast = cast;
    this.item = Prep.getEmpty();
    this.setFluid(fluid);
    this.setAmount(amount);
    this.consumeCast = consumeCast;
  }

  public @Nonnull ItemStack getResult() {
    return result;
  }

  public @Nonnull ItemStack getCast() {
    return cast;
  }

  public @Nonnull ItemStack getItem() {
    return item;
  }

  public Fluid getFluid() {
    return fluid;
  }

  public void setFluid(Fluid fluid) {
    this.fluid = fluid;
  }

  public float getAmount() {
    return amount;
  }

  public void setAmount(float amount) {
    this.amount = amount;
  }

  public boolean isConsumeCast() {
    return consumeCast;
  }
}