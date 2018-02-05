package crazypants.enderio.base.machine.recipes;

import java.util.UUID;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MachineRecipes {

  public static void addRecipes() {
    ClearConfigRecipe inst = new ClearConfigRecipe();
    MinecraftForge.EVENT_BUS.register(inst);
    ForgeRegistries.RECIPES.register(inst.setRegistryName(UUID.randomUUID().toString()));
  }

}
