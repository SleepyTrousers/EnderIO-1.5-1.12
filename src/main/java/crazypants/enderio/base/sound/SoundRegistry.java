package crazypants.enderio.base.sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public enum SoundRegistry implements IModSound {
  ZOMBIE_BUBBLE(SoundCategory.BLOCKS, "generator.zombie.bubble"),
  NIGHTVISION_ON(SoundCategory.PLAYERS, "ds.nightvision.on"),
  NIGHTVISION_OFF(SoundCategory.PLAYERS, "ds.nightvision.off"),
  JUMP(SoundCategory.PLAYERS, "ds.jump"),
  TELEPAD(SoundCategory.BLOCKS, "telepad.teleport"),
  TRAVEL_SOURCE_BLOCK(SoundCategory.BLOCKS, new ResourceLocation("entity.endermen.teleport")),
  TRAVEL_SOURCE_ITEM(SoundCategory.PLAYERS, new ResourceLocation("entity.endermen.teleport")),
  ITEM_BURN(SoundCategory.BLOCKS, new ResourceLocation("entity.generic.burn")),

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

  @SubscribeEvent
  public static void registerSounds(@Nonnull RegistryEvent.Register<SoundEvent> event) {
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
