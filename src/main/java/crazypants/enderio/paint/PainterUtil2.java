package crazypants.enderio.paint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.FluidUtil;

import crazypants.enderio.EnderIO;
import crazypants.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPiston;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraftforge.fluids.FluidStack;

import static crazypants.util.NbtValue.BLOCKSTATE;

public class PainterUtil2 {

  public static boolean isValid(@Nonnull ItemStack paintSource, Block target) {
    boolean solidPaint = false;
    boolean textureOnly = false;
    if (Prep.isValid(paintSource)) {
      if (!PaintSourceValidator.instance.isValidSourceDefault(paintSource)) {
        return false;
      }
      Block block = getBlockFromItem(paintSource);
      if (block == null || block == Blocks.AIR) {
        return false;
      }
      IBlockState bs = Block$getBlockFromItem_stack$getItem___$getStateFromMeta_stack$getMetadata___(paintSource, block);
      solidPaint = bs.isOpaqueCube();
      if (!shouldHaveModel(block)) {
        if (shouldHaveTexture(block)) {
          textureOnly = true;
        } else {
          return false;
        }
      } else if (block instanceof IPaintable) {
        bs = ((IPaintable) block).getPaintSource(block, paintSource);
        if (bs != null) {
          return false;
        }
      }      
    }

    if (target == null) {
      return Prep.isValid(paintSource);
    } else if (Prep.isInvalid(paintSource)) {
      return target instanceof IPaintable;
    } else if (target instanceof IPaintable.ITexturePaintableBlock) {
      return true;
    } else if (target instanceof IPaintable.ISolidBlockPaintableBlock) {
      return solidPaint && !textureOnly;
    } else if (target instanceof IPaintable.INonSolidBlockPaintableBlock) {
      return !solidPaint && !textureOnly;
    } else if (target instanceof IPaintable.IBlockPaintableBlock) {
      return !textureOnly;
    } else {
      return false;
    }
  }

  public static IBlockState rotate(@Nonnull IBlockState paintSource) {
    // TODO: Need to handle cases like stairs that have 'upper' and 'lower' so they are included in the rotation
    //cycle
    for (IProperty<?> prop : paintSource.getPropertyKeys()) {
      if(prop instanceof PropertyDirection) {
        return paintSource.cycleProperty(prop);
      } else if (prop == BlockSlab.HALF) {
        return paintSource.cycleProperty(prop);
      }
    }    
    if(paintSource.getBlock() instanceof BlockLog) {
      return paintSource.cycleProperty(BlockLog.LOG_AXIS);
    }
    return paintSource;
  }
  
  public static void writeNbt(NBTTagCompound nbtRoot, IBlockState paintSource) {
    if (nbtRoot == null) {
      return;
    }
    if (paintSource == null) {
      BLOCKSTATE.removeTag(nbtRoot);
    } else {
      BLOCKSTATE.setTag(nbtRoot, NBTUtil.writeBlockState(new NBTTagCompound(), paintSource));
    }
  }

  public static IBlockState readNbt(NBTTagCompound nbtRoot) {
    final NBTTagCompound tag = BLOCKSTATE.getTag(nbtRoot);
    if (tag != null) {
      return NBTUtil.readBlockState(tag);
      }
    return null;
  }

  public static boolean isPainted(@Nonnull ItemStack itemStack) {
    return BLOCKSTATE.hasTag(itemStack);
  }

  public static IBlockState getSourceBlock(@Nonnull ItemStack itemStack) {
    return readNbt(itemStack.getTagCompound());
  }

  public static void setSourceBlock(@Nonnull ItemStack itemStack, IBlockState paintSource) {
    if (Prep.isInvalid(itemStack)) {
      return;
    }
    if (paintSource == null) {
      BLOCKSTATE.removeTag(itemStack);
      return;
    } else {
      BLOCKSTATE.setTag(itemStack, NBTUtil.writeBlockState(new NBTTagCompound(), paintSource));
    }
  }

