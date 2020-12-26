package crazypants.enderio.gui.forms.models;

import java.util.List;
import java.util.stream.Collectors;

import javax.swing.table.AbstractTableModel;

import crazypants.enderio.gui.gamedata.RecipeHolder;
import crazypants.enderio.gui.xml.AbstractConditional;
import crazypants.enderio.gui.xml.Alias;
import crazypants.enderio.gui.xml.Capacitor;
import crazypants.enderio.gui.xml.Grindingball;
import crazypants.enderio.gui.xml.Recipe;

public class RecipeTableModel extends AbstractTableModel {

  private static final long serialVersionUID = 565945544292492321L;

  private final RecipeHolder holder;

  public RecipeTableModel(RecipeHolder holder) {
    this.holder = holder;
    holder.registerCallback(this::fireTableDataChanged);
  }

  @Override
  public int getRowCount() {
    return holder.getRecipes().getRecipes().size();
  }

  @Override
  public int getColumnCount() {
    return 5;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    AbstractConditional recipe = holder.getRecipes().getRecipes().get(rowIndex);
    switch (columnIndex) {
    case 0:
      return recipe.getSource();
    case 1:
      if (recipe instanceof Recipe) {
        return "Recipe: " + ((Recipe) recipe).getCraftings().stream().map(crafting -> ((Recipe) recipe).getNameForMappping(crafting.getClass()))
            .collect(Collectors.joining("/"));
      } else if (recipe instanceof Grindingball) {
        return "Grindingball";
      } else if (recipe instanceof Capacitor) {
        return "Capacitor Key";
      } else if (recipe instanceof Alias) {
        return "Alias";
      } else {
        return "other";
      }
    case 2:
      return recipe.getName();
    case 3:
      return recipe instanceof Alias ? null : recipe.isRequired();
    case 4:
      return recipe instanceof Alias ? null : recipe.isDisabled();
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
    case 2:
      return String.class;
    case 3:
    case 4:
      return Boolean.class;
    default:
      return Object.class;
    }
  }

  @Override
  public String getColumnName(int columnIndex) {
    switch (columnIndex) {
    case 0:
      return "Source";
    case 1:
      return "Type";
    case 2:
      return "Name";
    case 3:
      return "Required";
    case 4:
      return "Disabled";
    default:
      return "Other";
    }
  }

  public AbstractConditional getRecipeInRow(int row) {
    List<AbstractConditional> recipes = holder.getRecipes().getRecipes();
    if (row > 0 && row < recipes.size()) {
      return recipes.get(row);
    }
    return null;
  }

}
