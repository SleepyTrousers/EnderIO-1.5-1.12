package crazypants.enderio.sound;

import crazypants.enderio.EnderIO;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public enum SoundRegistry implements IModSound {
  ZOMBIE_BUBBLE(SoundCategory.BLOCKS, "generator.zombie.bubble"),
  NIGHTVISION_ON(SoundCategory.PLAYERS, "ds.nightvision.on"),
  NIGHTVISION_OFF(SoundCategory.PLAYERS, "ds.nightvision.off"),
  JUMP(SoundCategory.PLAYERS, "ds.jump"),
  TELEPAD(SoundCategory.BLOCKS, "telepad.teleport"),
  TRAVEL_SOURCE_BLOCK(SoundCategory.BLOCKS, new ResourceLocation("entity.endermen.teleport")),
  TRAVEL_SOURCE_ITEM(SoundCategory.PLAYERS, new ResourceLocation("entity.endermen.teleport")),

  ;

  private final ResourceLocation resourceLocation;
  private final SoundCategory soundCategory;
  private SoundEvent soundEvent = null;

  private SoundRegistry(SoundCategory soundCategory, ResourceLocation resourceLocation) {
    this.soundCategory = soundCategory;
    this.resourceLocation = resourceLocation;
  }

  private SoundRegistry(SoundCategory soundCategory, String name) {
    this(soundCategory, new ResourceLocation(EnderIO.DOMAIN, name));
  }

  public static void init() {
    for (SoundRegistry soundRegistry : values()) {
      soundRegistry.soundEvent = SoundEvent.REGISTRY.getObject(soundRegistry.resourceLocation);
    }
  }

  @Override
  public boolean isValid() {
    return soundEvent != null;
  }

  @Override
  public SoundEvent getSoundEvent() {
    return soundEvent;
  }

  @Override
  public SoundCategory getSoundCategory() {
    return soundCategory;
  }

}
