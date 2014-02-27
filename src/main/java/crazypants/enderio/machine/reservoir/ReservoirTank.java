package crazypants.enderio.machine.reservoir;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class ReservoirTank extends FluidTank {

  static final FluidStack WATER = FluidRegistry.getFluidStack("water", 0);

  ReservoirTank(int quantity, int capacity) {
    super(WATER.getFluid(), quantity, capacity);
  }

  ReservoirTank(int capacity) {
    this(0, capacity);
  }

  ReservoirTank(FluidStack liquid, int capacity) {
    super(liquid, capacity);
  }

  public int getAmount() {
    return getFluid().amount;
  }

  public float getFilledRatio() {
    return (float) getAmount() / getCapacity();
  }

  public boolean isFull() {
    return getAmount() >= getCapacity();
  }

  public void setAmount(int amount) {
    FluidStack newLiquid = WATER.copy();
    newLiquid.amount = Math.min(getCapacity(), amount);
    setFluid(newLiquid);
  }

  @Override
  public FluidStack getFluid() {
    FluidStack l = super.getFluid();
    if(l == null) {
      l = WATER.copy();
      setFluid(l);
    }
    return l;
  }

  public int getAvailableSpace() {
    return getCapacity() - getAmount();
  }

  public void addAmount(int amount) {
    setAmount(getAmount() + amount);
  }

  @Override
  public void setCapacity(int capacity) {
    super.setCapacity(capacity);
    if(getAmount() > capacity) {
      setAmount(capacity);
    }
  }
}
