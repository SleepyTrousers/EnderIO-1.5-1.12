package crazypants.enderio.machines.darksteel.upgrade.solar;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.machines.EnderIOMachines;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

@EventBusSubscriber(modid = EnderIOMachines.MODID)
public class SolarUpgradeManager {

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    final IForgeRegistry<IDarkSteelUpgrade> registry = event.getRegistry();
    registry.register(SolarUpgrade.SOLAR1);
    registry.register(SolarUpgrade.SOLAR2);
    registry.register(SolarUpgrade.SOLAR3);
  }

}