  public static String getTooltTipText(@Nonnull ItemStack itemStack) {
    String sourceName = null;
    if (itemStack.getItem() instanceof IWithPaintName) {
      sourceName = ((IWithPaintName) itemStack.getItem()).getPaintName(itemStack);
    } else {
      IBlockState state = getSourceBlock(itemStack);
      if (state != null) {
        Block block = state.getBlock();
        Item itemFromBlock = Item.getItemFromBlock(block);
        if (itemFromBlock != Items.AIR) {
          ItemStack is = new ItemStack(itemFromBlock, 1, block.getMetaFromState(state));
          sourceName = is.getDisplayName();
        } else {
          sourceName = block.getLocalizedName();
        }
      }
    }
    if (sourceName == null || sourceName.isEmpty()) {
      return EnderIO.lang.localize("blockPainter.unpainted");
    } else {
      return EnderIO.lang.localize("blockPainter.paintedWith", sourceName);
    }
  }

  public static @Nonnull ItemStack getPaintAsStack(IBlockState state) {
    if (state != null) {
      Block block = state.getBlock();
      Item itemFromBlock = Item.getItemFromBlock(block);
      if (itemFromBlock != Items.AIR) {
        return new ItemStack(itemFromBlock, 1, block.getMetaFromState(state));
      }
    }
    return Prep.getEmpty();
  }

  public interface IWithPaintName {
    String getPaintName(ItemStack stack);
  }

  // Note: Config-based white-/blacklisting is done by PaintSourceValidator and checked as part of the input slot validation of the Painter
  public static boolean shouldHaveModel(Block block) {
    if(block == null) {
      return false;
    }
    
    return block.getDefaultState().getRenderType() == EnumBlockRenderType.MODEL;
  }

  public static boolean shouldHaveTexture(Block block) {
    if (block == null) {
      return false;
    }
    EnumBlockRenderType rt = block.getDefaultState().getRenderType();
    return rt != EnumBlockRenderType.INVISIBLE;
  }

  public static Block getBlockFromItem(Item itemIn) {
    if (itemIn != null) {
      if (itemIn instanceof ItemBlock) {
        return ((ItemBlock) itemIn).getBlock();
      }
      FluidStack fluidStack = FluidUtil.getFluidTypeFromItem(new ItemStack(itemIn));
      if (fluidStack != null) {
        return fluidStack.getFluid().getBlock();
      }
    }
    return null;
  }

  public static Block getBlockFromItem(@Nonnull ItemStack itemStack) {
    if (Prep.isValid(itemStack)) {
      if (itemStack.getItem() instanceof ItemBlock) {
        return ((ItemBlock) itemStack.getItem()).getBlock();
      }
      FluidStack fluidStack = FluidUtil.getFluidTypeFromItem(itemStack);
      if (fluidStack != null && fluidStack.getFluid() != null) {
        return fluidStack.getFluid().getBlock();
      }
    }
    return null;
  }

  private static final BlockRenderLayer BREAKING = null;

  public static boolean canRenderInLayer(@Nullable IBlockState paintSource, @Nonnull BlockRenderLayer blockLayer) {
    if (blockLayer == BREAKING) {
      return true;
    } else if (paintSource != null) {
      return paintSource.getBlock().canRenderInLayer(paintSource, blockLayer);
    } else {
      return blockLayer == BlockRenderLayer.SOLID;
    }
  }

  // This line is in this excessively named method to show up nicely in a stack trace
  public static IBlockState Block$getBlockFromItem_stack$getItem___$getStateFromMeta_stack$getMetadata___(ItemStack paintSource, Block paintBlock) {
    if (paintSource.getItem().getClass() == ItemPiston.class) {
      // Vanilla bug. ItemPiston returns an invalid block meta.
      return paintBlock.getDefaultState();
    }
    return paintBlock.getStateFromMeta(paintSource.getItem().getMetadata(paintSource.getMetadata()));
  }

}
