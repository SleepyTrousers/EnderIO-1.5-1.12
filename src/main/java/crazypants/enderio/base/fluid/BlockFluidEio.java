package crazypants.enderio.base.fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.common.fluid.BlockFluidEnder;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.teleport.RandomTeleportUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.base.config.Config.rocketFuelIsExplosive;

public final class BlockFluidEio {

  /////////////////////////////////////////////////////////////////////////
  // Fire Water
  /////////////////////////////////////////////////////////////////////////

  static class FireWater extends BlockFluidEnder {

    protected FireWater(Fluid fluid, Material material, int fogColor) {
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    @Override
    public boolean isFlammable(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
      return true;
    }

    @Override
    public boolean isFireSource(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
      return true;
    }

    @Override
    public int getFireSpreadSpeed(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
      return 60;
    }
    
    @Override
    public Boolean isEntityInsideMaterial(@Nonnull IBlockAccess world, @Nonnull BlockPos blockpos, @Nonnull IBlockState iblockstate, @Nonnull Entity entity,
        double yToTest, @Nonnull Material materialIn, boolean testingHead) {
      return materialIn == Material.LAVA || materialIn == this.blockMaterial;
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Hootch
  /////////////////////////////////////////////////////////////////////////

  static class Hootch extends BlockFluidEnder {

    protected Hootch(Fluid fluid, Material material, int fogColor) {
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
      if (!world.isRemote && entity instanceof EntityLivingBase) {
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 150, 0, true, true));
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    @Override
    public boolean isFlammable(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
      return true;
    }

    @Override
    public boolean isFireSource(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
      return true;
    }

    @Override
    public int getFlammability(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
      return 1;
    }

    @Override
    public int getFireSpreadSpeed(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
      return 60;
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Rocket Fuel
  /////////////////////////////////////////////////////////////////////////

  static class RocketFuel extends BlockFluidEnder {

    protected RocketFuel(Fluid fluid, Material material, int fogColor) {
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
      if (!world.isRemote && entity instanceof EntityLivingBase) {
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 150, 3, true, true));
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    @Override
    public boolean isFlammable(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
      return true;
    }

    @Override
    public boolean isFireSource(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
      return true;
    }

    @Override
    public int getFireSpreadSpeed(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
      return 60;
    }

    @Override
    public void neighborChanged(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
      checkForFire(worldIn, pos);
      super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    protected void checkForFire(final World worldIn, final BlockPos pos) {
      if (rocketFuelIsExplosive) {
        NNList.FACING.apply(new Callback<EnumFacing>() {
          @Override
          public void apply(@Nonnull EnumFacing side) {
            IBlockState neighbor = worldIn.getBlockState(pos.offset(side));
            if (neighbor.getBlock() instanceof BlockFire && neighbor.getBlock() != ModObject.blockColdFire.getBlock()) {
              if (worldIn.rand.nextFloat() < .5f) {
                List<BlockPos> explosions = new ArrayList<BlockPos>();
                explosions.add(pos);
                BlockPos up = pos.up();
                while (worldIn.getBlockState(up).getBlock() instanceof RocketFuel) {
                  explosions.add(up);
                  up = up.up();
                }

                if (isSourceBlock(worldIn, pos)) {
                  worldIn.newExplosion(null, pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f, 2, true, true);
                }
                float strength = .5f;
                for (BlockPos explosion : explosions) {
                  worldIn.newExplosion(null, explosion.getX() + .5f, explosion.getY() + .5f, explosion.getZ() + .5f, strength, true, true);
                  strength = Math.min(strength * 1.05f, 7f);
                }

                return;
              }
            }
          }
        });
      }
    }

    @Override
    public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
      checkForFire(world, pos);
      super.updateTick(world, pos, state, rand);
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Nutrient Distillation
  /////////////////////////////////////////////////////////////////////////

  static class NutrientDistillation extends BlockFluidEnder {

    protected NutrientDistillation(Fluid fluid, Material material, int fogColor) {
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
      if (!world.isRemote && entity instanceof EntityPlayerMP) {
        long time = entity.world.getTotalWorldTime();
        EntityPlayerMP player = (EntityPlayerMP) entity;
        if (time % Config.nutrientFoodBoostDelay == 0 && player.getEntityData().getLong("eioLastFoodBoost") != time) {
          player.getFoodStats().addStats(1, 0.1f);
          player.getEntityData().setLong("eioLastFoodBoost", time);
        }
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Liquid Sunshine
  /////////////////////////////////////////////////////////////////////////

  static class LiquidSunshine extends BlockFluidEnder {

    protected LiquidSunshine(Fluid fluid, Material material, int fogColor) {
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
      if (!world.isRemote && entity instanceof EntityLivingBase) {
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 50, 0, true, true));
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.GLOWING, 1200, 0, true, true));
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Cloud Seed, Concentrated
  /////////////////////////////////////////////////////////////////////////

  static class CloudSeedConcentrated extends BlockFluidEnder {

    protected CloudSeedConcentrated(Fluid fluid, Material material, int fogColor) {
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
      if (!world.isRemote && entity instanceof EntityLivingBase) {
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 40, 0, true, true));
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Vapor Of Levity
  /////////////////////////////////////////////////////////////////////////

  static class VaporOfLevity extends BlockFluidEnder {

    protected VaporOfLevity(Fluid fluid, Material material, int fogColor) {
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
      if (entity instanceof EntityPlayer || (!world.isRemote && entity instanceof EntityLivingBase)) {
        ((EntityLivingBase) entity).motionY += 0.1;
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    private static final int[] COLORS = { 0x0c82d0, 0x90c8ec, 0x5174ed, 0x0d2f65, 0x4accee };

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
      if (rand.nextFloat() < .5f) {
        final EnumFacing face = NNList.FACING.get(rand.nextInt(NNList.FACING.size()));
        final BlockPos neighborPos = pos.offset(face);
        final IBlockState neighborState = worldIn.getBlockState(neighborPos);
        if (!neighborState.isFullCube()) {
          double xd = face.getFrontOffsetX() == 0 ? rand.nextDouble() : face.getFrontOffsetX() < 0 ? -0.05 : 1.05;
          double yd = face.getFrontOffsetY() == 0 ? rand.nextDouble() : face.getFrontOffsetY() < 0 ? -0.05 : 1.05;
          double zd = face.getFrontOffsetZ() == 0 ? rand.nextDouble() : face.getFrontOffsetZ() < 0 ? -0.05 : 1.05;

          double x = pos.getX() + xd;
          double y = pos.getY() + yd;
          double z = pos.getZ() + zd;

          int col = COLORS[rand.nextInt(COLORS.length)];

          worldIn.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, (col >> 16 & 255) / 255d, (col >> 8 & 255) / 255d, (col & 255) / 255d);
        }
      }
    }

    /*
     * BlockFluidClassic uses both random ticks and update ticks (after a neighbor change) to flow. This means we cannot decouple our snow gen completely from
     * that by using one or the other. However, we can stay out of the random tick, so we don't have to do anything if there wasn't either a block update around
     * us or we scheduled a update ourselves because there's the possibility to gen something. This means we may miss the corners if they are removed after we
     * finished. Same for sides if a block was placed below them.
     */
    @Override
    public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
      if (!world.isRemote && rand.nextFloat() < 0.05f) {
        final BlockPos neighborPos = getNeighbor(pos, rand);
        final IBlockState neighborState = world.getBlockState(neighborPos);
        if (canMakeSnow(world, neighborPos, neighborState)) {
          world.setBlockState(neighborPos, Blocks.SNOW_LAYER.getDefaultState());
        } else if (canMakeIce(world, neighborPos, neighborState)) {
          world.setBlockState(neighborPos, Blocks.ICE.getDefaultState());
        }
      }
      super.updateTick(world, pos, state, rand);
      if (canMakeMoreSnowOrIceAround(world, pos) && !world.isUpdateScheduled(pos, this)) {
        world.scheduleUpdate(pos, this, tickRate * 10);
      }
    }

    @Override
    public void randomTick(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random random) {
      // still do flowing here, but no snow gen
      super.updateTick(worldIn, pos, state, random); // Note: Block.randomTick() does this.updateTick()
    }

    protected boolean canMakeIce(@Nonnull World world, final @Nonnull BlockPos neighborPos, final @Nonnull IBlockState neighborState) {
      return neighborState.getBlock() == Blocks.WATER && neighborState.getValue(BlockLiquid.LEVEL) == 0
          && world.mayPlace(Blocks.ICE, neighborPos, false, EnumFacing.DOWN, null);
    }

    protected boolean canMakeSnow(@Nonnull World world, final @Nonnull BlockPos neighborPos, final @Nonnull IBlockState neighborState) {
      final BlockPos belowNeighborPos = neighborPos.down();
      final Block neighborBlock = neighborState.getBlock();
      return neighborBlock != Blocks.SNOW_LAYER && !(neighborBlock instanceof IFluidBlock) && !(neighborBlock instanceof BlockLiquid)
          && neighborBlock.isReplaceable(world, neighborPos) && world.getBlockState(belowNeighborPos).isSideSolid(world, belowNeighborPos, EnumFacing.UP);
    }

    protected boolean canMakeMoreSnowOrIceAround(@Nonnull World world, @Nonnull BlockPos pos) {
      for (int x = -1; x <= 1; x++) {
        for (int z = -1; z <= 1; z++) {
          if (x != 0 || z != 0) {
            final BlockPos neighborPos = pos.east(x).south(z);
            final IBlockState neighborState = world.getBlockState(neighborPos);
            if (canMakeSnow(world, neighborPos, neighborState) || canMakeIce(world, neighborPos, neighborState)) {
              return true;
            }
          }
        }
      }
      return false;
    }

    protected @Nonnull BlockPos getNeighbor(@Nonnull BlockPos pos, @Nonnull Random rand) {
      EnumFacing face = NNList.FACING.get(rand.nextInt(NNList.FACING.size()));
      if (face.getAxis() != Axis.Y && rand.nextBoolean()) {
        return pos.offset(face).offset(face.rotateY());
      } else {
        return pos.offset(face);
      }
    }

    @Override
    public float getFluidHeightForRender(IBlockAccess world, BlockPos pos) {
      IBlockState down = world.getBlockState(pos.down());
      if (down.getMaterial().isLiquid() || down.getBlock() instanceof IFluidBlock) {
        return 1;
      } else {
        return 0.995F;
      }
    }
  }

  /////////////////////////////////////////////////////////////////////////
  // Molten Glowstone
  /////////////////////////////////////////////////////////////////////////

  public static class MoltenGlowstone extends BlockFluidEnder {

    public MoltenGlowstone(Fluid fluid, Material material, int fogColor) { // 0xffbc5e
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
      if (!world.isRemote && entity instanceof EntityLivingBase) {
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 200, 0, true, true));
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.GLOWING, 2400, 0, true, true));
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    @Override
    public void init() {
      super.init();
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Molten Redstone
  /////////////////////////////////////////////////////////////////////////

  public static class MoltenRedstone extends BlockFluidEnder {

    public MoltenRedstone(Fluid fluid, Material material, int fogColor) { // 0xff0000
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

    @Override
    public void init() {
      super.init();
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Molten Ender
  /////////////////////////////////////////////////////////////////////////

  public static class MoltenEnder extends BlockFluidEnder {


    public MoltenEnder(Fluid fluid, Material material, int fogColor) { // 0xff0000
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
      if (!world.isRemote && entity.timeUntilPortal == 0) {
        RandomTeleportUtil.teleportEntity(world, entity);
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }


    @Override
    public void init() {
      super.init();
    }

  }

}
