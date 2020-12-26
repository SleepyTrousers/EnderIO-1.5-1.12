package crazypants.enderio.gui.forms;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import crazypants.enderio.gui.forms.ThingsEntry.Position;
import crazypants.enderio.gui.gamedata.RecipeHolder;
import crazypants.enderio.gui.xml.AbstractConditional;
import crazypants.enderio.gui.xml.Alias;
import crazypants.enderio.gui.xml.NameField;
import crazypants.enderio.gui.xml.NameField.NameValue;

public class ThingsDialog extends AbstractEditDialog<NameField> {

  private static final long serialVersionUID = -5665046007268630990L;

  private List<ThingsEntry> entries;
  private JTextField textFieldNbt;
  private JScrollPane scrollPane;
  private JButton btnAdd;
  private JPanel panelViewport;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      ThingsDialog dialog = new ThingsDialog((Frame) null, "Test", null, true);
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      for (AbstractConditional r : RecipeHolder.CORE.getRecipes().getRecipes()) {
        if (r instanceof Alias) {
          dialog.setElement(((Alias) r).getItem());
          break;
        }
      }
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public ThingsDialog(@Nullable Dialog owner, @Nonnull String title, @Nullable NameField element, boolean editable) {
    super(owner, title, element, editable);
  }

  public ThingsDialog(@Nullable Frame owner, @Nonnull String title, @Nullable NameField element, boolean editable) {
    super(owner, title, element, editable);
  }

  @Override
  protected @Nonnull JPanel makePanel() {
    entries = new ArrayList<>();

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    scrollPane = new JScrollPane();
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    panelViewport = new JPanel();
    scrollPane.setViewportView(panelViewport);
    panelViewport.setLayout(null);
    panel.add(scrollPane, BorderLayout.CENTER);

    if (editable) {
      btnAdd = new JButton("Add");
      btnAdd.addActionListener(unused -> {
        addEntry();
        resfreshEntries();
      });
    }
    resfreshEntries();

    JPanel nbtPane = new JPanel();
    nbtPane.setLayout(new BorderLayout());
    nbtPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    panel.add(nbtPane, BorderLayout.SOUTH);

    JLabel nbtLabel = new JLabel("nbt:");
    nbtPane.add(nbtLabel, BorderLayout.WEST);

    textFieldNbt = new JTextField();
    nbtPane.add(textFieldNbt, BorderLayout.CENTER);
    textFieldNbt.setEnabled(editable);

    return panel;
  }

  protected ThingsEntry addEntry() {
    ThingsEntry entry = new ThingsEntry(scrollPane, editable);
    entry.setBounds(5, 5, 421, 63);
    entry.setDoDel(() -> {
      entries.remove(entry);
      resfreshEntries();
    });
    entry.setDoUp(() -> {
      int idx = entries.indexOf(entry);
      entries.remove(idx);
      entries.add(idx - 1, entry);
      resfreshEntries();
    });
    entry.setDoDown(() -> {
      int idx = entries.indexOf(entry);
      entries.remove(idx);
      entries.add(idx + 1, entry);
      resfreshEntries();
    });
    entries.add(entry);
    return entry;
  }

  protected void resfreshEntries() {
    if (editable && entries.isEmpty()) {
      addEntry(); // never be completely empty
    }
    panelViewport.removeAll();
    for (ThingsEntry e : entries) {
      panelViewport.add(e);
    }
    if (btnAdd != null) {
      panelViewport.add(btnAdd);
      btnAdd.requestFocusInWindow();
    }
    validate();
    repaint();
  }

  @Override
  public void validate() {
    int w = scrollPane.getViewport().getExtentSize().width;
    reflow();
    super.validate();
    if (scrollPane.getViewport().getExtentSize().width != w) {
      // vertical scrollbar got added/removed
      reflow();
      super.validate();
    }
  }

  private void reflow() {
    if (!entries.isEmpty()) {
      int h = entries.get(0).getHeight();
      int w = Math.max(300, scrollPane.getViewport().getExtentSize().width - 10);
      int idx = 0;
      for (ThingsEntry entry : entries) {
        entry.setPosition(Position.compute(idx, entries.size()));
        entry.setBounds(5, idx * (h + 5) + 5, w, h);
        idx++;
      }
      int yMax = entries.size() * (h + 5) + 5;
      if (btnAdd != null) {
        // btnAdd.setBounds(5, yMax, btnAdd.getPreferredSize().width, btnAdd.getPreferredSize().height); // normal button size
        btnAdd.setBounds(5, yMax, w, h);
        yMax += btnAdd.getHeight() + 5;
      }
      panelViewport.setPreferredSize(new Dimension(w + 10, yMax));
    }
  }

  @Override
  public void setElement(@Nonnull NameField element) {
    entries.clear();
    for (NameValue name : element.getNames()) {
      if (!name.isEmpty()) {
        addEntry().setEntry(name);
      }
    }
    if (editable) {
      addEntry(); // always have an empty one at the end
    }
    resfreshEntries();
  }

  @Override
  public @Nonnull NameField getElement() {
    NameField result = new NameField();
    for (ThingsEntry entry : entries) {
      if (!entry.isEmpty()) {
        result.add(entry.getEntry());
      }
    }
    return result;
  }

  @Override
  @Nonnull
  NameField makeEmptyElement(@Nullable NameField inputElement) {
    return new NameField();
  }

}
