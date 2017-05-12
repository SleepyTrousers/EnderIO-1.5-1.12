package crazypants.enderio.sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.EnderIO;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public enum SoundRegistry implements IModSound {
  ZOMBIE_BUBBLE(SoundCategory.BLOCKS, "generator.zombie.bubble"),
  NIGHTVISION_ON(SoundCategory.PLAYERS, "ds.nightvision.on"),
  NIGHTVISION_OFF(SoundCategory.PLAYERS, "ds.nightvision.off"),
  JUMP(SoundCategory.PLAYERS, "ds.jump"),
  TELEPAD(SoundCategory.BLOCKS, "telepad.teleport"),
  TRAVEL_SOURCE_BLOCK(SoundCategory.BLOCKS, new ResourceLocation("entity.endermen.teleport")),
  TRAVEL_SOURCE_ITEM(SoundCategory.PLAYERS, new ResourceLocation("entity.endermen.teleport")),

  ;

  private final @Nonnull ResourceLocation resourceLocation;
  private final @Nonnull SoundCategory soundCategory;
  private @Nullable SoundEvent soundEvent = null;

  private SoundRegistry(@Nonnull SoundCategory soundCategory, @Nonnull ResourceLocation resourceLocation) {
    this.soundCategory = soundCategory;
    this.resourceLocation = resourceLocation;
  }

  private SoundRegistry(@Nonnull SoundCategory soundCategory, @Nonnull String name) {
    this(soundCategory, new ResourceLocation(EnderIO.DOMAIN, name));
  }

  public static void init() {    
    for (SoundRegistry soundRegistry : values()) {
      if (SoundEvent.REGISTRY.containsKey(soundRegistry.resourceLocation)) {
        soundRegistry.soundEvent = SoundEvent.REGISTRY.getObject(soundRegistry.resourceLocation);
      } else {
        soundRegistry.soundEvent = new SoundEvent(soundRegistry.resourceLocation);
        GameRegistry.register(soundRegistry.soundEvent.setRegistryName(soundRegistry.resourceLocation));
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
