package crazypants.enderio.machine;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;

public class MachineSound extends PositionedSound implements ITickableSound {

  private boolean donePlaying;

  public MachineSound(ResourceLocation sound, float x, float y, float z, float volume, float pitch) {
    super(sound);
    this.xPosF = x;
    this.yPosF = y;
    this.zPosF = z;
    this.volume = volume;
    this.field_147663_c = pitch;
    this.repeat = true;
  }

  @Override
  public void update() {
    ;
  }

  @Override
  public boolean isDonePlaying() {
    return donePlaying;
  }

  public void endPlaying() {
    donePlaying = true;
  }

  public void startPlaying() {
    donePlaying = false;
  }

  public MachineSound setVolume(float vol) {
    this.volume = vol;
    return this;
  }

  public MachineSound setPitch(float pitch) {
    this.field_147663_c = pitch;
    return this;
  }
}
