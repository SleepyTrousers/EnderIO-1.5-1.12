package crazypants.enderio.integration.tic.queues;

import javax.annotation.Nonnull;

import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class BasinQueue {
  final @Nonnull private ItemStack output;
  final @Nonnull private ItemStack cast;
  final @Nonnull private ItemStack fluidItem;
  private Fluid fluid;
  private int amount;

  public BasinQueue(@Nonnull ItemStack output, @Nonnull ItemStack cast, @Nonnull ItemStack fluidItem, int amount) {
    this.output = output;
    this.cast = cast;
    this.setFluid(null);
    this.fluidItem = fluidItem;
    this.setAmount(amount);
  }

  public BasinQueue(@Nonnull ItemStack output, @Nonnull ItemStack cast, Fluid fluid, int amount) {
    this.output = output;
    this.cast = cast;
    this.setFluid(fluid);
    this.fluidItem = Prep.getEmpty();
    this.setAmount(amount);
  }

  public @Nonnull ItemStack getOutput() {
    return output;
  }

  public @Nonnull ItemStack getCast() {
    return cast;
  }

  public Fluid getFluid() {
    return fluid;
  }

  public void setFluid(Fluid fluid) {
    this.fluid = fluid;
  }

  public @Nonnull ItemStack getFluidItem() {
    return fluidItem;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }
}