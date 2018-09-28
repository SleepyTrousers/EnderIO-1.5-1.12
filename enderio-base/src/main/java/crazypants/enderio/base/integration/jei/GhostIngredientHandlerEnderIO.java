package crazypants.enderio.base.integration.jei;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import mezz.jei.api.gui.IGhostIngredientHandler;

public class GhostIngredientHandlerEnderIO implements IGhostIngredientHandler<GuiContainerBaseEIO> {

  public GhostIngredientHandlerEnderIO() {
  }

  @SuppressWarnings("unchecked")
  @Override
  @Nonnull
  public <I> List<Target<I>> getTargets(@Nonnull GuiContainerBaseEIO gui, @Nonnull I ingredient, boolean doStart) {
    if (gui instanceof IHaveGhostTargets<?>) {
      return (List<Target<I>>) ((IHaveGhostTargets<?>) gui).getGhostTargets();
    }
    return NNList.emptyList();
  }

  @Override
  public void onComplete() {
  }

}
