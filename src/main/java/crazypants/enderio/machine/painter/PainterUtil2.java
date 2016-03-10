package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.render.paint.IPaintable;

public class PainterUtil2 {

  public static boolean isValid(ItemStack paintSource, ItemStack target) {
    if (paintSource == null && target == null) {
      return false;
    }
    Block block = null;
    if (target != null) {
      Item item = paintSource.getItem();
      if (item instanceof ItemBlock) {
        block = ((ItemBlock) item).getBlock();
      } else {
        return false;
      }
    }
    return isValid(paintSource, block);
  }

  public static boolean isValid(ItemStack paintSource, Block target) {
    boolean solidPaint = false;
    if (paintSource != null) {
      Item item = paintSource.getItem();
      if (item instanceof ItemBlock) {
        Block block = ((ItemBlock) item).getBlock();
        if (block instanceof IPaintable) {
          IBlockState paintSource2 = ((IPaintable) block).getPaintSource(block, paintSource);
          if (paintSource2 != null) {
            return false;
          }
        }
        solidPaint = block.isOpaqueCube();
      } else {
        return false;
      }
    }

    return (target == null && paintSource != null) //
        || (target instanceof IPaintable && paintSource == null) //
        || (target instanceof IPaintable.ITexturePaintableBlock) //
        || (target instanceof IPaintable.ISolidBlockPaintableBlock && solidPaint) //
        || (target instanceof IPaintable.INonSolidBlockPaintableBlock && !solidPaint);
  }

  // TODO: Check the marked blocks if they need an getOpposite() to our facing value or not
  public static IBlockState handleDynamicState(IBlockState paint, IBlockState state, IBlockAccess world, BlockPos pos) {
    if (paint != null) {
      Block block = paint.getBlock();
      if (block instanceof BlockStairs) {
        return paint.withProperty(BlockStairs.FACING, getFacing4(state, world, pos).getOpposite());
      }
      if (paint.getBlock() instanceof BlockAnvil) {
        return paint.withProperty(BlockAnvil.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (paint.getBlock() instanceof BlockBanner) {
        return paint.withProperty(BlockBanner.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (paint.getBlock() instanceof BlockChest) {
        return paint.withProperty(BlockChest.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (paint.getBlock() instanceof BlockDirectional) {
        return paint.withProperty(BlockDirectional.FACING, getFacing4(state, world, pos));
      }
      if (paint.getBlock() instanceof BlockDoor) {
        return paint.withProperty(BlockDoor.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (paint.getBlock() instanceof BlockEnderChest) {
        return paint.withProperty(BlockEnderChest.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (paint.getBlock() instanceof BlockEndPortalFrame) {
        return paint.withProperty(BlockEndPortalFrame.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (paint.getBlock() instanceof BlockFurnace) {
        return paint.withProperty(BlockFurnace.FACING, getFacing4(state, world, pos));
      }
      if (paint.getBlock() instanceof BlockHopper) {
        return paint.withProperty(BlockHopper.FACING, getFacing5(state, world, pos)); // opposite?
      }
      if (paint.getBlock() instanceof BlockLadder) {
        return paint.withProperty(BlockLadder.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (paint.getBlock() instanceof BlockStem) {
        return paint.withProperty(BlockStem.FACING, getFacing5u(state, world, pos)); // opposite?
      }
      if (paint.getBlock() instanceof BlockTorch) {
        return paint.withProperty(BlockTorch.FACING, getFacing5u(state, world, pos));
      }
      if (paint.getBlock() instanceof BlockTrapDoor) {
        return paint.withProperty(BlockTrapDoor.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (paint.getBlock() instanceof BlockTripWireHook) {
        return paint.withProperty(BlockTripWireHook.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (paint.getBlock() instanceof BlockWallSign) {
        return paint.withProperty(BlockWallSign.FACING, getFacing4(state, world, pos)); // opposite?
      }
    }
    return paint;
  }

  private static EnumFacing getFacing4(IBlockState state, IBlockAccess world, BlockPos pos) {
    EnumFacing facing = getFacing6(state, world, pos);
    if (facing.getAxis() == EnumFacing.Axis.Y) {
      return EnumFacing.NORTH;
    }
    return facing;
  }

  private static EnumFacing getFacing5(IBlockState state, IBlockAccess world, BlockPos pos) {
    EnumFacing facing = getFacing6(state, world, pos);
    if (facing == EnumFacing.UP) {
      return EnumFacing.DOWN;
    }
    return facing;
  }

  private static EnumFacing getFacing5u(IBlockState state, IBlockAccess world, BlockPos pos) {
    EnumFacing facing = getFacing6(state, world, pos);
    if (facing == EnumFacing.DOWN) {
      return EnumFacing.UP;
    }
    return facing;
  }

  private static EnumFacing getFacing6(IBlockState state, IBlockAccess world, BlockPos pos) {
    TileEntity tileEntity = world.getTileEntity(pos);
    if (tileEntity instanceof AbstractMachineEntity) {
      return ((AbstractMachineEntity) tileEntity).getFacing();
    }
    return EnumFacing.NORTH;
  }

}
