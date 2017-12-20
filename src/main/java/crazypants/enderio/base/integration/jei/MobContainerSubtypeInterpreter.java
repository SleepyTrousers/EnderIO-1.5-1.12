package crazypants.enderio.base.integration.jei;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.util.CapturedMob;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;

public class MobContainerSubtypeInterpreter implements ISubtypeInterpreter {

  public static void registerSubtypes(ISubtypeRegistry subtypeRegistry) {
    MobContainerSubtypeInterpreter dsusi = new MobContainerSubtypeInterpreter();
    subtypeRegistry.registerSubtypeInterpreter(ModObject.itemSoulVial.getItemNN(), dsusi);
    subtypeRegistry.registerSubtypeInterpreter(ModObject.itemBrokenSpawner.getItemNN(), dsusi);
  }

  @Override
  @Nullable
  public String getSubtypeInfo(@Nonnull ItemStack itemStack) {
    CapturedMob capturedMob = CapturedMob.create(itemStack);
    if (capturedMob != null) {
      return capturedMob.getDisplayName();
    }
    return "";
  }

}
