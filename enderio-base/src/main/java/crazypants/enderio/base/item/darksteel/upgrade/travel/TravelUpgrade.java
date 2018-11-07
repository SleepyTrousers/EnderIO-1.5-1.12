package crazypants.enderio.base.item.darksteel.upgrade.travel;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.material.material.Material;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class TravelUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "travel";

  public static final @Nonnull TravelUpgrade INSTANCE = new TravelUpgrade();

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    event.getRegistry().register(INSTANCE);
  }

  public TravelUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.travel", Material.ENDER_CRYSTAL.getStack(), DarkSteelConfig.travelCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return item.isForSlot(EntityEquipmentSlot.MAINHAND) && item.hasUpgradeCallbacks(this) && EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)
        && !hasUpgrade(stack, item);
  }

}
