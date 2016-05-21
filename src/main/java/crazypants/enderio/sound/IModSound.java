package crazypants.enderio.sound;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public interface IModSound {

  /**
   * Checks if the sound is valid and can be played.
   * 
   * @return true if the sound can be played
   */
  boolean isValid();

  /**
   * 
   * @return the soundEvent to play. Must not return null if isValid() is true.
   */
  SoundEvent getSoundEvent();

  /**
   * 
   * @return the SoundCategory to use for playing the sound. Must not return null if isValid() is true.
   */
  SoundCategory getSoundCategory();

}