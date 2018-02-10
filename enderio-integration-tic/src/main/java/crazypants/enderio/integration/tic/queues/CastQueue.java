package crazypants.enderio.integration.tic.queues;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.stackable.Things;

import net.minecraftforge.fluids.Fluid;

public class CastQueue {
  final @Nonnull private Things result;
  final @Nonnull private Things cast;
  final @Nullable private Things item;
  final private boolean consumeCast;
  private Fluid fluid;
  private float amount;

  public CastQueue(@Nonnull Things result, @Nonnull Things cast, @Nonnull Things item, float amount, boolean consumeCast) {
    this.result = result;
    this.cast = cast;
    this.item = item;
    this.setFluid(null);
    this.setAmount(amount);
    this.consumeCast = consumeCast;
  }

  public CastQueue(@Nonnull Things result, @Nonnull Things cast, @Nonnull Fluid fluid, float amount, boolean consumeCast) {
    this.result = result;
    this.cast = cast;
    this.item = null;
    this.setFluid(fluid);
    this.setAmount(amount);
    this.consumeCast = consumeCast;
  }

  public @Nonnull Things getResult() {
    return result;
  }

  public @Nonnull Things getCast() {
    return cast;
  }

  public @Nullable Things getItem() {
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