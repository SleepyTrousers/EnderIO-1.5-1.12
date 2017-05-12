package crazypants.enderio.sound;

import javax.annotation.Nonnull;

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
   * @return the soundEvent to play. Will throw an exception if isValid() is false.
   */
  @Nonnull
  SoundEvent getSoundEvent();

  /**
   * 
   * @return the SoundCategory to use for playing the sound. Will throw an exception if isValid() is false.
   */
  @Nonnull
  SoundCategory getSoundCategory();

}