package crazypants.enderio.base.item.darksteel.upgrade.direct;

import java.util.Iterator;
import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.material.alloy.Alloy;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class DirectUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "direct";
  private static final Random random = new Random();

  public static final @Nonnull DirectUpgrade INSTANCE = new DirectUpgrade();

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    event.getRegistry().register(INSTANCE);
  }

  public DirectUpgrade() {
    super(UPGRADE_NAME, 0, "enderio.darksteel.upgrade." + UPGRADE_NAME, Alloy.VIBRANT_ALLOY.getStackBlock(), DarkSteelConfig.directCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return item.isForSlot(EntityEquipmentSlot.MAINHAND) && EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack) && !hasAnyUpgradeVariant(stack);
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void blockDropEvent(BlockEvent.HarvestDropsEvent event) {
    if (event.getHarvester() == null) {
      return;
    }

    for (EnumHand hand : EnumHand.values()) {
      ItemStack stack = event.getHarvester().getHeldItem(NullHelper.notnullJ(hand, "EnumHand.values()"));

      if (hasAnyUpgradeVariant(stack) && EnergyUpgradeManager.getEnergyStored(stack) > 0) {
        EnergyUpgradeManager.extractEnergy(stack, doDirect(event) * DarkSteelConfig.directEnergyCost.get(), false);
        return;
      }
    }
  }

  // also used by TraitPickup
  public static int doDirect(BlockEvent.HarvestDropsEvent event) {
    int count = 0;
    final InventoryPlayer inventory = event.getHarvester().inventory;
    for (Iterator<ItemStack> iterator = event.getDrops().iterator(); iterator.hasNext();) {
      ItemStack next = NullHelper.notnullF(iterator.next(), "null stack in HarvestDropsEvent");
      if (random.nextFloat() < event.getDropChance()) {
        if (inventory.addItemStackToInventory(next)) {
          count++;
          iterator.remove();
        }
      } else {
        iterator.remove();
      }
    }
    event.setDropChance(1); // we already implemented the drop chance
    return count;
  }

}
