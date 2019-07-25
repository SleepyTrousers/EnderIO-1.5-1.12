package crazypants.enderio.base.integration.jei;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.material.upgrades.ItemUpgrades;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;

public class UpgradeItemSubtypeInterpreter implements ISubtypeInterpreter {

  public static void registerSubtypes(ISubtypeRegistry subtypeRegistry) {
    UpgradeItemSubtypeInterpreter dsusi = new UpgradeItemSubtypeInterpreter();
    subtypeRegistry.registerSubtypeInterpreter(ModObject.itemDarkSteelUpgrade.getItemNN(), dsusi);
  }

  @Override
  public @Nonnull String apply(@Nullable ItemStack itemStack) {
    if (itemStack == null) {
      throw new NullPointerException("You want me to return something Nonnull for a null ItemStack? F.U.");
    }
    IDarkSteelUpgrade upgrade = ((ItemUpgrades) ModObject.itemDarkSteelUpgrade.getItemNN()).getUpgrade(itemStack);
    if (upgrade != null) {
      return NullHelper.first(NullHelper.first(upgrade.getRegistryName(), "").toString(), "");
    }
    return "base";
  }

}
