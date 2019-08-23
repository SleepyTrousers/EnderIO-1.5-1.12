package crazypants.enderio.base.handler.darksteel;

import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.EnderIO;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class DarkSteelRepairRecipe {

  @SubscribeEvent
  public static void handleAnvilEvent(AnvilUpdateEvent evt) {
    if (evt.getLeft().getCount() == 1 && evt.getLeft().getItem() instanceof IDarkSteelItem) {
      final ItemStack targetStack = evt.getLeft();
      final IDarkSteelItem item = (IDarkSteelItem) targetStack.getItem();
      if (item.isItemForRepair(evt.getRight())) {
        final ItemStack ingots = evt.getRight();

        // repair event
        final int maxIngots = item.getIngotsRequiredForFullRepair();
        int ingouts = ingots.getCount();
        final int damage = targetStack.getItemDamage();
        final int maxDamage = targetStack.getMaxDamage();

        final double damPerc = (double) damage / maxDamage;
        int requiredIngots = (int) Math.ceil(damPerc * maxIngots);
        if (ingouts > requiredIngots) {
          ingouts = requiredIngots;
        }

        final int damageAddedPerIngot = (int) Math.ceil((double) maxDamage / maxIngots);
        final int totalDamageRemoved = damageAddedPerIngot * ingouts;

        final ItemStack resultStack = targetStack.copy();
        resultStack.setItemDamage(Math.max(0, damage - totalDamageRemoved));

        evt.setOutput(resultStack);
        evt.setCost(ingouts + (int) Math.ceil(DarkSteelRepairRecipe.getEnchantmentRepairCost(resultStack.copy()) / 2d));
        evt.setMaterialCost(ingouts);
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
          break;
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

}
