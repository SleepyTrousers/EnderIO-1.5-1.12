package crazypants.enderio;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class CommonProxy {


  protected long serverTickCount = 0;
  protected long clientTickCount = 0;
  protected final TickTimer tickTimer = new TickTimer();

  public CommonProxy() {
  }

  public World getClientWorld() {
    return null;
  }

  public EntityPlayer getClientPlayer() {
    return null;
  }

  public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    return 5;
  }

  public void loadIcons() {
  }
  
  public void preInit() {       
  }
  
  public void init() {
    MinecraftForge.EVENT_BUS.register(tickTimer);
  }

  public long getTickCount() {
    return serverTickCount;
  }

  public boolean isNeiInstalled() {
    return false;
  }

  public void setInstantConfusionOnPlayer(EntityPlayer ent, int duration) {
    ent.addPotionEffect(new PotionEffect(Potion.confusion.getId(), duration, 1, true, true));
  }

  protected void onServerTick() {
    ++serverTickCount;
  }

  protected void onClientTick() {
  }

  public final class TickTimer {

    @SubscribeEvent
    public void onTick(ServerTickEvent evt) {
      if(evt.phase == Phase.END) {
        onServerTick();
      }
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent evt) {
      if(evt.phase == Phase.END) {
        onClientTick();
      }
    }
  }

}
