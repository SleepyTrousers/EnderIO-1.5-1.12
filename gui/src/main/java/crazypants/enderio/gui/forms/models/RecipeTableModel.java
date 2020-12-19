package crazypants.enderio.gui.forms.models;

import java.util.stream.Collectors;

import javax.swing.table.AbstractTableModel;

import crazypants.enderio.gui.gamedata.RecipeHolder;
import crazypants.enderio.gui.xml.AbstractConditional;
import crazypants.enderio.gui.xml.Capacitor;
import crazypants.enderio.gui.xml.Grindingball;
import crazypants.enderio.gui.xml.Recipe;

public class RecipeTableModel extends AbstractTableModel {

  private static final long serialVersionUID = 565945544292492321L;

  private final RecipeHolder holder;

  public RecipeTableModel(RecipeHolder holder) {
    this.holder = holder;
  }

  @Override
  public int getRowCount() {
    return holder.getRecipes().getRecipes().size();
  }

  @Override
  public int getColumnCount() {
    return 4;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    AbstractConditional recipe = holder.getRecipes().getRecipes().get(rowIndex);
    switch (columnIndex) {
    case 0:
      if (recipe instanceof Recipe) {
        return "Recipe: " + ((Recipe) recipe).getCraftings().stream().map(crafting -> ((Recipe) recipe).getNameForMappping(crafting.getClass()))
            .collect(Collectors.joining("/"));
      } else if (recipe instanceof Grindingball) {
        return "Grindingball";
      } else if (recipe instanceof Capacitor) {
        return "Capacitor Key";
      } else {
        return "other";
      }
    case 1:
      return recipe.getName();
    case 2:
      if (recipe instanceof Recipe) {
        return ((Recipe) recipe).isRequired();
      } else if (recipe instanceof Grindingball) {
        return ((Grindingball) recipe).isRequired();
      } else if (recipe instanceof Capacitor) {
        return ((Capacitor) recipe).isRequired();
      } else {
        return null;
      }
    case 3:
      if (recipe instanceof Recipe) {
        return ((Recipe) recipe).isDisabled();
      } else if (recipe instanceof Grindingball) {
        return ((Grindingball) recipe).isDisabled();
      } else if (recipe instanceof Capacitor) {
        return ((Capacitor) recipe).isDisabled();
      } else {
        return null;
      }
    default:
      break;
    }
    return null;
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch (columnIndex) {
    case 0:
    case 1:
      return String.class;
    case 2:
    case 3:
      return Boolean.class;
    default:
      return Object.class;
    }
  }

  @Override
  public String getColumnName(int columnIndex) {
    switch (columnIndex) {
    case 0:
      return "Type";
    case 1:
      return "Name";
    case 2:
      return "Required";
    case 3:
      return "Disabled";
    default:
      return "Other";
    }
  }

}
