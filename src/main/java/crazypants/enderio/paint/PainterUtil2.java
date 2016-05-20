package crazypants.enderio.paint;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import com.enderio.core.common.util.FluidUtil;
import com.google.common.base.Strings;

import crazypants.enderio.EnderIO;

import static crazypants.util.NbtValue.SOURCE_BLOCK;
import static crazypants.util.NbtValue.SOURCE_META;

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
    boolean textureOnly = false;
    if (paintSource != null) {
      Block block = getBlockFromItem(paintSource);
      IBlockState bs = block.getDefaultState();
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
      solidPaint = block.isOpaqueCube(bs);
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
    if (res != null) {
      String name = res.toString();
      if (!name.trim().isEmpty()) {
        SOURCE_BLOCK.setString(nbtRoot, name);
        SOURCE_META.setInt(nbtRoot, block.getMetaFromState(paintSource));
      }
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
    if (res != null) {
      String name = res.toString();
      if (!name.trim().isEmpty()) {
        SOURCE_BLOCK.setString(itemStack, name);
        SOURCE_META.setInt(itemStack, block.getMetaFromState(paintSource));
      }
    }
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

  // Note: Config-based white-/blacklisting is done by PaintSourceValidator and checked as part of the input slot validation of the Painter
  public static boolean shouldHaveModel(Block block) {
    if(block == null) {
      return false;
    }
    return block.getRenderType(block.getDefaultState()) == EnumBlockRenderType.MODEL;
  }

  public static boolean shouldHaveTexture(Block block) {
    if (block == null) {
      return false;
    }
    EnumBlockRenderType rt = block.getRenderType(block.getDefaultState());
    return  rt != null && rt != EnumBlockRenderType.INVISIBLE;
  }

  public static Block getBlockFromItem(Item itemIn) {
    if (itemIn instanceof ItemBlock) {
      return ((ItemBlock) itemIn).getBlock();
    }
    if (itemIn != null) {
      FluidStack fluidStack = FluidUtil.getFluidFromItem(new ItemStack(itemIn));
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
      FluidStack fluidStack = FluidUtil.getFluidFromItem(itemStack);
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

}
