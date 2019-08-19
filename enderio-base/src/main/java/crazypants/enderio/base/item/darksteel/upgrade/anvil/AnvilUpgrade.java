package crazypants.enderio.base.item.darksteel.upgrade.anvil;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IRule;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.handler.darksteel.Rules;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class AnvilUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "anvil";

  public static final @Nonnull NNList<AnvilUpgrade> INSTANCES = new NNList<>(new AnvilUpgrade(0), new AnvilUpgrade(1), new AnvilUpgrade(2));

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    INSTANCES.apply(instance -> event.getRegistry().register(instance));
  }

  public static AnvilUpgrade loadAnyFromItem(@Nonnull ItemStack stack) {
    int level = INSTANCES.get(0).getUpgradeVariantLevel(stack);
    return level < 0 ? null : INSTANCES.get(level);
  }

  public static AnvilUpgrade getHighestEquippedEquipped(@Nonnull EntityPlayer player) {
    int level = -1;
    for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
      level = Math.max(level, INSTANCES.get(0).getUpgradeVariantLevel(player.getItemStackFromSlot(NullHelper.notnullJ(slot, "Enum.values()"))));
    }
    return level < 0 ? null : INSTANCES.get(level);
  }

  public AnvilUpgrade(int level) {
    super(UPGRADE_NAME, level, "enderio.darksteel.upgrade." + UPGRADE_NAME + "." + level, DarkSteelConfig.anvilUpgradeCost.get(level));
  }

  @Override
  @Nonnull
  public List<IRule> getRules() {
    return new NNList<>(Rules.withLevels(variant, INSTANCES), Rules.itemTypeTooltip(Lang.DSU_CLASS_EVERYTHING));
  }

  public boolean allowsEditingOtherEquippedItems() {
    return variant >= 1;
  }

  public boolean allowsEditingSlotItems() {
    return variant >= 2;
  }

  public boolean allowsAnvilRecipes() {
    return variant >= 2;
  }

}
