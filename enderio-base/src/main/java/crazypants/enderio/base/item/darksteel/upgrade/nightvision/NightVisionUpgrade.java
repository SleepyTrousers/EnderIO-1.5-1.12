package crazypants.enderio.base.item.darksteel.upgrade.nightvision;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IRule;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.handler.darksteel.Rules;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class NightVisionUpgrade extends AbstractUpgrade {

  // TODO 1.13 change IDs to nicht_vision
  private static final @Nonnull String UPGRADE_NAME = "nightVision";

  public static final @Nonnull NightVisionUpgrade INSTANCE = new NightVisionUpgrade();

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    event.getRegistry().register(INSTANCE);
  }

  public NightVisionUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.nightVision", DarkSteelConfig.nightVisionCost);
  }

  @Override
  @Nonnull
  public List<IRule> getRules() {
    return new NNList<>(Rules.forSlot(EntityEquipmentSlot.HEAD), Rules.itemTypeTooltip(EntityEquipmentSlot.HEAD));
  }

}
