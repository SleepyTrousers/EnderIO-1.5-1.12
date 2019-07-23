package crazypants.enderio.base.item.darksteel.upgrade.spoon;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class SpoonUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "spoon";

  public static final @Nonnull SpoonUpgrade INSTANCE = new SpoonUpgrade();

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    event.getRegistry().register(INSTANCE);
  }

  public SpoonUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.spoon", new ItemStack(Items.DIAMOND_SHOVEL), DarkSteelConfig.spoonCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return item.isPickaxe() && item.hasUpgradeCallbacks(this) && EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack) && !hasUpgrade(stack, item);
  }

  @Override
  @Nonnull
  public List<IDarkSteelUpgrade> getDependencies() {
    return new NNList<>(EnergyUpgrade.UPGRADES.get(0));
  }

  @Override
  @Nonnull
  public List<Supplier<String>> getItemClassesForTooltip() {
    return new NNList<>(Lang.DSU_CLASS_TOOLS_PICKAXE::get);
  }

  @Override
  public boolean canOtherBeRemoved(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item, @Nonnull IDarkSteelUpgrade other) {
    return !EnergyUpgradeManager.isLowestPowerUpgrade(other);
  }

}
