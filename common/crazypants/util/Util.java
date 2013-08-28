package crazypants.util;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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
  
  public static void dropItems(World world, ItemStack stack, int x, int y, int z) {
    if (stack.stackSize <= 0) {
      return;
    }

    float f1 = 0.7F;
    double d = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
    double d1 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
    double d2 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
    EntityItem entityitem = new EntityItem(world, x + d, y + d1, z + d2, stack);
    entityitem.delayBeforeCanPickup = 10;

    world.spawnEntityInWorld(entityitem);
  }

  public static void dropItems(World world, IInventory inventory, int x, int y, int z) {
    for (int l = 0; l < inventory.getSizeInventory(); ++l) {
      ItemStack items = inventory.getStackInSlot(l);

      if (items != null && items.stackSize > 0) {
        dropItems(world, inventory.getStackInSlot(l).copy(), x, y, z);
      }
    }
  }

}
