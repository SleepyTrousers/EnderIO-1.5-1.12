package crazypants.enderio.machine.soul;

import static crazypants.enderio.machine.MachineObject.itemFrankenSkull;

import crazypants.enderio.config.Config;
import crazypants.enderio.material.skull.FrankenSkull;
import crazypants.util.CapturedMob;
import net.minecraft.item.ItemStack;

public class SoulBinderReanimationRecipe extends AbstractSoulBinderRecipe {

  public static SoulBinderReanimationRecipe instance = new SoulBinderReanimationRecipe();

  private SoulBinderReanimationRecipe() {
    super(Config.soulBinderReanimationRF, Config.soulBinderReanimationLevels, "SoulBinderReanimationRecipe", "Zombie", "SpecialMobs.SpecialZombie");
  }

  @Override
  public ItemStack getInputStack() {
    return new ItemStack(itemFrankenSkull.getItem(), 1, FrankenSkull.ZOMBIE_CONTROLLER.ordinal());
  }

  @Override
  public ItemStack getOutputStack() {
    return new ItemStack(itemFrankenSkull.getItem(), 1, FrankenSkull.FRANKEN_ZOMBIE.ordinal());
  }
 
  @Override
  protected ItemStack getOutputStack(ItemStack input, CapturedMob mobType) {
    return getOutputStack();
  }

}
