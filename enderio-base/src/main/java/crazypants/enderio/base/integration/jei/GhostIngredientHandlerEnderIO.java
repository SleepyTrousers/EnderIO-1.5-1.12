package crazypants.enderio.base.integration.jei;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import mezz.jei.api.gui.IGhostIngredientHandler;

public class GhostIngredientHandlerEnderIO implements IGhostIngredientHandler<GuiContainerBaseEIO> {

  public GhostIngredientHandlerEnderIO() {
  }

  @SuppressWarnings("unchecked")
  @Override
  @Nonnull
  public <I> List<Target<I>> getTargets(@Nonnull GuiContainerBaseEIO gui, @Nonnull I ingredient, boolean doStart) {
    List<Target<I>> list = Collections.emptyList();
    if (gui instanceof IHaveGhostTargets<?>) {
      list = (List<Target<I>>) ((IHaveGhostTargets<?>)gui).getGhostTargets();
    }
    return list;
  }

  @Override
  public void onComplete() {
  }

}
