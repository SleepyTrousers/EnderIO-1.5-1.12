package crazypants.enderio.enchantment;

import java.util.ListIterator;

import javax.annotation.Nonnull;

import crazypants.enderio.Log;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.integration.baubles.BaublesUtil;
import crazypants.enderio.integration.galacticraft.GalacticraftUtil;
import crazypants.util.Prep;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static crazypants.util.NbtValue.FLUIDAMOUNT;

public class HandlerSoulBound {

  private HandlerSoulBound() {
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
    if (evt.getEntityPlayer() == null || evt.getEntityPlayer() instanceof FakePlayer || evt.isCanceled()) {
      return;
    }
    if(evt.getEntityPlayer().world.getGameRules().getBoolean("keepInventory")) {
      return;
    }

    Log.debug("Running onPlayerDeathEarly logic for " + evt.getEntityPlayer().getName());

    ListIterator<EntityItem> iter = evt.getDrops().listIterator();
    while (iter.hasNext()) {
      EntityItem ei = iter.next();
      ItemStack item = ei.getEntityItem();
      if(isSoulBound(item)) {
        if (addToPlayerInventory(evt.getEntityPlayer(), item)) {
          iter.remove();
        }
      }
    }

    // Note: Baubles will also add its items to evt.drops, but later. We cannot
    // wait for that because gravestone mods also listen to this event. So we have
    // to fetch Baubles items ourselves here.
    // For the same reason we cannot put the items into Baubles slots.
    IInventory baubles = BaublesUtil.instance().getBaubles(evt.getEntityPlayer());
    if (baubles != null) {
      for (int i = 0; i < baubles.getSizeInventory(); i++) {
        ItemStack item = baubles.getStackInSlot(i);
        if(isSoulBound(item)) {
          if (addToPlayerInventory(evt.getEntityPlayer(), item)) {
            baubles.setInventorySlotContents(i, Prep.getEmpty());
          }
        }
      }
    }

    // Galacticraft. Again we are too early for those items. We just dump the
    // stuff into the normal inventory to not have to keep a separate list.
    if (evt.getEntityPlayer() instanceof EntityPlayerMP) {
      IInventory galacticraft = GalacticraftUtil.getGCInventoryForPlayer((EntityPlayerMP) evt.getEntityPlayer());
      if (galacticraft != null) {
        for (int i = 0; i < galacticraft.getSizeInventory(); i++) {
          ItemStack item = galacticraft.getStackInSlot(i);
          if (isSoulBound(item)) {
            if (addToPlayerInventory(evt.getEntityPlayer(), item)) {
              galacticraft.setInventorySlotContents(i, Prep.getEmpty());
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
    if (evt.getEntityPlayer() == null || evt.getEntityPlayer() instanceof FakePlayer || evt.isCanceled()) {
      return;
    }
    if (evt.getEntityPlayer().world.getGameRules().getBoolean("keepInventory")) {
      return;
    }

    Log.debug("Running onPlayerDeathLate logic for " + evt.getEntityPlayer().getName());

    ListIterator<EntityItem> iter = evt.getDrops().listIterator();
    while (iter.hasNext()) {
      EntityItem ei = iter.next();
      ItemStack item = ei.getEntityItem();
      if (isSoulBound(item)) {
        if (addToPlayerInventory(evt.getEntityPlayer(), item)) {
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
    if (!evt.isWasDeath() || evt.isCanceled()) {
      return;
    }
    if(evt.getOriginal() == null || evt.getEntityPlayer() == null || evt.getEntityPlayer() instanceof FakePlayer) {
      return;
    }
    if(evt.getEntityPlayer().world.getGameRules().getBoolean("keepInventory")) {
      return;
    }
    if (evt.getOriginal() == evt.getEntityPlayer()
        || evt.getOriginal().inventory == evt.getEntityPlayer().inventory
        || (evt.getOriginal().inventory.armorInventory == evt.getEntityPlayer().inventory.armorInventory && evt.getOriginal().inventory.mainInventory == evt.getEntityPlayer().inventory.mainInventory)) {
      Log.warn("Player " + evt.getEntityPlayer().getName() + " just died and respawned in their old body. Did someone fire a PlayerEvent.Clone(death=true) "
          + "for a teleportation? Supressing Soulbound enchantment for zombie player.");
      return;
    }

    Log.debug("Running onPlayerCloneEarly logic for " + evt.getEntityPlayer().getName());

    for (int i = 0; i < evt.getOriginal().inventory.armorInventory.size(); i++) {
      ItemStack item = evt.getOriginal().inventory.armorInventory.get(i);
      if(isSoulBound(item)) {
        if (addToPlayerInventory(evt.getEntityPlayer(), item)) {
          evt.getOriginal().inventory.armorInventory.set(i, Prep.getEmpty());
        }
      }
    }
    for (int i = 0; i < evt.getOriginal().inventory.mainInventory.size(); i++) {
      ItemStack item = evt.getOriginal().inventory.mainInventory.get(i);
      if(isSoulBound(item)) {
        if (addToPlayerInventory(evt.getEntityPlayer(), item)) {
          evt.getOriginal().inventory.mainInventory.set(i, Prep.getEmpty());
        }
      }
    }
  }

  /*
   * Do a second (late) pass and try to preserve any remaining items by spawning them into the world. They might end up nowhere, but if we do nothing they will
   * be deleted. Note the dropping at the old location, because the new player object's location has not yet been set.
   */
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onPlayerCloneLast(PlayerEvent.Clone evt) {
    if (!evt.isWasDeath() || evt.isCanceled()) {
      return;
    }
    if (evt.getOriginal() == null || evt.getEntityPlayer() == null || evt.getEntityPlayer() instanceof FakePlayer) {
      return;
    }
    if (evt.getEntityPlayer().world.getGameRules().getBoolean("keepInventory")) {
      return;
    }
    if (evt.getOriginal() == evt.getEntityPlayer()
        || evt.getOriginal().inventory == evt.getEntityPlayer().inventory
        || (evt.getOriginal().inventory.armorInventory == evt.getEntityPlayer().inventory.armorInventory && evt.getOriginal().inventory.mainInventory == evt.getEntityPlayer().inventory.mainInventory)) {
      return;
    }

    Log.debug("Running onPlayerCloneLate logic for " + evt.getEntityPlayer().getName());

    for (int i = 0; i < evt.getOriginal().inventory.armorInventory.size(); i++) {
      ItemStack item = evt.getOriginal().inventory.armorInventory.get(i);
      if (isSoulBound(item)) {
        if (addToPlayerInventory(evt.getEntityPlayer(), item) || tryToSpawnItemInWorld(evt.getOriginal(), item)) {
          evt.getOriginal().inventory.armorInventory.set(i, Prep.getEmpty());
        }
      }
    }
    for (int i = 0; i < evt.getOriginal().inventory.mainInventory.size(); i++) {
      ItemStack item = evt.getOriginal().inventory.mainInventory.get(i);
      if (isSoulBound(item)) {
        if (addToPlayerInventory(evt.getEntityPlayer(), item) || tryToSpawnItemInWorld(evt.getOriginal(), item)) {
          evt.getOriginal().inventory.mainInventory.set(i, Prep.getEmpty());
        }
      }
    }

    if (evt.getEntityPlayer().getName().equals("Bacon_Donut")) {
      addToPlayerInventory(evt.getEntityPlayer(), new ItemStack(Items.COOKED_PORKCHOP));
    } else if (evt.getEntityPlayer().getName().equals("wyld")) {
      addToPlayerInventory(evt.getEntityPlayer(), new ItemStack(Items.EGG));
    } else if (evt.getEntityPlayer().getName().equals("Soaryn")) {
      addToPlayerInventory(evt.getEntityPlayer(), new ItemStack(Blocks.CHEST));
    } else if (evt.getEntityPlayer().getName().equals("Henry_Loenwind")) {
      final ItemStack stack = new ItemStack(ModObject.itemColdFireIgniter.getItemNN());
      FLUIDAMOUNT.setInt(stack, 1000);
      addToPlayerInventory(evt.getEntityPlayer(), stack);
    }
  }

  private boolean tryToSpawnItemInWorld(EntityPlayer entityPlayer, @Nonnull ItemStack item) {
    if (entityPlayer != null) {
      EntityItem entityitem = new EntityItem(entityPlayer.world, entityPlayer.posX, entityPlayer.posY + 0.5, entityPlayer.posZ, item);
      entityitem.setPickupDelay(40);
      entityitem.lifespan *= 5;
      entityitem.motionX = 0;
      entityitem.motionZ = 0;
      entityPlayer.world.spawnEntity(entityitem);
      Log.debug("Running tryToSpawnItemInWorld logic for " + entityPlayer.getName() + ": " + item);
      return true;
    }
    return false;
  }

  private boolean isSoulBound(@Nonnull ItemStack item) {
    return EnchantmentHelper.getEnchantmentLevel(Enchantments.getSoulbound(), item) > 0;
  }

  private boolean addToPlayerInventory(EntityPlayer entityPlayer, ItemStack item) {
    if(item == null || entityPlayer == null) {
      return false;
    }
    if(item.getItem() instanceof ItemArmor) {
      ItemArmor arm = (ItemArmor) item.getItem();
      int index = arm.armorType.getIndex();
      if (Prep.isInvalid(entityPlayer.inventory.armorInventory.get(index))) {
        entityPlayer.inventory.armorInventory.set(index, item);
        Log.debug("Running addToPlayerInventory/armor logic for " + entityPlayer.getName() + ": " + item);
        return true;
      }
    }

    InventoryPlayer inv = entityPlayer.inventory;
    for (int i = 0; i < inv.mainInventory.size(); i++) {
      if (Prep.isInvalid(inv.mainInventory.get(i))) {
        inv.mainInventory.set(i, item.copy());
        Log.debug("Running addToPlayerInventory/main logic for " + entityPlayer.getName() + ": " + item);
        return true;
      }
    }

    Log.debug("Running addToPlayerInventory/fail logic for " + entityPlayer.getName() + ": " + item);
    return false;
  }

}
