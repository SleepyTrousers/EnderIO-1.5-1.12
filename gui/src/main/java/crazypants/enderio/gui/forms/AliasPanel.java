package crazypants.enderio.gui.forms;

import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import crazypants.enderio.gui.xml.Alias;
import crazypants.enderio.gui.xml.InvalidRecipeConfigException;
import crazypants.enderio.gui.xml.builder.IXMLBuilder;
import crazypants.enderio.gui.xml.builder.XMLBuilder;
import net.miginfocom.swing.MigLayout;

public class AliasPanel extends JPanel {
  private JTextField textName;
  private JTextField textValue;
  private JTextField textConditions;

  private final Alias alias = new Alias();
  private JTextPane errorPane;

  private Frame findFrame(Container p) {
    if (p == null || p instanceof Frame) {
      return (Frame) p;
    } else {
      return findFrame(p.getParent());
    }
  }

  /**
   * Create the panel.
   */
  public AliasPanel() {
    setLayout(new MigLayout("", "[][grow][]", "[][][][grow]"));

    JLabel lblNewLabel = new JLabel("Name:");
    add(lblNewLabel, "cell 0 0,alignx right,aligny center");

    textName = new JTextField();
    add(textName, "cell 1 0 2 1,growx,aligny top");
    textName.setColumns(10);
    listener(textName, alias::setName);

    JLabel lblNewLabel_1 = new JLabel("Value:");
    add(lblNewLabel_1, "cell 0 1,alignx right,aligny center");

    textValue = new JTextField();
    add(textValue, "cell 1 1,growx,aligny center");
    textValue.setColumns(10);
    listener(textValue, value -> {
      alias.getItem().clear();
      alias.getItem().add(value);
    });

    JButton btnNewButton = new JButton("Edit");
    btnNewButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ThingsDialog dialog = new ThingsDialog(findFrame(AliasPanel.this.getParent()), "Alias: " + alias.getName());
        dialog.setValue(alias.getItem());
        dialog.setVisible(true);
        if (dialog.wasClosedWithSuccess()) {
          alias.setItem(dialog.getValue().copy());
          textValue.setText(alias.getItem().getName());
        }
      }
    });
    add(btnNewButton, "cell 2 1,alignx left,aligny top");

    JLabel lblNewLabel_2 = new JLabel("Conditions:");
    add(lblNewLabel_2, "cell 0 2,alignx right,aligny center");

    textConditions = new JTextField();
    textConditions.setEditable(false);
    add(textConditions, "cell 1 2,growx,aligny center");
    textConditions.setColumns(10);

    JButton btnNewButton_1 = new JButton("Edit");
    add(btnNewButton_1, "cell 2 2,alignx left,aligny top");

    errorPane = new JTextPane();
    errorPane.setBackground(UIManager.getColor("Panel.background"));
    errorPane.setEditable(false);
    add(errorPane, "cell 1 3 2 1,grow");

    Alias a = new Alias();
    a.setName("Empty");
    setAlias(a);
  }

  public Alias getAlias() {
    return alias;
  }

  public void setAlias(Alias alias) {
    this.alias.setName(alias.getName());
    textName.setText(this.alias.getName());

    this.alias.setItem(alias.getItem().copy());
    textValue.setText(this.alias.getItem().getName());

    // TODO: Copy conditions
    String conditionDisplay = this.alias.getConditionDisplay();
    textConditions.setText(conditionDisplay.isEmpty() ? "(none)" : conditionDisplay);
  }

  private void listener(JTextField field, Consumer<String> callback) {
    field.getDocument().addDocumentListener(new DocumentListener() {

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
        callback.accept(field.getText());
        try {
          alias.validate();
          IXMLBuilder x = XMLBuilder.single();
          alias.write(x);
          errorPane.setText(x.writeXML());
          if (errorPane.getForeground() == Color.RED) {
            errorPane.setForeground(new JTextPane().getForeground());
          }
        } catch (InvalidRecipeConfigException e1) {
          errorPane.setText(e1.getLocalizedMessage());
          errorPane.setForeground(Color.RED);
        }
      }
    });
  }

}
