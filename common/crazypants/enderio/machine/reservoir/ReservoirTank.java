package crazypants.enderio.machine.reservoir;

import net.minecraftforge.liquids.*;

public class ReservoirTank extends LiquidTank {

  static final LiquidStack WATER = LiquidDictionary.getLiquid("Water", 0);

  ReservoirTank(int quantity, int capacity) {
    super(WATER.itemID, quantity, capacity);
  }

  ReservoirTank(int capacity) {
    this(0, capacity);
  }

  ReservoirTank(LiquidStack liquid, int capacity) {
    super(liquid, capacity);
  }

  public int getAmount() {
    return getLiquid().amount;
  }

  public float getFilledRatio() {
    return (float) getAmount() / getCapacity();
  }

  public boolean isFull() {
    return getAmount() >= getCapacity();
  }

  public void setAmount(int amount) {
    LiquidStack newLiquid = WATER.copy();
    newLiquid.amount = Math.min(getCapacity(), amount);
    setLiquid(newLiquid);
  }

  @Override
  public LiquidStack getLiquid() {
    LiquidStack l = super.getLiquid();
    if (l == null) {
      l = WATER.copy();
      setLiquid(l);
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
    if (getAmount() > capacity) {
      setAmount(capacity);
    }
  }
}
