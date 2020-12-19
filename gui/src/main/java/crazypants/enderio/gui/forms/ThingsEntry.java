package crazypants.enderio.gui.forms;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import crazypants.enderio.gui.forms.actions.AutoCompletion;
import crazypants.enderio.gui.gamedata.ValueRepository;

public final class ThingsEntry extends JPanel {

  private static final long serialVersionUID = -6073869658356100355L;

  private enum Types {
    ITEM("Item"),
    OREDICT("OreDict"),
    ALIAS("Alias"),
    CUSTOM("Custom");

    private final String text;

    private Types(String text) {
      this.text = text;
    }

    static String[] getTexts() {
      return Stream.of(values()).map(x -> x.text).collect(Collectors.toList()).toArray(new String[0]);
    }
  }

  private final JPanel panelInner = new JPanel();
  private final JComboBox<String> comboPlusMinus = new JComboBox<>();
  private final JComboBox<String> comboType = new JComboBox<>();
  private final JPanel panelEntryArea = new JPanel();
  private final CardLayout cardLayout = new CardLayout(0, 0);
  private final JComboBox<String> comboItem = new JComboBox<>();
  private final JComboBox<String> comboOreDict = new JComboBox<>();
  private final JComboBox<?> comboAlias = new JComboBox<Object>();
  private final JTextField textFreetext = new JTextField();
  private final JButton btnDelete = new JButton();
  private final JPanel panelLeft = new JPanel();
  private final JButton btnUp = new JButton();
  private final JButton btnDown = new JButton();

  public ThingsEntry(JScrollPane scrollPane) {
    this.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

    this.setLayout(new BorderLayout());
    panelInner.setLayout(new BorderLayout());
    panelEntryArea.setLayout(cardLayout);
    panelLeft.setLayout(new BorderLayout());

    comboPlusMinus.setModel(new DefaultComboBoxModel<>(new String[] { "Include", "Exclude" }));

    comboType.setModel(new DefaultComboBoxModel<>(Types.getTexts()));
    comboType.addActionListener(e -> {
      int selectedIndex = comboType.getSelectedIndex();
      cardLayout.show(panelEntryArea, Types.values()[selectedIndex].toString());
      panelEntryArea.getComponent(selectedIndex).requestFocusInWindow();
    });

    comboItem.setModel(new DefaultComboBoxModel<>(ValueRepository.ITEMS.getAllValues().toArray(new String[0])));
    AutoCompletion.enable(comboItem);

    comboOreDict.setModel(new DefaultComboBoxModel<>(ValueRepository.OREDICTS.getAllValues().toArray(new String[0])));
    AutoCompletion.enable(comboOreDict);

    // TODO: alias model
    AutoCompletion.enable(comboAlias);

    textFreetext.setColumns(10);

    btnDelete.setIcon(new ImageIcon(MainWindow.class.getResource("/javax/swing/plaf/metal/icons/ocean/paletteClose.gif")));
    btnUp.setIcon(new ImageIcon(ThingsDialog.class.getResource("/javax/swing/plaf/metal/icons/sortUp.png")));
    btnDown.setIcon(new ImageIcon(ThingsDialog.class.getResource("/javax/swing/plaf/metal/icons/sortDown.png")));

    // Structure
    this.add(panelLeft, BorderLayout.WEST);
    this.add(panelInner, BorderLayout.CENTER);
    this.add(btnDelete, BorderLayout.EAST);

    panelLeft.add(btnUp, BorderLayout.NORTH);
    panelLeft.add(btnDown, BorderLayout.SOUTH);

    panelInner.add(comboPlusMinus, BorderLayout.WEST);
    panelInner.add(comboType, BorderLayout.CENTER);
    panelInner.add(panelEntryArea, BorderLayout.SOUTH);

    panelEntryArea.add(comboItem, Types.ITEM.toString());
    panelEntryArea.add(comboOreDict, Types.OREDICT.toString());
    panelEntryArea.add(comboAlias, Types.ALIAS.toString());
    panelEntryArea.add(textFreetext, Types.CUSTOM.toString());
  }

}