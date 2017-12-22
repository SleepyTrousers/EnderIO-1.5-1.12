package crazypants.enderio.machines.machine.buffer;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.machines.init.MachineObject;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import static crazypants.enderio.machines.init.MachineObject.block_buffer;

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

  public static final @Nonnull PropertyEnum<BufferType> TYPE = NullHelper.notnullM(PropertyEnum.<BufferType> create("type", BufferType.class),
      "PropertyEnum.create()");

  @Override
  public @Nonnull String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

  public @Nonnull String getUnlocalizedName() {
    return "tile." + MachineObject.block_buffer.getUnlocalisedName() + "." + getName();
  }

  public static @Nonnull BufferType getTypeFromMeta(int meta) {
    return NullHelper.first(values()[meta >= 0 && meta < values().length ? meta : 0], ITEM);
  }

  public static int getMetaFromType(BufferType value) {
    return value.ordinal();
  }

  public static @Nonnull ItemStack getStack(BufferType type) {
    return new ItemStack(block_buffer.getBlockNN(), 1, type.ordinal());
  }

}
