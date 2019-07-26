package crazypants.enderio.machines.machine.solar;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.machines.config.config.SolarConfig;
import crazypants.enderio.machines.init.MachineObject;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public enum SolarType implements IStringSerializable {

  SIMPLE(".simple"),
  NORMAL(""),
  ADVANCED(".advanced"),
  VIBRANT(".vibrant");

  public static final @Nonnull PropertyEnum<SolarType> KIND = PropertyEnum.<SolarType> create("kind", SolarType.class);

  private final @Nonnull String unlocalisedName;

  private SolarType(@Nonnull String unlocalisedName) {
    this.unlocalisedName = unlocalisedName;
  }

  public boolean connectTo(@Nonnull SolarType other) {
    return this == other;
  }

  @Override
  public @Nonnull String getName() {
    return NullHelper.notnullJ(name().toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
  }

  public static @Nonnull SolarType getTypeFromMeta(int meta) {
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
  }

  public static int getMetaFromType(@Nonnull SolarType solarType) {
    return solarType.ordinal();
  }

  public @Nonnull String getUnlocalisedName() {
    return unlocalisedName;
  }

  public int getRfperTick() {
    return SolarConfig.blockGen.get(this.ordinal()).get();
  }

  public int getRfperSecond() {
    return SolarConfig.upgradeGen.get(this.ordinal()).get();
  }

  public int getUpgradeLevelCost() {
    return SolarConfig.upgradeCost.get(this.ordinal()).get();
  }

  public @Nonnull IBlockState getBlockState() {
    return MachineObject.block_solar_panel.getBlockNN().getDefaultState().withProperty(KIND, this);
  }

  public @Nonnull ItemStack getItemStack(int size) {
    return new ItemStack(MachineObject.block_solar_panel.getItemNN(), size, this.ordinal());
  }

  public @Nonnull ItemStack getItemStack() {
    return getItemStack(1);
  }

}