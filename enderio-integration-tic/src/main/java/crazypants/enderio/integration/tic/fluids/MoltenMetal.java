package crazypants.enderio.integration.tic.fluids;

import javax.annotation.Nonnull;

import com.enderio.core.common.fluid.BlockFluidEnder;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class MoltenMetal extends BlockFluidEnder {

  public static MoltenMetal create(@Nonnull Fluid fluid, @Nonnull Material material, int fogColor) {
    return new MoltenMetal(fluid, material, fogColor);
  }

  protected MoltenMetal(@Nonnull Fluid fluid, @Nonnull Material material, int fogColor) {
    super(fluid, material, fogColor);
  }

  @Override
  public void onEntityCollidedWithBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
    if (!world.isRemote && !entity.isImmuneToFire()) {
      entity.attackEntityFrom(DamageSource.LAVA, 4.0F);
      entity.setFire(15);
    }
    super.onEntityCollidedWithBlock(world, pos, state, entity);
  }

}