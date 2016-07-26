package crazypants.enderio.item.darksteel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.util.OreDictionaryHelper;
import com.google.common.collect.ImmutableList;

import crazypants.enderio.EnderIO;
import crazypants.enderio.item.darksteel.upgrade.ApiaristArmorUpgrade;
import crazypants.enderio.item.darksteel.upgrade.ElytraUpgrade;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.GliderUpgrade;
import crazypants.enderio.item.darksteel.upgrade.IDarkSteelUpgrade;
import crazypants.enderio.item.darksteel.upgrade.JumpUpgrade;
import crazypants.enderio.item.darksteel.upgrade.NaturalistEyeUpgrade;
import crazypants.enderio.item.darksteel.upgrade.NightVisionUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SolarUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SoundDetectorUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SpeedUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SpoonUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SwimUpgrade;
import crazypants.enderio.item.darksteel.upgrade.TheOneProbeUpgrade;
import crazypants.enderio.item.darksteel.upgrade.TravelUpgrade;
import crazypants.enderio.jei.ItemHelper;
import crazypants.enderio.material.Alloy;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DarkSteelRecipeManager {

  public static DarkSteelRecipeManager instance = new DarkSteelRecipeManager();

  private List<IDarkSteelUpgrade> upgrades = new ArrayList<IDarkSteelUpgrade>();

  public DarkSteelRecipeManager() {
    upgrades.add(EnergyUpgrade.EMPOWERED);
    upgrades.add(EnergyUpgrade.EMPOWERED_TWO);
    upgrades.add(EnergyUpgrade.EMPOWERED_THREE);
    upgrades.add(EnergyUpgrade.EMPOWERED_FOUR);
    upgrades.add(JumpUpgrade.JUMP_ONE);
    upgrades.add(JumpUpgrade.JUMP_TWO);
    upgrades.add(JumpUpgrade.JUMP_THREE);
    upgrades.add(SpeedUpgrade.SPEED_ONE);
    upgrades.add(SpeedUpgrade.SPEED_TWO);
    upgrades.add(SpeedUpgrade.SPEED_THREE);
    upgrades.add(GliderUpgrade.INSTANCE);
    upgrades.add(ElytraUpgrade.INSTANCE);
    upgrades.add(SoundDetectorUpgrade.INSTANCE);
    upgrades.add(SwimUpgrade.INSTANCE);
    upgrades.add(NightVisionUpgrade.INSTANCE);
    upgrades.add(TravelUpgrade.INSTANCE);
    upgrades.add(SpoonUpgrade.INSTANCE);
    upgrades.add(SolarUpgrade.SOLAR_ONE);
    upgrades.add(SolarUpgrade.SOLAR_TWO);
    upgrades.add(SolarUpgrade.SOLAR_THREE);
    //TODO: 1.9 Thaumcraft
//    if(Loader.isModLoaded("Thaumcraft")) {
//      ThaumcraftCompat.loadUpgrades(upgrades);
//    }
    if(Loader.isModLoaded("Forestry")) {
      upgrades.add(NaturalistEyeUpgrade.INSTANCE);
      upgrades.add(ApiaristArmorUpgrade.HELMET);
      upgrades.add(ApiaristArmorUpgrade.CHEST);
      upgrades.add(ApiaristArmorUpgrade.LEGS);
      upgrades.add(ApiaristArmorUpgrade.BOOTS);
    }
    if (TheOneProbeUpgrade.INSTANCE.isAvailable()) {
      upgrades.add(TheOneProbeUpgrade.INSTANCE);
    }
  }

  @SubscribeEvent
  public void handleAnvilEvent(AnvilUpdateEvent evt) {
    if(evt.getLeft() == null || evt.getRight() == null) {
      return;
    }

    if(evt.getLeft().getItem() instanceof IDarkSteelItem && OreDictionaryHelper.hasName(evt.getRight(), Alloy.DARK_STEEL.getOreIngot())) {
      handleRepair(evt);
    } else {    
      handleUpgrade(evt);
    }
  }

  private void handleRepair(AnvilUpdateEvent evt) {
    ItemStack targetStack = evt.getLeft();
    ItemStack ingots = evt.getRight();
    
    //repair event
    IDarkSteelItem targetItem = (IDarkSteelItem)targetStack.getItem();
    int maxIngots = targetItem.getIngotsRequiredForFullRepair();
    
    double damPerc = (double)targetStack.getItemDamage()/ targetStack.getMaxDamage();
    int requiredIngots = (int)Math.ceil(damPerc * maxIngots);
    if(ingots.stackSize > requiredIngots) {
      return;
    }
    
    int damageAddedPerIngot = (int)Math.ceil((double)targetStack.getMaxDamage()/maxIngots);
    int totalDamageRemoved = damageAddedPerIngot * ingots.stackSize;
    
    ItemStack resultStack = targetStack.copy();
    resultStack.setItemDamage(Math.max(0, resultStack.getItemDamage() - totalDamageRemoved));
    
    evt.setOutput(resultStack);
    evt.setCost(ingots.stackSize + (int)Math.ceil(getEnchantmentRepairCost(resultStack)/2));
  }

  private void handleUpgrade(AnvilUpdateEvent evt) {
    for (IDarkSteelUpgrade upgrade : upgrades) {
      if(upgrade.isUpgradeItem(evt.getRight()) && upgrade.canAddToItem(evt.getLeft())) {
        ItemStack res = new ItemStack(evt.getLeft().getItem(), 1, evt.getLeft().getItemDamage());
        if(evt.getLeft().getTagCompound() != null) {
          res.setTagCompound((NBTTagCompound) evt.getLeft().getTagCompound().copy());
        }
        upgrade.writeToItem(res);
        evt.setOutput(res);
        evt.setCost(upgrade.getLevelCost());
        return;
      }
    }
  }

  public static int getEnchantmentRepairCost(ItemStack itemStack) {
    //derived from ContainerRepair
    int res = 0;
    Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemStack);
    Iterator<Enchantment> iter = map1.keySet().iterator();
    while (iter.hasNext()) {
      Enchantment i1 =  iter.next();
      Enchantment enchantment = i1;
      
      int level = map1.get(enchantment).intValue();
      if(enchantment.canApply(itemStack)) {
        if(level > enchantment.getMaxLevel()) {
          level = enchantment.getMaxLevel();
        }
        int costPerLevel = 0;
        switch (enchantment.getRarity()) {
        case VERY_RARE:
          costPerLevel = 8;
          break;
        case RARE:
          costPerLevel = 4;        
        case UNCOMMON:
          costPerLevel = 2;
          break;
        case COMMON:
          costPerLevel = 1;
        }
        res += costPerLevel * level;
      }
    }
    return res;
  }

  public List<IDarkSteelUpgrade> getUpgrades() {
    return upgrades;
  }

  public void addCommonTooltipEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    for (IDarkSteelUpgrade upgrade : upgrades) {
      if(upgrade.hasUpgrade(itemstack)) {
        upgrade.addCommonEntries(itemstack, entityplayer, list, flag);
      }
    }
  }

  public void addBasicTooltipEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    for (IDarkSteelUpgrade upgrade : upgrades) {
      if(upgrade.hasUpgrade(itemstack)) {
        upgrade.addBasicEntries(itemstack, entityplayer, list, flag);
      }
    }
  }

  public void addAdvancedTooltipEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {

    List<IDarkSteelUpgrade> applyableUpgrades = new ArrayList<IDarkSteelUpgrade>();
    for (IDarkSteelUpgrade upgrade : upgrades) {
      if(upgrade.hasUpgrade(itemstack)) {
        upgrade.addDetailedEntries(itemstack, entityplayer, list, flag);
      } else if(upgrade.canAddToItem(itemstack)) {
        applyableUpgrades.add(upgrade);
      }
    }
    if(!applyableUpgrades.isEmpty()) {
      list.add(TextFormatting.YELLOW + EnderIO.lang.localize("tooltip.anvilupgrades") + " ");
      for (IDarkSteelUpgrade up : applyableUpgrades) {
        list.add(TextFormatting.DARK_AQUA + "" + "" + EnderIO.lang.localizeExact(up.getUnlocalizedName() + ".name") + ": ");
        list.add(TextFormatting.DARK_AQUA + "" + TextFormatting.ITALIC + "  " + up.getUpgradeItemName() + " + " + up.getLevelCost()
            + " " + EnderIO.lang.localize("item.darkSteel.tooltip.lvs"));
      }
    }
  }

  public Iterator<IDarkSteelUpgrade> recipeIterator() {
    return ImmutableList.copyOf(upgrades).iterator();
  }

  public String getUpgradesAsString(ItemStack stack) {
    String result = "";
    for (IDarkSteelUpgrade upgrade : upgrades) {
      if (upgrade.hasUpgrade(stack)) {
        result += upgrade.getUnlocalizedName();
      }
    }
    return result.isEmpty() ? null : result;
  }

  public List<Triple<ItemStack, ItemStack, ItemStack>> getAllRecipes() {
    List<Triple<ItemStack, ItemStack, ItemStack>> list = new ArrayList<Triple<ItemStack, ItemStack, ItemStack>>();
    Set<String> seen = new HashSet<String>();
    List<ItemStack> items = getRecipes(seen, list, ItemHelper.getValidItems());
    while (!items.isEmpty()) {
      items = getRecipes(seen, list, items);
    }
    return list;
  }

  private List<ItemStack> getRecipes(Set<String> seen, List<Triple<ItemStack, ItemStack, ItemStack>> list, List<ItemStack> input) {
    List<ItemStack> output = new ArrayList<ItemStack>();
    for (ItemStack stack : input) {
      for (IDarkSteelUpgrade upgrade : upgrades) {
        if (upgrade.canAddToItem(stack)) {
          ItemStack newStack = stack.copy();
          upgrade.writeToItem(newStack);
          String id = newStack.getItem() + getUpgradesAsString(newStack);
          if (!seen.contains(id)) {
            seen.add(id);
            list.add(Triple.of(stack, upgrade.getUpgradeItem(), newStack));
            output.add(newStack);
          }
        }
      }
    }
    return output;
  }

}
