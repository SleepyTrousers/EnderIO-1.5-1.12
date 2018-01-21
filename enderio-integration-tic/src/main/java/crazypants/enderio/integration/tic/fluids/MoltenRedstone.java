package crazypants.enderio.integration.tic.fluids;

import javax.annotation.Nonnull;

import com.enderio.core.common.fluid.BlockFluidEnder;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class MoltenRedstone extends BlockFluidEnder {

  public MoltenRedstone(@Nonnull Fluid fluid, @Nonnull Material material, int fogColor) { // 0xff0000
    super(fluid, material, fogColor);
  }

  @Override
  public void onEntityCollidedWithBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
    if (!world.isRemote && entity instanceof EntityLivingBase) {
      ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.HASTE, 20 * 60, 0, true, true));
      ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 20 * 60, 0, true, true));
      ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SPEED, 20 * 60, 0, true, true));
      ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.HUNGER, 20 * 60, 0, true, true));
    }
    super.onEntityCollidedWithBlock(world, pos, state, entity);
  }

}