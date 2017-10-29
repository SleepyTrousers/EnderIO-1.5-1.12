package crazypants.enderio.machine.soul;

import static crazypants.enderio.machine.MachineObject.itemFrankenSkull;

import crazypants.enderio.config.Config;
import crazypants.enderio.material.skull.FrankenSkull;
import crazypants.util.CapturedMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SoulBinderSentientRecipe extends AbstractSoulBinderRecipe {

  public static SoulBinderSentientRecipe instance = new SoulBinderSentientRecipe();

  private SoulBinderSentientRecipe() {
    super(Config.soulBinderReanimationRF, Config.soulBinderReanimationLevels, "SoulBinderSentientRecipe", new ResourceLocation("witch"), new ResourceLocation("enderzoo","WitherWitch"));
  }

  @Override
  public ItemStack getInputStack() {
    return new ItemStack(itemFrankenSkull.getItem(), 1, FrankenSkull.ENDER_RESONATOR.ordinal()); //TODO Fix when FrankenSkull are added
  }

  @Override
  public ItemStack getOutputStack() {
    return new ItemStack(itemFrankenSkull.getItem(), 1, FrankenSkull.SENTIENT_ENDER.ordinal());
  }
 
  @Override
  protected ItemStack getOutputStack(ItemStack input, CapturedMob mobType) {
    return getOutputStack();
  }

}
