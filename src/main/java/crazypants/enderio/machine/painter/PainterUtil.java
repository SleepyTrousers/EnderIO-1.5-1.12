package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.base.Strings;
import crazypants.enderio.EnderIO;

public final class PainterUtil {

    private PainterUtil() {}

    public static boolean isMetadataEquivelent(ItemStack one, ItemStack two) {
        if (one == null || two == null) {
            return false;
        }
        return PainterUtil.getSourceBlock(one) == PainterUtil.getSourceBlock(two)
                && PainterUtil.getSourceBlockMetadata(one) == PainterUtil.getSourceBlockMetadata(two);
    }

    public static Block getSourceBlock(ItemStack item) {
        NBTTagCompound tag = item.getTagCompound();
        return getSourceBlock(tag);
    }

    public static Block getSourceBlock(NBTTagCompound tag) {
        if (tag != null) {
            String blockId = tag.getString(BlockPainter.KEY_SOURCE_BLOCK_ID);
            if (!Strings.isNullOrEmpty(blockId)) {
                Block res = (Block) Block.blockRegistry.getObject(blockId);
                return res;
            }
        }
        return null;
    }

    public static int getSourceBlockMetadata(ItemStack item) {
        NBTTagCompound tag = item.getTagCompound();
        return getSourceBlockMetadata(tag);
    }

    public static int getSourceBlockMetadata(NBTTagCompound tag) {
        if (tag != null) {
            return tag.getInteger(BlockPainter.KEY_SOURCE_BLOCK_META);
        }
        return 0;
    }

    public static String getTooltTipText(ItemStack item) {
        String sourceName = "";
        Block sourceId = PainterUtil.getSourceBlock(item);
        int meta = PainterUtil.getSourceBlockMetadata(item);
        if (sourceId != null) {
            Item itemFromBlock = Item.getItemFromBlock(sourceId);
            if (itemFromBlock != null) {
                ItemStack is = new ItemStack(itemFromBlock, 1, meta);
                sourceName = is.getDisplayName();
            }
        }
        return EnderIO.lang.localize("blockPainter.paintedWith") + " " + sourceName;
    }

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

    public static void setSourceBlock(NBTTagCompound tag, Block source, int meta) {
        if (tag == null) {
            return;
        }
        String name = Block.blockRegistry.getNameForObject(source);
        if (name != null && !name.trim().isEmpty()) {
            meta = normalizeFacadeMetadata(source, meta);
            tag.setString(BlockPainter.KEY_SOURCE_BLOCK_ID, name);
            tag.setInteger(BlockPainter.KEY_SOURCE_BLOCK_META, meta);
        }
    }

    public static ItemStack applyDefaultPaintedState(ItemStack stack) {
        setSourceBlock(stack, Blocks.stone, 0);
        return stack;
    }

    public static int normalizeFacadeMetadata(Block facadeID, int facadeMeta) {
        if (facadeID instanceof BlockRotatedPillar) {
            return facadeMeta & 3;
        }
        return facadeMeta;
    }

    public static int adjustFacadeMetadata(Block facadeID, int facadeMeta, int side) {
        if (facadeID instanceof BlockRotatedPillar) {
            int dir = facadeMeta & 0xC;
            switch (side) {
                case 0:
                case 1:
                    dir = 0;
                    break;
                case 4:
                case 5:
                    dir = 4;
                    break;
                case 2:
                case 3:
                    dir = 8;
                    break;
            }
            facadeMeta = (facadeMeta & 3) | dir;
        }
        return facadeMeta;
    }

    public static int rotateFacadeMetadata(Block facadeID, int facadeMeta, ForgeDirection axis) {
        if (facadeID instanceof BlockRotatedPillar) {
            int dir = facadeMeta & 0xC;
            ForgeDirection orientation;
            switch (dir) {
                case 0:
                    orientation = ForgeDirection.UP;
                    break;
                case 4:
                    orientation = ForgeDirection.EAST;
                    break;
                case 8:
                    orientation = ForgeDirection.SOUTH;
                    break;
                default:
                    return facadeMeta;
            }
            orientation = orientation.getRotation(axis);
            switch (orientation) {
                case UP:
                case DOWN:
                    dir = 0;
                    break;
                case WEST:
                case EAST:
                    dir = 4;
                    break;
                case NORTH:
                case SOUTH:
                    dir = 8;
                    break;
                default:
                    return facadeMeta;
            }
            return (facadeMeta & 3) | dir;
        }
        return facadeMeta;
    }
}
