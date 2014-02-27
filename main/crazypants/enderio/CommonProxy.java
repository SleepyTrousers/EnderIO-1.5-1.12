package crazypants.enderio;

import crazypants.util.DebugGuiTPS;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.text.DecimalFormat;

public class CommonProxy {

  private static final DecimalFormat FORMAT = new DecimalFormat("########0.000");

  private boolean showTpdGUI;

  public CommonProxy() {
    String prop = System.getProperty("EnderIO.showTpsGui");
    showTpdGUI = prop != null && prop.trim().length() > 0;
  }

  public World getClientWorld() {
    return null;
  }

  public EntityPlayer getClientPlayer() {
    return null;
  }

  public void load() {
    if(showTpdGUI) {
      DebugGuiTPS.showTpsGUI();
    }
  }

  //TODO:1.7
//  public ConduitRenderer getRendererForConduit(IConduit conduit) {
//    return null;
//  }

  public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    return 5;

  }

}
