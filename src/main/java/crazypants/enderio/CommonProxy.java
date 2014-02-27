package crazypants.enderio;

import java.text.DecimalFormat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class CommonProxy {

  private static final DecimalFormat FORMAT = new DecimalFormat("########0.000");

  public CommonProxy() {
  }

  public World getClientWorld() {
    return null;
  }

  public EntityPlayer getClientPlayer() {
    return null;
  }

  //TODO:1.7
  //  public ConduitRenderer getRendererForConduit(IConduit conduit) {
  //    return null;
  //  }

  public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    return 5;

  }

  public void load() {
  }

}
