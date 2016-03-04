package crazypants.enderio.material.fusedQuartz;

import java.util.Locale;

import crazypants.enderio.config.Config;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public enum FusedQuartzType implements IStringSerializable {

  FUSED_QUARTZ("fusedQuartz", BaseMaterial.QUARTZ, Upgrade.NONE),
  FUSED_GLASS("fusedGlass", BaseMaterial.GLASS, Upgrade.NONE),
  ENLIGHTENED_FUSED_QUARTZ("enlightenedFusedQuartz", BaseMaterial.QUARTZ, Upgrade.ENLIGHTENED),
  ENLIGHTENED_FUSED_GLASS("enlightenedFusedGlass", BaseMaterial.GLASS, Upgrade.ENLIGHTENED),
  DARK_FUSED_QUARTZ("darkFusedQuartz", BaseMaterial.QUARTZ, Upgrade.DARKENED),
  DARK_FUSED_GLASS("darkFusedGlass", BaseMaterial.GLASS, Upgrade.DARKENED);

  private static enum BaseMaterial {
    QUARTZ,
    GLASS
  }

  private static enum Upgrade {
    NONE,
    ENLIGHTENED,
    DARKENED
  }

  public static final PropertyEnum<FusedQuartzType> KIND = PropertyEnum.<FusedQuartzType> create("kind", FusedQuartzType.class);

  private final String unlocalisedName;
  private final BaseMaterial baseMaterial;
  private final Upgrade upgrade;

  private FusedQuartzType(String unlocalisedName, BaseMaterial baseMaterial, Upgrade upgrade) {
    this.unlocalisedName = unlocalisedName;
    this.baseMaterial = baseMaterial;
    this.upgrade = upgrade;
  }

  public boolean connectTo(FusedQuartzType other) {
    return (Config.clearGlassConnectToFusedQuartz && Config.glassConnectToTheirVariants)
        || (Config.clearGlassConnectToFusedQuartz && this.upgrade == other.upgrade)
        || (Config.glassConnectToTheirVariants && this.baseMaterial == other.baseMaterial);
  }

  @Override
  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

  public static FusedQuartzType getTypeFromMeta(int meta) {
    return values()[meta >= 0 && meta < values().length ? meta : 0];
  }

  public static int getMetaFromType(FusedQuartzType fusedQuartzType) {
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

  public String getUnlocalisedName() {
    return unlocalisedName;
  }
}