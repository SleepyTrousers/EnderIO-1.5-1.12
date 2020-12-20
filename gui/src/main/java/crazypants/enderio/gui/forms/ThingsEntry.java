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
import crazypants.enderio.gui.gamedata.AliasRepository;
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
  private final JComboBox<String> comboAlias = new JComboBox<>();
  private final JTextField textFreetext = new JTextField();
  private final JButton btnDelete = new JButton();
  private final JPanel panelLeft = new JPanel();
  private final JButton btnUp = new JButton();
  private final JButton btnDown = new JButton();

  private Runnable doUp, doDown, doDel;

  public void setDoUp(Runnable doUp) {
    this.doUp = doUp;
  }

  public void setDoDown(Runnable doDown) {
    this.doDown = doDown;
  }

  public void setDoDel(Runnable doDel) {
    this.doDel = doDel;
  }

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

    comboAlias.setModel(new DefaultComboBoxModel<>(AliasRepository.getCore().toArray(new String[0])));
    AutoCompletion.enable(comboAlias);

    textFreetext.setColumns(10);

    btnDelete.addActionListener(unused -> doDel.run());
    btnUp.addActionListener(unused -> doUp.run());
    btnDown.addActionListener(unused -> doDown.run());

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

  public boolean isEmpty() {
    return getRawEntry().isEmpty();
  }

  public String getEntry() {
    return (comboPlusMinus.getSelectedIndex() == 0 ? "" : "-") + getRawEntry();
  }

  private String getRawEntry() {
    switch (Types.values()[comboType.getSelectedIndex()]) {
    case ALIAS:
      return comboAlias.getSelectedItem().toString();
    case CUSTOM:
      return textFreetext.getText();
    case ITEM:
      return comboItem.getSelectedItem().toString();
    case OREDICT:
      return comboOreDict.getSelectedItem().toString();
    default:
      return "";
    }
  }

  public enum Position {
    FIRST,
    MIDDLE,
    LAST,
    SINGLE;

    protected boolean hasUp() {
      return this != FIRST && this != SINGLE;
    }

    protected boolean hasDown() {
      return this != LAST && this != SINGLE;
    }

    protected boolean hasDel() {
      return this != SINGLE;
    }

    public static Position compute(int idx, int size) {
      return size == 1 ? SINGLE : idx == 0 ? FIRST : idx == size - 1 ? LAST : MIDDLE;
    }
  }

  public void setPosition(Position position) {
    btnUp.setEnabled(position.hasUp() && doUp != null);
    btnDown.setEnabled(position.hasDown() && doDown != null);
    btnDelete.setEnabled(position.hasDel() && doDel != null);
  }

}