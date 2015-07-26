package crazypants.enderio.machine.drain;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import crazypants.util.BlockCoord;

public final class FluidHelper {

  private final World world;
  private final FluidStack stack;
  private final Fluid fluid;
  private final Block block;
  private final ForgeDirection densityDir;
  private final FType type;
  private final BlockCoord startbc;
  private IDrainingCallback hook;

  /*
   * Set this to block removed water from forming infinite pools. The block expires when the given te is gc()ed.
   */
  public void setDrainingCallback(IDrainingCallback hook) {
    this.hook = hook;
  }

  private FluidHelper(World world, FluidStack stack, BlockCoord startbc) throws Exception {
    this.world = world;
    this.stack = stack;
    this.fluid = stack.getFluid();
    this.block = fluid.getBlock();
    this.densityDir = fluid.getDensity() > 0 ? ForgeDirection.DOWN : ForgeDirection.UP;
    if (block instanceof BlockFluidClassic) {
      this.type = FType.CLASSIC;
    } else if (block instanceof BlockFluidFinite) {
      this.type = FType.FINITE;
    } else if (block instanceof BlockLiquid) {
      this.type = FType.VANILLA;
    } else {
      throw new Exception();
    }
    this.startbc = startbc;
  }

  public static boolean isSourceBlock(World world, BlockCoord bc) {
    Block block = bc.getBlock(world);
    if (block instanceof BlockFluidClassic) {
      return ((BlockFluidClassic) block).isSourceBlock(world, bc.x, bc.y, bc.z);
    } else if (block instanceof BlockFluidFinite) {
      return ((BlockFluidFinite)block).canDrain(world, bc.x, bc.y, bc.z);
    } else if (block instanceof BlockLiquid) {
      return world.getBlockMetadata(bc.x, bc.y, bc.z) == 0;
    } else {
      return false;
    }
  }

  private static final ForgeDirection[] DIRECTIONS_INIT = {ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST};
  
  public static FluidHelper getInstance(World world, BlockCoord bc) {
    for (ForgeDirection forgeDirection : DIRECTIONS_INIT) {
      BlockCoord direction = bc.getLocation(forgeDirection);
      if (isSourceBlock(world, direction)) {
        Fluid fluidForBlock = FluidRegistry.lookupFluidForBlock(direction.getBlock(world));
        if (fluidForBlock != null) {
          FluidHelper result = getInstance(world, new FluidStack(fluidForBlock, 1000), direction);
          if (result != null) {
            return result;
          }
        }
      }
    }
    for (ForgeDirection forgeDirection : DIRECTIONS_INIT) {
      BlockCoord direction = bc.getLocation(forgeDirection);
      Fluid fluidForBlock = FluidRegistry.lookupFluidForBlock(direction.getBlock(world));
      if (fluidForBlock != null) {
        FluidHelper result = getInstance(world, new FluidStack(fluidForBlock, 1000), direction);
        if (result != null) {
          return result;
        }
      }
    }
    return null;
  }
  
  public static FluidHelper getInstance(World world, BlockCoord bc, FluidStack fs) {
    for (ForgeDirection forgeDirection : DIRECTIONS_INIT) {
      BlockCoord direction = bc.getLocation(forgeDirection);
      if (isSourceBlock(world, direction) && isSameLiquid(fs, world, direction)) {
        return getInstance(world, fs, direction);
      }
    }
    for (ForgeDirection forgeDirection : DIRECTIONS_INIT) {
      BlockCoord direction = bc.getLocation(forgeDirection);
      if (isSameLiquid(fs, world, direction)) {
        return getInstance(world, fs, direction);
      }
    }
    return null;
  }
  
  public static FluidHelper getInstance(World world, FluidStack stack, BlockCoord startbc) {
    try {
      return new FluidHelper(world, stack, startbc);
    } catch (Throwable t) {
      // NPE for fluids that have no block
      // E for fluid that don't extend one of the known classses
      return null;
    }
  }

  public static FluidHelper getInstance(World world, FluidStack stack) {
    return getInstance(world, stack, null);
  }

  private enum FType {
    VANILLA,
    CLASSIC,
    FINITE
  }
  
  private static boolean isInWorld(BlockCoord bc) {
    return bc.y > 0 && bc.y <= 255;
  }
  
  /*
   * same liquid
   */
  public boolean isSameLiquid(BlockCoord bc) {
    return bc.getBlock(world) == block;
  }
  
  public static boolean isSameLiquid(FluidStack fs, World world, BlockCoord bc) {
    return bc.getBlock(world) == fs.getFluid().getBlock();
  }
  
  public boolean isSourceBlock(BlockCoord bc) {
    switch (type) {
    case CLASSIC:
      return ((BlockFluidClassic) block).isSourceBlock(world, bc.x, bc.y, bc.z);
    case FINITE:
      return false;
    case VANILLA:
      return world.getBlockMetadata(bc.x, bc.y, bc.z) == 0;
    }
    throw new IllegalStateException("unreachable code");
  }
  
  /*
   * Replacement for isFlowingVertically() that does the right thing
   */
  public boolean isFlowingVertically2(BlockCoord bc) {
    BlockCoord downflow = bc.getLocation(densityDir == ForgeDirection.DOWN ? ForgeDirection.DOWN : ForgeDirection.UP);
    return isSameLiquid(bc.getLocation(ForgeDirection.UP)) && isSameLiquid(bc.getLocation(ForgeDirection.DOWN)) && !isSourceBlock(downflow);
  }

