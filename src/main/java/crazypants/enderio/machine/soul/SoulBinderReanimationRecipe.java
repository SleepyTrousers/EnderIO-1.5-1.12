package crazypants.enderio.machine.soul;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.material.FrankenSkull;

public class SoulBinderReanimationRecipe extends AbstractSoulBinderRecipe {

  public static SoulBinderReanimationRecipe instance = new SoulBinderReanimationRecipe();

  private SoulBinderReanimationRecipe() {
    super(Config.soulBinderReanimationRF, Config.soulBinderReanimationLevels, "SoulBinderReanimationRecipe", EntityZombie.class);
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
