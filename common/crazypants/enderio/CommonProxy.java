package crazypants.enderio;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.render.ConduitRenderer;

public class CommonProxy {

  private final ServerTickHandler serverTickHandler = new ServerTickHandler();

  public World getClientWorld() {
    return null;
  }

  public EntityPlayer getClientPlayer() {
    return null;
  }

  public void load() {
    TickRegistry.registerTickHandler(serverTickHandler, Side.SERVER);
  }

  public ConduitRenderer getRendererForConduit(IConduit conduit) {
    return null;
  }

  public ServerTickHandler getServerTickHandler() {
    return serverTickHandler;
  }

  public void serverStopped() {
    serverTickHandler.serverStopped();
  }

  public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    return 5;

  }

}
