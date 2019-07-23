package crazypants.enderio.base.item.darksteel.upgrade.explosive;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ExplosiveDepthUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "depth";

  public static final @Nonnull ExplosiveDepthUpgrade INSTANCE = new ExplosiveDepthUpgrade();

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    event.getRegistry().register(INSTANCE);
  }

  public ExplosiveDepthUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade." + UPGRADE_NAME, new ItemStack(Items.SKULL, 1, 4), DarkSteelConfig.explosiveDepthUpgradeCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return ExplosiveUpgrade.INSTANCE.hasAnyUpgradeVariant(stack) && !hasUpgrade(stack);
  }

  @Override
  @Nonnull
  public List<IDarkSteelUpgrade> getDependencies() {
    return new NNList<>(ExplosiveUpgrade.INSTANCE);
  }

  @Override
  @Nonnull
  public List<Supplier<String>> getItemClassesForTooltip() {
    return ExplosiveUpgrade.INSTANCE.getItemClassesForTooltip();
  }

  @Override
  public boolean canOtherBeRemoved(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item, @Nonnull IDarkSteelUpgrade other) {
    return other != ExplosiveUpgrade.INSTANCE;
  }

}
