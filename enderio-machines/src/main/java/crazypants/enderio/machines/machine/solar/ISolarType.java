package crazypants.enderio.machines.machine.solar;

import javax.annotation.Nonnull;

import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.util.PropertyEnumExtendable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public interface ISolarType extends Comparable<ISolarType>, IStringSerializable {

  public static final @Nonnull PropertyEnumExtendable<ISolarType> KIND = PropertyEnumExtendable.create("kind", ISolarType.class);

  static @Nonnull ISolarType getTypeFromMeta(int meta) {
    return KIND.byID(meta);
  }

  static int getMetaFromType(@Nonnull ISolarType solarType) {
    return KIND.byIdentity(solarType);
  }

  default boolean connectTo(@Nonnull ISolarType other) {
    return this == other;
  }

  @Nonnull
  String getUnlocalisedName();

  int getRfperTick();

  int getRfperSecond();

  int getUpgradeLevelCost();

  default @Nonnull IBlockState getBlockState() {
    return MachineObject.block_solar_panel.getBlockNN().getDefaultState().withProperty(KIND, this);
  }

  default @Nonnull ItemStack getItemStack(int size) {
    return new ItemStack(MachineObject.block_solar_panel.getItemNN(), size, ISolarType.getMetaFromType(this));
  }

  default @Nonnull ItemStack getItemStack() {
    return getItemStack(1);
  }

  @Override
  default int compareTo(ISolarType o) {
    return Integer.compare(KIND.byIdentity(this), KIND.byIdentity(o));
  }

  default boolean hasParticles() {
    return false;
  }

  default @Nonnull Vector3d getParticleColor() {
    return new Vector3d(0, 0, 0);
  }

}