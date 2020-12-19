package crazypants.enderio.gui.forms;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import crazypants.enderio.gui.forms.models.RecipeTableModel;
import crazypants.enderio.gui.gamedata.RecipeHolder;

public class RecipeList extends JPanel {

  private JTable table;

  public RecipeList() {
    setBorder(new EmptyBorder(5, 5, 5, 5));
    setLayout(new BorderLayout(0, 0));

    table = new JTable(new RecipeTableModel(RecipeHolder.CORE));
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFillsViewportHeight(true);
    JScrollPane scrollPane = new JScrollPane(table);
    add(scrollPane, BorderLayout.CENTER);
  }

}