  /*
   * same liquid and nearer to a source block
   */
  public boolean isUpflow(BlockCoord bc0, BlockCoord bc1) {
    switch (type) {
    case CLASSIC:
      return world.getBlockMetadata(bc1.x, bc1.y, bc1.z) < world.getBlockMetadata(bc0.x, bc0.y, bc0.z);
    case FINITE:
      return world.getBlockMetadata(bc1.x, bc1.y, bc1.z) > world.getBlockMetadata(bc0.x, bc0.y, bc0.z);
    case VANILLA:
      return (world.getBlockMetadata(bc1.x, bc1.y, bc1.z) < world.getBlockMetadata(bc0.x, bc0.y, bc0.z)) || (world.getBlockMetadata(bc1.x, bc1.y, bc1.z) & 8) != 0;
      // other block has higher level of liquid OR other block is downflow and current block is not
    }
    throw new IllegalStateException("unreachable code");
  }

  /*
   * move a source block, or delete it if it flows out of the world
   */
  public void doFlow(BlockCoord bc0, BlockCoord bc1) {
    if (isInWorld(bc1)) {
      world.setBlock(bc1.x, bc1.y, bc1.z, world.getBlock(bc0.x, bc0.y, bc0.z), world.getBlockMetadata(bc0.x, bc0.y, bc0.z), 3);
    }
    switch (type) {
    case FINITE:
      world.setBlockToAir(bc0.x, bc0.y, bc0.z);
      break;
    case CLASSIC:
    case VANILLA:
      if (adjCount(bc0) > 1) {
        world.setBlock(bc0.x, bc0.y, bc0.z, world.getBlock(bc0.x, bc0.y, bc0.z), 1, 3);
        if (fluid == FluidRegistry.WATER && hook != null) {
          preventWater(bc0);
          preventWater(bc1);
        }
      } else {
        world.setBlockToAir(bc0.x, bc0.y, bc0.z);
      }
      break;
    }
  }

  private void preventWater(BlockCoord bc) {
    int result = 0;
    for (ForgeDirection forgeDirection : DIRECTIONS) {
      final BlockCoord bc1 = bc.getLocation(forgeDirection);
      if (isSameLiquid(bc1) && !isSourceBlock(bc1)) {
        hook.onWaterDrainNearby(world, bc1);
      }
    }
    hook.onWaterDrain(world, bc);
  }
  
  private int adjCount(BlockCoord bc) {
    int result = 0;
    for (ForgeDirection forgeDirection : DIRECTIONS) {
      if (isSameLiquid(bc.getLocation(forgeDirection))) {
        result++;
      }
    }
    return result;
  }
  
  public static final ForgeDirection[] DIRECTIONS = {ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST};
  
  private Set<BlockCoord> seen;
  
  public boolean findAndPullSourceBlock(BlockCoord bc) {
    seen = new HashSet<BlockCoord>();
    return findAndPullSourceBlock(bc, false);
  }

  public static class ReturnObject {
    public boolean isDry = false;
    public boolean inProgress = false;
    public FluidStack result = null;
  }

  public ReturnObject eatOrPullFluid() {
    return eatOrPullFluid(startbc);
  }

  public ReturnObject eatOrPullFluid(BlockCoord bc) {
    ReturnObject result = new ReturnObject();
    if (isSameLiquid(bc)) {
      if (!isSourceBlock(bc)) {
        seen = new HashSet<BlockCoord>();
        result.inProgress = findAndPullSourceBlock(bc, true);
      }
      if (isSourceBlock(bc)) {
        switch (type) {
        case CLASSIC:
        case FINITE:
          if (((IFluidBlock) block).canDrain(world, bc.x, bc.y, bc.z)) {
            result.result = ((IFluidBlock) block).drain(world, bc.x, bc.y, bc.z, true);
          } else {
            result.isDry = true;
          }
          break;
        case VANILLA:
          result.result = stack.copy();
          result.result.amount = 1000;
          if (fluid == FluidRegistry.WATER && hook != null) {
            hook.onWaterDrain(world, bc);
          }
          world.setBlockToAir(bc.x, bc.y, bc.z);
          break;
        default:
          throw new IllegalStateException("unreachable code");
        }
      }
      if (!result.inProgress && result.result == null) {
        result.isDry = true;
      }
    } else {
      result.isDry = true;
    }
    return result;
  }
  
  private boolean findAndPullSourceBlock(BlockCoord bc, boolean foundStepUp) {
    if (!seen.contains(bc)) {
      seen.add(bc);

      BlockCoord upflow = bc.getLocation(densityDir == ForgeDirection.UP ? ForgeDirection.DOWN : ForgeDirection.UP);

      // try to go up first
      if (isInWorld(upflow) && isSameLiquid(upflow)) {
        if (isSourceBlock(upflow)) {
          doFlow(upflow, bc);
          return true;
        } else if (findAndPullSourceBlock(upflow, true)) {
          return true;
        }
      }

      // then look around
      for (ForgeDirection dir : DIRECTIONS) {
        BlockCoord bc2 = bc.getLocation(dir);
        if (isSameLiquid(bc2)) {
          if (isSourceBlock(bc2)) {
            if (foundStepUp) { // don't flow unless there is a "down" to flow to
              if (isSameLiquid(bc2.getLocation(densityDir)) && !isSourceBlock(bc2.getLocation(densityDir)) && 
                  isSameLiquid(bc.getLocation(densityDir)) && !isSourceBlock(bc.getLocation(densityDir))) {
                // if we can drop the source block down by one without disconnecting it from us, we do so
                doFlow(bc2, bc2.getLocation(densityDir));
              } else {
                doFlow(bc2, bc);
              }
            }
            return true;
          } else if (isUpflow(bc, bc2) && !isFlowingVertically2(bc2)) {
            if (findAndPullSourceBlock(bc2, foundStepUp)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }
  
}
