package crazypants.enderio.gui.forms;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import crazypants.enderio.gui.forms.actions.LoadCoreRecipes;
import crazypants.enderio.gui.forms.actions.LoadDataFile;
import crazypants.enderio.gui.forms.actions.SelectGameFolder;
import net.miginfocom.swing.MigLayout;

public class MainWindow {

  private JFrame frame;
  private JButton btnLoadData;
  private JLabel labelDataFile;
  final JFileChooser fc = new JFileChooser();
  private JLabel labelInstallationFolder;
  private JLabel labelCoreRecipes;
  private final Action actionSelectInstallation = new SwingActionSelectInstallation();

  /**
   * Launch the application.
   */
  public static void main(String datafile) {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          MainWindow window = new MainWindow();
          window.frame.setVisible(true);
          if (datafile != null) {
            EventQueue.invokeLater(new SelectGameFolder(window, datafile));
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public MainWindow() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    fc.setCurrentDirectory(new File(System.getProperty("user.dir")));

    frame = new JFrame();
    frame.setBounds(100, 100, 925, 366);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new CardLayout(0, 0));

    JPanel mainPanel = new JPanel();
    frame.getContentPane().add(mainPanel, "mainPanel");
    mainPanel.setLayout(new MigLayout("", "[][][][grow][][grow]", "[][][][][][][][][][grow]"));

    btnLoadData = new JButton("Load Data File");
    btnLoadData.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        EventQueue.invokeLater(new LoadDataFile(MainWindow.this));
      }
    });

    JButton btnNewButton = new JButton("Select Minecraft Installation");
    btnNewButton.setAction(actionSelectInstallation);
    btnNewButton.setToolTipText("Select the Minecraft client installation to edit recipes for. It must contain\nEnder IO and must have run at least once.");
    mainPanel.add(btnNewButton, "cell 1 1");

    labelInstallationFolder = new JLabel("No Installation Selected");
    mainPanel.add(labelInstallationFolder, "cell 3 1");
    btnLoadData.setToolTipText(
        "Data files are written by Ender IO when the game is started.\nThey contain information on available game objects such as\nItems, Potions, etc. Having a data file loaded is required so\nthe editor can validate your recipes.");
    mainPanel.add(btnLoadData, "cell 1 3");

    labelDataFile = new JLabel("No Data File Loaded");
    mainPanel.add(labelDataFile, "cell 3 3");

    JButton btnLoadCore = new JButton("Load Core Recipes");
    btnLoadCore.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        EventQueue.invokeLater(new LoadCoreRecipes(MainWindow.this));
      }
    });
    btnLoadCore.setToolTipText("When Ender IO core recipes are loaded, they can be viewed,\ndisabled and replaced. This is optional but recommended.");
    mainPanel.add(btnLoadCore, "cell 1 5");

    labelCoreRecipes = new JLabel("No Core Recipes Loaded");
    mainPanel.add(labelCoreRecipes, "cell 3 5");

    JButton btnNewButton_4 = new JButton("Browse Recipes");
    btnNewButton_4.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        CardLayout cl = (CardLayout) (frame.getContentPane().getLayout());
        cl.show(frame.getContentPane(), "coreRecipeList");
      }
    });
    mainPanel.add(btnNewButton_4, "cell 4 5");

    JButton btnNewButton_2 = new JButton("Open User Recipe File");
    mainPanel.add(btnNewButton_2, "cell 1 7");

    JLabel lblNewLabel_2 = new JLabel("No User Recipe File");
    mainPanel.add(lblNewLabel_2, "cell 3 7");

    JButton btnNewButton_5 = new JButton("Browse Recipes");
    mainPanel.add(btnNewButton_5, "cell 4 7,alignx center");

    JButton btnNewButton_3 = new JButton("Start New User Recipe File");
    mainPanel.add(btnNewButton_3, "cell 1 8");

    JLabel lblNewLabel_3 = new JLabel("No Recipes");
    mainPanel.add(lblNewLabel_3, "cell 3 8");

    JButton btnNewButton_6 = new JButton("New Recipe");
    mainPanel.add(btnNewButton_6, "cell 4 8");

    JButton btnNewButton_7 = new JButton("Save User Recipe File");
    mainPanel.add(btnNewButton_7, "cell 1 9");

    RecipeList coreRecipeList = new RecipeList();
    frame.getContentPane().add(coreRecipeList, "coreRecipeList");

    JMenuBar menuBar = new JMenuBar();
    frame.setJMenuBar(menuBar);

    JMenu menuFile = new JMenu("File");
    menuBar.add(menuFile);

    JMenuItem mntmNewMenuItem = new JMenuItem("Select Minecraft Installation...");
    mntmNewMenuItem.setAction(actionSelectInstallation);
    menuFile.add(mntmNewMenuItem);

    JMenuItem mntmNewMenuItem_1 = new JMenuItem("Load Data File");
    menuFile.add(mntmNewMenuItem_1);

    JMenuItem mntmNewMenuItem_2 = new JMenuItem("Load Core Recipes");
    menuFile.add(mntmNewMenuItem_2);

  }

  public JButton getBtnLoadData() {
    return btnLoadData;
  }

  public JLabel getLabelDataFile() {
    return labelDataFile;
  }

  public JLabel getLabelInstallationFolder() {
    return labelInstallationFolder;
  }

  public JLabel getLabelCoreRecipes() {
    return labelCoreRecipes;
  }

  private class SwingActionSelectInstallation extends AbstractAction {

    private static final long serialVersionUID = 780982506472999421L;

    public SwingActionSelectInstallation() {
      putValue(NAME, "SwingActionSelectInstallation");
      putValue(SHORT_DESCRIPTION, "Some short description");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      int returnVal = fc.showOpenDialog(frame);

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        EventQueue.invokeLater(new SelectGameFolder(MainWindow.this, file.toString()));
      }
    }
  }
}
