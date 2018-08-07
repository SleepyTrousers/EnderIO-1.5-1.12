package crazypants.enderio.base.item.darksteel.upgrade.energy;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.EnderIO;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class EnergyUpgradePowerAdapter {

  private static final @Nonnull ResourceLocation KEY = new ResourceLocation(EnderIO.DOMAIN, "powerhandler");

  @SubscribeEvent
  public static void attachCapabilities(@Nonnull AttachCapabilitiesEvent<ItemStack> evt) {
    if (evt.getCapabilities().containsKey(KEY)) {
      return;
    }
    final ItemStack stack = evt.getObject();
    if (stack != null && stack.getItem() instanceof IDarkSteelItem) {
      EnergyUpgradeCap cap = new EnergyUpgradeCap(stack);
      evt.addCapability(KEY, cap);
    }

  }

}
