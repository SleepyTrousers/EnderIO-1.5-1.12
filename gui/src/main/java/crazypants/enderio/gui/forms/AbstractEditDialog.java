package crazypants.enderio.gui.forms;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import crazypants.enderio.gui.xml.IRecipeConfigElement;

public abstract class AbstractEditDialog<T extends IRecipeConfigElement> extends JDialog {

  private static final long serialVersionUID = 2722085591062402965L;

  private final @Nonnull JPanel contentPanel = new JPanel();
  private boolean closedWithSuccess = false;
  protected final boolean editable;

  private @Nonnull T element;

  private JTextPane errorPane;

  public AbstractEditDialog() {
    this(null, "Testing", null, true);
  }

  public AbstractEditDialog(@Nullable Frame owner, @Nonnull String title, @Nullable T element, boolean editable) {
    super(owner, title, true);
    this.element = makeEmptyElement(element);
    this.editable = editable;
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setBounds(100, 100, 450, 300);
    getContentPane().setLayout(new BorderLayout());

    contentPanel.setLayout(new CardLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);

    JSplitPane splitPane = new JSplitPane();
    splitPane.setContinuousLayout(true);
    splitPane.setResizeWeight(1.0);
    splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    /*
     * This is not what a SplitPane is there for, but by setting both divider and border to 0, we degrade it to a layout element that has one child at its
     * minimal size and grows the other to fill all available space. Exactly what we want here.
     */
    splitPane.setDividerSize(0);
    splitPane.setBorder(BorderFactory.createEmptyBorder());
    contentPanel.add(splitPane);

    splitPane.setLeftComponent(makePanel());

    errorPane = new JTextPane();
    errorPane.setBackground(UIManager.getColor("Panel.background"));
    errorPane.setEditable(false);
    splitPane.setRightComponent(errorPane);

    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
    if (editable) {
      JButton okButton = new JButton("OK");
      okButton.setActionCommand("OK");
      okButton.addActionListener(unused -> close(true));
      buttonPane.add(okButton);
      getRootPane().setDefaultButton(okButton);
      JButton cancelButton = new JButton("Cancel");
      cancelButton.addActionListener(unused -> close(false));
      cancelButton.setActionCommand("Cancel");
      buttonPane.add(cancelButton);
    } else {
      JButton closeButton = new JButton("Close");
      closeButton.setActionCommand("Close");
      closeButton.addActionListener(unused -> close(false));
      buttonPane.add(closeButton);
      getRootPane().setDefaultButton(closeButton);
    }

    if (element != null) {
      setElement(element);
    }
  }

  abstract protected @Nonnull JPanel makePanel();

  /**
   * Create the empty element to initialize the object with.
   * 
   * @param inputElement
   *          The constructor element parameter. Can be ignored as {@link #setElement(IRecipeConfigElement)} will be called later if it is not null.
   * 
   * @return An element to be stored for {@link #getElement()}.
   */
  abstract @Nonnull T makeEmptyElement(@Nullable T inputElement);

  public void setElement(@Nonnull T element) {
    this.element = element;
  }

  public @Nonnull T getElement() {
    return element;
  }

  private void close(boolean success) {
    closedWithSuccess = success;
    setVisible(false);
  }

  public boolean wasClosedWithSuccess() {
    return editable && closedWithSuccess;
  }

  protected void setResultText(@Nonnull String text, boolean isError) {
    if (isError) {
      errorPane.setForeground(Color.RED);
    } else if (errorPane.getForeground() == Color.RED) {
      errorPane.setForeground(new JTextPane().getForeground());
    }
    errorPane.setText(text);
  }

}
