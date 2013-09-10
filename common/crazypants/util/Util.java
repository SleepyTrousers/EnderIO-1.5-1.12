package crazypants.util;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
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

  // derived from ItemBlock.onItemUse
  public static BlockCoord canPlaceItem(ItemStack itemUsed, int blockIdToBePlaced, EntityPlayer player, World world, int x, int y, int z, int side) {
    int i1 = world.getBlockId(x, y, z);

    if (i1 == Block.snow.blockID && (world.getBlockMetadata(x, y, z) & 7) < 1) {
      side = 1;
    } else if (i1 != Block.vine.blockID && i1 != Block.tallGrass.blockID && i1 != Block.deadBush.blockID
        && (Block.blocksList[i1] == null || !Block.blocksList[i1].isBlockReplaceable(world, x, y, z))) {

      if (side == 0) {
        --y;
      } else if (side == 1) {
        ++y;
      } else if (side == 2) {
        --z;
      } else if (side == 3) {
        ++z;
      } else if (side == 4) {
        --x;
      } else if (side == 5) {
        ++x;
      }
    }

    if (itemUsed.stackSize == 0) {
      return null;
    } else if (!player.canPlayerEdit(x, y, z, side, itemUsed)) {
      return null;
    } else if (y == 255 && Block.blocksList[blockIdToBePlaced].blockMaterial.isSolid()) {
      return null;
    } else if (world.canPlaceEntityOnSide(blockIdToBePlaced, x, y, z, false, side, player, itemUsed)) {
      return new BlockCoord(x, y, z);
    }
    return null;
  }

  public static void dropItems(World world, ItemStack stack, int x, int y, int z, boolean doRandomSpread) {
    if (stack.stackSize <= 0) {
      return;
    }

    if (doRandomSpread) {
      float f1 = 0.7F;
      double d = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      double d1 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      double d2 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      EntityItem entityitem = new EntityItem(world, x + d, y + d1, z + d2, stack);
      entityitem.delayBeforeCanPickup = 10;
      world.spawnEntityInWorld(entityitem);
    } else {
      EntityItem entityitem = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack);
      entityitem.motionX = 0;
      entityitem.motionY = 0;
      entityitem.motionZ = 0;
      entityitem.delayBeforeCanPickup = 0;
      world.spawnEntityInWorld(entityitem);
    }

  }

  public static void dropItems(World world, IInventory inventory, int x, int y, int z, boolean doRandomSpread) {
    for (int l = 0; l < inventory.getSizeInventory(); ++l) {
      ItemStack items = inventory.getStackInSlot(l);

      if (items != null && items.stackSize > 0) {
        dropItems(world, inventory.getStackInSlot(l).copy(), x, y, z, doRandomSpread);
      }
    }
  }

}
