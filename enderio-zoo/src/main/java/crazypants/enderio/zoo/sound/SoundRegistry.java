package crazypants.enderio.zoo.sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.events.ModSoundRegisterEvent;
import crazypants.enderio.base.sound.IModSound;
import crazypants.enderio.zoo.EnderIOZoo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOZoo.MODID)
public enum SoundRegistry implements IModSound {
  OWL_HOOT(SoundCategory.NEUTRAL, "owl.hootSingle"),
  OWL_HOOT2(SoundCategory.NEUTRAL, "owl.hootDouble"),
  OWL_HURT(SoundCategory.NEUTRAL, "owl.hurt"),
  WOLF_HURT(SoundCategory.HOSTILE, "direwolf.hurt"),
  WOLF_HOWL(SoundCategory.HOSTILE, "direwolf.howl"),
  WOLF_GROWL(SoundCategory.HOSTILE, "direwolf.growl"),
  WOLF_DEATH(SoundCategory.HOSTILE, "direwolf.death"),

  ;

  private final @Nonnull ResourceLocation resourceLocation;
  private final @Nonnull SoundCategory soundCategory;
  private @Nullable SoundEvent soundEvent = null;

  private SoundRegistry(@Nonnull SoundCategory soundCategory, @Nonnull ResourceLocation resourceLocation) {
    this.soundCategory = soundCategory;
    this.resourceLocation = resourceLocation;
  }

  private SoundRegistry(@Nonnull SoundCategory soundCategory, @Nonnull String name) {
    this(soundCategory, new ResourceLocation(EnderIOZoo.DOMAIN, name));
  }

  @SubscribeEvent
  public static void registerSounds(@Nonnull ModSoundRegisterEvent event) {
    for (SoundRegistry soundRegistry : values()) {
      if (SoundEvent.REGISTRY.containsKey(soundRegistry.resourceLocation)) {
        soundRegistry.soundEvent = event.getRegistry().getValue(soundRegistry.resourceLocation);
      } else {
        SoundEvent soundEvent_nullchecked = soundRegistry.soundEvent = new SoundEvent(soundRegistry.resourceLocation);
        event.getRegistry().register(soundEvent_nullchecked.setRegistryName(soundRegistry.resourceLocation));
      }
    }
  }

  @Override
  public boolean isValid() {
    return soundEvent != null;
  }

  @Override
  public @Nonnull SoundEvent getSoundEvent() {
    return NullHelper.notnull(soundEvent, "trying to play unregistered sound");
  }

  @Override
  public @Nonnull SoundCategory getSoundCategory() {
    return soundCategory;
  }

}
