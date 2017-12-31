package crazypants.enderio.base.handler.darksteel;

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

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.item.darksteel.upgrade.elytra.ElytraUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.explosive.ExplosiveUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.flippers.SwimUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.glider.GliderUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.jump.JumpUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.nightvision.NightVisionUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.sound.SoundDetectorUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.speed.SpeedUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.spoon.SpoonUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.travel.TravelUpgrade;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

@EventBusSubscriber(modid = EnderIO.MODID)
public class DarkSteelRecipeManager {

  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    final IForgeRegistry<IDarkSteelUpgrade> registry = event.getRegistry();
    registry.register(EnergyUpgrade.EMPOWERED);
    registry.register(EnergyUpgrade.EMPOWERED_TWO);
    registry.register(EnergyUpgrade.EMPOWERED_THREE);
    registry.register(EnergyUpgrade.EMPOWERED_FOUR);
    registry.register(JumpUpgrade.JUMP_ONE);
    registry.register(JumpUpgrade.JUMP_TWO);
    registry.register(JumpUpgrade.JUMP_THREE);
    registry.register(SpeedUpgrade.SPEED_ONE);
    registry.register(SpeedUpgrade.SPEED_TWO);
    registry.register(SpeedUpgrade.SPEED_THREE);
    registry.register(GliderUpgrade.INSTANCE);
    registry.register(ElytraUpgrade.INSTANCE);
    registry.register(SoundDetectorUpgrade.INSTANCE);
    registry.register(SwimUpgrade.INSTANCE);
    registry.register(NightVisionUpgrade.INSTANCE);
    registry.register(TravelUpgrade.INSTANCE);
    registry.register(SpoonUpgrade.INSTANCE);
    registry.register(ExplosiveUpgrade.INSTANCE);
  }

  @SubscribeEvent
  public static void handleAnvilEvent(AnvilUpdateEvent evt) {
    if (isRepair(evt)) {
      handleRepair(evt);
    } else {
      handleUpgrade(evt);
    }
  }

  private static boolean isRepair(AnvilUpdateEvent evt) {
    if (evt.getLeft().getItem() instanceof IDarkSteelItem) {
      IDarkSteelItem dsi = (IDarkSteelItem) evt.getLeft().getItem();
      if (dsi.isItemForRepair(evt.getRight())) {
        return true;
      }
    }
    return false;
  }

  private static void handleRepair(AnvilUpdateEvent evt) {
    ItemStack targetStack = evt.getLeft();
    ItemStack ingots = evt.getRight();

    // repair event
    IDarkSteelItem targetItem = (IDarkSteelItem) targetStack.getItem();
    int maxIngots = targetItem.getIngotsRequiredForFullRepair();

    double damPerc = (double) targetStack.getItemDamage() / targetStack.getMaxDamage();
    int requiredIngots = (int) Math.ceil(damPerc * maxIngots);
    if (ingots.getCount() > requiredIngots) {
      return;
    }

    int damageAddedPerIngot = (int) Math.ceil((double) targetStack.getMaxDamage() / maxIngots);
    int totalDamageRemoved = damageAddedPerIngot * ingots.getCount();

    ItemStack resultStack = targetStack.copy();
    resultStack.setItemDamage(Math.max(0, resultStack.getItemDamage() - totalDamageRemoved));

    evt.setOutput(resultStack);
    evt.setCost(ingots.getCount() + (int) Math.ceil(getEnchantmentRepairCost(resultStack) / 2));
  }

  private static void handleUpgrade(AnvilUpdateEvent evt) {
    for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
      if (upgrade.isUpgradeItem(evt.getRight()) && upgrade.canAddToItem(evt.getLeft())) {
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

  private static int getEnchantmentRepairCost(@Nonnull ItemStack itemStack) {
    // derived from ContainerRepair
    int res = 0;
    Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemStack);
    Iterator<Enchantment> iter = map1.keySet().iterator();
    while (iter.hasNext()) {
      Enchantment i1 = iter.next();
      Enchantment enchantment = i1;

      int level = map1.get(enchantment).intValue();
      if (enchantment.canApply(itemStack)) {
        if (level > enchantment.getMaxLevel()) {
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

  public static void addCommonTooltipEntries(@Nonnull ItemStack itemstack, EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
      if (upgrade.hasUpgrade(itemstack)) {
        upgrade.addCommonEntries(itemstack, entityplayer, list, flag);
      }
    }
  }

  public static void addBasicTooltipEntries(@Nonnull ItemStack itemstack, EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
      if (upgrade.hasUpgrade(itemstack)) {
        upgrade.addBasicEntries(itemstack, entityplayer, list, flag);
      }
    }
  }

  public static void addAdvancedTooltipEntries(@Nonnull ItemStack itemstack, EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    List<IDarkSteelUpgrade> applyableUpgrades = new ArrayList<IDarkSteelUpgrade>();
    for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
      if (upgrade.hasUpgrade(itemstack)) {
        upgrade.addDetailedEntries(itemstack, entityplayer, list, flag);
      } else if (upgrade.canAddToItem(itemstack)) {
        applyableUpgrades.add(upgrade);
      }
    }
    if (!applyableUpgrades.isEmpty()) {
      list.add(TextFormatting.YELLOW + EnderIO.lang.localize("tooltip.anvilupgrades") + " ");
      for (IDarkSteelUpgrade up : applyableUpgrades) {
        list.add(Lang.DARK_STEEL_LEVELS1.get(TextFormatting.DARK_AQUA, EnderIO.lang.localizeExact(up.getUnlocalizedName() + ".name")));
        list.add(Lang.DARK_STEEL_LEVELS2.get(TextFormatting.DARK_AQUA, TextFormatting.ITALIC, up.getUpgradeItemName(), up.getLevelCost()));
      }
    }
  }

  public static @Nonnull Iterator<IDarkSteelUpgrade> recipeIterator() {
    return ImmutableList.copyOf(UpgradeRegistry.getUpgrades()).iterator();
  }

  public static String getUpgradesAsString(@Nonnull ItemStack stack) {
    String result = "";
    for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
      if (upgrade.hasUpgrade(stack)) {
        result += upgrade.getUnlocalizedName();
      }
    }
    return result.isEmpty() ? null : result;
  }

  public static NNList<ItemStack> getRecipes(@Nonnull Set<String> seen, @Nonnull NNList<UpgradePath> list, @Nonnull NNList<ItemStack> input) {
    NNList<ItemStack> output = new NNList<ItemStack>();
    NNIterator<ItemStack> iterator = input.iterator();
    while (iterator.hasNext()) {
      ItemStack stack = iterator.next();
      for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
        if (upgrade.canAddToItem(stack)) {
          ItemStack newStack = stack.copy();
          upgrade.writeToItem(newStack);
          String id = newStack.getItem() + getUpgradesAsString(stack) + ":" + getUpgradesAsString(newStack);
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
    NNList<ItemStack> items = getRecipes(seen, list, validItems);
    while (!items.isEmpty()) {
      items = getRecipes(seen, list, items);
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
