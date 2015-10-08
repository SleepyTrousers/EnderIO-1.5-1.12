package crazypants.enderio.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.PacketMagnetState.SlotType;
import crazypants.enderio.network.PacketHandler;
import crazypants.util.BaublesUtil;
import static crazypants.enderio.item.darksteel.DarkSteelItems.itemMagnet;
import static crazypants.util.BotaniaUtil.hasSolegnoliaAround;

public class MagnetController implements IEntitySelector {

  public MagnetController() {
    PacketHandler.INSTANCE.registerMessage(PacketMagnetState.class, PacketMagnetState.class, PacketHandler.nextID(), Side.SERVER);
  }

  
  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.phase != TickEvent.Phase.END) {
      return;
    }
    ActiveMagnet mag = getActiveMagnet(event.player);
    if (mag != null && event.player.getHealth() > 0f) {
      doHoover(event.player);
      if(event.side == Side.SERVER && event.player.worldObj.getTotalWorldTime() % 20 == 0) {
        ItemMagnet.drainPerSecondPower(mag.item);
        event.player.inventory.setInventorySlotContents(mag.slot, mag.item);
        event.player.inventory.markDirty();
      }
    }
  }

  private ActiveMagnet getActiveMagnet(EntityPlayer player) {
    ItemStack[] inv = player.inventory.mainInventory;
    int maxSlot = Config.magnetAllowInMainInventory ? 4 * 9 : 9;
    for (int i = 0; i < maxSlot;i++) {
      if(ItemMagnet.isActive(inv[i]) && ItemMagnet.hasPower(inv[i])) {
        return new ActiveMagnet(inv[i], i);
      }
    }
    return null;
  }
  
  public void doHoover(EntityPlayer player) {
    
    if (blacklist == null) {
      initBlacklist();
    }

    AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
        player.posX - Config.magnetRange, player.posY - Config.magnetRange, player.posZ - Config.magnetRange,
        player.posX + Config.magnetRange, player.posY + Config.magnetRange, player.posZ + Config.magnetRange);
        
    List<Entity> interestingItems = player.worldObj.selectEntitiesWithinAABB(EntityItem.class, aabb, this);        
    List<Entity> xp = player.worldObj.selectEntitiesWithinAABB(EntityXPOrb.class, aabb, this);
    if(!xp.isEmpty()) {
      interestingItems.addAll(xp);
    }

    for (Entity entity : interestingItems) {
      double x = player.posX + 0.5D - entity.posX;
      double y = player.posY + 1D - entity.posY;
      double z = player.posZ + 0.5D - entity.posZ;

      double distance = Math.sqrt(x * x + y * y + z * z);
      if(distance < 1.25) {
        entity.onCollideWithPlayer(player);
      } else {
        double speed = 0.035;
        entity.motionX += x / distance * speed;
        entity.motionY += y * speed;
        if(y > 0) {
          entity.motionY = 0.12;
        }
        entity.motionZ += z / distance * speed;
      }

    }
  }

  private static List<Item> blacklist = null;

  private static void initBlacklist() {
    blacklist = new ArrayList<Item>();
    for (String name : Config.magnetBlacklist) {
      String[] parts = name.split(":");
      if (parts.length == 2) {
        Item item = GameRegistry.findItem(parts[0], parts[1]);
        if (item != null) {
          blacklist.add(item);
        }
      }
    }
  }

  @Override
  public boolean isEntityApplicable(Entity var1) {
    if (var1.isDead) {
      return false;
    }
    if (var1 instanceof EntityItem) {
      if (!blacklist.isEmpty()) {
        Item item = ((EntityItem) var1).getEntityItem().getItem();
        for (Item blacklisted : blacklist) {
          if (blacklisted == item) {
            return false;
          }
        }
      }
      return !hasSolegnoliaAround(var1);
    }
    return true;
  }
  
  private static class ActiveMagnet {
    ItemStack item;
    int slot;
    
    ActiveMagnet(ItemStack item, int slot) {    
      this.item = item;
      this.slot = slot;
    }        
  }

  public static void setMagnetActive(EntityPlayerMP player, SlotType type, int slot, boolean targetState) {
    ItemStack stack = null;
    switch (type) {
    case INVENTORY:
      stack = player.inventory.getStackInSlot(slot);
      if (stack == null || stack.getItem() == null || stack.getItem() != itemMagnet || ItemMagnet.isActive(stack) == targetState) {
        return;
      }
      ItemMagnet.setActive(stack, targetState);
      player.inventory.setInventorySlotContents(slot, stack);
      player.inventory.markDirty();
      return;
    case ARMOR:
      return;
    case BAUBLES:
      IInventory baubles = BaublesUtil.instance().getBaubles(player);
      if (baubles == null) {
        return;
      }
      stack = baubles.getStackInSlot(slot);
      if (stack == null || stack.getItem() == null || stack.getItem() != itemMagnet || ItemMagnet.isActive(stack) == targetState) {
        return;
      }
      if (!Config.magnetAllowDeactivatedInBaublesSlot && !targetState) {
        ItemStack[] inv = player.inventory.mainInventory;
        for (int i = 0; i < inv.length; i++) {
          if (inv[i] == null) {
            ItemMagnet.setActive(stack, targetState);
            baubles.setInventorySlotContents(slot, null);
            player.inventory.setInventorySlotContents(i, stack);
            player.inventory.markDirty();
            return;
          }
        }
      } else {
        ItemMagnet.setActive(stack, targetState);
        baubles.setInventorySlotContents(slot, stack);
        return;
      }
    }
  }

}
