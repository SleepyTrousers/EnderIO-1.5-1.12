package crazypants.enderio.base.material.glass;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.config.config.BlockConfig;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum FusedQuartzType implements IStringSerializable {

  FUSED_QUARTZ("fusedQuartz", BaseMaterial.QUARTZ, Upgrade.NONE, IPassingCallback.NONE, 0),
  FUSED_GLASS("fusedGlass", BaseMaterial.GLASS, Upgrade.NONE, IPassingCallback.NONE, 0),
  ENLIGHTENED_FUSED_QUARTZ("enlightenedFusedQuartz", BaseMaterial.QUARTZ, Upgrade.ENLIGHTENED, IPassingCallback.NONE, 0),
  ENLIGHTENED_FUSED_GLASS("enlightenedFusedGlass", BaseMaterial.GLASS, Upgrade.ENLIGHTENED, IPassingCallback.NONE, 0),
  DARK_FUSED_QUARTZ("darkFusedQuartz", BaseMaterial.QUARTZ, Upgrade.DARKENED, IPassingCallback.NONE, 0),
  DARK_FUSED_GLASS("darkFusedGlass", BaseMaterial.GLASS, Upgrade.DARKENED, IPassingCallback.NONE, 0),

  HOLY_FUSED_QUARTZ("holyFusedQuartz", BaseMaterial.QUARTZ, Upgrade.NONE, IPassingCallback.PLAYER, 0),
  HOLY_FUSED_GLASS("holyFusedGlass", BaseMaterial.GLASS, Upgrade.NONE, IPassingCallback.PLAYER, 0),
  HOLY_ENLIGHTENED_FUSED_QUARTZ("holyEnlightenedFusedQuartz", BaseMaterial.QUARTZ, Upgrade.ENLIGHTENED, IPassingCallback.PLAYER, 0),
  HOLY_ENLIGHTENED_FUSED_GLASS("holyEnlightenedFusedGlass", BaseMaterial.GLASS, Upgrade.ENLIGHTENED, IPassingCallback.PLAYER, 0),
  HOLY_DARK_FUSED_QUARTZ("holyDarkFusedQuartz", BaseMaterial.QUARTZ, Upgrade.DARKENED, IPassingCallback.PLAYER, 0),
  HOLY_DARK_FUSED_GLASS("holyDarkFusedGlass", BaseMaterial.GLASS, Upgrade.DARKENED, IPassingCallback.PLAYER, 0),

  UNHOLY_FUSED_QUARTZ("unholyFusedQuartz", BaseMaterial.QUARTZ, Upgrade.NONE, IPassingCallback.MOB, 0),
  UNHOLY_FUSED_GLASS("unholyFusedGlass", BaseMaterial.GLASS, Upgrade.NONE, IPassingCallback.MOB, 0),
  UNHOLY_ENLIGHTENED_FUSED_QUARTZ("unholyEnlightenedFusedQuartz", BaseMaterial.QUARTZ, Upgrade.ENLIGHTENED, IPassingCallback.MOB, 0),
  UNHOLY_ENLIGHTENED_FUSED_GLASS("unholyEnlightenedFusedGlass", BaseMaterial.GLASS, Upgrade.ENLIGHTENED, IPassingCallback.MOB, 0),
  UNHOLY_DARK_FUSED_QUARTZ("unholyDarkFusedQuartz", BaseMaterial.QUARTZ, Upgrade.DARKENED, IPassingCallback.MOB, 1),
  UNHOLY_DARK_FUSED_GLASS("unholyDarkFusedGlass", BaseMaterial.GLASS, Upgrade.DARKENED, IPassingCallback.MOB, 1),

  PASTURE_FUSED_QUARTZ("pastureFusedQuartz", BaseMaterial.QUARTZ, Upgrade.NONE, IPassingCallback.ANIMAL, 1),
  PASTURE_FUSED_GLASS("pastureFusedGlass", BaseMaterial.GLASS, Upgrade.NONE, IPassingCallback.ANIMAL, 1),
  PASTURE_ENLIGHTENED_FUSED_QUARTZ("pastureEnlightenedFusedQuartz", BaseMaterial.QUARTZ, Upgrade.ENLIGHTENED, IPassingCallback.ANIMAL, 1),
  PASTURE_ENLIGHTENED_FUSED_GLASS("pastureEnlightenedFusedGlass", BaseMaterial.GLASS, Upgrade.ENLIGHTENED, IPassingCallback.ANIMAL, 1),
  PASTURE_DARK_FUSED_QUARTZ("pastureDarkFusedQuartz", BaseMaterial.QUARTZ, Upgrade.DARKENED, IPassingCallback.ANIMAL, 1),
  PASTURE_DARK_FUSED_GLASS("pastureDarkFusedGlass", BaseMaterial.GLASS, Upgrade.DARKENED, IPassingCallback.ANIMAL, 1),

  NOT_HOLY_FUSED_QUARTZ("notHolyFusedQuartz", BaseMaterial.QUARTZ, Upgrade.NONE, IPassingCallback.NON_PLAYER, 1),
  NOT_HOLY_FUSED_GLASS("notHolyFusedGlass", BaseMaterial.GLASS, Upgrade.NONE, IPassingCallback.NON_PLAYER, 1),
  NOT_HOLY_ENLIGHTENED_FUSED_QUARTZ("notHolyEnlightenedFusedQuartz", BaseMaterial.QUARTZ, Upgrade.ENLIGHTENED, IPassingCallback.NON_PLAYER, 1),
  NOT_HOLY_ENLIGHTENED_FUSED_GLASS("notHolyEnlightenedFusedGlass", BaseMaterial.GLASS, Upgrade.ENLIGHTENED, IPassingCallback.NON_PLAYER, 1),
  NOT_HOLY_DARK_FUSED_QUARTZ("notHolyDarkFusedQuartz", BaseMaterial.QUARTZ, Upgrade.DARKENED, IPassingCallback.NON_PLAYER, 1),
  NOT_HOLY_DARK_FUSED_GLASS("notHolyDarkFusedGlass", BaseMaterial.GLASS, Upgrade.DARKENED, IPassingCallback.NON_PLAYER, 1),

  NOT_UNHOLY_FUSED_QUARTZ("notUnholyFusedQuartz", BaseMaterial.QUARTZ, Upgrade.NONE, IPassingCallback.NON_MOB, 1),
  NOT_UNHOLY_FUSED_GLASS("notUnholyFusedGlass", BaseMaterial.GLASS, Upgrade.NONE, IPassingCallback.NON_MOB, 1),
  NOT_UNHOLY_ENLIGHTENED_FUSED_QUARTZ("notUnholyEnlightenedFusedQuartz", BaseMaterial.QUARTZ, Upgrade.ENLIGHTENED, IPassingCallback.NON_MOB, 2),
  NOT_UNHOLY_ENLIGHTENED_FUSED_GLASS("notUnholyEnlightenedFusedGlass", BaseMaterial.GLASS, Upgrade.ENLIGHTENED, IPassingCallback.NON_MOB, 2),
  NOT_UNHOLY_DARK_FUSED_QUARTZ("notUnholyDarkFusedQuartz", BaseMaterial.QUARTZ, Upgrade.DARKENED, IPassingCallback.NON_MOB, 2),
  NOT_UNHOLY_DARK_FUSED_GLASS("notUnholyDarkFusedGlass", BaseMaterial.GLASS, Upgrade.DARKENED, IPassingCallback.NON_MOB, 2),

  NOT_PASTURE_FUSED_QUARTZ("notPastureFusedQuartz", BaseMaterial.QUARTZ, Upgrade.NONE, IPassingCallback.NON_ANIMAL, 2),
  NOT_PASTURE_FUSED_GLASS("notPastureFusedGlass", BaseMaterial.GLASS, Upgrade.NONE, IPassingCallback.NON_ANIMAL, 2),
  NOT_PASTURE_ENLIGHTENED_FUSED_QUARTZ("notPastureEnlightenedFusedQuartz", BaseMaterial.QUARTZ, Upgrade.ENLIGHTENED, IPassingCallback.NON_ANIMAL, 2),
  NOT_PASTURE_ENLIGHTENED_FUSED_GLASS("notPastureEnlightenedFusedGlass", BaseMaterial.GLASS, Upgrade.ENLIGHTENED, IPassingCallback.NON_ANIMAL, 2),
  NOT_PASTURE_DARK_FUSED_QUARTZ("notPastureDarkFusedQuartz", BaseMaterial.QUARTZ, Upgrade.DARKENED, IPassingCallback.NON_ANIMAL, 2),
  NOT_PASTURE_DARK_FUSED_GLASS("notPastureDarkFusedGlass", BaseMaterial.GLASS, Upgrade.DARKENED, IPassingCallback.NON_ANIMAL, 2),

  ;

  private enum BaseMaterial {
    QUARTZ,
    GLASS
  }

  private enum Upgrade {
    NONE,
    ENLIGHTENED,
    DARKENED
  }

  public static final @Nonnull PropertyEnum<FusedQuartzType> KIND = PropertyEnum.<FusedQuartzType> create("kind", FusedQuartzType.class);

  public static final @Nonnull PropertyEnum<FusedQuartzType> KIND0 = PropertyEnum.<FusedQuartzType> create("kind", FusedQuartzType.class,
      type -> type != null && type.getGrouping() == 0);

  public static final @Nonnull PropertyEnum<FusedQuartzType> KIND1 = PropertyEnum.<FusedQuartzType> create("kind", FusedQuartzType.class,
      type -> type != null && type.getGrouping() == 1);

  public static final @Nonnull PropertyEnum<FusedQuartzType> KIND2 = PropertyEnum.<FusedQuartzType> create("kind", FusedQuartzType.class,
      type -> type != null && type.getGrouping() == 2);

  private final @Nonnull String oreDictName;
  private final @Nonnull BaseMaterial baseMaterial;
  private final @Nonnull Upgrade upgrade;
  private final @Nonnull IPassingCallback passingCallback;
  private final int grouping;
  private Block block;

  private FusedQuartzType(@Nonnull String oreDictName, @Nonnull BaseMaterial baseMaterial, @Nonnull Upgrade upgrade, @Nonnull IPassingCallback passingCallback,
      int grouping) {
    this.oreDictName = oreDictName;
    this.baseMaterial = baseMaterial;
    this.upgrade = upgrade;
    this.passingCallback = passingCallback;
    this.grouping = grouping;
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

  public static @Nonnull FusedQuartzType getTypeFromMeta(@Nonnull PropertyEnum<FusedQuartzType> kind, int meta) {
    if (kind == KIND1) {
      meta += 16;
    } else if (kind == KIND2) {
      meta += 32;
    }
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
  }

  public static int getMetaFromType(@Nonnull FusedQuartzType fusedQuartzType) {
    int meta = fusedQuartzType.ordinal();
    while (meta >= 16) {
      meta -= 16;
    }
    return meta;
  }

  public boolean isEnlightened() {
    return upgrade == Upgrade.ENLIGHTENED;
  }

  public boolean isDarkened() {
    return upgrade == Upgrade.DARKENED;
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

  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
    if (isBlastResistant()) {
      tooltip.add(Lang.BLOCK_BLAST_RESISTANT.get());
    }
    if (isEnlightened()) {
      tooltip.add(Lang.BLOCK_LIGHT_EMITTER.get());
    }
    if (getLightOpacity() > 0) {
      tooltip.add(Lang.BLOCK_LIGHT_BLOCKER.get());
    }
    passingCallback.addInformation(stack, worldIn, tooltip, flagIn);
  }

  private int getGrouping() {
    return grouping;
  }

  public @Nullable IWidgetIcon getIcon0() {
    switch (upgrade) {
    case DARKENED:
      return IconEIO.GLASS_DARK;
    case ENLIGHTENED:
      return IconEIO.GLASS_LIGHT;
    case NONE:
    default:
      return null;
    }
  }

  public @Nullable IWidgetIcon getIcon1() {
    return passingCallback.getIcon1();
  }

  public @Nullable IWidgetIcon getIcon2() {
    return passingCallback.getIcon2();
  }

}
