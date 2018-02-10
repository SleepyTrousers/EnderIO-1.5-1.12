package crazypants.enderio.base.integration.tic;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

import net.minecraftforge.fluids.Fluid;

public interface ITicHandler {

  void registerTableCast(@Nonnull Things output, @Nonnull Things cast, Fluid fluid, float amount, boolean consumeCast);

  void registerTableCast(@Nonnull Things output, @Nonnull Things cast, @Nonnull Things fluid, float amount, boolean consumeCast);

  void registerBasinCasting(@Nonnull Things output, @Nonnull Things cast, Fluid fluid, int amount);

  void registerBasinCasting(@Nonnull Things output, @Nonnull Things cast, @Nonnull Things fluid, float amount);

  void registerSmelterySmelting(@Nonnull Things input, Fluid output, float amount);

  void registerSmelterySmelting(@Nonnull Things input, @Nonnull Things output, float amount);

  void registerAlloyRecipe(@Nonnull Things output, NNList<Things> input);

}