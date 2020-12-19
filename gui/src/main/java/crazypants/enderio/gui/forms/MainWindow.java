package crazypants.enderio.gui.forms;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
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

import crazypants.enderio.gui.gamedata.GameLocation;
import crazypants.enderio.gui.gamedata.RecipeHolder;
import crazypants.enderio.gui.gamedata.ValueRepository;
import net.miginfocom.swing.MigLayout;

public class MainWindow {

  private JFrame frame;

  public JFrame getFrame() {
    return frame;
  }

  private JButton btnLoadData;
  private JLabel labelDataFile;
  final JFileChooser fc = new JFileChooser();
  private JLabel labelInstallationFolder;
  private JLabel labelCoreRecipes;
  private SwingActionSelectInstallation actionSelectInstallation;
  private final Action actionLoadDataFile = new SwingActionLoadDataFile();
  private final Action actionLoadCoreRecipes = new SwingActionLoadCoreRecipes();

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
            EventQueue.invokeLater(() -> window.actionSelectInstallation.load(new File(datafile)));
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
    actionSelectInstallation = new SwingActionSelectInstallation();

    frame = new JFrame();
    frame.setBounds(100, 100, 925, 366);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new CardLayout(0, 0));

    JPanel mainPanel = new JPanel();
    frame.getContentPane().add(mainPanel, "mainPanel");
    mainPanel.setLayout(new MigLayout("", "[][][][grow][][grow]", "[][][][][][][][][][grow]"));

    btnLoadData = new JButton();
    btnLoadData.setAction(actionLoadDataFile);

    JButton btnNewButton = new JButton();
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

    JButton btnLoadCore = new JButton();
    btnLoadCore.setAction(actionLoadCoreRecipes);
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
    btnNewButton_7.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        crazypants.enderio.gui.forms.ThingsDialog.main(null);
      }
    });
    mainPanel.add(btnNewButton_7, "cell 1 9");

    RecipeList coreRecipeList = new RecipeList(() -> {
      CardLayout cl = (CardLayout) (frame.getContentPane().getLayout());
      cl.show(frame.getContentPane(), "mainPanel");

    });
    frame.getContentPane().add(coreRecipeList, "coreRecipeList");

    JMenuBar menuBar = new JMenuBar();
    frame.setJMenuBar(menuBar);

    JMenu menuFile = new JMenu("File");
    menuBar.add(menuFile);

    JMenuItem mntmNewMenuItem = new JMenuItem();
    mntmNewMenuItem.setAction(actionSelectInstallation);
    menuFile.add(mntmNewMenuItem);

    JMenuItem mntmNewMenuItem_1 = new JMenuItem();
    mntmNewMenuItem_1.setAction(actionLoadDataFile);
    menuFile.add(mntmNewMenuItem_1);

    JMenuItem mntmNewMenuItem_2 = new JMenuItem();
    mntmNewMenuItem_2.setAction(actionLoadCoreRecipes);
    menuFile.add(mntmNewMenuItem_2);

  }

  public JLabel getLabelCoreRecipes() {
    return labelCoreRecipes;
  }

  private class SwingActionSelectInstallation extends AbstractAction {

    private static final long serialVersionUID = 780982506472999421L;

    public SwingActionSelectInstallation() {
      putValue(NAME, "Select Minecraft Installation...");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      if (fc.showOpenDialog(getFrame()) == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        if (file != null) {
          EventQueue.invokeLater(() -> load(file));
        }
      }
    }

    protected void load(@Nonnull File file) {
      GameLocation.setFile(file);
      if (GameLocation.isValid()) {
        labelInstallationFolder.setText(GameLocation.getGAME().toString());
      } else {
        labelInstallationFolder.setText(file + " is not a valid Minecraft installation");
      }
    }
  }

  private class SwingActionLoadDataFile extends AbstractAction {

    private static final long serialVersionUID = -1055763974932578774L;

    public SwingActionLoadDataFile() {
      putValue(NAME, "Load Data File");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      EventQueue.invokeLater(() -> {
        String error = ValueRepository.read();
        if (error != null) {
          labelDataFile.setText("Error Loading Data File");
          labelDataFile.setToolTipText(error);
        } else {
          Map<String, Integer> counts = ValueRepository.getCounts();
          if (counts.isEmpty()) {
            labelDataFile.setText("No Data File Loaded");
            labelDataFile.setToolTipText(null);
          } else {
            labelDataFile.setText("Loaded " + GameLocation.getDATA().toString());
            labelDataFile.setToolTipText(counts.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining(", ")));
          }
        }
      });

    }
  }

  private class SwingActionLoadCoreRecipes extends AbstractAction {
    private static final long serialVersionUID = -1659127751450751243L;

    public SwingActionLoadCoreRecipes() {
      putValue(NAME, "Load Core Recipes");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      List<String> errors = RecipeHolder.readCore();
      getLabelCoreRecipes().setText("Loaded " + RecipeHolder.CORE.getRecipes().getRecipes().size() + " core recipes");
      if (!errors.isEmpty()) {
        // TODO
      }
    }
  }
}
