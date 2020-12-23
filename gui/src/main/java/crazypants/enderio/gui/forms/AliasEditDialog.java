package crazypants.enderio.gui.forms;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import crazypants.enderio.gui.xml.Alias;
import crazypants.enderio.gui.xml.InvalidRecipeConfigException;
import crazypants.enderio.gui.xml.builder.IXMLBuilder;
import crazypants.enderio.gui.xml.builder.XMLBuilder;
import net.miginfocom.swing.MigLayout;

public class AliasEditDialog extends AbstractEditDialog<Alias> {

  private static final long serialVersionUID = 7306462310545868581L;

  private JTextField textName;
  private JTextField textValue;
  private JTextField textConditions;

  @Override
  @Nonnull
  Alias makeEmptyElement(@Nullable Alias inputElement) {
    Alias alias = new Alias();
    alias.setName("Empty");
    return alias;
  }

  private Frame findFrame(Container p) {
    if (p == null || p instanceof Frame) {
      return (Frame) p;
    } else {
      return findFrame(p.getParent());
    }
  }

  public AliasEditDialog(@Nullable Frame owner, @Nonnull String title, @Nullable Alias element, boolean editable) {
    super(owner, title, element, editable);
  }

  @Override
  protected @Nonnull JPanel makePanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new MigLayout("", "[][grow][]", "[][][][grow]"));

    JLabel lblNewLabel = new JLabel("Name:");
    panel.add(lblNewLabel, "cell 0 0,alignx right,aligny center");

    textName = new JTextField();
    panel.add(textName, "cell 1 0 2 1,growx,aligny top");
    textName.setColumns(10);
    textName.setEditable(editable);
    listener(textName, getElement()::setName);

    JLabel lblNewLabel_1 = new JLabel("Value:");
    panel.add(lblNewLabel_1, "cell 0 1,alignx right,aligny center");

    textValue = new JTextField();
    panel.add(textValue, "cell 1 1,growx,aligny center");
    textValue.setColumns(10);
    textValue.setEditable(editable);
    listener(textValue, value -> {
      getElement().getItem().clear();
      getElement().getItem().add("" + value);
    });

    JButton btnNewButton = new JButton(editable ? "Edit" : "View");
    btnNewButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ThingsDialog dialog = new ThingsDialog(findFrame(getParent()), "Alias: " + getElement().getName());
        dialog.setValue(getElement().getItem());
        dialog.setVisible(true);
        if (editable && dialog.wasClosedWithSuccess()) {
          getElement().setItem(dialog.getValue().copy());
          textValue.setText(getElement().getItem().getName());
        }
      }
    });
    panel.add(btnNewButton, "cell 2 1,alignx left,aligny top");

    JLabel lblNewLabel_2 = new JLabel("Conditions:");
    panel.add(lblNewLabel_2, "cell 0 2,alignx right,aligny center");

    textConditions = new JTextField();
    textConditions.setEditable(false);
    panel.add(textConditions, "cell 1 2,growx,aligny center");
    textConditions.setColumns(10);

    JButton btnNewButton_1 = new JButton(editable ? "Edit" : "View");
    panel.add(btnNewButton_1, "cell 2 2,alignx left,aligny top");

    return panel;
  }

  @Override
  public void setElement(@Nonnull Alias alias) {
    Alias element = copyAlias(alias, getElement());

    textName.setText(element.getName());

    textValue.setText(element.getItem().getName());

    String conditionDisplay = element.getConditionDisplay();
    textConditions.setText(conditionDisplay.isEmpty() ? "(none)" : conditionDisplay);
  }

  protected @Nonnull Alias copyAlias(@Nullable Alias from, @Nonnull Alias to) {
    if (from != null) {
      to.setName(from.getName());
      to.setItem(from.getItem().copy());
      // TODO: Copy conditions
    }
    return to;
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
          getElement().validate();
          IXMLBuilder x = XMLBuilder.single();
          getElement().write(x);
          setResultText(x.writeXML(), false);
        } catch (InvalidRecipeConfigException e1) {
          setResultText("" + e1.getLocalizedMessage(), true);
        }
      }
    });
  }

}
