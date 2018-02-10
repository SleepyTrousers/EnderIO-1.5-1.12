package crazypants.enderio.integration.tic.queues;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import net.minecraftforge.fluids.Fluid;

public class BasinQueue {
  final @Nonnull private Things output;
  final @Nonnull private Things cast;
  final private Things fluidItem;
  private Fluid fluid;
  private float amount;

  public BasinQueue(@Nonnull Things output, @Nonnull Things cast, @Nonnull Things fluid, float amount) {
    this.output = output;
    this.cast = cast;
    this.fluid = null;
    this.fluidItem = fluid;
    this.amount = amount;
  }

  public BasinQueue(@Nonnull Things output, @Nonnull Things cast, Fluid fluid, float amount) {
    this.output = output;
    this.cast = cast;
    this.setFluid(fluid);
    this.fluidItem = null;
    this.amount = amount;
  }

  public @Nonnull Things getOutput() {
    return output;
  }

  public @Nonnull Things getCast() {
    return cast;
  }

  public Fluid getFluid() {
    return fluid;
  }

  public void setFluid(Fluid fluid) {
    this.fluid = fluid;
  }

  public Things getFluidItem() {
    return fluidItem;
  }

  public float getAmount() {
    return amount;
  }

  public void setAmount(float amount) {
    this.amount = amount;
  }
}