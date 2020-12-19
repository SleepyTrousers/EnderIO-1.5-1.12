package crazypants.enderio.gui.forms;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

public class ThingsDialog extends JDialog {

  private final JPanel contentPanel = new JPanel();
  JScrollPane scrollPane;
  private JPanel panelEntry_0;
  private JPanel panelEntry_1;
  private JPanel panelEntry_2;
  private JPanel panelEntry_3;
  private JPanel panelViewport;

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

    panelEntry_0 = new ThingsEntry(scrollPane);
    panelEntry_0.setBounds(5, 5, 421, 63);
    panelViewport.add(panelEntry_0);

    panelEntry_1 = new ThingsEntry(scrollPane);
    panelEntry_1.setBounds(254, 5, 421, 63);
    panelViewport.add(panelEntry_1);
    panelEntry_2 = new ThingsEntry(scrollPane);
    panelEntry_2.setBounds(503, 5, 421, 63);
    panelViewport.add(panelEntry_2);
    panelEntry_3 = new ThingsEntry(scrollPane);
    panelEntry_3.setBounds(752, 5, 421, 63);
    panelViewport.add(panelEntry_3);

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

  @Override
  public void validate() {
    reflow();
    super.validate();
  }

  private void reflow() {
    int h = panelEntry_0.getHeight();
    int w = Math.max(300, scrollPane.getViewport().getExtentSize().width - 10);
    panelEntry_0.setBounds(5, 0 * h + 1 * 5, w, h);
    panelEntry_1.setBounds(5, 1 * h + 2 * 5, w, h);
    panelEntry_2.setBounds(5, 2 * h + 3 * 5, w, h);
    panelEntry_3.setBounds(5, 3 * h + 4 * 5, w, h);
    panelViewport.setPreferredSize(new Dimension(w + 10, 4 * h + 5 * 5));
  }
}
