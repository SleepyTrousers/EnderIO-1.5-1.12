package crazypants.enderio.machines.machine.niard;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.xp.XpUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import static info.loenwind.enderioaddons.config.Config.niardAllowWaterInHell;

public class EngineNiard {

  private final TileNiard owner;
  private Fluid fluid = null;
  private Block block;
  private FluidType type;
  private EnumFacing downflowDirection;
  private int radius = -1;
  private RadiusIterator radiusItr;

  public EngineNiard(TileNiard owner) {
    this.owner = owner;
  }

  public EngineNiard setFluid(Fluid fluid) {
    if (fluid.canBePlacedInWorld()) {
      this.radiusItr = radius >= 0 ? new RadiusIterator(owner.offset(), radius) : null;
      this.fluid = fluid;
      this.downflowDirection = fluid.getDensity() > 0 ? EnumFacing.DOWN : EnumFacing.UP;
      block = fluid.getBlock();
      if (block instanceof BlockFluidClassic) {
        type = FluidType.CLASSIC;
      } else if (block instanceof BlockFluidFinite) {
        type = FluidType.FINITE;
      } else if (block instanceof BlockLiquid) {
        type = FluidType.VANILLA;
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
      List<BlockPos> seen = new ArrayList<>();
      BlockPos base = radiusItr.next();
      BlockPos next = base.offset(downflowDirection);
      while (isInWorld(next) && (owner.getWorld().isAirBlock(next) || (isSameLiquid(next) && isFlowingBlock(next)))) {
        seen.add(next);
        next = next.offset(downflowDirection);
      }
      if (!seen.isEmpty()) {
        setSourceBlock(seen.remove(seen.size() - 1));
        if (!seen.isEmpty()) {
          for (BlockPos bc : seen) {
            setVerticalBlock(bc, false);
          }
        }
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

  private boolean isInWorld(BlockPos bc) {
    return owner.getWorld().blockExists(bc);
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
      return owner.getWorld().getBlockMetadata(bc.x, bc.y, bc.z) != 0;
    }
    throw new IllegalStateException("unreachable code");
  }

  private void setSourceBlock(BlockPos bc) {
    Block blockToSet = block;
    int metaToSet = 0;
    final World world = owner.getWorld();
    switch (type) {
    case CLASSIC:
      metaToSet = ((BlockFluidClassic) block).getMaxRenderHeightMeta();
      break;
    case FINITE:
      metaToSet = ((BlockFluidFinite) block).getMaxRenderHeightMeta();
      break;
    case VANILLA:
      if (world.provider.doesWaterVaporize() && fluid == FluidRegistry.WATER && !niardAllowWaterInHell.getBoolean()) {
        world.playSoundEffect(bc.getX() + 0.5F, bc.getY() + 0.1F, bc.getZ() + 0.5F, "random.fizz", 0.5F,
            2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
        for (int l = 0; l < 8; ++l) {
          spawnParticle(world, "largesmoke", bc.getX() - 1 + 3 * Math.random(), bc.getY() + Math.random(), bc.z - 1 + 3 * Math.random(), 0.0D, 0.0D, 0.0D);
        }
        setVerticalBlock(bc, false);
        return;
      }
      metaToSet = 0;
      break;
    }
    world.setBlockState(bc, blockToSet, metaToSet, 3);
  }

  private void setVerticalBlock(BlockPos bc, boolean blockUpdate) {
    Block blockToSet = block;
    int metaToSet = 0;
    switch (type) {
    case CLASSIC:
      metaToSet = 1;
      break;
    case FINITE:
      return;
    case VANILLA:
      metaToSet = 8;
      break;
    }
    owner.getWorld().setBlock(bc.x, bc.y, bc.z, blockToSet, metaToSet, blockUpdate ? 3 : 2);
  }

}