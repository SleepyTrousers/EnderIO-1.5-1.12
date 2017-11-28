package crazypants.enderio.machine.buffer;

import static crazypants.enderio.machine.MachineObject.block_buffer;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.machine.MachineObject;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

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

  public static @Nonnull BufferType get(TileBuffer buffer) {
    return !buffer.hasPower() ? ITEM : !buffer.hasInventory() ? POWER : !buffer.isCreative() ? OMNI : CREATIVE;
  }

  public static final @Nonnull PropertyEnum<BufferType> TYPE = NullHelper.notnullM(PropertyEnum.<BufferType> create("type", BufferType.class),
      "PropertyEnum.create()");

  @Override
  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

  public @Nonnull String getUnlocalizedName() {
    return "tile." + MachineObject.block_buffer.getUnlocalisedName() + "." + getName();
  }

  @Nonnull
  public static BufferType getTypeFromMeta(int meta) {
    return values()[meta >= 0 && meta < values().length ? meta : 0];
  }

  public static int getMetaFromType(BufferType value) {
    return value.ordinal();
  }

  public static @Nonnull ItemStack getStack(BufferType type) {
    return new ItemStack(block_buffer.getBlock(), 1, type.ordinal());
  }

}
