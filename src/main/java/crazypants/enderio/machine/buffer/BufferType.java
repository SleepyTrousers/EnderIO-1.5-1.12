package crazypants.enderio.machine.buffer;

import java.util.Locale;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;

public enum BufferType implements IStringSerializable {

  ITEM(true, false, false),
  POWER(false, true, false),
  OMNI(true, true, false),
  CREATIVE(true, true, true);

  final boolean hasInventory;
  final boolean hasPower;
  final boolean isCreative;

  private BufferType(boolean hasInventory, boolean hasPower, boolean isCreative) {
    this.hasInventory = hasInventory;
    this.hasPower = hasPower;
    this.isCreative = isCreative;
  }

  public static BufferType get(TileBuffer buffer) {
    return !buffer.hasPower() ? ITEM : !buffer.hasInventory() ? POWER : !buffer.isCreative() ? OMNI : CREATIVE;
  }

  public static final PropertyEnum<BufferType> TYPE = PropertyEnum.<BufferType> create("type", BufferType.class);

  @Override
  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

  public String getUnlocalizedName() {
    return "tile." + ModObject.blockBuffer.getUnlocalisedName() + "." + getName();
  }

  public static BufferType getTypeFromMeta(int meta) {
    return values()[meta >= 0 && meta < values().length ? meta : 0];
  }

  public static int getMetaFromType(BufferType value) {
    return value.ordinal();
  }

  public static ItemStack getStack(BufferType type) {
    return new ItemStack(EnderIO.blockBuffer, 1, type.ordinal());
  }

}
