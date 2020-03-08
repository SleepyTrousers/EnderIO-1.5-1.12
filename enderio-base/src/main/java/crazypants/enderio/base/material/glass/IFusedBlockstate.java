package crazypants.enderio.base.material.glass;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;

public interface IFusedBlockstate {

  static @Nonnull IFusedBlockstate get(@Nonnull IBlockState state) {
    return ((BlockFusedQuartzBase<?>) state.getBlock()).getFusedBlockstate(state);
  }

  @Nonnull
  EnumDyeColor getColor();

  @Nonnull
  FusedQuartzType getType();

  default boolean isEnlightened() {
    return getType().isEnlightened();
  }

  default boolean isDarkened() {
    return getType().isDarkened();
  }

  default boolean isBlastResistant() {
    return getType().isBlastResistant();
  }

  default int getLightOpacity() {
    return getType().getLightOpacity();
  }

  default boolean canPass(@Nonnull Entity entity) {
    return getType().canPass(entity);
  }

}
