package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Strings;

@Deprecated
public final class PainterUtil {

  
  private PainterUtil() {
  }

  @Deprecated
  public static boolean isMetadataEquivelent(ItemStack one, ItemStack two) {
    if(one == null || two == null) {
      return false;
    }
    return PainterUtil.getSourceBlock(one) == PainterUtil.getSourceBlock(two)
        && PainterUtil.getSourceBlockMetadata(one) == PainterUtil.getSourceBlockMetadata(two);
  }

  @Deprecated
  public static Block getSourceBlock(ItemStack item) {
    NBTTagCompound tag = item.getTagCompound();
    return getSourceBlock(tag);
  }
  
  @Deprecated
  public static Block getSourceBlock(NBTTagCompound tag) {
    if(tag != null) {
      String blockId = tag.getString(BlockPainter.KEY_SOURCE_BLOCK_ID);
      if(!Strings.isNullOrEmpty(blockId)) {
        Block res = Block.blockRegistry.getObject(new ResourceLocation(blockId));
        return res;
      }
    }
    return null;
  }

  @Deprecated
  public static int getSourceBlockMetadata(ItemStack item) {
    NBTTagCompound tag = item.getTagCompound();
    return getSourceBlockMetadata(tag);
  }

  @Deprecated
  public static int getSourceBlockMetadata(NBTTagCompound tag) {
    if(tag != null) {
      return tag.getInteger(BlockPainter.KEY_SOURCE_BLOCK_META);
    }
    return 0;
  }

  @Deprecated
  public static void setSourceBlock(ItemStack item, Block source, int meta) {
    NBTTagCompound tag = item.getTagCompound();
    if (tag == null) {
      tag = new NBTTagCompound();
      item.setTagCompound(tag);
    }
    setSourceBlock(item.getTagCompound(), source, meta);
    if (item.getTagCompound().hasNoTags()) {
      item.setTagCompound(null);
    }
  }
  
  public static void setSourceBlock(ItemStack fac, IBlockState facade) {
    setSourceBlock(fac, facade.getBlock(), facade.getBlock().getMetaFromState(facade));    
  }
  
  @Deprecated
  public static void setSourceBlock(NBTTagCompound tag, Block source, int meta) {
    if (tag == null || source == null) {
      return;
    }
    String name = Block.blockRegistry.getNameForObject(source).toString();
    if(name != null && !name.trim().isEmpty()) {
      meta = normalizeFacadeMetadata(source, meta);
      tag.setString(BlockPainter.KEY_SOURCE_BLOCK_ID, name);
      tag.setInteger(BlockPainter.KEY_SOURCE_BLOCK_META, meta);
    }
  }
  
  public static ItemStack applyDefaultPaintedState(ItemStack stack) {
    setSourceBlock(stack, Blocks.stone, 0);
    return stack;
  }

  @Deprecated
  public static int normalizeFacadeMetadata(Block facadeID, int facadeMeta) {
    if(facadeID instanceof BlockRotatedPillar) {
      return facadeMeta & 3;
    }
    return facadeMeta;
  }

//TODO: 1.8
  @Deprecated
  public static int adjustFacadeMetadata(Block facadeID, int facadeMeta, EnumFacing side) {
    
//    if(facadeID instanceof BlockRotatedPillar) {
//      int dir = facadeMeta & 0xC;
//      switch (side) {
//        case 0:
//        case 1: dir = 0; break;
//        case 4:
//        case 5: dir = 4; break;
//        case 2:
//        case 3: dir = 8; break;
//      }
//      facadeMeta = (facadeMeta & 3) | dir;
//    }
    return facadeMeta;
  }

  @Deprecated
  public static int rotateFacadeMetadata(Block facadeID, int facadeMeta, EnumFacing axis) {
    if(facadeID instanceof BlockRotatedPillar) {
      int dir = facadeMeta & 0xC;
      EnumFacing orientation;
      switch (dir) {
        case 0: orientation = EnumFacing.UP; break;
        case 4: orientation = EnumFacing.EAST; break;
        case 8: orientation = EnumFacing.SOUTH; break;
        default: return facadeMeta;
      }
      orientation = orientation.rotateAround(axis.getAxis());
      switch (orientation) {
        case UP:
        case DOWN: dir = 0; break;
        case WEST:
        case EAST: dir = 4; break;
        case NORTH:
        case SOUTH: dir = 8; break;
        default: return facadeMeta;
      }
      return (facadeMeta & 3) | dir;
    }
    return facadeMeta;
  }
  
}
