package crazypants.enderio.machines.machine.niard;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.network.PacketSpawnParticles;
import crazypants.enderio.base.xp.XpUtil;
import crazypants.enderio.machines.config.config.NiardConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class EngineNiard {

  private final @Nonnull TileNiard owner;
  private Fluid fluid = null;
  private @Nonnull Block block = Blocks.WATER;
  private @Nonnull FluidType type = FluidType.VANILLA;
  private @Nonnull EnumFacing downflowDirection = EnumFacing.DOWN;
  private int radius = -1;
  private RadiusIterator radiusItr;

  public EngineNiard(@Nonnull TileNiard owner) {
    this.owner = owner;
  }

  public EngineNiard setFluid(Fluid fluid) {
    if (fluid.canBePlacedInWorld()) {
      this.radiusItr = radius >= 0 ? new RadiusIterator(owner.offset(), radius) : null;
      this.fluid = fluid;
      this.downflowDirection = fluid.getDensity() > 0 ? EnumFacing.DOWN : EnumFacing.UP;
      Block fluidBlock = fluid.getBlock();
      if (fluidBlock instanceof BlockFluidClassic) {
        this.type = FluidType.CLASSIC;
        this.block = fluidBlock;
      } else if (fluidBlock instanceof BlockFluidFinite) {
        this.type = FluidType.FINITE;
        this.block = fluidBlock;
      } else if (fluidBlock instanceof BlockLiquid) {
        this.type = FluidType.VANILLA;
        this.block = fluidBlock;
      } else {
        this.fluid = null;
      }
    } else {
      this.fluid = null;
    }
    return this;
  }

  public EngineNiard setRadius(int radius) {
    if (radius != this.radius) {
      this.radius = radius;
      this.radiusItr = new RadiusIterator(owner.offset(), radius);
    }
    return this;
  }

  public boolean work() {
    if (fluid == null || radius < 0) {
      return false;
    }
    for (int i = 0; i < radiusItr.size(); i++) {
      NNList<BlockPos> seen = new NNList<>();
      BlockPos base = radiusItr.next();
      BlockPos next = base.offset(downflowDirection);
      while (isInWorld(next) && (owner.getWorld().isAirBlock(next) || (isSameLiquid(next) && isFlowingBlock(next)))) {
        seen.add(next);
        next = next.offset(downflowDirection);
      }
      if (!seen.isEmpty()) {
        setSourceBlock(seen.remove(seen.size() - 1));
        seen.apply((Callback<BlockPos>) bc -> setVerticalBlock(bc, false));
        owner.getWorld().notifyBlockUpdate(base, owner.getWorld().getBlockState(base), owner.getWorld().getBlockState(base), 3);
        return true;
      }
    }
    return false;
  }

  public int work(int xp_in_mb) {
    int remaining = XpUtil.liquidToExperience(xp_in_mb);
    if (radius >= 0) {
      for (int i = 0; i < radiusItr.size(); i++) {
        BlockPos next = radiusItr.next().offset(EnumFacing.DOWN);
        if (isInWorld(next) && owner.getWorld().isAirBlock(next)) {
          int i1 = EntityXPOrb.getXPSplit(remaining / (owner.getWorld().rand.nextInt(4) + 1));
          remaining -= i1;
          final EntityXPOrb xpOrb = new EntityXPOrb(owner.getWorld(), next.getX() + 0.5D, next.getY() + 0.7D, next.getZ() + 0.5D, i1);
          xpOrb.motionX /= 4d;
          xpOrb.motionY = 0;
          xpOrb.motionZ /= 4d;
          owner.getWorld().spawnEntity(xpOrb);
          if (remaining <= 0) {
            return 0;
          }
        }
      }
    }
    return XpUtil.experienceToLiquid(remaining);
  }

  // Tools of the trade

  private boolean isInWorld(@Nonnull BlockPos bc) {
    return bc.getY() >= 0 && bc.getY() < 256 && owner.getWorld().isBlockLoaded(bc);
  }

  private boolean isSameLiquid(@Nonnull BlockPos bc) {
    final Block wblock = owner.getWorld().getBlockState(bc).getBlock();
    return wblock == block || (block == Blocks.WATER && wblock == Blocks.FLOWING_WATER) || (block == Blocks.LAVA && wblock == Blocks.FLOWING_LAVA);
  }

  private boolean isFlowingBlock(@Nonnull BlockPos bc) {
    switch (type) {
    case CLASSIC:
      return !((BlockFluidClassic) block).isSourceBlock(owner.getWorld(), bc);
    case FINITE:
      return false;
    case VANILLA:
      return owner.getWorld().getBlockState(bc).getValue(BlockLiquid.LEVEL) != 0;
    }
    throw new IllegalStateException("unreachable code");
  }

  private void setSourceBlock(@Nonnull BlockPos bc) {
    IBlockState metaToSet;
    final World world = owner.getWorld();
    switch (type) {
    case CLASSIC:
      metaToSet = block.getStateFromMeta(((BlockFluidClassic) block).getMaxRenderHeightMeta());
      break;
    case FINITE:
      metaToSet = block.getStateFromMeta(((BlockFluidFinite) block).getMaxRenderHeightMeta());
      break;
    case VANILLA:
      if (world.provider.doesWaterVaporize() && fluid == FluidRegistry.WATER && !NiardConfig.allowWaterInHell.get()) {
        world.playSound(null, bc, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
            2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
        PacketSpawnParticles packet = new PacketSpawnParticles();
        for (int k = 0; k < 8; ++k) {
          packet.add(bc.getX() - 1 + 3 * world.rand.nextDouble(), bc.getY() + world.rand.nextDouble(), bc.getZ() - 1 + 3 * world.rand.nextDouble(), 1,
              EnumParticleTypes.SMOKE_LARGE);
        }
        setVerticalBlock(bc, false);
        return;
      }
      metaToSet = block.getDefaultState().withProperty(BlockLiquid.LEVEL, 0);
      break;
    default:
      return;
    }
    world.setBlockState(bc, metaToSet, 3);
  }

  private void setVerticalBlock(@Nonnull BlockPos bc, boolean blockUpdate) {
    IBlockState metaToSet;
    switch (type) {
    case CLASSIC:
      metaToSet = block.getDefaultState().withProperty(NullHelper.notnullF(BlockFluidClassic.LEVEL, "BlockFluidClassic.LEVEL"), 1);
      break;
    case FINITE:
      return;
    case VANILLA:
      metaToSet = block.getDefaultState().withProperty(BlockLiquid.LEVEL, 8);
      break;
    default:
      return;
    }
    owner.getWorld().setBlockState(bc, metaToSet, blockUpdate ? 3 : 2);
  }

}