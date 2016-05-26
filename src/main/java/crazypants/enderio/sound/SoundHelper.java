package crazypants.enderio.sound;

import crazypants.enderio.Log;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SoundHelper {

  private SoundHelper() {
  }

  public static final Vec3d BLOCK_CENTER = new Vec3d(.5, .5, .5);
  public static final Vec3d BLOCK_TOP = new Vec3d(.5, 1, .5);
  public static final Vec3d BLOCK_LOW = new Vec3d(.5, .1, .5);

  public static void playSound(World world, BlockPos soundLocation, IModSound sound, float volume, float pitch) {
    playSound(world, soundLocation, BLOCK_CENTER, sound, volume, pitch);
  }

  public static void playSound(World world, BlockPos soundLocation, Vec3d offset, IModSound sound, float volume, float pitch) {
    if (sound != null && sound.isValid()) {
      world.playSound(null, soundLocation.getX() + offset.xCoord, soundLocation.getY() + offset.yCoord, soundLocation.getZ() + offset.zCoord,
          sound.getSoundEvent(), sound.getSoundCategory(), volume, pitch);
    } else {
      Log.error("SoundHelper: Asked to play invalid sound " + sound);
    }
  }

  public static void playSoundAtPlayer(World world, EntityPlayer player, IModSound sound, float volume, float pitch) {
    if (sound != null && sound.isValid()) {
      world.playSound(player, player.posX, player.posY, player.posZ, sound.getSoundEvent(), sound.getSoundCategory(), volume, pitch);
    } else {
      Log.error("SoundHelper: Asked to play invalid sound " + sound);
    }
  }
  
  public static void playSound(World world, Entity soundLocation, IModSound sound, float volume, float pitch) {
    if (sound != null && sound.isValid()) {
      world.playSound(null, soundLocation.posX, soundLocation.posY, soundLocation.posZ, sound.getSoundEvent(), sound.getSoundCategory(), volume, pitch);
    } else {
      Log.error("SoundHelper: Asked to play invalid sound " + sound);
    }
  }

}
