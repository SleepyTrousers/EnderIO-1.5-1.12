package crazypants.enderio.item;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.config.Config;
import crazypants.enderio.item.PacketMagnetState.SlotType;
import crazypants.enderio.network.PacketHandler;
import crazypants.util.BaublesUtil;
import crazypants.util.MagnetUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import static crazypants.enderio.item.darksteel.DarkSteelItems.itemMagnet;
import static crazypants.util.BotaniaUtil.hasSolegnoliaAround;

public class MagnetController {

  public MagnetController() {
    PacketHandler.INSTANCE.registerMessage(PacketMagnetState.class, PacketMagnetState.class, PacketHandler.nextID(), Side.SERVER);
  }

  
  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.phase != TickEvent.Phase.END) {
      return;
    }
    ActiveMagnet mag = getMagnet(event.player, true);
    if (mag != null && event.player.getHealth() > 0f) {
      doHoover(event.player);
      if(event.side == Side.SERVER && event.player.worldObj.getTotalWorldTime() % 20 == 0) {
        ItemMagnet.drainPerSecondPower(mag.item);
        event.player.inventory.setInventorySlotContents(mag.slot, mag.item);
        event.player.inventory.markDirty();
      }
    }
  }

  static ActiveMagnet getMagnet(EntityPlayer player, boolean activeOnly) {
    ItemStack[] inv = player.inventory.mainInventory;
    int maxSlot = Config.magnetAllowInMainInventory ? 4 * 9 : 9;
    for (int i = 0; i < maxSlot;i++) {
      if (ItemMagnet.isMagnet(inv[i]) && (!activeOnly || (ItemMagnet.isActive(inv[i]) && ItemMagnet.hasPower(inv[i])))) {
        return new ActiveMagnet(inv[i], i);
      }
    }
    if (ItemMagnet.isMagnet(player.inventory.offHandInventory[0])
        && (!activeOnly || (ItemMagnet.isActive(player.inventory.offHandInventory[0]) && ItemMagnet.hasPower(player.inventory.offHandInventory[0])))) {
      return new ActiveMagnet(player.inventory.offHandInventory[0], player.inventory.mainInventory.length + player.inventory.armorInventory.length);
    }
    return null;
  }
  
  private static final double collisionDistanceSq = 1.25 * 1.25;
  private static final double speed = 0.035;
  private static final double speed4 = speed * 4;

  public void doHoover(EntityPlayer player) {
    
    if (blacklist == null) {
      initBlacklist();
    }

    AxisAlignedBB aabb = new AxisAlignedBB(
        player.posX - Config.magnetRange, player.posY - Config.magnetRange, player.posZ - Config.magnetRange,
        player.posX + Config.magnetRange, player.posY + Config.magnetRange, player.posZ + Config.magnetRange);
        
    List<Entity> interestingItems = selectEntitiesWithinAABB(player.worldObj, aabb);

    if (interestingItems != null) {
      for (Entity entity : interestingItems) {
        double x = player.posX - entity.posX;
        double y = player.posY + player.eyeHeight - entity.posY;
        double z = player.posZ - entity.posZ;

        double distance = x * x + y * y + z * z;
        if (distance < collisionDistanceSq) {
          entity.onCollideWithPlayer(player);
        } else {
          double distancespeed = speed4 / distance;
          entity.motionX += x * distancespeed;
          if (y > 0) {
            entity.motionY = 0.12;
          } else {
            entity.motionY += y * speed;
          }
          entity.motionZ += z * distancespeed;
        }
      }
    }
  }

  private static List<Item> blacklist = null;

  private static void initBlacklist() {
    blacklist = new ArrayList<Item>();
    for (String name : Config.magnetBlacklist) {
      String[] parts = name.split(":");
      if (parts.length == 2) {
        Item item = Item.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1]));        
        if (item != null) {
          blacklist.add(item);
        }
      }
    }
  }
  
  private static boolean isBlackListed(EntityItem entity) {
    for (Item blacklisted : blacklist) {
      if (blacklisted == entity.getEntityItem().getItem()) {
        return true;
      }
    }
    return false;
  }

  private List<Entity> selectEntitiesWithinAABB(World world, AxisAlignedBB bb) {
    List<Entity> arraylist = null;

    int itemsRemaining = Config.magnetMaxItems;
    if (itemsRemaining <= 0) {
      itemsRemaining = Integer.MAX_VALUE;
    }

    final int minChunkX = MathHelper.floor_double((bb.minX) / 16.0D);
    final int maxChunkX = MathHelper.floor_double((bb.maxX) / 16.0D);
    final int minChunkZ = MathHelper.floor_double((bb.minZ) / 16.0D);
    final int maxChunkZ = MathHelper.floor_double((bb.maxZ) / 16.0D);
    final int minChunkY = MathHelper.floor_double((bb.minY) / 16.0D);
    final int maxChunkY = MathHelper.floor_double((bb.maxY) / 16.0D);

    for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX) {
      for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ) {
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        final int minChunkYClamped = MathHelper.clamp_int(minChunkY, 0, chunk.getEntityLists().length - 1);
        final int maxChunkYClamped = MathHelper.clamp_int(maxChunkY, 0, chunk.getEntityLists().length - 1);
        for (int chunkY = minChunkYClamped; chunkY <= maxChunkYClamped; ++chunkY) {
          for (Entity entity : chunk.getEntityLists()[chunkY]) {
            if (!entity.isDead) {
              boolean isValidTarget = false;
              if(entity.getEntityBoundingBox().intersectsWith(bb)) {
                if (entity instanceof EntityItem) {
                  isValidTarget = !hasSolegnoliaAround(entity) && !isBlackListed((EntityItem)entity);
                } else if(entity instanceof EntityXPOrb) {
                  isValidTarget = true;
                }
              }
              isValidTarget = isValidTarget && !MagnetUtil.isReserved(entity);
              if (isValidTarget) {
                if (arraylist == null) {
                  arraylist = new ArrayList<Entity>(Config.magnetMaxItems > 0 ? Config.magnetMaxItems : 20);
                }
                arraylist.add(entity);
                if (itemsRemaining-- <= 0) {
                  return arraylist;
                }
              }
            }
          }
        }
      }
    }

    return arraylist;
  }

  static class ActiveMagnet {
    ItemStack item;
    int slot;
    
    ActiveMagnet(ItemStack item, int slot) {    
      this.item = item;
      this.slot = slot;
    }        
  }

  public static void setMagnetActive(EntityPlayerMP player, SlotType type, int slot, boolean isActive) {
    ItemStack stack = null;
    IInventory baubles = null;
    int dropOff = -1;
    switch (type) {
    case INVENTORY:
      stack = player.inventory.getStackInSlot(slot);
      break;
    case ARMOR:
      return;
    case BAUBLES:
      baubles = BaublesUtil.instance().getBaubles(player);
      if (baubles != null) {
        stack = baubles.getStackInSlot(slot);
      }
      break;
    }
    if (stack == null || stack.getItem() == null || stack.getItem() != itemMagnet || ItemMagnet.isActive(stack) == isActive) {
      return;
    }
    if (!Config.magnetAllowDeactivatedInBaublesSlot && type == SlotType.BAUBLES && !isActive) {
      ItemStack[] inv = player.inventory.mainInventory;
      for (int i = 0; i < inv.length && dropOff < 0; i++) {
        if (inv[i] == null) {
          dropOff = i;
        }
      }
      if (dropOff < 0) {
        return;
      }
    }
    ItemMagnet.setActive(stack, isActive);
    switch (type) {
    case INVENTORY:
      player.inventory.setInventorySlotContents(slot, stack);
      player.inventory.markDirty();
      break;
    case ARMOR:
      return;
    case BAUBLES:
      if (baubles != null) {
        if (dropOff < 0) {
          baubles.setInventorySlotContents(slot, stack);
        } else {
          baubles.setInventorySlotContents(slot, null);
          player.inventory.setInventorySlotContents(dropOff, stack);
        }
        player.inventory.markDirty();
      }
      break;
    }
  }
}
