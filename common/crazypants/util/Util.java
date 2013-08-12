package crazypants.util;

import net.minecraft.block.Block;
import net.minecraft.item.*;

public class Util {

  public static Block getBlock(int blockId) {
    if (blockId <= 0) {
      return null;
    }
    if (blockId > Block.blocksList.length) {
      return null;
    }
    return Block.blocksList[blockId];
  }

  public static Item getItem(int itemId) {
    if (itemId <= 0) {
      return null;
    }
    if (itemId > Item.itemsList.length) {
      return null;
    }
    return Item.itemsList[itemId];
  }

  public static Block getBlockFromItemId(int itemId) {
    Item item = getItem(itemId);
    if (item instanceof ItemBlock) {
      return getBlock(((ItemBlock) item).getBlockID());
    }
    return null;
  }

  public static ItemStack consumeItem(ItemStack stack) {
    if (stack.stackSize == 1) {
      if (stack.getItem().hasContainerItem()) {
        return stack.getItem().getContainerItemStack(stack);
      } else {
        return null;
      }
    } else {
      stack.splitStack(1);
      return stack;
    }
  }

}
