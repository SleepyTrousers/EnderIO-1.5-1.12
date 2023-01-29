package crazypants.enderio.item.darksteel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;

import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.config.Config;

public class SoundDetector {

    private static final int MAX_ENTITIES = 64;

    public static SoundDetector instance = new SoundDetector();

    private List<SoundSource> sounds;
    private ArrayBlockingQueue<SoundSource> soundQueue = new ArrayBlockingQueue<SoundSource>(MAX_ENTITIES);

    private Minecraft mc = Minecraft.getMinecraft();

    boolean enabled = false;

    double maxRangeSq = Config.darkSteelSoundLocatorRange * Config.darkSteelSoundLocatorRange;

    @SubscribeEvent
    public void onSound(PlaySoundAtEntityEvent evt) {
        if (enabled && evt.entity != null
                && evt.entity != Minecraft.getMinecraft().thePlayer
                && soundQueue.size() < MAX_ENTITIES) {
            soundQueue.offer(new SoundSource(evt.entity, evt.volume));
        }
    }

    @SubscribeEvent
    public void onSound(PlaySoundSourceEvent evt) {
        if (enabled && soundQueue.size() < MAX_ENTITIES) {
            soundQueue.offer(
                    new SoundSource(
                            evt.sound.getXPosF(),
                            evt.sound.getYPosF(),
                            evt.sound.getZPosF(),
                            evt.sound.getVolume()));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (!enabled || mc.thePlayer == null || mc.thePlayer.worldObj == null) {
            return;
        }

        sounds = new ArrayList<SoundSource>(MAX_ENTITIES);
        soundQueue.drainTo(sounds);

        try {
            Vector3d eye = Util.getEyePositionEio(mc.thePlayer);
            for (SoundSource ss : sounds) {
                double distSq = ss.pos.distanceSquared(eye);
                int minDist = ss.isEntity ? 4 : 49;
                if (distSq > minDist && distSq <= maxRangeSq) {
                    mc.thePlayer.worldObj.spawnEntityInWorld(new SoundEntity(mc.thePlayer.worldObj, ss.pos, ss.volume));
                }
            }
        } catch (Exception ex) {
            // Probably not necessary anymore but saftey first!
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private static class SoundSource {

        Vector3d pos;
        float volume;
        boolean isEntity;

        public SoundSource(Entity ent, float volume) {
            AxisAlignedBB bb = ent.boundingBox;
            if (bb != null) {
                pos = new Vector3d(
                        bb.minX + (bb.maxX - bb.minX) / 2,
                        bb.minY + (bb.maxY - bb.minY) / 2,
                        bb.minZ + (bb.maxZ - bb.minZ) / 2);
            } else {
                pos = new Vector3d(ent.posX, ent.posY, ent.posZ);
            }
            this.volume = volume;
            isEntity = true;
        }

        public SoundSource(double x, double y, double z, float volume) {
            pos = new Vector3d(x, y, z);
            this.volume = volume;
            isEntity = false;
        }
    }
}
