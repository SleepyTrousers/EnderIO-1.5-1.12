package crazypants.enderio.handler.darksteel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.google.common.collect.ImmutableList;

import crazypants.enderio.EnderIO;
import crazypants.enderio.integration.forestry.ForestryUtil;
import crazypants.enderio.integration.top.TOPUtil;
import crazypants.enderio.item.darksteel.upgrade.elytra.ElytraUpgrade;
import crazypants.enderio.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.flippers.SwimUpgrade;
import crazypants.enderio.item.darksteel.upgrade.glider.GliderUpgrade;
import crazypants.enderio.item.darksteel.upgrade.jump.JumpUpgrade;
import crazypants.enderio.item.darksteel.upgrade.nightvision.NightVisionUpgrade;
import crazypants.enderio.item.darksteel.upgrade.sound.SoundDetectorUpgrade;
import crazypants.enderio.item.darksteel.upgrade.speed.SpeedUpgrade;
import crazypants.enderio.item.darksteel.upgrade.spoon.SpoonUpgrade;
import crazypants.enderio.item.darksteel.upgrade.travel.TravelUpgrade;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DarkSteelRecipeManager {

  public static final @Nonnull DarkSteelRecipeManager instance = new DarkSteelRecipeManager();

  private final @Nonnull NNList<IDarkSteelUpgrade> upgrades = new NNList<IDarkSteelUpgrade>();

  public void addUpgrade(@Nonnull IDarkSteelUpgrade upgrade) {
    upgrades.add(upgrade);
  }

  public DarkSteelRecipeManager() {
    addUpgrade(EnergyUpgrade.EMPOWERED);
    addUpgrade(EnergyUpgrade.EMPOWERED_TWO);
    addUpgrade(EnergyUpgrade.EMPOWERED_THREE);
    addUpgrade(EnergyUpgrade.EMPOWERED_FOUR);
    addUpgrade(JumpUpgrade.JUMP_ONE);
    addUpgrade(JumpUpgrade.JUMP_TWO);
    addUpgrade(JumpUpgrade.JUMP_THREE);
    addUpgrade(SpeedUpgrade.SPEED_ONE);
    addUpgrade(SpeedUpgrade.SPEED_TWO);
    addUpgrade(SpeedUpgrade.SPEED_THREE);
    addUpgrade(GliderUpgrade.INSTANCE);
    addUpgrade(ElytraUpgrade.INSTANCE);
    addUpgrade(SoundDetectorUpgrade.INSTANCE);
    addUpgrade(SwimUpgrade.INSTANCE);
    addUpgrade(NightVisionUpgrade.INSTANCE);
    addUpgrade(TravelUpgrade.INSTANCE);
    addUpgrade(SpoonUpgrade.INSTANCE);
    
    //TODO: Mod Thaumcraft
//    if(Loader.isModLoaded("Thaumcraft")) {
//      ThaumcraftCompat.loadUpgrades(upgrades);
//    }
    ForestryUtil.addUpgrades(this);
    TOPUtil.addUpgrades(this);
  }

  @SubscribeEvent
  public void handleAnvilEvent(AnvilUpdateEvent evt) {
    if(isRepair(evt)) {
      handleRepair(evt);
    } else {
      handleUpgrade(evt);
    }
  }
  
  private boolean isRepair(AnvilUpdateEvent evt) {
    if(evt.getLeft().getItem() instanceof IDarkSteelItem) {
      IDarkSteelItem dsi = (IDarkSteelItem)evt.getLeft().getItem();
      if(dsi.isItemForRepair(evt.getRight())) {
        return true;
      }
    }
    return false;
  }

  private void handleRepair(AnvilUpdateEvent evt) {
    ItemStack targetStack = evt.getLeft();
    ItemStack ingots = evt.getRight();
    
    //repair event
    IDarkSteelItem targetItem = (IDarkSteelItem)targetStack.getItem();
    int maxIngots = targetItem.getIngotsRequiredForFullRepair();
    
    double damPerc = (double)targetStack.getItemDamage()/ targetStack.getMaxDamage();
    int requiredIngots = (int)Math.ceil(damPerc * maxIngots);
    if (ingots.getCount() > requiredIngots) {
      return;
    }
    
    int damageAddedPerIngot = (int)Math.ceil((double)targetStack.getMaxDamage()/maxIngots);
    int totalDamageRemoved = damageAddedPerIngot * ingots.getCount();
    
    ItemStack resultStack = targetStack.copy();
    resultStack.setItemDamage(Math.max(0, resultStack.getItemDamage() - totalDamageRemoved));
    
    evt.setOutput(resultStack);
    evt.setCost(ingots.getCount() + (int) Math.ceil(getEnchantmentRepairCost(resultStack) / 2));
  }

  private void handleUpgrade(AnvilUpdateEvent evt) {
    for (IDarkSteelUpgrade upgrade : upgrades) {
      if(upgrade.isUpgradeItem(evt.getRight()) && upgrade.canAddToItem(evt.getLeft())) {
        ItemStack res = new ItemStack(evt.getLeft().getItem(), 1, evt.getLeft().getItemDamage());
        final NBTTagCompound tagCompound = evt.getLeft().getTagCompound();
        if (tagCompound != null) {
          res.setTagCompound(tagCompound.copy());
        }
        upgrade.writeToItem(res);
        evt.setOutput(res);
        evt.setCost(upgrade.getLevelCost());
        return;
      }
    }
  }

  public static int getEnchantmentRepairCost(@Nonnull ItemStack itemStack) {
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

  public NNList<IDarkSteelUpgrade> getUpgrades() {
    return upgrades;
  }

  public void addCommonTooltipEntries(@Nonnull ItemStack itemstack, EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    for (IDarkSteelUpgrade upgrade : upgrades) {
      if(upgrade.hasUpgrade(itemstack)) {
        upgrade.addCommonEntries(itemstack, entityplayer, list, flag);
      }
    }
  }

  public void addBasicTooltipEntries(@Nonnull ItemStack itemstack, EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    for (IDarkSteelUpgrade upgrade : upgrades) {
      if(upgrade.hasUpgrade(itemstack)) {
        upgrade.addBasicEntries(itemstack, entityplayer, list, flag);
      }
    }
  }

  public void addAdvancedTooltipEntries(@Nonnull ItemStack itemstack, EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
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

  public @Nonnull Iterator<IDarkSteelUpgrade> recipeIterator() {
    return ImmutableList.copyOf(upgrades).iterator();
  }

  public String getUpgradesAsString(@Nonnull ItemStack stack) {
    String result = "";
    for (IDarkSteelUpgrade upgrade : upgrades) {
      if (upgrade.hasUpgrade(stack)) {
        result += upgrade.getUnlocalizedName();
      }
    }
    return result.isEmpty() ? null : result;
  }

  public NNList<ItemStack> getRecipes(@Nonnull Set<String> seen, @Nonnull NNList<UpgradePath> list, @Nonnull NNList<ItemStack> input) {
    NNList<ItemStack> output = new NNList<ItemStack>();
    NNIterator<ItemStack> iterator = input.iterator();
    while (iterator.hasNext()) {
      ItemStack stack = iterator.next();
      for (IDarkSteelUpgrade upgrade : upgrades) {
        if (upgrade.canAddToItem(stack)) {
          ItemStack newStack = stack.copy();
          upgrade.writeToItem(newStack);
          String id = newStack.getItem() + getUpgradesAsString(newStack);
          if (!seen.contains(id)) {
            seen.add(id);
            list.add(new UpgradePath(stack, upgrade.getUpgradeItem(), newStack));
            output.add(newStack);
          }
        }
      }
    }
    return output;
  }

  public static @Nonnull NNList<UpgradePath> getAllRecipes(@Nonnull NNList<ItemStack> validItems) {
    NNList<UpgradePath> list = new NNList<UpgradePath>();
    Set<String> seen = new HashSet<String>();
    NNList<ItemStack> items = instance.getRecipes(seen, list, validItems);
    while (!items.isEmpty()) {
      items = instance.getRecipes(seen, list, items);
    }
    return list;
  }

  public static class UpgradePath {
    public final @Nonnull ItemStack input, upgrade, output;

    UpgradePath(@Nonnull ItemStack input, @Nonnull ItemStack upgrade, @Nonnull ItemStack output) {
      super();
      this.input = input;
      this.upgrade = upgrade;
      this.output = output;
    }

    public @Nonnull ItemStack getInput() {
      return input;
    }

    public @Nonnull ItemStack getUpgrade() {
      return upgrade;
    }

    public @Nonnull ItemStack getOutput() {
      return output;
    }
  }

}
