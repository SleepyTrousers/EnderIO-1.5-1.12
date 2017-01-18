package crazypants.enderio.conduit.packet;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.me.IMEConduit;
import crazypants.enderio.conduit.oc.IOCConduit;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;

public enum ConTypeEnum {

  POWER(IPowerConduit.class),
  FLUID(ILiquidConduit.class),
  ITEM(IItemConduit.class),
  REDSTONE(IRedstoneConduit.class),
  OC(IOCConduit.class),
  ME(IMEConduit.class);

  final Class<? extends IConduit> baseType;

  private ConTypeEnum(Class<? extends IConduit> baseType) {
    this.baseType = baseType;
  }

  public Class<? extends IConduit> getBaseType() {
    return baseType;
  }

  public static ConTypeEnum get(IConduit con) {
    Class<? extends IConduit> from = con.getBaseConduitType();
    for (ConTypeEnum ct : ConTypeEnum.values()) {
      if (ct.getBaseType() == from) {
        return ct;
      }
    }
    return null;
  }
}