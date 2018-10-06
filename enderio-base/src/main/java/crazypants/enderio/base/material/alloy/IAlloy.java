package crazypants.enderio.base.material.alloy;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public interface IAlloy {

  @Nonnull String getBaseName();

  @Nonnull String getFluidName();

  float getHardness();

  int getColor();

  int getMeltingPoint();

  @Nonnull ItemStack getStackNugget();

  @Nonnull ItemStack getStackNugget(int size);

  @Nonnull ItemStack getStackIngot();

  @Nonnull ItemStack getStackIngot(int size);

  @Nonnull ItemStack getStackBall();

  @Nonnull ItemStack getStackBall(int size);

  @Nonnull ItemStack getStackBlock();

  @Nonnull ItemStack getStackBlock(int size);

  @Nonnull String getOreName();

  @Nonnull String getOreNugget();

  @Nonnull String getOreIngot();

  @Nonnull String getOreBall();

  @Nonnull String getOreBlock();

  @Nonnull String getName();
}
