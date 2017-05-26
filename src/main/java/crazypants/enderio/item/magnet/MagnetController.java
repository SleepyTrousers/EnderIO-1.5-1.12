package crazypants.enderio.item.magnet;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.MagnetUtil;

import crazypants.enderio.config.Config;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.integration.baubles.BaublesUtil;
import crazypants.enderio.item.magnet.PacketMagnetState.SlotType;
import crazypants.enderio.network.PacketHandler;
import crazypants.util.Prep;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import static crazypants.enderio.integration.botania.BotaniaUtil.hasSolegnoliaAround;

public class MagnetController {

  public MagnetController() {
    PacketHandler.INSTANCE.registerMessage(PacketMagnetState.Handler.class, PacketMagnetState.class, PacketHandler.nextID(), Side.SERVER);
  }

  @SubscribeEvent
  public void onPlayerTick(@Nonnull TickEvent.PlayerTickEvent event) {
    if (event.phase != TickEvent.Phase.END || event.player.getHealth() <= 0f || event.player.isSpectator()) {
      return;
    }
    ActiveMagnet mag = getMagnet(event.player, true);
    if (mag != null) {
      doHoover(event.player);
      if (event.side == Side.SERVER && event.player.world.getTotalWorldTime() % 20 == 0) {
        ((ItemMagnet) mag.item.getItem()).drainPerSecondPower(mag.item);
        event.player.inventory.setInventorySlotContents(mag.slot, mag.item);
        event.player.inventory.markDirty();
      }
    }
  }

  public static ActiveMagnet getMagnet(EntityPlayer player, boolean activeOnly) {
    NonNullList<ItemStack> inv = player.inventory.mainInventory;
    int maxSlot = Config.magnetAllowInMainInventory ? inv.size() : InventoryPlayer.getHotbarSize();
    for (int i = 0; i < maxSlot; i++) {
      final ItemStack item = inv.get(i);
      if (ItemMagnet.isMagnet(item) && (!activeOnly || (ItemMagnet.isActive(item) && ItemMagnet.hasPower(item)))) {
        return new ActiveMagnet(item, i);
      }
    }
    final ItemStack item = player.inventory.offHandInventory.get(0);
    if (ItemMagnet.isMagnet(item) && (!activeOnly || (ItemMagnet.isActive(item) && ItemMagnet.hasPower(item)))) {
      return new ActiveMagnet(item, player.inventory.mainInventory.size() + player.inventory.armorInventory.size());
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

    AxisAlignedBB aabb = new AxisAlignedBB(player.posX - Config.magnetRange, player.posY - Config.magnetRange, player.posZ - Config.magnetRange,
        player.posX + Config.magnetRange, player.posY + Config.magnetRange, player.posZ + Config.magnetRange);

    List<Entity> interestingItems = selectEntitiesWithinAABB(player.world, aabb);

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
      if (name != null && !name.isEmpty()) {
        Item item = Item.REGISTRY.getObject(new ResourceLocation(name));
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

    final int minChunkX = MathHelper.floor((bb.minX) / 16.0D);
    final int maxChunkX = MathHelper.floor((bb.maxX) / 16.0D);
    final int minChunkZ = MathHelper.floor((bb.minZ) / 16.0D);
    final int maxChunkZ = MathHelper.floor((bb.maxZ) / 16.0D);
    final int minChunkY = MathHelper.floor((bb.minY) / 16.0D);
    final int maxChunkY = MathHelper.floor((bb.maxY) / 16.0D);

    for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX) {
      for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ) {
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        final ClassInheritanceMultiMap<Entity>[] entityLists = chunk.getEntityLists();
        final int minChunkYClamped = MathHelper.clamp(minChunkY, 0, entityLists.length - 1);
        final int maxChunkYClamped = MathHelper.clamp(maxChunkY, 0, entityLists.length - 1);
        for (int chunkY = minChunkYClamped; chunkY <= maxChunkYClamped; ++chunkY) {
          for (Entity entity : entityLists[chunkY]) {
            if (!entity.isDead) {
              boolean isValidTarget = false;
              if (entity.getEntityBoundingBox().intersectsWith(bb)) {
                if (entity instanceof EntityItem) {
                  isValidTarget = !hasSolegnoliaAround(entity) && !isBlackListed((EntityItem) entity);
                } else if (entity instanceof EntityXPOrb) {
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

  public static class ActiveMagnet {
    final @Nonnull ItemStack item;
    final int slot;

    ActiveMagnet(@Nonnull ItemStack item, int slot) {
      this.item = item;
      this.slot = slot;
    }

    public @Nonnull ItemStack getItem() {
      return item;
    }

    public int getSlot() {
      return slot;
    }
  }

  public static void setMagnetActive(EntityPlayerMP player, SlotType type, int slot, boolean isActive) {
    ItemStack stack = Prep.getEmpty();
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
        if (Prep.isValid(stack)) {
          // mustn't change the item that is in the slot or Baubles will ignore the change
          stack = stack.copy();
        }
      }
      break;
    }
    if (stack.getItem() != ModObject.itemMagnet.getItem() || ItemMagnet.isActive(stack) == isActive) {
      return;
    }
    if (!Config.magnetAllowDeactivatedInBaublesSlot && type == SlotType.BAUBLES && !isActive) {
      dropOff = player.inventory.getFirstEmptyStack();
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
          baubles.setInventorySlotContents(slot, Prep.getEmpty());
          player.inventory.setInventorySlotContents(dropOff, stack);
        }
        player.inventory.markDirty();
      }
      break;
    }
  }
}
