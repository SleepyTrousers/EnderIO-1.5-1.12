package crazypants.enderio.integration.tic.queues;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.integration.tic.ITicHandler;
import net.minecraftforge.fluids.Fluid;

public class TicHandler implements ITicHandler {

  public static final @Nonnull TicHandler instance = new TicHandler();

  @Override
  public void registerTableCast(@Nonnull Things output, @Nonnull Things cast, Fluid fluid, float amount, boolean consumeCast) {
    if (!output.isPotentiallyValid() || !cast.isPotentiallyValid() || fluid == null) {
      return;
    }

    TiCQueues.getCastQueue().add(new CastQueue(output, cast, fluid, amount, consumeCast));
  }

  @Override
  public void registerTableCast(@Nonnull Things output, @Nonnull Things cast, @Nonnull Things fluid, float amount, boolean consumeCast) {
    if (!output.isPotentiallyValid() || !cast.isPotentiallyValid() || !fluid.isPotentiallyValid()) {
      return;
    }

    TiCQueues.getCastQueue().add(new CastQueue(output, cast, fluid, amount, consumeCast));
  }

  @Override
  public void registerBasinCasting(@Nonnull Things output, @Nonnull Things cast, Fluid fluid, int amount) {
    if (!output.isPotentiallyValid() || fluid == null) {
      return;
    }
    TiCQueues.getBasinQueue().add(new BasinQueue(output, cast, fluid, amount));
  }

  @Override
  public void registerBasinCasting(@Nonnull Things output, @Nonnull Things cast, @Nonnull Things fluid, float amount) {
    if (!output.isPotentiallyValid() || !fluid.isPotentiallyValid()) {
      return;
    }
    TiCQueues.getBasinQueue().add(new BasinQueue(output, cast, fluid, amount));
  }

  @Override
  public void registerSmelterySmelting(@Nonnull Things input, Fluid output, float amount) {
    if (!input.isPotentiallyValid() || output == null) {
      return;
    }
    TiCQueues.getSmeltQueue().add(new SmeltQueue(input, output, amount));
  }

  @Override
  public void registerSmelterySmelting(@Nonnull Things input, @Nonnull Things output, float amount) {
    if (!input.isPotentiallyValid() || !output.isPotentiallyValid()) {
      return;
    }
    TiCQueues.getSmeltQueue().add(new SmeltQueue(input, output, amount));
  }

  @Override
  public void registerAlloyRecipe(@Nonnull Things output, NNList<Things> input) {
    if (!output.isPotentiallyValid() || input.size() < 2) {
      return;
    }
    for (Things thing : input) {
      if (thing == null || !thing.isPotentiallyValid()) {
        return;
      }
    }

    TiCQueues.getAlloyQueue().add(Pair.of(output, input));
  }

}
