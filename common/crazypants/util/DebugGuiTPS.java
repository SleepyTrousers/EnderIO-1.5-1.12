package crazypants.util;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;

public class DebugGuiTPS extends JFrame {

  public static void showTpsGUI() {
    DebugGuiTPS g = new DebugGuiTPS();
    g.setVisible(true);
  }

  private static final DecimalFormat FORMAT = new DecimalFormat("########0.000");

  private JPanel contentPane = new JPanel();

  private DebugGuiTPS() {
    Timer timer = new Timer(2000, new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        updateTps();
      }
    });
    timer.setRepeats(true);
    timer.start();

    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

    setTitle("TPS");
    setLayout(new BorderLayout());
    add(contentPane, BorderLayout.CENTER);
    setSize(200, 120);
  }

  private void updateTps() {

    contentPane.removeAll();

    MinecraftServer server = MinecraftServer.getServer();
    if(server == null) {
      return;
    }

    String s = "Avg tick: " + FORMAT.format(average(server.tickTimeArray) * 1.0E-6D) + " ms";
    JLabel l = new JLabel(s);
    contentPane.add(l);

    if(server.worldServers != null) {
      for (Integer id : DimensionManager.getIDs()) {
        s = "Lvl " + id + " tick: " + FORMAT.format(average(server.worldTickTimes.get(id)) * 1.0E-6D) + " ms";
        l = new JLabel(s);
        contentPane.add(l);
      }
    }
    revalidate();
    repaint();

  }

  private double average(long[] values) {
    long i = 0L;
    for (int j = 0; j < values.length; ++j) {
      i += values[j];
    }
    return (double) i / (double) values.length;
  }

}
