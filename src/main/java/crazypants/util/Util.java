package crazypants.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

import crazypants.enderio.Log;
import crazypants.vecmath.Vector3d;

public class Util {

  public static Block getBlockFromItemId(ItemStack itemId) {
    Item item = itemId.getItem();
    if(item instanceof ItemBlock) {
      return ((ItemBlock) item).field_150939_a;
    }
    return null;
  }

  public static ItemStack consumeItem(ItemStack stack) {
    if(stack.getItem() instanceof ItemPotion) {
      if(stack.stackSize == 1) {
        return new ItemStack(Items.glass_bottle);
      } else {
        stack.splitStack(1);
        return stack;
      }
    }
    if(stack.stackSize == 1) {
      if(stack.getItem().hasContainerItem(stack)) {
        return stack.getItem().getContainerItem(stack);
      } else {
        return null;
      }
    } else {
      stack.splitStack(1);
      return stack;
    }
  }

  public static void giveExperience(EntityPlayer thePlayer, float experience) {
    if(experience < 1.0F) {
      int rndRound = MathHelper.floor_float(experience);
      if(rndRound < MathHelper.ceiling_float_int(experience) && (float) Math.random() < experience) {
        ++rndRound;
      }
      experience = rndRound;
    }
    int intExp = Math.round(experience);
    while (intExp > 0) {
      int j = EntityXPOrb.getXPSplit(intExp);
      intExp -= j;
      thePlayer.worldObj.spawnEntityInWorld(new EntityXPOrb(thePlayer.worldObj, thePlayer.posX, thePlayer.posY + 0.5D, thePlayer.posZ + 0.5D, j));
    }
  }

  // derived from ItemBlock.onItemUse
  public static BlockCoord canPlaceItem(ItemStack itemUsed, Block blockIdToBePlaced, EntityPlayer player, World world, int x, int y, int z, int side) {

    if(blockIdToBePlaced == null) {
      return null;
    }

    Block block = world.getBlock(x, y, z);

    if(block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1) {
      side = 1;
    } else if(block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush
        && (block == null || !block.isReplaceable(world, x, y, z))) {

      if(side == 0) {
        --y;
      } else if(side == 1) {
        ++y;
      } else if(side == 2) {
        --z;
      } else if(side == 3) {
        ++z;
      } else if(side == 4) {
        --x;
      } else if(side == 5) {
        ++x;
      }
    }

    if(itemUsed.stackSize == 0) {
      return null;
    } else if(!player.canPlayerEdit(x, y, z, side, itemUsed)) {
      return null;
    } else if(y == 255 && blockIdToBePlaced.getMaterial().isSolid()) {
      return null;
    } else if(world.canPlaceEntityOnSide(blockIdToBePlaced, x, y, z, false, side, player, itemUsed)) {
      return new BlockCoord(x, y, z);
    }
    return null;

  }

  public static EntityItem createDrop(World world, ItemStack stack, double x, double y, double z, boolean doRandomSpread) {
    if(stack == null || stack.stackSize <= 0) {
      return null;
    }
    if(doRandomSpread) {
      float f1 = 0.7F;
      double d = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      double d1 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      double d2 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      EntityItem entityitem = new EntityItem(world, x + d, y + d1, z + d2, stack);
      entityitem.delayBeforeCanPickup = 10;
      return entityitem;
    } else {
      EntityItem entityitem = new EntityItem(world, x, y, z, stack);
      entityitem.motionX = 0;
      entityitem.motionY = 0;
      entityitem.motionZ = 0;
      entityitem.delayBeforeCanPickup = 0;
      return entityitem;
    }
  }

  public static void dropItems(World world, ItemStack stack, double x, double y, double z, boolean doRandomSpread) {
    if(stack == null || stack.stackSize <= 0) {
      return;
    }

    if(doRandomSpread) {
      float f1 = 0.7F;
      double d = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      double d1 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      double d2 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      EntityItem entityitem = new EntityItem(world, x + d, y + d1, z + d2, stack);
      entityitem.delayBeforeCanPickup = 10;
      world.spawnEntityInWorld(entityitem);
    } else {
      EntityItem entityitem = new EntityItem(world, x, y, z, stack);
      entityitem.motionX = 0;
      entityitem.motionY = 0;
      entityitem.motionZ = 0;
      entityitem.delayBeforeCanPickup = 0;
      world.spawnEntityInWorld(entityitem);
    }

  }

