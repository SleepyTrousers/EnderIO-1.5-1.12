package crazypants.enderio.gui.forms;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import crazypants.enderio.gui.gamedata.RecipeHolder;
import crazypants.enderio.gui.xml.AbstractConditional;
import crazypants.enderio.gui.xml.Alias;

public class TestDummy extends JDialog {

  private final JPanel contentPanel = new JPanel();

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      TestDummy dialog = new TestDummy();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public TestDummy() {
    setBounds(100, 100, 450, 300);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setLayout(new CardLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
      }
      {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
      }
    }

    AliasPanel aliasPanel = new AliasPanel();
    for (AbstractConditional r : RecipeHolder.CORE.getRecipes().getRecipes()) {
      if (r instanceof Alias) {
        aliasPanel.setAlias(((Alias) r));
        break;
      }
    }
    contentPanel.add(aliasPanel);

  }

}
