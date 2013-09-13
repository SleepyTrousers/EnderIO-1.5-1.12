package crazypants.enderio;

import java.text.DecimalFormat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.render.ConduitRenderer;
import crazypants.util.DebugGuiTPS;

public class CommonProxy {

  private static final DecimalFormat FORMAT = new DecimalFormat("########0.000");

  private boolean showTpdGUI;

  public CommonProxy() {
    String prop = System.getProperty("EnderIO.showTpsGui") ;
    showTpdGUI = prop != null && prop.trim().length() > 0;
  }
  
  public World getClientWorld() {
    return null;
  }

  public EntityPlayer getClientPlayer() {
    return null;
  }

  public void load() {
    if (showTpdGUI) {
      DebugGuiTPS.showTpsGUI();
    }
  }

  public ConduitRenderer getRendererForConduit(IConduit conduit) {
    return null;
  }

  public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    return 5;

  }

}
