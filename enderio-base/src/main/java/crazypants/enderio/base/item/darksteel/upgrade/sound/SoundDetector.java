package crazypants.enderio.base.item.darksteel.upgrade.sound;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class SoundDetector {

  private static final int MAX_PARTICLES = 64;

  private static final ArrayBlockingQueue<SoundSource> soundQueue = new ArrayBlockingQueue<SoundSource>(MAX_PARTICLES);

  private static boolean enabled = false;

  @SubscribeEvent
  public static void onSound(PlaySoundSourceEvent evt) {
    if (enabled && soundQueue.size() < MAX_PARTICLES) {
      switch (evt.getSound().getCategory()) {
      case BLOCKS:
        soundQueue.offer(new SoundSource(evt.getSound().getXPosF(), evt.getSound().getYPosF(), evt.getSound().getZPosF(), evt.getSound().getVolume(), false));
        break;
      case AMBIENT:
      case HOSTILE:
      case NEUTRAL:
      case PLAYERS:
        soundQueue.offer(new SoundSource(evt.getSound().getXPosF(), evt.getSound().getYPosF(), evt.getSound().getZPosF(), evt.getSound().getVolume(), true));
        break;
      default:
        break;
      }
    }
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public static void onClientTick(TickEvent.ClientTickEvent event) {

    if (!enabled) {
      return;
    }

    List<SoundSource> sounds = new ArrayList<SoundSource>(MAX_PARTICLES);
    soundQueue.drainTo(sounds);

    try {
      final Minecraft minecraft = Minecraft.getMinecraft();
      Vector3d eye = Util.getEyePositionEio(minecraft.player);
      for (SoundSource ss : sounds) {
        double distSq = ss.pos.distanceSquared(eye);
        int minDist = ss.isEntity ? 4 : 49;
        if (distSq > minDist && distSq <= DarkSteelConfig.soundLocatorRange.get() * DarkSteelConfig.soundLocatorRange.get()) {
          minecraft.effectRenderer.addEffect(new SoundParticle(minecraft.player.world, ss));
        }
      }
    } catch (Exception ex) {
      // Probably not necessary anymore but safety first!
    }
  }

  public static boolean isEnabled() {
    return enabled;
  }

  public static void setEnabled(boolean enabled) {
    SoundDetector.enabled = enabled;
  }

  public static class SoundSource {

    Vector3d pos;
    float volume;
    boolean isEntity;

    public SoundSource(Entity ent, float volume) {
      AxisAlignedBB bb = ent.getEntityBoundingBox();
      pos = new Vector3d(bb.minX + (bb.maxX - bb.minX) / 2, bb.minY + (bb.maxY - bb.minY) / 2, bb.minZ + (bb.maxZ - bb.minZ) / 2);
      this.volume = volume;
      isEntity = true;
    }

    public SoundSource(double x, double y, double z, float volume, boolean isEntity) {
      this.pos = new Vector3d(x, y, z);
      this.volume = volume;
      this.isEntity = isEntity;
    }

  }

}
