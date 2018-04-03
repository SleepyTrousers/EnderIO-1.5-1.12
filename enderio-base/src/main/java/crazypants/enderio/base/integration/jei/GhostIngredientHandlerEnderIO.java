package crazypants.enderio.base.integration.jei;

import java.util.ArrayList;
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
    List<Target<I>> list = new ArrayList<Target<I>>();
    list.addAll(gui.getTargetSlots());
    return list;
  }

  @Override
  public void onComplete() {
  }

}
