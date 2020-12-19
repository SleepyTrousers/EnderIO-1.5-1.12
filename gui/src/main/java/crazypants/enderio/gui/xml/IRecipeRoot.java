package crazypants.enderio.gui.xml;

import java.util.List;

import javax.annotation.Nonnull;

public interface IRecipeRoot extends IRecipeConfigElement {

  @Nonnull
  List<AbstractConditional> getRecipes();

}
