package crazypants.enderio.base.integration.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager.UpgradePath;
import crazypants.enderio.base.handler.darksteel.UpgradeRegistry;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.lang.Lang;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class DescriptionRecipeCategory {

  public static void register(IModRegistry registry) {
    NNList<ItemStack> items = new NNList<>();
    NNList<Item> dsitems = new NNList<>();
    for (IModObject mo : ModObjectRegistry.getRegistry()) {
      final Item item = mo.getItem();
      if (item != null) {
        final CreativeTabs creativeTab = item.getCreativeTab();
        if (creativeTab != null) {
          if (item instanceof IDarkSteelItem) {
            dsitems.add(item);
          } else {
            item.getSubItems(creativeTab, items);
          }
        }
      }
    }
    items.apply(new Callback<ItemStack>() {
      @Override
      public void apply(@Nonnull ItemStack itemStack) {
        NNList<String> allTooltips = SpecialTooltipHandler.getAllTooltips(itemStack);
        if (!allTooltips.isEmpty()) {
          allTooltips.add(0, TextFormatting.WHITE.toString() + TextFormatting.BOLD + itemStack.getDisplayName() + TextFormatting.RESET);
          allTooltips.add(1, "");
          registry.addIngredientInfo(itemStack, VanillaTypes.ITEM, allTooltips.toArray(new String[0]));
        }
      }
    });
    dsitems.apply(new Callback<Item>() {
      @Override
      public void apply(@Nonnull Item item) {
        ItemStack itemStack = new ItemStack(item);
        NNList<String> allTooltips;
        try {
          DarkSteelRecipeManager.setSkipUpgradeTooltips(true);
          allTooltips = SpecialTooltipHandler.getAllTooltips(itemStack);
        } finally {
          DarkSteelRecipeManager.setSkipUpgradeTooltips(false);
        }

        NNList<UpgradePath> list = DarkSteelRecipeManager.getAllRecipes(new NNList<>(itemStack));
        Map<String, IDarkSteelUpgrade> set = new HashMap<>();
        for (UpgradePath upgradePath : list) {
          set.put(EnderIO.lang.localizeExact(upgradePath.getDsupgrade().getUnlocalizedName() + ".name"), upgradePath.getDsupgrade());
        }
        List<String> names = new ArrayList<>(set.keySet());
        Collections.sort(names);
        if (!names.isEmpty()) {
          allTooltips.add("");
          allTooltips.add(Lang.DARK_STEEL_ANVIL_UPGRADES_ALL.get());
        }
        for (String name : names) {
          IDarkSteelUpgrade upgrade = set.get(name);
          if (upgrade != null) {
            allTooltips.add("");
            if (upgrade instanceof IAdvancedTooltipProvider) {
              ((IAdvancedTooltipProvider) upgrade).addDetailedEntries(itemStack, null, allTooltips, false);
            } else {
              allTooltips.add(Lang.DARK_STEEL_LEVELS1.get(TextFormatting.DARK_AQUA, name));
              SpecialTooltipHandler.addDetailedTooltipFromResources(allTooltips, upgrade.getUnlocalizedName());
            }
            allTooltips.add(Lang.DARK_STEEL_LEVELS2.get(TextFormatting.DARK_AQUA, TextFormatting.ITALIC,
                UpgradeRegistry.getUpgradeItem(upgrade).getDisplayName(), upgrade.getLevelCost()) + TextFormatting.RESET);
          }
        }

        if (!allTooltips.isEmpty()) {
          allTooltips.add(0, TextFormatting.WHITE.toString() + TextFormatting.BOLD + itemStack.getDisplayName() + TextFormatting.RESET);
          allTooltips.add(1, "");
          registry.addIngredientInfo(itemStack, VanillaTypes.ITEM, allTooltips.toArray(new String[0]));
        }
      }
    });
  }

}
