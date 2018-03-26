package crazypants.enderio.base.integration.jei;

import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.filter.gui.BasicItemFilterGui;
import mezz.jei.api.gui.IGhostIngredientHandler;

public class GhostIngredientHandlerEnderIO implements IGhostIngredientHandler<BasicItemFilterGui> {

  public GhostIngredientHandlerEnderIO() {
  }

  @Override
  @Nonnull
  public <I> List<Target<I>> getTargets(@Nonnull BasicItemFilterGui gui, @Nonnull I ingredient, boolean doStart) {
    return gui.<I> getTargetSlots();
  }

  @Override
  public void onComplete() {
  }

}
