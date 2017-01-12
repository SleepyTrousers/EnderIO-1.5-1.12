package crazypants.enderio.loot;

import net.minecraft.world.storage.loot.functions.LootFunctionManager;

public class Loot {

  public static void create() {
    LootFunctionManager.registerFunction(new LootSelector.Serializer());
    LootFunctionManager.registerFunction(new SetRandomEnergy.Serializer());
    LootFunctionManager.registerFunction(new SetRandomDarkUpgrade.Serializer());
    AnvilCapacitorRecipe.create();
  }

}
