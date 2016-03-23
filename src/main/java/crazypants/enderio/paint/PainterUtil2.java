package crazypants.enderio.paint;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameData;

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
  
  @Deprecated
  public static IBlockState handleDynamicState(IBlockState paintSource, IBlockState state, IBlockAccess world, BlockPos pos) {
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
    ResourceLocation res = Block.blockRegistry.getNameForObject(block);
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
        if (Block.blockRegistry.containsKey(res)) {
          Block block = Block.blockRegistry.getObject(res);
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
    ResourceLocation res = Block.blockRegistry.getNameForObject(block);
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
  public static boolean isBlacklisted(Block block) {
    if(block == null) {
      return false;
    }
    String blockDomain = GameData.getBlockRegistry().getNameForObject(block).getResourceDomain();
    return (block.getRenderType() != 3 && "minecraft".equals(blockDomain)) || block.getRenderType() == 1 || block
            .getRenderType() == -1;    
  }

}
