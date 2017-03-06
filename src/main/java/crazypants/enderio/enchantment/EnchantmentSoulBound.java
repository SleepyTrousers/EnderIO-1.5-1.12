package crazypants.enderio.enchantment;

import java.util.ListIterator;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.enderio.core.api.common.enchant.IAdvancedEnchant;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.util.BaublesUtil;
import crazypants.util.GalacticraftUtil;

public class EnchantmentSoulBound extends Enchantment implements IAdvancedEnchant {

  public static EnchantmentSoulBound create(int id) {
    EnchantmentSoulBound res = new EnchantmentSoulBound(id);
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  private final int id;

  private EnchantmentSoulBound(int id) {
    super(id, Config.enchantmentSoulBoundWeight, EnumEnchantmentType.all);
    this.id = id;
    setName("enderio.soulBound");
  }

  @Override
  public int getMaxEnchantability(int level) {
    return super.getMaxEnchantability(level) + 30;
  }

  @Override
  public int getMinEnchantability(int level) {
    return super.getMinEnchantability(level);
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

  /*
   * This is called the moment the player dies and drops his stuff.
   * 
   * We go early, so we can get our items before other mods put them into some
   * grave. Also remove them from the list so they won't get duped. If the
   * inventory overflows, e.g. because everything there and the armor is
   * soulbound, let the remainder be dropped/graved.
   */
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onPlayerDeath(PlayerDropsEvent evt) {
    if (evt.entityPlayer == null || evt.entityPlayer instanceof FakePlayer || evt.isCanceled()) {
      return;
    }
    if(evt.entityPlayer.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
      return;
    }

    ListIterator<EntityItem> iter = evt.drops.listIterator();
    while (iter.hasNext()) {
      EntityItem ei = iter.next();
      ItemStack item = ei.getEntityItem();
      if(isSoulBound(item)) {
        if (addToPlayerInventory(evt.entityPlayer, item)) {
          iter.remove();
        }
      }
    }

    // Note: Baubles will also add its items to evt.drops, but later. We cannot
    // wait for that because gravestone mods also listen to this event. So we have
    // to fetch Baubles items ourselves here.
    // For the same reason we cannot put the items into Baubles slots.
    /*IInventory baubles = BaublesUtil.instance().getBaubles(evt.entityPlayer);
    if (baubles != null) {
      for (int i = 0; i < baubles.getSizeInventory(); i++) {
        ItemStack item = baubles.getStackInSlot(i);
        if(isSoulBound(item)) {
          if (addToPlayerInventory(evt.entityPlayer, item)) {
            baubles.setInventorySlotContents(i, null);
          }
        }
      }
    }*/

    // Galacticraft. Again we are too early for those items. We just dump the
    // stuff into the normal inventory to not have to keep a separate list.
    if (evt.entityPlayer instanceof EntityPlayerMP) {
      IInventory galacticraft = GalacticraftUtil.getGCInventoryForPlayer((EntityPlayerMP) evt.entityPlayer);
      if (galacticraft != null) {
        for (int i = 0; i < galacticraft.getSizeInventory(); i++) {
          ItemStack item = galacticraft.getStackInSlot(i);
          if (isSoulBound(item)) {
            if (addToPlayerInventory(evt.entityPlayer, item)) {
              galacticraft.setInventorySlotContents(i, null);
            }
          }
        }
      }
    }

  }

  /*
   * This is called when the user presses the "respawn" button. The original
   * inventory would be empty, but onPlayerDeath() above placed items in it.
   * 
   * Note: Without other death-modifying mods, the content of the old inventory
   * would always fit into the new one (both being empty but for soulbound items
   * in the old one) and the old one would be discarded just after this method.
   * But better play it safe and assume that an overflow is possible and that
   * another mod may move stuff out of the old inventory, too.
   */
  @SubscribeEvent
  public void onPlayerClone(PlayerEvent.Clone evt) {
    if (!evt.wasDeath || evt.isCanceled()) {
      return;
    }
    if(evt.original == null || evt.entityPlayer == null || evt.entityPlayer instanceof FakePlayer) {
      return;
    }
    if(evt.entityPlayer.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
      return;
    }
    for (int i = 0; i < evt.original.inventory.mainInventory.length; i++) {
      ItemStack item = evt.original.inventory.mainInventory[i];
      if(isSoulBound(item)) {
        if (addToPlayerInventory(evt.entityPlayer, item)) {
          evt.original.inventory.mainInventory[i] = null;
        }
      }
    }
    for (int i = 0; i < evt.original.inventory.armorInventory.length; i++) {
      ItemStack item = evt.original.inventory.armorInventory[i];
      if(isSoulBound(item)) {
        if (addToPlayerInventory(evt.entityPlayer, item)) {
          evt.original.inventory.armorInventory[i] = null;
        }
      }
    }
  }

  private boolean isSoulBound(ItemStack item) {
    return EnchantmentHelper.getEnchantmentLevel(id, item) > 0;
  }

  private boolean addToPlayerInventory(EntityPlayer entityPlayer, ItemStack item) {
    if(item == null || entityPlayer == null) {
      return false;
    }
    if(item.getItem() instanceof ItemArmor) {
      ItemArmor arm = (ItemArmor) item.getItem();
      int index = 3 - arm.armorType;
      if(entityPlayer.inventory.armorItemInSlot(index) == null) {
        entityPlayer.inventory.armorInventory[index] = item;
        return true;
      }
    }

    InventoryPlayer inv = entityPlayer.inventory;
    for (int i = 0; i < inv.mainInventory.length; i++) {
      if(inv.mainInventory[i] == null) {
        inv.mainInventory[i] = item.copy();
        return true;
      }
    }

    return false;
  }

  @Override
  public String[] getTooltipDetails(ItemStack stack) {
    return new String[] { EnderIO.lang.localizeExact("description.enchantment.enderio.soulBound") };
  }
}
