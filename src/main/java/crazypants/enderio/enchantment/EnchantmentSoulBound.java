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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.enderio.core.api.common.enchant.IAdvancedEnchant;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.config.Config;
import crazypants.util.BaublesUtil;
import crazypants.util.GalacticraftUtil;

public class EnchantmentSoulBound extends Enchantment implements IAdvancedEnchant {

  public static EnchantmentSoulBound create(int id) {
    EnchantmentSoulBound res = new EnchantmentSoulBound(id);
    Enchantment.addToBookList(res);
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  private final int id;

  private EnchantmentSoulBound(int id) {
    super(id, new ResourceLocation(EnderIO.DOMAIN, "soulBound"), Config.enchantmentSoulBoundWeight, EnumEnchantmentType.ALL);
    this.id = id;
    setName("soulBound");
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
    if(evt.entityPlayer.worldObj.getGameRules().getBoolean("keepInventory")) {
      return;
    }

    Log.debug("Running onPlayerDeathEarly logic for " + evt.entityPlayer.getName());

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
    IInventory baubles = BaublesUtil.instance().getBaubles(evt.entityPlayer);
    if (baubles != null) {
      for (int i = 0; i < baubles.getSizeInventory(); i++) {
        ItemStack item = baubles.getStackInSlot(i);
        if(isSoulBound(item)) {
          if (addToPlayerInventory(evt.entityPlayer, item)) {
            baubles.setInventorySlotContents(i, null);
          }
        }
      }
    }

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
   * Do a second (late) pass. If any mod has added items to the list in the meantime, this gives us a chance to save them, too. If some gravestone mod has
   * removed drops, we'll get nothing here.
   */
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onPlayerDeathLate(PlayerDropsEvent evt) {
    if (evt.entityPlayer == null || evt.entityPlayer instanceof FakePlayer || evt.isCanceled()) {
      return;
    }
    if (evt.entityPlayer.worldObj.getGameRules().getBoolean("keepInventory")) {
      return;
    }

    Log.debug("Running onPlayerDeathLate logic for " + evt.entityPlayer.getName());

    ListIterator<EntityItem> iter = evt.drops.listIterator();
    while (iter.hasNext()) {
      EntityItem ei = iter.next();
      ItemStack item = ei.getEntityItem();
      if (isSoulBound(item)) {
        if (addToPlayerInventory(evt.entityPlayer, item)) {
          iter.remove();
        }
      }
    }

  }

  /*
   * This is called when the user presses the "respawn" button. The original inventory would be empty, but onPlayerDeath() above placed items in it.
   * 
   * Note: Without other death-modifying mods, the content of the old inventory would always fit into the new one (both being empty but for soulbound items in
   * the old one) and the old one would be discarded just after this method. But better play it safe and assume that an overflow is possible and that another
   * mod may move stuff out of the old inventory, too.
   */
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onPlayerClone(PlayerEvent.Clone evt) {
    if (!evt.wasDeath || evt.isCanceled()) {
      return;
    }
    if(evt.original == null || evt.entityPlayer == null || evt.entityPlayer instanceof FakePlayer) {
      return;
    }
    if(evt.entityPlayer.worldObj.getGameRules().getBoolean("keepInventory")) {
      return;
    }
    if (evt.original == evt.entityPlayer
        || evt.original.inventory == evt.entityPlayer.inventory
        || (evt.original.inventory.armorInventory == evt.entityPlayer.inventory.armorInventory && evt.original.inventory.mainInventory == evt.entityPlayer.inventory.mainInventory)) {
      Log.warn("Player " + evt.entityPlayer.getName() + " just died and respawned in their old body. Did someone fire a PlayerEvent.Clone(death=true) "
          + "for a teleportation? Supressing Soulbound enchantment for zombie player.");
      return;
    }

    Log.debug("Running onPlayerCloneEarly logic for " + evt.entityPlayer.getName());

    for (int i = 0; i < evt.original.inventory.armorInventory.length; i++) {
      ItemStack item = evt.original.inventory.armorInventory[i];
      if(isSoulBound(item)) {
        if (addToPlayerInventory(evt.entityPlayer, item)) {
          evt.original.inventory.armorInventory[i] = null;
        }
      }
    }
    for (int i = 0; i < evt.original.inventory.mainInventory.length; i++) {
      ItemStack item = evt.original.inventory.mainInventory[i];
      if(isSoulBound(item)) {
        if (addToPlayerInventory(evt.entityPlayer, item)) {
          evt.original.inventory.mainInventory[i] = null;
        }
      }
    }
  }

  /*
   * Do a second (late) pass and try to preserve any remaining items by spawning them into the world. They might end up nowhere, but if we do nothing the will
   * be deleted. Note the dropping at the old location, because the new player object's location has not yet been set.
   */
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onPlayerCloneLast(PlayerEvent.Clone evt) {
    if (!evt.wasDeath || evt.isCanceled()) {
      return;
    }
    if (evt.original == null || evt.entityPlayer == null || evt.entityPlayer instanceof FakePlayer) {
      return;
    }
    if (evt.entityPlayer.worldObj.getGameRules().getBoolean("keepInventory")) {
      return;
    }
    if (evt.original == evt.entityPlayer
        || evt.original.inventory == evt.entityPlayer.inventory
        || (evt.original.inventory.armorInventory == evt.entityPlayer.inventory.armorInventory && evt.original.inventory.mainInventory == evt.entityPlayer.inventory.mainInventory)) {
      return;
    }

    Log.debug("Running onPlayerCloneLate logic for " + evt.entityPlayer.getName());

    for (int i = 0; i < evt.original.inventory.armorInventory.length; i++) {
      ItemStack item = evt.original.inventory.armorInventory[i];
      if (isSoulBound(item)) {
        if (addToPlayerInventory(evt.entityPlayer, item) || tryToSpawnItemInWorld(evt.original, item)) {
          evt.original.inventory.armorInventory[i] = null;
        }
      }
    }
    for (int i = 0; i < evt.original.inventory.mainInventory.length; i++) {
      ItemStack item = evt.original.inventory.mainInventory[i];
      if (isSoulBound(item)) {
        if (addToPlayerInventory(evt.entityPlayer, item) || tryToSpawnItemInWorld(evt.original, item)) {
          evt.original.inventory.mainInventory[i] = null;
        }
      }
    }
  }

  private boolean tryToSpawnItemInWorld(EntityPlayer entityPlayer, ItemStack item) {
    if (entityPlayer != null && entityPlayer.worldObj != null) {
      EntityItem entityitem = new EntityItem(entityPlayer.worldObj, entityPlayer.posX, entityPlayer.posY + 0.5, entityPlayer.posZ, item);
      entityitem.setPickupDelay(40);
      entityitem.lifespan *= 2;
      entityitem.motionX = 0;
      entityitem.motionZ = 0;
      entityPlayer.worldObj.spawnEntityInWorld(entityitem);
      Log.debug("Running tryToSpawnItemInWorld logic for " + entityPlayer.getName() + ": " + item);
      return true;
    }
    return false;
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
        Log.debug("Running addToPlayerInventory/armor logic for " + entityPlayer.getName() + ": " + item);
        return true;
      }
    }

    InventoryPlayer inv = entityPlayer.inventory;
    for (int i = 0; i < inv.mainInventory.length; i++) {
      if(inv.mainInventory[i] == null) {
        inv.mainInventory[i] = item.copy();
        Log.debug("Running addToPlayerInventory/main logic for " + entityPlayer.getName() + ": " + item);
        return true;
      }
    }

    Log.debug("Running addToPlayerInventory/fail logic for " + entityPlayer.getName() + ": " + item);
    return false;
  }

  @Override
  public String[] getTooltipDetails(ItemStack stack) {
    return new String[] { EnderIO.lang.localizeExact("description.enchantment.enderio.soulBound") };
  }
}
