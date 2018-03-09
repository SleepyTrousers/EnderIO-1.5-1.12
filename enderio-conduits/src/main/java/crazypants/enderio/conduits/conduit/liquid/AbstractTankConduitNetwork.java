package crazypants.enderio.conduits.conduit.liquid;

import javax.annotation.Nonnull;

import crazypants.enderio.conduits.conduit.AbstractConduitNetwork;
import net.minecraftforge.fluids.FluidStack;

public class AbstractTankConduitNetwork<T extends AbstractTankConduit> extends AbstractConduitNetwork<ILiquidConduit, T> {

  protected FluidStack liquidType;
  protected boolean fluidTypeLocked = false;

  protected AbstractTankConduitNetwork(@Nonnull Class<T> cl) {
    super(cl, ILiquidConduit.class);
  }

  public FluidStack getFluidType() {
    return liquidType;
  }

  @Override
  public void addConduit(@Nonnull T con) {
    super.addConduit(con);
    con.setFluidType(liquidType);
    if (con.fluidTypeLocked && !fluidTypeLocked) {
      setFluidTypeLocked(true);
    }
  }

  public boolean setFluidType(FluidStack newType) {
    if (liquidType != null && liquidType.isFluidEqual(newType)) {
      return false;
    }
    if (newType != null) {
      liquidType = newType.copy();
      liquidType.amount = 0;
    } else {
      liquidType = null;
    }
    for (AbstractTankConduit conduit : getConduits()) {
      conduit.setFluidType(liquidType);
    }
    return true;
  }

  public void setFluidTypeLocked(boolean fluidTypeLocked) {
    if (this.fluidTypeLocked == fluidTypeLocked) {
      return;
    }
    this.fluidTypeLocked = fluidTypeLocked;
    for (AbstractTankConduit conduit : getConduits()) {
      conduit.setFluidTypeLocked(fluidTypeLocked);
    }
  }

  public boolean canAcceptLiquid(FluidStack acceptable) {
    return areFluidsCompatable(liquidType, acceptable);
  }

  public static boolean areFluidsCompatable(FluidStack a, FluidStack b) {
    if (a == null || b == null) {
      return true;
    }
    return a.isFluidEqual(b);
  }

  public int getTotalVolume() {
    int totalVolume = 0;
    for (T con : getConduits()) {
      totalVolume += con.getTank().getFluidAmount();
    }
    return totalVolume;
  }

}
