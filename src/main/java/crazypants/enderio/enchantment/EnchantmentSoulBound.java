package crazypants.enderio.enchantment;

import java.util.ListIterator;
import java.util.Map;

import tterrag.core.api.common.enchant.IAdvancedEnchant;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderio.config.Config;
import crazypants.util.Lang;

@Interface(iface = "tterrag.core.api.common.enchant.IAdvancedEnchant", modid = "ttCore")
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

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onPlayerDeath(PlayerDropsEvent evt) {
    if(evt.entityPlayer == null || evt.entityPlayer instanceof FakePlayer) {
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
        addToPlayerInventory(evt.entityPlayer, item);
        iter.remove();
      }
    }

  }

  @SubscribeEvent
  public void onPlayerClone(PlayerEvent.Clone evt) {
    if(!evt.wasDeath) {
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
        addToPlayerInventory(evt.entityPlayer, item);
      }
    }
    for (int i = 0; i < evt.original.inventory.armorInventory.length; i++) {
      ItemStack item = evt.original.inventory.armorInventory[i];
      if(isSoulBound(item)) {
        addToPlayerInventory(evt.entityPlayer, item);
      }
    }
  }

  private boolean isSoulBound(ItemStack item) {
    if(item == null) {
      return false;
    }
    Map<Integer, Integer> enchants = EnchantmentHelper.getEnchantments(item);
    if(enchants != null) {
      for (int enchId : enchants.keySet()) {
        if(id == enchId) {
          return true;
        }
      }
    }
    return false;
  }

  private void addToPlayerInventory(EntityPlayer entityPlayer, ItemStack item) {
    if(item == null || entityPlayer == null) {
      return;
    }
    if(item.getItem() instanceof ItemArmor) {
      ItemArmor arm = (ItemArmor) item.getItem();
      int index = 3 - arm.armorType;
      if(entityPlayer.inventory.armorItemInSlot(index) == null) {
        entityPlayer.inventory.armorInventory[index] = item;
        return;
      }
    }

    InventoryPlayer inv = entityPlayer.inventory;
    for (int i = 0; i < inv.mainInventory.length; i++) {
      if(inv.mainInventory[i] == null) {
        inv.mainInventory[i] = item.copy();
        return;
      }
    }

  }

  @Override
  public String[] getTooltipDetails(ItemStack stack) {
    return new String[] { Lang.localize("description.enchantment.enderio.soulBound", false) };
  }
}
