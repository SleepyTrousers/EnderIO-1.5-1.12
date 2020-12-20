package crazypants.enderio.gui.forms;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import crazypants.enderio.gui.forms.ThingsEntry.Position;

public class ThingsDialog extends JDialog {

  private static final long serialVersionUID = -5665046007268630990L;

  private final JPanel contentPanel = new JPanel();
  private final JScrollPane scrollPane;
  private final JPanel panelViewport;
  private final JButton btnAdd;

  private List<ThingsEntry> entries = new ArrayList<>();

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      ThingsDialog dialog = new ThingsDialog();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public ThingsDialog() {
    setBounds(100, 100, 468, 712);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new CardLayout(0, 0));
    scrollPane = new JScrollPane();
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    contentPanel.add(scrollPane, "name_603792603466633");
    JPanel panelHeader = new JPanel();
    scrollPane.setColumnHeaderView(panelHeader);
    panelViewport = new JPanel();
    scrollPane.setViewportView(panelViewport);
    panelViewport.setLayout(null);

    for (int i = 0; i < 6; i++) {
      addEntry();
    }

    btnAdd = new JButton("Add");
    btnAdd.addActionListener(unused -> {
      addEntry();
      validate();
      repaint();
    });
    panelViewport.add(btnAdd);

    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);

    JButton okButton = new JButton("OK");
    okButton.setActionCommand("OK");
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    JButton cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand("Cancel");
    buttonPane.add(cancelButton);
  }

  protected void addEntry() {
    ThingsEntry entry = new ThingsEntry(scrollPane);
    entry.setBounds(5, 5, 421, 63);
    panelViewport.add(entry);
    entry.setDoDel(() -> {
      entries.remove(entry);
      panelViewport.remove(entry);
      validate();
      repaint();
    });
    entry.setDoUp(() -> {
      int idx = entries.indexOf(entry);
      entries.remove(idx);
      entries.add(idx - 1, entry);
      panelViewport.removeAll();
      for (ThingsEntry e : entries) {
        panelViewport.add(e);
      }
      panelViewport.add(btnAdd);
      validate();
      repaint();
    });
    entry.setDoDown(() -> {
      int idx = entries.indexOf(entry);
      entries.remove(idx);
      entries.add(idx + 1, entry);
      panelViewport.removeAll();
      for (ThingsEntry e : entries) {
        panelViewport.add(e);
      }
      panelViewport.add(btnAdd);
      validate();
      repaint();
    });
    entries.add(entry);
  }

  @Override
  public void validate() {
    reflow();
    super.validate();
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
      btnAdd.setBounds(5, yMax, btnAdd.getPreferredSize().width, btnAdd.getPreferredSize().height);
      // btnAdd.setBounds(5, yMax, w, h);
      yMax += btnAdd.getHeight() + 5;
      panelViewport.setPreferredSize(new Dimension(w + 10, yMax));
    }
  }

}
