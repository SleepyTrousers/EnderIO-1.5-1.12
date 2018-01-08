package crazypants.enderio.base.loot;

import javax.annotation.Nonnull;

import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Loot {

  public static void init(@Nonnull FMLPreInitializationEvent event) {
    LootFunctionManager.registerFunction(new LootSelector.Serializer());
    LootFunctionManager.registerFunction(new SetRandomEnergy.Serializer());
    LootFunctionManager.registerFunction(new SetRandomDarkUpgrade.Serializer());
    AnvilCapacitorRecipe.create();
  }

}
