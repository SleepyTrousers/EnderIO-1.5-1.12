package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.common.registry.GameData;
import crazypants.util.Lang;

public final class PainterUtil {

  private PainterUtil() {
  }

  public static boolean isMetadataEquivelent(ItemStack one, ItemStack two) {
    if(one == null || two == null) {
      return false;
    }
    return PainterUtil.getSourceBlock(one) == PainterUtil.getSourceBlock(two)
        && PainterUtil.getSourceBlockMetadata(one) == PainterUtil.getSourceBlockMetadata(two);
  }

  public static Block getSourceBlock(ItemStack item) {
    NBTTagCompound tag = item.getTagCompound();
    if(tag != null) {
      String blockId = tag.getString(BlockPainter.KEY_SOURCE_BLOCK_ID);
      Block res = GameData.blockRegistry.get(blockId);
      return res;
    }
    return null;
  }

  public static int getSourceBlockMetadata(ItemStack item) {
    NBTTagCompound tag = item.getTagCompound();
    if(tag != null) {
      return tag.getInteger(BlockPainter.KEY_SOURCE_BLOCK_META);
    }
    return 0;
  }

  public static String getTooltTipText(ItemStack item) {
    String sourceName = "";
    Block sourceId = PainterUtil.getSourceBlock(item);
    int meta = PainterUtil.getSourceBlockMetadata(item);
    if(sourceId != null) {
      if(sourceId != null) {
        sourceName = sourceId.getUnlocalizedName();
        //sourceName = sourceId.getUnlocalizedName(new ItemStack(sourceId, 1, meta));
        sourceName = StatCollector.translateToLocal(sourceName + ".name");
      }
    }
    return Lang.localize("blockPainter.paintedWith") + " " + sourceName;
  }

  public static void setSourceBlock(ItemStack item, Block source, int meta) {
    NBTTagCompound tag = item.getTagCompound();
    if(tag == null) {
      tag = new NBTTagCompound();
      item.setTagCompound(tag);
    }
    tag.setString(BlockPainter.KEY_SOURCE_BLOCK_ID, Block.blockRegistry.getNameForObject(source));
    tag.setInteger(BlockPainter.KEY_SOURCE_BLOCK_META, meta);
  }

}
