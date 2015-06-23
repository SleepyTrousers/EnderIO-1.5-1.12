package crazypants.enderio.machine.soul;

import net.minecraft.item.ItemStack;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.material.FrankenSkull;

public class SoulBinderReanimationRecipe extends AbstractSoulBinderRecipe {

  public static SoulBinderReanimationRecipe instance = new SoulBinderReanimationRecipe();

  private SoulBinderReanimationRecipe() {
    super(Config.soulBinderReanimationRF, Config.soulBinderReanimationLevels, "SoulBinderReanimationRecipe", "Zombie", "SpecialMobs.SpecialZombie");
  }

  @Override
  public ItemStack getInputStack() {
    return new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.ZOMBIE_CONTROLLER.ordinal());
  }

  @Override
  public ItemStack getOutputStack() {
    return new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.FRANKEN_ZOMBIE.ordinal());
  }
 
}