  public static void dropItems(World world, ItemStack stack, int x, int y, int z, boolean doRandomSpread) {
    if(stack == null || stack.stackSize <= 0) {
      return;
    }

    if(doRandomSpread) {
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

  public static void dropItems(World world, ItemStack[] inventory, int x, int y, int z, boolean doRandomSpread) {
    if(inventory == null) {
      return;
    }
    for (ItemStack stack : inventory) {
      if(stack != null && stack.stackSize > 0) {
        dropItems(world, stack.copy(), x, y, z, doRandomSpread);
      }
    }
  }

  public static void dropItems(World world, IInventory inventory, int x, int y, int z, boolean doRandomSpread) {
    for (int l = 0; l < inventory.getSizeInventory(); ++l) {
      ItemStack items = inventory.getStackInSlot(l);

      if(items != null && items.stackSize > 0) {
        dropItems(world, inventory.getStackInSlot(l).copy(), x, y, z, doRandomSpread);
      }
    }
  }

  public static boolean dumpModObjects(File file) {

    StringBuilder sb = new StringBuilder();
    for (Object key : Block.blockRegistry.getKeys()) {
      if(key != null) {
        sb.append(key.toString());
        sb.append("\n");
      }
    }
    for (Object key : Item.itemRegistry.getKeys()) {
      if(key != null) {
        sb.append(key.toString());
        sb.append("\n");
      }
    }

    try {
      Files.write(sb, file, Charsets.UTF_8);
      return true;
    } catch (IOException e) {
      Log.warn("Error dumping ore dictionary entries: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  public static boolean dumpOreNames(File file) {

    try {
      String[] oreNames = OreDictionary.getOreNames();
      Files.write(Joiner.on("\n").join(oreNames), file, Charsets.UTF_8);
      return true;
    } catch (IOException e) {
      Log.warn("Error dumping ore dictionary entries: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  public static ItemStack decrStackSize(IInventory inventory, int slot, int size) {
    ItemStack item = inventory.getStackInSlot(slot);
    if(item != null) {
      if(item.stackSize <= size) {
        ItemStack result = item;
        inventory.setInventorySlotContents(slot, null);
        inventory.markDirty();
        return result;
      }
      ItemStack split = item.splitStack(size);
      if(item.stackSize == 0) {
        inventory.setInventorySlotContents(slot, null);
      }
      inventory.markDirty();
      return split;
    }
    return null;
  }

  public static Vec3 getEyePosition(EntityPlayer player) {
    Vec3 v = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
    if(player.worldObj.isRemote) {
      //take into account any eye changes done by mods.
      v.yCoord += player.getEyeHeight() - player.getDefaultEyeHeight();
    } else {
      v.yCoord += player.getEyeHeight();
      if(player instanceof EntityPlayerMP && player.isSneaking()) {
        v.yCoord -= 0.08;
      }
    }
    return v;
  }

  public static Vector3d getEyePositionEio(EntityPlayer player) {
    Vector3d res = new Vector3d(player.posX, player.posY, player.posZ);
    if(player.worldObj.isRemote) {
      //take into account any eye changes done by mods.
      res.y += player.getEyeHeight() - player.getDefaultEyeHeight();
    } else {
      res.y += player.getEyeHeight();
      if(player instanceof EntityPlayerMP && player.isSneaking()) {
        res.y -= 0.08;
      }
    }
    return res;
  }

  public static Vector3d getLookVecEio(EntityPlayer player) {
    Vec3 lv = player.getLookVec();
    return new Vector3d(lv.xCoord, lv.yCoord, lv.zCoord);
  }

  public static boolean isEquipped(EntityPlayer player, Class<? extends Item> class1) {
    if(player == null || player.inventory == null || player.inventory.getCurrentItem() == null) {
      return false;
    }
    //player.inventory.getCurrentItem().getClass().getItem().isAssignableFrom(class1)
    return class1.isAssignableFrom(player.inventory.getCurrentItem().getItem().getClass());
  }

  public static boolean isType(ItemStack stack, Class<?> class1) {
    if(stack == null || class1 == null) {
      return false;
    }
    return class1.isAssignableFrom(stack.getItem().getClass());
  }

  //Code adapted from World.func_147447_a (rayTraceBlocks) to return all collided blocks
  public static List<MovingObjectPosition> raytraceAll(World world, Vec3 startVec, Vec3 endVec, boolean includeLiquids) {

    List<MovingObjectPosition> result = new ArrayList<MovingObjectPosition>();
    boolean p_147447_4_ = false;
    boolean p_147447_5_ = false;

    if(!Double.isNaN(startVec.xCoord) && !Double.isNaN(startVec.yCoord) && !Double.isNaN(startVec.zCoord)) {
      if(!Double.isNaN(endVec.xCoord) && !Double.isNaN(endVec.yCoord) && !Double.isNaN(endVec.zCoord)) {
        
        int i = MathHelper.floor_double(endVec.xCoord);
        int j = MathHelper.floor_double(endVec.yCoord);
        int k = MathHelper.floor_double(endVec.zCoord);
        int l = MathHelper.floor_double(startVec.xCoord);
        int i1 = MathHelper.floor_double(startVec.yCoord);
        int j1 = MathHelper.floor_double(startVec.zCoord);
        Block block = world.getBlock(l, i1, j1);
        int k1 = world.getBlockMetadata(l, i1, j1);

        if((!p_147447_4_ || block.getCollisionBoundingBoxFromPool(world, l, i1, j1) != null) && block.canCollideCheck(k1, includeLiquids)) {
          MovingObjectPosition movingobjectposition = block.collisionRayTrace(world, l, i1, j1, startVec, endVec);
          if(movingobjectposition != null) {
            result.add(movingobjectposition);
          }
        }

        MovingObjectPosition movingobjectposition2 = null;
        k1 = 200;

        while (k1-- >= 0) {
          if(Double.isNaN(startVec.xCoord) || Double.isNaN(startVec.yCoord) || Double.isNaN(startVec.zCoord)) {
            return null;
          }

          if(l == i && i1 == j && j1 == k) {
            if(p_147447_5_) {
              result.add(movingobjectposition2);
            } else {
              return result;
            }
          }

          boolean flag6 = true;
          boolean flag3 = true;
          boolean flag4 = true;
          double d0 = 999.0D;
          double d1 = 999.0D;
          double d2 = 999.0D;

          if(i > l) {
            d0 = l + 1.0D;
          } else if(i < l) {
            d0 = l + 0.0D;
          } else {
            flag6 = false;
          }

          if(j > i1) {
            d1 = i1 + 1.0D;
          } else if(j < i1) {
            d1 = i1 + 0.0D;
          } else {
            flag3 = false;
          }

          if(k > j1) {
            d2 = j1 + 1.0D;
          } else if(k < j1) {
            d2 = j1 + 0.0D;
          } else {
            flag4 = false;
          }

          double d3 = 999.0D;
          double d4 = 999.0D;
          double d5 = 999.0D;
          double d6 = endVec.xCoord - startVec.xCoord;
          double d7 = endVec.yCoord - startVec.yCoord;
          double d8 = endVec.zCoord - startVec.zCoord;

          if(flag6) {
            d3 = (d0 - startVec.xCoord) / d6;
          }
          if(flag3) {
            d4 = (d1 - startVec.yCoord) / d7;
          }
          if(flag4) {
            d5 = (d2 - startVec.zCoord) / d8;
          }

          boolean flag5 = false;
          byte b0;

          if(d3 < d4 && d3 < d5) {
            if(i > l) {
              b0 = 4;
            } else {
              b0 = 5;
            }

            startVec.xCoord = d0;
            startVec.yCoord += d7 * d3;
            startVec.zCoord += d8 * d3;
          } else if(d4 < d5) {
            if(j > i1) {
              b0 = 0;
            } else {
              b0 = 1;
            }

            startVec.xCoord += d6 * d4;
            startVec.yCoord = d1;
            startVec.zCoord += d8 * d4;
          } else {
            if(k > j1) {
              b0 = 2;
            } else {
              b0 = 3;
            }

            startVec.xCoord += d6 * d5;
            startVec.yCoord += d7 * d5;
            startVec.zCoord = d2;
          }

          Vec3 vec32 = Vec3.createVectorHelper(startVec.xCoord, startVec.yCoord, startVec.zCoord);
          l = (int) (vec32.xCoord = MathHelper.floor_double(startVec.xCoord));
          if(b0 == 5) {
            --l;
            ++vec32.xCoord;
          }

          i1 = (int) (vec32.yCoord = MathHelper.floor_double(startVec.yCoord));

          if(b0 == 1) {
            --i1;
            ++vec32.yCoord;
          }

          j1 = (int) (vec32.zCoord = MathHelper.floor_double(startVec.zCoord));

          if(b0 == 3) {
            --j1;
            ++vec32.zCoord;
          }

          Block block1 = world.getBlock(l, i1, j1);
          int l1 = world.getBlockMetadata(l, i1, j1);

          if(!p_147447_4_ || block1.getCollisionBoundingBoxFromPool(world, l, i1, j1) != null) {
            if(block1.canCollideCheck(l1, includeLiquids)) {
              MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(world, l, i1, j1, startVec, endVec);
              if(movingobjectposition1 != null) {
                result.add(movingobjectposition1);
              }
            } else {
              movingobjectposition2 = new MovingObjectPosition(l, i1, j1, b0, startVec, false);
            }
          }
        }
        if(p_147447_5_ ) {
          result.add(movingobjectposition2);
        } else {
          return result;
        }
      } else {
        return result;
      }
    } else {
      return result;
    }
    return result;
  }
  
  // copied from WAILA source to avoid API dependency
  public static String WailaStyle     = "\u00A4";
  public static String WailaIcon      = "\u00A5";
  public static String TAB         = WailaStyle + WailaStyle +"a";
  public static String ALIGNRIGHT  = WailaStyle + WailaStyle +"b";
  public static String ALIGNCENTER = WailaStyle + WailaStyle +"c";  
  public static String HEART       = WailaStyle + WailaIcon  +"a";
  public static String HHEART      = WailaStyle + WailaIcon  +"b";
  public static String EHEART      = WailaStyle + WailaIcon  +"c";

}
