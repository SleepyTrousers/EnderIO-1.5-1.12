package crazypants.enderio.integration.tic.queues;

import javax.annotation.Nonnull;

import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class SmeltQueue {
  final @Nonnull private ItemStack input;
  final @Nonnull private ItemStack output;
  private Fluid fluidOutput;
  private float amount;

  public SmeltQueue(@Nonnull ItemStack input, @Nonnull ItemStack output, float amount) {
    super();
    this.input = input;
    this.output = output;
    this.setFluidOutput(null);
    this.setAmount(amount);
  }

  public SmeltQueue(@Nonnull ItemStack input, Fluid fluidOutput, float amount) {
    super();
    this.input = input;
    this.output = Prep.getEmpty();
    this.setFluidOutput(fluidOutput);
    this.setAmount(amount);
  }

  public @Nonnull ItemStack getInput() {
    return input;
  }

  public @Nonnull ItemStack getOutput() {
    return output;
  }

  public Fluid getFluidOutput() {
    return fluidOutput;
  }

  public void setFluidOutput(Fluid fluidOutput) {
    this.fluidOutput = fluidOutput;
  }

  public float getAmount() {
    return amount;
  }

  public void setAmount(float amount) {
    this.amount = amount;
  }
}