package crazypants.enderio.base.item.darksteel.upgrade.sound;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class SoundDetectorUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "soundDetector";

  public static final @Nonnull SoundDetectorUpgrade INSTANCE = new SoundDetectorUpgrade();

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    event.getRegistry().register(INSTANCE);
  }

  public SoundDetectorUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.sound", new ItemStack(Blocks.NOTEBLOCK), DarkSteelConfig.soundLocatorCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return item.isForSlot(EntityEquipmentSlot.HEAD) && !hasUpgrade(stack, item);
  }

}
