package crazypants.enderio;

import java.text.DecimalFormat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.render.ConduitRenderer;

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

  public ConduitRenderer getRendererForConduit(IConduit conduit) {
    return null;
  }

  public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    return 5;

  }

  public void load() {
  }

  public boolean isNeiInstalled() {    
    return false;
  }

}
