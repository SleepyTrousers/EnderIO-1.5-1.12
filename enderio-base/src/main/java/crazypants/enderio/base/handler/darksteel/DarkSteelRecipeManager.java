package crazypants.enderio.base.handler.darksteel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.NullHelper;
import com.google.common.collect.ImmutableList;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.item.darksteel.upgrade.elytra.ElytraUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.flippers.SwimUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.glider.GliderUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.hoe.HoeUpgrade;
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
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(modid = EnderIO.MODID)
public class DarkSteelRecipeManager {

  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    final IForgeRegistry<IDarkSteelUpgrade> registry = event.getRegistry();
    for (EnergyUpgrade energyUpgrade : EnergyUpgrade.UPGRADES) {
      registry.register(energyUpgrade);
    }
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
    registry.register(HoeUpgrade.INSTANCE);
  }

  @SubscribeEvent
  public static void handleAnvilEvent(AnvilUpdateEvent evt) {
    if (evt.getLeft().getItem() instanceof IDarkSteelItem) {
      if (isRepair(evt, (IDarkSteelItem) evt.getLeft().getItem())) {
        handleRepair(evt, (IDarkSteelItem) evt.getLeft().getItem());
      } else {
        handleUpgrade(evt, (IDarkSteelItem) evt.getLeft().getItem());
      }
    }
  }

  private static boolean isRepair(AnvilUpdateEvent evt, @Nonnull IDarkSteelItem item) {
    if (item.isItemForRepair(evt.getRight())) {
      return true;
    }
    return false;
  }

  private static void handleRepair(AnvilUpdateEvent evt, @Nonnull IDarkSteelItem item) {
    ItemStack targetStack = evt.getLeft();
    ItemStack ingots = evt.getRight();

    // repair event
    int maxIngots = item.getIngotsRequiredForFullRepair();

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

  private static void handleUpgrade(AnvilUpdateEvent evt, @Nonnull IDarkSteelItem item) {
    for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
      if (upgrade.isUpgradeItem(evt.getRight()) && upgrade.canAddToItem(evt.getLeft(), item)) {
        ItemStack res = new ItemStack(evt.getLeft().getItem(), 1, evt.getLeft().getItemDamage());
        final NBTTagCompound tagCompound = evt.getLeft().getTagCompound();
        if (tagCompound != null) {
          res.setTagCompound(tagCompound.copy());
        }
        upgrade.addToItem(res, item);
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
    SpecialTooltipHandler.addCommonTooltipFromResources(list, itemstack.getUnlocalizedName());
    if (itemstack.getItem() instanceof IDarkSteelItem) {
      for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
        if (upgrade instanceof IAdvancedTooltipProvider && upgrade.hasUpgrade(itemstack, (IDarkSteelItem) itemstack.getItem())) {
          ((IAdvancedTooltipProvider) upgrade).addCommonEntries(itemstack, entityplayer, list, flag);
        }
      }
    }
  }

  public static void addBasicTooltipEntries(@Nonnull ItemStack itemstack, EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addBasicTooltipFromResources(list, itemstack.getUnlocalizedName());
    if (itemstack.getItem() instanceof IDarkSteelItem) {
      for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
        if (upgrade instanceof IAdvancedTooltipProvider && upgrade.hasUpgrade(itemstack, (IDarkSteelItem) itemstack.getItem())) {
          ((IAdvancedTooltipProvider) upgrade).addBasicEntries(itemstack, entityplayer, list, flag);
        }
      }
    }
  }

  public static void addAdvancedTooltipEntries(@Nonnull ItemStack itemstack, EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack.getUnlocalizedName());
    if (itemstack.getItem() instanceof IDarkSteelItem) {
      List<IDarkSteelUpgrade> applyableUpgrades = new ArrayList<IDarkSteelUpgrade>();
      for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
        if (upgrade instanceof IAdvancedTooltipProvider && upgrade.hasUpgrade(itemstack, (IDarkSteelItem) itemstack.getItem())) {
          ((IAdvancedTooltipProvider) upgrade).addDetailedEntries(itemstack, entityplayer, list, flag);
        } else if (upgrade.canAddToItem(itemstack, (IDarkSteelItem) itemstack.getItem())) {
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
  }

  public static @Nonnull Iterator<IDarkSteelUpgrade> recipeIterator() {
    return ImmutableList.copyOf(UpgradeRegistry.getUpgrades()).iterator();
  }

  public static @Nonnull String getUpgradesAsString(@Nonnull ItemStack stack) {
    String result = "";
    for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
      if (upgrade.hasUpgrade(stack)) {
        result += "/" + upgrade.getUnlocalizedName();
      }
    }
    return result.isEmpty() ? "" : NullHelper.first(result.substring(1), "");
  }

  public static NNList<ItemStack> getRecipes(@Nonnull Set<UpgradePath> list, @Nonnull NNList<ItemStack> input) {
    NNList<ItemStack> output = new NNList<ItemStack>();
    NNIterator<ItemStack> iterator = input.iterator();
    while (iterator.hasNext()) {
      ItemStack inputStack = iterator.next();
      if (inputStack.getItem() instanceof IDarkSteelItem) {
        for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
          if (upgrade.canAddToItem(inputStack, (IDarkSteelItem) inputStack.getItem())) {
            ItemStack outputStack = inputStack.copy();
            upgrade.addToItem(outputStack, (IDarkSteelItem) outputStack.getItem());
            final UpgradePath path = new UpgradePath(inputStack, upgrade.getUpgradeItem(), outputStack);
            if (!list.contains(path)) {
              list.add(path);
              output.add(outputStack);
            }
          }
        }
      }
    }
    return output;
  }

  public static @Nonnull NNList<UpgradePath> getAllRecipes(@Nonnull NNList<ItemStack> validItems) {
    Set<UpgradePath> list = new HashSet<UpgradePath>();
    NNList<ItemStack> items = getRecipes(list, validItems);
    while (!items.isEmpty()) {
      items = getRecipes(list, items);
    }
    return new NNList<>(list);
  }

  public static class UpgradePath {
    private final @Nonnull ItemStack input, upgrade, output;
    private final @Nonnull String id;

    UpgradePath(@Nonnull ItemStack input, @Nonnull ItemStack upgrade, @Nonnull ItemStack output) {
      this.input = input;
      this.upgrade = upgrade;
      this.output = output;
      this.id = input.getItem().getRegistryName() + ":" + getUpgradesAsString(input) + ":" + getUpgradesAsString(output);
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

    @Override
    public int hashCode() {
      return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      UpgradePath other = (UpgradePath) obj;
      if (!id.equals(other.id))
        return false;
      return true;
    }

  }

}
