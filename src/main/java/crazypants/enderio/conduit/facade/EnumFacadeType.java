package crazypants.enderio.conduit.facade;

import java.util.Locale;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;

public enum EnumFacadeType implements IStringSerializable {

  BASIC,
  HARDENED;

  public static final PropertyEnum<EnumFacadeType> TYPE = PropertyEnum.<EnumFacadeType> create("type", EnumFacadeType.class);

  @Override
  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }
  
  public String getUnlocName(Item me) {
    return this == BASIC ? me.getUnlocalizedName() : me.getUnlocalizedName() + ".hardened";
  }
  
  public static EnumFacadeType getTypeFromMeta(int meta) {
    return values()[meta >= 0 && meta < values().length ? meta : 0];
  }

  public static int getMetaFromType(EnumFacadeType value) {
    return value.ordinal();
  }
  

  
}
