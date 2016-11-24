package crazypants.enderio.paint;

import javax.annotation.Nullable;

import com.enderio.core.common.util.FluidUtil;
import com.google.common.base.Strings;

import crazypants.enderio.EnderIO;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPiston;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import static crazypants.util.NbtValue.SOURCE_BLOCK;
import static crazypants.util.NbtValue.SOURCE_META;

public class PainterUtil2 {

  public static boolean isValid(ItemStack paintSource, Block target) {
    boolean solidPaint = false;
    boolean textureOnly = false;
    if (paintSource != null) {
      Block block = getBlockFromItem(paintSource);
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
      return paintSource != null;
    } else if (paintSource == null) {
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

  public static IBlockState rotate(IBlockState paintSource) {
    //TODO: Need to handle cases like stairs and slabs that have 'upper' and 'lower' so they are included in the rotation
    //cycle
    for(IProperty<?> prop : paintSource.getPropertyNames()) {
      if(prop instanceof PropertyDirection) {
        PropertyDirection pd = (PropertyDirection)prop;               
        return paintSource.cycleProperty(pd);
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
      SOURCE_BLOCK.removeTag(nbtRoot);
      SOURCE_META.removeTag(nbtRoot);
      return;
    }
    Block block = paintSource.getBlock();
    ResourceLocation res = Block.REGISTRY.getNameForObject(block);
    String name = res.toString();
    if (!name.trim().isEmpty()) {
      SOURCE_BLOCK.setString(nbtRoot, name);
      SOURCE_META.setInt(nbtRoot, block.getMetaFromState(paintSource));
    }
  }

  public static IBlockState readNbt(NBTTagCompound nbtRoot) {
    if (nbtRoot != null) {
      String blockId = SOURCE_BLOCK.getString(nbtRoot);
      if (!Strings.isNullOrEmpty(blockId)) {
        ResourceLocation res = new ResourceLocation(blockId);
        if (Block.REGISTRY.containsKey(res)) {
          Block block = Block.REGISTRY.getObject(res);
          int meta = SOURCE_META.getInt(nbtRoot);
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
    if (itemStack == null) {
      return;
    }
    if (paintSource == null) {
      SOURCE_BLOCK.removeTag(itemStack);
      SOURCE_META.removeTag(itemStack);
      return;
    }
    Block block = paintSource.getBlock();
    ResourceLocation res = Block.REGISTRY.getNameForObject(block);
    String name = res.toString();
    if (!name.trim().isEmpty()) {
      SOURCE_BLOCK.setString(itemStack, name);
      SOURCE_META.setInt(itemStack, block.getMetaFromState(paintSource));
    }
  }

  public static String getTooltTipText(ItemStack itemStack) {
    String sourceName = null;
    if (itemStack.getItem() instanceof IWithPaintName) {
      sourceName = ((IWithPaintName) itemStack.getItem()).getPaintName(itemStack);
    } else {
      IBlockState state = getSourceBlock(itemStack);
      if (state != null) {
        Block block = state.getBlock();
        Item itemFromBlock = Item.getItemFromBlock(block);
        if (itemFromBlock != null) {
          ItemStack is = new ItemStack(itemFromBlock, 1, block.getMetaFromState(state));
          sourceName = is.getDisplayName();
        }
      }
    }
    if (sourceName == null || sourceName.isEmpty()) {
      return EnderIO.lang.localize("blockPainter.unpainted");
    } else {
      return EnderIO.lang.localize("blockPainter.paintedWith", sourceName);
    }
  }

  public static ItemStack getPaintAsStack(IBlockState state) {
    if (state != null) {
      Block block = state.getBlock();
      Item itemFromBlock = Item.getItemFromBlock(block);
      if (itemFromBlock != null) {
        return new ItemStack(itemFromBlock, 1, block.getMetaFromState(state));
      }
    }
    return null;
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
    return  rt != null && rt != EnumBlockRenderType.INVISIBLE;
  }

  public static Block getBlockFromItem(Item itemIn) {
    if (itemIn instanceof ItemBlock) {
      return ((ItemBlock) itemIn).getBlock();
    }
    if (itemIn != null) {
      FluidStack fluidStack = FluidUtil.getFluidTypeFromItem(new ItemStack(itemIn));
      if (fluidStack != null) {
        return fluidStack.getFluid().getBlock();
      }
    }
    return null;
  }

  public static Block getBlockFromItem(ItemStack itemStack) {
    if (itemStack != null) {
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

  public static boolean canRenderInLayer(@Nullable IBlockState paintSource, BlockRenderLayer blockLayer) {
    if (paintSource != null) {      
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
