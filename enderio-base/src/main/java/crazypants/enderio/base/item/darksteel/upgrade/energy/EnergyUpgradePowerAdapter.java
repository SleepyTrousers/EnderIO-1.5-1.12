package crazypants.enderio.base.item.darksteel.upgrade.energy;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.EnderIO;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnergyUpgradePowerAdapter {

  public static void init(@Nonnull FMLPreInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(EnergyUpgradePowerAdapter.class);
  }

  private static final @Nonnull ResourceLocation KEY = new ResourceLocation(EnderIO.DOMAIN, "powerhandler");

  @SubscribeEvent
  public static void attachCapabilities(@Nonnull AttachCapabilitiesEvent<ItemStack> evt) {
    if (evt.getCapabilities().containsKey(KEY)) {
      return;
    }
    if (evt.getObject().getItem() instanceof IDarkSteelItem) {
      EnergyUpgadeCap cap = new EnergyUpgadeCap(evt.getObject());
      evt.addCapability(KEY, cap);
    }

  }

}
