package crazypants.enderio.base.item.darksteel.upgrade.padding;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class PaddingHandler {

  @SubscribeEvent
  public static void onPlaySoundEvent(PlaySoundEvent event) {
    final EntityPlayerSP player = Minecraft.getMinecraft().player;
    if (NullHelper.untrust(player) != null && PaddingUpgrade.INSTANCE.hasUpgrade(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD))) {
      event.setResultSound(make(NullHelper.first(event.getResultSound(), event.getSound())));
    }
  }

  private static ISound make(@Nonnull ISound parent) {
    if (parent instanceof ITickableSound) {
      return new TickableMutedSound((ITickableSound) parent);
    } else {
      return new MutedSound(parent);
    }
  }

  private static class MutedSound implements ISound {

    protected final @Nonnull ISound parent;

    public MutedSound(@Nonnull ISound parent) {
      this.parent = parent;
    }

    @Override
    public @Nonnull ResourceLocation getSoundLocation() {
      return parent.getSoundLocation();
    }

    @Override
    @Nullable
    public SoundEventAccessor createAccessor(@Nonnull SoundHandler handler) {
      return parent.createAccessor(handler);
    }

    @Override
    public @Nonnull Sound getSound() {
      return parent.getSound();
    }

    @Override
    public @Nonnull SoundCategory getCategory() {
      return parent.getCategory();
    }

    @Override
    public boolean canRepeat() {
      return parent.canRepeat();
    }

    @Override
    public int getRepeatDelay() {
      return parent.getRepeatDelay();
    }

    @Override
    public float getVolume() {
      final EntityPlayerSP player = Minecraft.getMinecraft().player;
      if (NullHelper.untrust(player) != null && PaddingUpgrade.INSTANCE.hasUpgrade(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD))) {
        final double distanceSq = player.getPosition().distanceSq(getXPosF(), getYPosF(), getZPosF());
        final double cutoffDistance = DarkSteelConfig.cutoffDistance.get();
        final float volume = Math.min(.1f, Math.min(1f, (float) (distanceSq / (cutoffDistance * cutoffDistance))));
        return parent.getVolume() * volume;
      }
      return parent.getVolume();
    }

    @Override
    public float getPitch() {
      final EntityPlayerSP player = Minecraft.getMinecraft().player;
      if (NullHelper.untrust(player) != null && PaddingUpgrade.INSTANCE.hasUpgrade(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD))) {
        return parent.getPitch() * DarkSteelConfig.pitchAdjust.get();
      }
      return parent.getPitch();
    }

    @Override
    public float getXPosF() {
      return parent.getXPosF();
    }

    @Override
    public float getYPosF() {
      return parent.getYPosF();
    }

    @Override
    public float getZPosF() {
      return parent.getZPosF();
    }

    @Override
    public @Nonnull AttenuationType getAttenuationType() {
      return parent.getAttenuationType();
    }

  }

  private static class TickableMutedSound extends MutedSound implements ITickableSound {

    public TickableMutedSound(@Nonnull ITickableSound parent) {
      super(parent);
    }

    @Override
    public void update() {
      ((ITickableSound) parent).update();
    }

    @Override
    public boolean isDonePlaying() {
      return ((ITickableSound) parent).isDonePlaying();
    }

  }

}
