package crazypants.enderio.base.material.glass;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.config.config.BlockConfig;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.entity.Entity;
import net.minecraft.util.IStringSerializable;

public enum FusedQuartzType implements IStringSerializable {

  FUSED_QUARTZ("fusedQuartz", BaseMaterial.QUARTZ, Upgrade.NONE, IPassingCallback.NONE),
  FUSED_GLASS("fusedGlass", BaseMaterial.GLASS, Upgrade.NONE, IPassingCallback.NONE),
  ENLIGHTENED_FUSED_QUARTZ("enlightenedFusedQuartz", BaseMaterial.QUARTZ, Upgrade.ENLIGHTENED, IPassingCallback.NONE),
  ENLIGHTENED_FUSED_GLASS("enlightenedFusedGlass", BaseMaterial.GLASS, Upgrade.ENLIGHTENED, IPassingCallback.NONE),
  DARK_FUSED_QUARTZ("darkFusedQuartz", BaseMaterial.QUARTZ, Upgrade.DARKENED, IPassingCallback.NONE),
  DARK_FUSED_GLASS("darkFusedGlass", BaseMaterial.GLASS, Upgrade.DARKENED, IPassingCallback.NONE);

  private static enum BaseMaterial {
    QUARTZ,
    GLASS
  }

  private static enum Upgrade {
    NONE,
    ENLIGHTENED,
    DARKENED
  }

  public static interface IPassingCallback {

    boolean canPass(@Nonnull Entity entity);

    static @Nonnull IPassingCallback NONE = new IPassingCallback() {

      @Override
      public boolean canPass(@Nonnull Entity entity) {
        return false;
      }
    };

  }

  public static final @Nonnull PropertyEnum<FusedQuartzType> KIND = PropertyEnum.<FusedQuartzType> create("kind", FusedQuartzType.class);

  private final @Nonnull String oreDictName;
  private final @Nonnull BaseMaterial baseMaterial;
  private final @Nonnull Upgrade upgrade;
  private final @Nonnull IPassingCallback passingCallback;
  private Block block;

  private FusedQuartzType(@Nonnull String oreDictName, @Nonnull BaseMaterial baseMaterial, @Nonnull Upgrade upgrade,
      @Nonnull IPassingCallback passingCallback) {
    this.oreDictName = oreDictName;
    this.baseMaterial = baseMaterial;
    this.upgrade = upgrade;
    this.passingCallback = passingCallback;
  }

  public boolean connectTo(FusedQuartzType other) {
    return other != null && ((BlockConfig.clearGlassConnectToFusedQuartz.get() && BlockConfig.glassConnectToTheirVariants.get())
        || (BlockConfig.clearGlassConnectToFusedQuartz.get() && this.upgrade == other.upgrade)
        || (BlockConfig.glassConnectToTheirVariants.get() && this.baseMaterial == other.baseMaterial));
  }

  @Override
  public @Nonnull String getName() {
    return NullHelper.notnullJ(name().toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
  }

  public static @Nonnull FusedQuartzType getTypeFromMeta(int meta) {
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
  }

  public static int getMetaFromType(@Nonnull FusedQuartzType fusedQuartzType) {
    return fusedQuartzType.ordinal();
  }

  public boolean isEnlightened() {
    return upgrade == Upgrade.ENLIGHTENED;
  }

  public boolean isBlastResistant() {
    return baseMaterial == BaseMaterial.QUARTZ;
  }

  public int getLightOpacity() {
    return upgrade == Upgrade.DARKENED ? 255 : 0;
  }

  public @Nonnull String getOreDictName() {
    return oreDictName;
  }

  public @Nonnull Block getBlock() {
    return NullHelper.notnull(block, "block not initialized");
  }

  public void setBlock(@Nonnull Block block) {
    this.block = block;
  }

  public boolean canPass(@Nonnull Entity entity) {
    return passingCallback.canPass(entity);
  }

}