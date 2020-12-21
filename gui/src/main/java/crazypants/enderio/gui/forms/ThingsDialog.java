package crazypants.enderio.gui.forms;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

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

public class ThingsDialog extends JDialog {

  private static final long serialVersionUID = -5665046007268630990L;

  private JPanel contentPanel = new JPanel();
  private JScrollPane scrollPane;
  private JPanel panelViewport;
  private JButton btnAdd;

  private List<ThingsEntry> entries = new ArrayList<>();
  private JTextField textField;
  private boolean closedWithSuccess = false;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      ThingsDialog dialog = new ThingsDialog();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      for (AbstractConditional r : RecipeHolder.CORE.getRecipes().getRecipes()) {
        if (r instanceof Alias) {
          dialog.setValue(((Alias) r).getItem());
          break;
        }
      }
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public ThingsDialog(Frame owner, String title) {
    super(owner, title, true);
    init();
  }

  public ThingsDialog() {
    init();
  }

  private void init() {
    setBounds(100, 100, 468, 712);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new CardLayout(0, 0));
    scrollPane = new JScrollPane();
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    contentPanel.add(scrollPane, "name_603792603466633");
    panelViewport = new JPanel();
    scrollPane.setViewportView(panelViewport);
    panelViewport.setLayout(null);

    btnAdd = new JButton("Add");
    btnAdd.addActionListener(unused -> {
      addEntry();
      resfreshEntries();
    });
    resfreshEntries();

    JPanel bottomPane = new JPanel();
    bottomPane.setLayout(new BorderLayout());
    getContentPane().add(bottomPane, BorderLayout.SOUTH);

    JPanel nbtPane = new JPanel();
    nbtPane.setLayout(new BorderLayout());
    nbtPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    bottomPane.add(nbtPane, BorderLayout.NORTH);

    JLabel nbtLabel = new JLabel("nbt:");
    nbtPane.add(nbtLabel, BorderLayout.WEST);

    textField = new JTextField();
    nbtPane.add(textField, BorderLayout.CENTER);
    textField.setColumns(10);

    // edit

    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    bottomPane.add(buttonPane, BorderLayout.SOUTH);

    JButton okButton = new JButton("OK");
    okButton.setActionCommand("OK");
    okButton.addActionListener(unused -> close(true));
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    JButton cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand("Cancel");
    cancelButton.addActionListener(unused -> close(false));
    buttonPane.add(cancelButton);
  }

  private void close(boolean success) {
    closedWithSuccess = success;
    setVisible(false);
  }

  public boolean wasClosedWithSuccess() {
    return closedWithSuccess;
  }

  protected ThingsEntry addEntry() {
    ThingsEntry entry = new ThingsEntry(scrollPane);
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
    if (entries.isEmpty()) {
      addEntry(); // never be completely empty
    }
    panelViewport.removeAll();
    for (ThingsEntry e : entries) {
      panelViewport.add(e);
    }
    panelViewport.add(btnAdd);
    btnAdd.requestFocusInWindow();
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
      // btnAdd.setBounds(5, yMax, btnAdd.getPreferredSize().width, btnAdd.getPreferredSize().height); // normal button size
      btnAdd.setBounds(5, yMax, w, h);
      yMax += btnAdd.getHeight() + 5;
      panelViewport.setPreferredSize(new Dimension(w + 10, yMax));
    }
  }

  public void setValue(NameField nf) {
    entries.clear();
    for (NameValue name : nf.getNames()) {
      if (!name.isEmpty()) {
        addEntry().setEntry(name);
      }
    }
    addEntry(); // always have an empty one at the end
    resfreshEntries();
  }

  public NameField getValue() {
    NameField result = new NameField();
    for (ThingsEntry entry : entries) {
      if (!entry.isEmpty()) {
        result.add(entry.getEntry());
      }
    }
    return result;
  }
}
