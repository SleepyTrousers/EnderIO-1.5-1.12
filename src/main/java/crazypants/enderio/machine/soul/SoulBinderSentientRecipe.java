package crazypants.enderio.machine.soul;

import net.minecraft.item.ItemStack;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.material.FrankenSkull;

public class SoulBinderSentientRecipe extends AbstractSoulBinderRecipe {

  public static SoulBinderSentientRecipe instance = new SoulBinderSentientRecipe();

  private SoulBinderSentientRecipe() {
    super(Config.soulBinderReanimationRF, Config.soulBinderReanimationLevels, "SoulBinderSentientRecipe", "Witch", "enderzoo.WitherWitch");
  }

  @Override
  public ItemStack getInputStack() {
    return new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.ENDER_RESONATOR.ordinal());
  }

  @Override
  public ItemStack getOutputStack() {
    return new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.SENTIENT_ENDER.ordinal());
  }
 
}
