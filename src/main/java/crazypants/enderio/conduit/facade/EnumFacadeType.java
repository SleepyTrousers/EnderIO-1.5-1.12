package crazypants.enderio.conduit.facade;

import java.util.Locale;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;

public enum EnumFacadeType implements IStringSerializable {

  BASIC("", false, false),
  HARDENED(".hardened", true, false),
  TRANSPARENT(".transparent", false, true),
  TRANSPARENT_HARDENED(".transparent.hardened", true, true);

  public static final PropertyEnum<EnumFacadeType> TYPE = PropertyEnum.<EnumFacadeType> create("type", EnumFacadeType.class);

  private final String namePostfix;
  private final boolean hardened, transparent;

  private EnumFacadeType(String namePostfix, boolean hardened, boolean transparent) {
    this.namePostfix = namePostfix;
    this.hardened = hardened;
    this.transparent = transparent;
  }

  @Override
  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }
  
  public String getUnlocName(Item me) {
    return me.getUnlocalizedName() + namePostfix;
  }
  
  public static EnumFacadeType getTypeFromMeta(int meta) {
    return values()[meta >= 0 && meta < values().length ? meta : 0];
  }

  public static int getMetaFromType(EnumFacadeType value) {
    return value.ordinal();
  }

  public boolean isHardened() {
    return hardened;
  }

  public boolean isTransparent() {
    return transparent;
  }

}
