package crazypants.enderio.conduit.liquid;

import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.conduit.AbstractConduitNetwork;

public class AbstractTankConduitNetwork<T extends AbstractTankConduit> extends AbstractConduitNetwork<ILiquidConduit, T> {

  protected FluidStack liquidType;
  protected boolean fluidTypeLocked = false;

  protected AbstractTankConduitNetwork(Class<T> cl) {
    super(cl);
  }

  public FluidStack getFluidType() {
    return liquidType;
  }

  @Override
  public Class<ILiquidConduit> getBaseConduitType() {
    return ILiquidConduit.class;
  }

  @Override
  public void addConduit(T con) {
    super.addConduit(con);
    con.setFluidType(liquidType);
    if(con.fluidTypeLocked && !fluidTypeLocked) {
      setFluidTypeLocked(true);
    }
  }

  public boolean setFluidType(FluidStack newType) {
    if(liquidType != null && liquidType.isFluidEqual(newType)) {
      return false;
    }
    if(newType != null) {
      liquidType = newType.copy();
      liquidType.amount = 0;
    } else {
      liquidType = null;
    }
    for (AbstractTankConduit conduit : conduits) {
      conduit.setFluidType(liquidType);
    }
    return true;
  }

  public void setFluidTypeLocked(boolean fluidTypeLocked) {
    if(this.fluidTypeLocked == fluidTypeLocked) {
      return;
    }
    this.fluidTypeLocked = fluidTypeLocked;
    for (AbstractTankConduit conduit : conduits) {
      conduit.setFluidTypeLocked(fluidTypeLocked);
    }
  }

  public boolean canAcceptLiquid(FluidStack acceptable) {
    return areFluidsCompatable(liquidType, acceptable);
  }

  public static boolean areFluidsCompatable(FluidStack a, FluidStack b) {
    if(a == null || b == null) {
      return true;
    }
    return a.isFluidEqual(b);
  }

  public int getTotalVolume() {
    int totalVolume = 0;
    for (T con : conduits) {
      totalVolume += con.getTank().getFluidAmount();
    }
    return totalVolume;
  }

}
