package crazypants.enderio.machines.machine.niard;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.xp.XpUtil;
import crazypants.enderio.machines.config.config.NiardConfig;
import crazypants.enderio.machines.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;

public class EngineNiard {

  private final @Nonnull TileNiard owner;
  private Fluid fluid = null;
  private @Nonnull Block block = Blocks.WATER;
  private @Nonnull FluidType type = FluidType.VANILLA;
  private @Nonnull EnumFacing downflowDirection = EnumFacing.DOWN;
  private int radius = -1;
  private RadiusIterator radiusItr = new RadiusIterator(BlockPos.ORIGIN, 0);

  public EngineNiard(@Nonnull TileNiard owner) {
    this.owner = owner;
  }

  public EngineNiard setFluid(Fluid fluid) {
    if (fluid != this.fluid) {
      if (fluid.canBePlacedInWorld()) {
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
    }
    return this;
  }

  public boolean work() {
    if (fluid == null) {
      return false;
    }
    if (radius != owner.getRange()) {
      radius = owner.getRange();
      radiusItr = new RadiusIterator(owner.getLocation(), radius);
    }
    for (int i = 0; i < radiusItr.size(); i++) {
      NNList<BlockPos> seen = new NNList<>();
      BlockPos base = radiusItr.next();
      BlockPos next = base.offset(downflowDirection);
      while (isInWorld(next) && (isSameLiquid(next) ? isFlowingBlock(next) : canPlace(next))) {
        seen.add(0, next);
        next = next.offset(downflowDirection);
      }
      if (!seen.isEmpty()) {
        setSourceBlock(seen.remove(0));
        seen.apply((Callback<BlockPos>) bc -> setVerticalBlock(bc, false));
        // this complicated thing is needed for vanilla fluids to start flowing correctly
        owner.getWorld().getBlockState(base.offset(downflowDirection)).neighborChanged(owner.getWorld(), base.offset(downflowDirection),
            owner.getWorld().getBlockState(owner.getLocation()).getBlock(), owner.getLocation());
        return true;
      }
    }
    return false;
  }

  private boolean canPlace(@Nonnull BlockPos pos) {
    World world = owner.getWorld();
    IBlockState destBlockState = world.getBlockState(pos);
    Material destMaterial = destBlockState.getMaterial();
    boolean isDestNonSolid = !destMaterial.isSolid();
    boolean isLiquid = destMaterial.isLiquid();
    boolean isDestReplaceable = destBlockState.getBlock().isReplaceable(world, pos);
    return world.isAirBlock(pos) || (!isLiquid && (isDestNonSolid || isDestReplaceable));
  }

  public int work(int xp_in_mb) {
    int remaining = XpUtil.liquidToExperience(xp_in_mb);
    if (radius != owner.getRange()) {
      radius = owner.getRange();
      radiusItr = new RadiusIterator(owner.getLocation(), radius);
    }
    if (radius >= 0) {
      for (int i = 0; i < radiusItr.size(); i++) {
        BlockPos next = radiusItr.next().offset(EnumFacing.DOWN);
        if (isInWorld(next) && canPlace(next)) {
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
    final World world = owner.getWorld();
    final FluidStack stack = new FluidStack(fluid, Fluid.BUCKET_VOLUME);

    if (world.provider.doesWaterVaporize() && fluid.doesVaporize(stack) && !NiardConfig.allowWaterInHell.get()) {
      PacketHandler.sendToAllAround(new PacketSFXFluidFizzle(stack, bc), owner);
      setVerticalBlock(bc, false);
      return;
    }

    // First clean up, we may target locations destroyBlockOnFluidPlacement doesn't clean, so call it manually and then clear the pos hard. That's with drops
    // but without sound.
    FluidUtil.destroyBlockOnFluidPlacement(world, bc);
    if (!world.isAirBlock(bc)) {
      IBlockState iblockstate = world.getBlockState(bc);
      iblockstate.getBlock().dropBlockAsItem(world, bc, iblockstate, 0);
      world.setBlockState(bc, Blocks.AIR.getDefaultState(), 3);
    }

    getFluidBlockHandler(bc).fill(stack, true);
    // TODO: do we need to check the return value here?
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

  /**
   * see {@link FluidUtil#getFluidBlockHandler}
   */
  IFluidHandler getFluidBlockHandler(BlockPos pos) {
    if (block instanceof IFluidBlock) {
      return new FluidBlockWrapper((IFluidBlock) block, owner.getWorld(), pos);
    } else if (block instanceof BlockLiquid) {
      return new BlockLiquidWrapper((BlockLiquid) block, owner.getWorld(), pos);
    } else {
      return new BlockWrapper(block, owner.getWorld(), pos);
    }
  }

}