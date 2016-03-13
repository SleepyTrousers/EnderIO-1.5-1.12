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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.google.common.base.Strings;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.IConduitBundle;
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
        if (isBlacklisted(block)) {
          return false;
        } else if (block instanceof IPaintable) {
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

    if (target == null) {
      return paintSource != null;
    } else if (paintSource == null) {
      return target instanceof IPaintable;
    } else if (target instanceof IPaintable.ITexturePaintableBlock) {
      return true;
    } else if (target instanceof IPaintable.ISolidBlockPaintableBlock) {
      return solidPaint;
    } else if (target instanceof IPaintable.INonSolidBlockPaintableBlock) {
      return !solidPaint;
    } else if (target instanceof IPaintable.IBlockPaintableBlock) {
      return true;
    } else {
      return false;
    }
  }

  // TODO: Check the marked blocks if they need an getOpposite() to our facing value or not
  public static IBlockState handleDynamicState(IBlockState paintSource, IBlockState state, IBlockAccess world, BlockPos pos) {
    if (paintSource != null) {
      Block block = paintSource.getBlock();
      if (block instanceof BlockStairs) {
        return paintSource.withProperty(BlockStairs.FACING, getFacing4(state, world, pos).getOpposite());
      }
      if (block instanceof BlockAnvil) {
        return paintSource.withProperty(BlockAnvil.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (block instanceof BlockBanner) {
        return paintSource.withProperty(BlockBanner.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (block instanceof BlockChest) {
        return paintSource.withProperty(BlockChest.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (block instanceof BlockDirectional) {
        return paintSource.withProperty(BlockDirectional.FACING, getFacing4(state, world, pos));
      }
      if (block instanceof BlockDoor) {
        return paintSource.withProperty(BlockDoor.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (block instanceof BlockEnderChest) {
        return paintSource.withProperty(BlockEnderChest.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (block instanceof BlockEndPortalFrame) {
        return paintSource.withProperty(BlockEndPortalFrame.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (block instanceof BlockFurnace) {
        return paintSource.withProperty(BlockFurnace.FACING, getFacing4(state, world, pos));
      }
      if (block instanceof BlockHopper) {
        return paintSource.withProperty(BlockHopper.FACING, getFacing5(state, world, pos)); // opposite?
      }
      if (block instanceof BlockLadder) {
        return paintSource.withProperty(BlockLadder.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (block instanceof BlockStem) {
        return paintSource.withProperty(BlockStem.FACING, getFacing5u(state, world, pos)); // opposite?
      }
      if (block instanceof BlockTorch) {
        return paintSource.withProperty(BlockTorch.FACING, getFacing5u(state, world, pos));
      }
      if (block instanceof BlockTrapDoor) {
        return paintSource.withProperty(BlockTrapDoor.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (block instanceof BlockTripWireHook) {
        return paintSource.withProperty(BlockTripWireHook.FACING, getFacing4(state, world, pos)); // opposite?
      }
      if (block instanceof BlockWallSign) {
        return paintSource.withProperty(BlockWallSign.FACING, getFacing4(state, world, pos)); // opposite?
      }
    }
    return paintSource;
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
    if (tileEntity instanceof IConduitBundle) {
      return ((IConduitBundle) tileEntity).getFacing();
    }
    return EnumFacing.NORTH;
  }

  public static void writeNbt(NBTTagCompound nbtRoot, IBlockState paintSource) {
    if (nbtRoot == null || paintSource == null) {
      return;
    }
    Block block = paintSource.getBlock();
    ResourceLocation res = Block.blockRegistry.getNameForObject(block);
    if (res != null) {
      String name = res.toString();
      if (!name.trim().isEmpty()) {
        nbtRoot.setString(BlockPainter.KEY_SOURCE_BLOCK_ID, name);
        int meta = block.getMetaFromState(paintSource);
        nbtRoot.setInteger(BlockPainter.KEY_SOURCE_BLOCK_META, meta);
      }
    }
  }

  public static IBlockState readNbt(NBTTagCompound nbtRoot) {
    if (nbtRoot != null) {
      String blockId = nbtRoot.getString(BlockPainter.KEY_SOURCE_BLOCK_ID);
      if (!Strings.isNullOrEmpty(blockId)) {
        ResourceLocation res = new ResourceLocation(blockId);
        if (Block.blockRegistry.containsKey(res)) {
          Block block = Block.blockRegistry.getObject(res);
          int meta = nbtRoot.getInteger(BlockPainter.KEY_SOURCE_BLOCK_META);
          return block.getStateFromMeta(meta);
        }
      }
    }
    return null;
  }

  public static IBlockState getSourceBlock(ItemStack itemStack) {
    return readNbt(itemStack.getTagCompound());
  }

  public static void setSourceBlock(ItemStack itemStack, IBlockState paintSource) {
    NBTTagCompound tag = itemStack.getTagCompound();
    if (tag == null) {
      tag = new NBTTagCompound();
      itemStack.setTagCompound(tag);
    }
    writeNbt(tag, paintSource);
  }

  public static String getTooltTipText(ItemStack itemStack) {
    String sourceName = "";
    IBlockState state = getSourceBlock(itemStack);
    if (state != null) {
      Block block = state.getBlock();
      Item itemFromBlock = Item.getItemFromBlock(block);
      if (itemFromBlock != null) {
        ItemStack is = new ItemStack(itemFromBlock, 1, block.getMetaFromState(state));
        sourceName = is.getDisplayName();
      }
    } else {
      return EnderIO.lang.localize("blockPainter.unpainted");
    }
    return EnderIO.lang.localize("blockPainter.paintedWith", sourceName);
  }

  // TODO: Find out what the replacement for findUniqueIdentifierFor() is. Extra points for getting the one who slapped the @Deprecated on to also add some
  // documentation what to use instead.
  public static boolean isBlacklisted(Block block) {
    return block != null
        && ((block.getRenderType() != 3 && "minecraft".equals(GameRegistry.findUniqueIdentifierFor(block).modId)) || block.getRenderType() == 1 || block
            .getRenderType() == -1);
  }

}
