package crazypants.enderio.base.sound;

import crazypants.enderio.base.Log;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * <ul>
 * <li><em>On the client:</em>
 * <ul>
 * 
 * <li>playSound(EntityPlayer, 3 doubles)
 * <ul>
 * <li>plays if player param is the player</li>
 * <li>plays at given coords</li>
 * <li>plays without distance delay</li>
 * </ul>
 * </li>
 * 
 * <li>playSound(EntityPlayer, BlockPos)
 * <ul>
 * <li>plays if player param is the player</li>
 * <li>plays at center of BlockPos</li>
 * <li>plays without distance delay</li>
 * </ul>
 * </li>
 * 
 * <li>playSound(BlockPos)
 * <ul>
 * <li>plays always</li>
 * <li>plays at center of BlockPos</li>
 * <li>plays with distance delay from param</li>
 * </ul>
 * </li>
 * 
 * <li>playSound(3 doubles)
 * <ul>
 * <li>plays always</li>
 * <li>plays at given coords</li>
 * <li>plays with distance delay from param</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * 
 * <li><em>On the server:</em>
 * <ul>
 * <li>playSound(EntityPlayer, 3 doubles)
 * <ul>
 * <li>plays if player param NOT is the player</li>
 * <li>plays at given coords</li>
 * <li>plays without distance delay</li>
 * <li>uses playSound(EntityPlayer, 3 doubles) on the client</li>
 * </ul>
 * </li>
 * 
 * <li>playSound(EntityPlayer, BlockPos)
 * <ul>
 * <li>plays if player param NOT is the player</li>
 * <li>plays at center of BlockPos</li>
 * <li>plays without distance delay</li>
 * <li>uses playSound(EntityPlayer, 3 doubles) on the client</li>
 * </ul>
 * </li>
 * 
 * <li>playSound(BlockPos)
 * <ul>
 * <li>doesn't exist</li>
 * </ul>
 * </li>
 * 
 * <li>playSound(3 doubles)
 * <ul>
 * <li>does nothing</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * </ul>
 *
 */
public final class SoundHelper {

  private SoundHelper() {
  }

  public static final Vec3d BLOCK_CENTER = new Vec3d(.5, .5, .5);
  public static final Vec3d BLOCK_TOP = new Vec3d(.5, 1, .5);
  public static final Vec3d BLOCK_LOW = new Vec3d(.5, .1, .5);

  /**
   * Plays a sound at the given location. If called on a server, it will play it for all players.
   * 
   */
  public static void playSound(World world, double soundLocationX, double soundLocationY, double soundLocationZ, IModSound sound, float volume, float pitch) {
    if (sound != null && sound.isValid()) {
      if (world instanceof WorldServer) {
        world.playSound(null, soundLocationX, soundLocationY, soundLocationZ, sound.getSoundEvent(), sound.getSoundCategory(), volume, pitch);
      } else {
        world.playSound(soundLocationX, soundLocationY, soundLocationZ, sound.getSoundEvent(), sound.getSoundCategory(), volume, pitch, false);
      }
    } else {
      Log.error("SoundHelper: Asked to play invalid sound " + sound);
    }
  }

  /**
   * Plays a sound at the center of the given BlockPos. If called on a server, it will play it for all players.
   * 
   */
  public static void playSound(World world, BlockPos soundLocation, IModSound sound, float volume, float pitch) {
    playSound(world, soundLocation, BLOCK_CENTER, sound, volume, pitch);
  }

  /**
   * Plays a sound at an offset to the given BlockPos. If called on a server, it will play it for all players.
   * <p>
   * 
   * See {@link SoundHelper#BLOCK_CENTER}, {@link SoundHelper#BLOCK_TOP} and {@link SoundHelper#BLOCK_LOW} for common offsets.
   * 
   */
  public static void playSound(World world, BlockPos soundLocation, Vec3d offset, IModSound sound, float volume, float pitch) {
    playSound(world, soundLocation.getX() + offset.x, soundLocation.getY() + offset.y, soundLocation.getZ() + offset.z, sound, volume, pitch);
  }

  /**
   * Plays a sound at the location of given entity. If called on a server, it will play it for all players.
   * 
   */
  public static void playSound(World world, Entity soundLocation, IModSound sound, float volume, float pitch) {
    playSound(world, soundLocation.posX, soundLocation.posY, soundLocation.posZ, sound, volume, pitch);
  }

}
