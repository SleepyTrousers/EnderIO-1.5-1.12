package crazypants.enderio.integration.tic.queues;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import net.minecraftforge.fluids.Fluid;

public class SmeltQueue {
  final @Nonnull private Things input;
  final private Things output;
  private Fluid fluidOutput;
  private float amount;

  public SmeltQueue(@Nonnull Things input, @Nonnull Things output, float amount) {
    super();
    this.input = input;
    this.output = output;
    this.setFluidOutput(null);
    this.setAmount(amount);
  }

  public SmeltQueue(@Nonnull Things input, Fluid fluidOutput, float amount) {
    super();
    this.input = input;
    this.output = null;
    this.setFluidOutput(fluidOutput);
    this.setAmount(amount);
  }

  public @Nonnull Things getInput() {
    return input;
  }

  public Things getOutput() {
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