package crazypants.enderio.integration.forestry.fertilizer;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.IFertilizer;
import crazypants.enderio.api.farm.IFertilizerResult;
import crazypants.enderio.base.farming.fertilizer.Bonemeal;
import crazypants.enderio.base.farming.fertilizer.FertilizerResult;
import crazypants.enderio.integration.forestry.EnderIOIntegrationForestry;
import crazypants.enderio.integration.forestry.ForestryItemStacks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

public class ForestryFertilizer extends IForgeRegistryEntry.Impl<IFertilizer> implements IFertilizer {

  private Bonemeal bonemealDelegate = null;

  public ForestryFertilizer() {
    setRegistryName(EnderIOIntegrationForestry.MODID, "fertilizer");
  }

  @Override
  public boolean matches(@Nonnull ItemStack stack) {
    return isValid() && bonemealDelegate.matches(stack);
  }

  @Override
  public IFertilizerResult apply(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc) {
    if (isValid()) {
      return bonemealDelegate.apply(stack, player, world, bc);
    } else {
      return new FertilizerResult(stack, false);
    }
  }

  @Override
  public boolean applyOnAir() {
    return false;
  }

  @Override
  public boolean applyOnPlant() {
    return true;
  }

  public boolean isValid() {
    if (bonemealDelegate == null && ForestryItemStacks.FORESTRY_FERTILIZER != null) {
      bonemealDelegate = new Bonemeal(ForestryItemStacks.FORESTRY_FERTILIZER);
    }
    return bonemealDelegate != null;
  }

}
