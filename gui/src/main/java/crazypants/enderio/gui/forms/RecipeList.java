package crazypants.enderio.gui.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

import crazypants.enderio.gui.forms.models.RecipeTableModel;
import crazypants.enderio.gui.gamedata.RecipeHolder;

public class RecipeList extends JPanel {

  private static final int NAME_COLUMN = 2;

  private static final long serialVersionUID = 9195886433273124236L;

  private JTable table;
  private Runnable closeAction;

  public RecipeList(Runnable closeAction) {
    this.closeAction = closeAction;
    initialize();
  }

  private void initialize() {
    setBorder(new EmptyBorder(5, 5, 5, 5));
    setLayout(new BorderLayout(0, 0));

    table = new JTable(new RecipeTableModel(RecipeHolder.CORE));
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFillsViewportHeight(true);
    table.setAutoCreateRowSorter(true);

    TableColumnAdjuster tca = new TableColumnAdjuster(table);
    tca.setGrowingColumn(NAME_COLUMN);
    tca.setColumnDataIncluded(true);
    tca.setColumnHeaderIncluded(true);
    tca.setDynamicAdjustment(true);
    tca.adjustColumns();

    JScrollPane scrollPane = new JScrollPane(table);
    add(scrollPane, BorderLayout.CENTER);

    JPanel panelBottom = new JPanel();
    add(panelBottom, BorderLayout.SOUTH);

    JButton btnNewButton_8 = new JButton("Copy to user recipes and edit");
    panelBottom.add(btnNewButton_8);

    JButton btnNewButton_9 = new JButton("Copy to user recipes and disable");
    panelBottom.add(btnNewButton_9);

    Component horizontalStrut_1 = Box.createHorizontalStrut(20);
    panelBottom.add(horizontalStrut_1);

    JButton btnNewButton_10 = new JButton("Show details");
    panelBottom.add(btnNewButton_10);

    JPanel panelTop = new JPanel();
    add(panelTop, BorderLayout.NORTH);
    panelTop.setLayout(new BorderLayout(0, 0));

    JPanel panelBackButton = new JPanel();
    panelTop.add(panelBackButton, BorderLayout.WEST);

    JButton btnBack = new JButton("Back");
    btnBack.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        closeAction.run();
      }
    });
    panelBackButton.add(btnBack);

    JPanel panelQuickFilter = new JPanel();
    panelTop.add(panelQuickFilter, BorderLayout.EAST);

    JLabel lblNewLabel = new JLabel("Quick filter:");
    panelQuickFilter.add(lblNewLabel);

    JTextField

    quickFilter = new JTextField();
    panelQuickFilter.add(quickFilter);
    quickFilter.setColumns(10);

    JButton btnResetQuickFilter = new JButton("");
    panelQuickFilter.add(btnResetQuickFilter);
    btnResetQuickFilter.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        quickFilter.setText("");
      }
    });
    btnResetQuickFilter.setIcon(new ImageIcon(MainWindow.class.getResource("/javax/swing/plaf/metal/icons/ocean/paletteClose.gif")));
    btnResetQuickFilter.setEnabled(false);

    quickFilter.getDocument().addDocumentListener(new DocumentListener() {

      @Override
      public void removeUpdate(DocumentEvent e) {
        changedUpdate(e);
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        changedUpdate(e);
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        if (setFilter(quickFilter.getText())) {
          if (quickFilter.getBackground() == Color.RED) {
            quickFilter.setBackground(new JTextField().getBackground());
            // Nicer, but also resets the cursor:
            // SwingUtilities.updateComponentTreeUI(textField);
          }
        } else {
          quickFilter.setBackground(Color.RED);
        }
        btnResetQuickFilter.setEnabled(!quickFilter.getText().isEmpty());
      }
    });

  }

  public boolean setFilter(String pattern) {
    if (pattern == null || pattern.isEmpty()) {
      ((TableRowSorter<?>) table.getRowSorter()).setRowFilter(null);
    } else {
      ((TableRowSorter<?>) table.getRowSorter()).setRowFilter(new ContainsFilter(pattern, NAME_COLUMN));
    }
    return table.getRowCount() > 0;
  }

  private static class ContainsFilter extends RowFilter<Object, Object> {

    private int[] columns;
    private final String pattern;

    ContainsFilter(String pattern, int... columns) {
      this.pattern = pattern.toLowerCase(Locale.ENGLISH);
      this.columns = columns;
    }

    @Override
    public boolean include(Entry<? extends Object, ? extends Object> value) {
      for (int i = 0; i < columns.length; i++) {
        if (include(value, columns[i])) {
          return true;
        }
      }
      return false;
    }

    protected boolean include(Entry<? extends Object, ? extends Object> value, int index) {
      return value.getStringValue(index).toLowerCase(Locale.ENGLISH).contains(pattern);
    }
  }

}
