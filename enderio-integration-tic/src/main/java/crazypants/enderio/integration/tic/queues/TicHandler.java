package crazypants.enderio.integration.tic.queues;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.base.integration.tic.ITicHandler;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class TicHandler implements ITicHandler {

  public static final @Nonnull TicHandler instance = new TicHandler();

  @Override
  public void registerTableCast(@Nonnull ItemStack result, @Nonnull ItemStack cast, Fluid fluid, float amount, boolean consumeCast) {
    if (Prep.isInvalid(result) || Prep.isInvalid(cast) || fluid == null) {
      return;
    }

    TiCQueues.getCastQueue().add(new CastQueue(result, cast, fluid, amount, consumeCast));
  }

  @Override
  public String registerTableCast(@Nonnull ItemStack result, @Nonnull ItemStack cast, @Nonnull ItemStack item, float amount, boolean consumeCast,
      boolean simulate) {
    if (Prep.isInvalid(result)) {
      return "Result item not found";
    }

    if (Prep.isInvalid(cast)) {
      return "Cast item not found";
    }

    if (Prep.isInvalid(item)) {
      return "Fluid item not found";
    }

    if (!simulate) {
      item = item.copy();
      item.setCount(1);
      TiCQueues.getCastQueue().add(new CastQueue(result, cast, item, amount, consumeCast));
    }

    return null;
  }

  @Override
  public void registerBasinCasting(@Nonnull ItemStack output, @Nonnull ItemStack cast, Fluid fluid, int amount) {
    if (Prep.isInvalid(output) || fluid == null) {
      return;
    }
    TiCQueues.getBasinQueue().add(new BasinQueue(output, cast, fluid, amount));
  }

  @Override
  public void registerBasinCasting(@Nonnull ItemStack output, @Nonnull ItemStack cast, @Nonnull ItemStack fluid, int amount) {
    if (Prep.isInvalid(output) || Prep.isInvalid(fluid)) {
      return;
    }
    TiCQueues.getBasinQueue().add(new BasinQueue(output, cast, fluid, amount));
  }

  @Override
  public void registerSmelterySmelting(@Nonnull ItemStack input, Fluid output, float amount) {
    if (Prep.isInvalid(input) || output == null) {
      return;
    }
    TiCQueues.getSmeltQueue().add(new SmeltQueue(input, output, amount));
  }

  @Override
  public void registerSmelterySmelting(@Nonnull ItemStack input, @Nonnull ItemStack output, float amount) {
    if (Prep.isInvalid(input) || Prep.isInvalid(output)) {
      return;
    }
    TiCQueues.getSmeltQueue().add(new SmeltQueue(input, output, amount));
  }

  @Override
  public void registerAlloyRecipe(@Nonnull ItemStack result, ItemStack... input) {
    if (Prep.isInvalid(result) || input.length < 2) {
      return;
    }
    for (ItemStack itemStack : input) {
      if (itemStack == null || Prep.isInvalid(itemStack)) {
        return;
      }
    }

    TiCQueues.getAlloyQueue().add(Pair.of(result, input));
  }

}
