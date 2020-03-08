package crazypants.enderio.base.material.glass;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.render.IWidgetIcon;

import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IPassingCallback {

  boolean canPass(@Nonnull Entity entity);

  @SideOnly(Side.CLIENT)
  default void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {

  }

  default @Nullable IWidgetIcon getIcon1() {
    return null;
  }

  default @Nullable IWidgetIcon getIcon2() {
    return null;
  }

  @Nonnull
  IPassingCallback NONE = new IPassingCallback() {

    @Override
    public boolean canPass(@Nonnull Entity entity) {
      return false;
    }

  };

  @Nonnull
  IPassingCallback PLAYER = new IPassingCallback() {

    @Override
    public boolean canPass(@Nonnull Entity entity) {
      return entity instanceof EntityPlayer;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
      tooltip.add(Lang.BLOCK_ALLOW_PLAYERS.get());
    }

    @Override
    @Nullable
    public IWidgetIcon getIcon1() {
      return IconEIO.GLASS_PLAYER;
    }

  };

  @Nonnull
  IPassingCallback MOB = new IPassingCallback() {

    @Override
    public boolean canPass(@Nonnull Entity entity) {
      return entity instanceof IMob;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
      tooltip.add(Lang.BLOCK_ALLOW_MONSTERS.get());
    }

    @Override
    @Nullable
    public IWidgetIcon getIcon1() {
      return IconEIO.GLASS_MONSTER;
    }

  };

  @Nonnull
  IPassingCallback ANIMAL = new IPassingCallback() {

    @Override
    public boolean canPass(@Nonnull Entity entity) {
      return (entity instanceof IAnimals) && !(entity instanceof IMob);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
      tooltip.add(Lang.BLOCK_ALLOW_ANIMALS.get());
    }

    @Override
    @Nullable
    public IWidgetIcon getIcon1() {
      return IconEIO.GLASS_ANIMAL;
    }

  };

  @Nonnull
  IPassingCallback NON_PLAYER = new IPassingCallback() {

    @Override
    public boolean canPass(@Nonnull Entity entity) {
      return !(entity instanceof EntityPlayer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
      tooltip.add(Lang.BLOCK_DISALLOW_PLAYERS.get());
    }

    @Override
    @Nullable
    public IWidgetIcon getIcon1() {
      return IconEIO.GLASS_PLAYER;
    }

    @Override
    @Nullable
    public IWidgetIcon getIcon2() {
      return IconEIO.GLASS_NOT;
    }

  };

  @Nonnull
  IPassingCallback NON_MOB = new IPassingCallback() {

    @Override
    public boolean canPass(@Nonnull Entity entity) {
      return !(entity instanceof IMob);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
      tooltip.add(Lang.BLOCK_DISALLOW_MONSTERS.get());
    }

    @Override
    @Nullable
    public IWidgetIcon getIcon1() {
      return IconEIO.GLASS_MONSTER;
    }

    @Override
    @Nullable
    public IWidgetIcon getIcon2() {
      return IconEIO.GLASS_NOT;
    }

  };

  @Nonnull
  IPassingCallback NON_ANIMAL = new IPassingCallback() {

    @Override
    public boolean canPass(@Nonnull Entity entity) {
      return !((entity instanceof IAnimals) && !(entity instanceof IMob));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
      tooltip.add(Lang.BLOCK_DISALLOW_ANIMALS.get());
    }

    @Override
    @Nullable
    public IWidgetIcon getIcon1() {
      return IconEIO.GLASS_ANIMAL;
    }

    @Override
    @Nullable
    public IWidgetIcon getIcon2() {
      return IconEIO.GLASS_NOT;
    }

  };

}
