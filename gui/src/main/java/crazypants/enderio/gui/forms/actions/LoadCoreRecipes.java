package crazypants.enderio.gui.forms.actions;

import java.util.List;

import crazypants.enderio.gui.forms.MainWindow;
import crazypants.enderio.gui.gamedata.RecipeHolder;

public class LoadCoreRecipes implements Runnable {

  private MainWindow parent;

  public LoadCoreRecipes(MainWindow parent) {
    this.parent = parent;
  }

  @Override
  public void run() {
    List<String> errors = RecipeHolder.readCore();
    if (parent != null) {
      parent.getLabelCoreRecipes().setText("Loaded " + RecipeHolder.CORE.getRecipes().getRecipes().size() + " core recipes");
    }
    if (!errors.isEmpty() && parent != null) {
      // TODO
    }
  }

}
