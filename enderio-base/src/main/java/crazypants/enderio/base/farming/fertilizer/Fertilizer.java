package crazypants.enderio.base.farming.fertilizer;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Fertilizer {

  /**
   * Not a fertilizer. Using this handler class any item can be "used" as a fertilizer. Meaning, fertilizing will always fail.
   */
  private static @Nonnull IFertilizer NONE = new AbstractFertilizer(Prep.getEmpty()) {

    @Override
    public Result apply(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc) {
      return new Result(stack, false);
    }

  };

  private static final NNList<IFertilizer> validFertilizers = new NNList<>();

  static {
    registerFertilizer(new Bonemeal(new ItemStack(Items.DYE, 1, 15)));
  }

  public static void registerFertilizer(@Nonnull IFertilizer fertilizer) {
    if (fertilizer.isValid()) {
      validFertilizers.add(fertilizer);
    }
  }

  /**
   * Returns the singleton instance for the fertilizer that was given as parameter. If the given item is no fertilizer, it will return an instance of
   * {@link Fertilizer#NONE}.
   * 
   */
  public static @Nonnull IFertilizer getInstance(@Nonnull ItemStack stack) {
    for (IFertilizer fertilizer : validFertilizers) {
      if (fertilizer.matches(stack)) {
        return fertilizer;
      }
    }
    return NONE;
  }

  /**
   * Returns true if the given item can be used as fertilizer.
   */
  public static boolean isFertilizer(@Nonnull ItemStack stack) {
    return getInstance(stack) != NONE;
  }

}
