package crazypants.enderio.item.darksteel;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.config.Config;

public class SoundEntity extends Entity {

  int lifeSpan = Config.darkSteelSoundLocatorLifespan;
  float volume;

  public SoundEntity(World world, Vector3d pos, float volume) {
    super(world);
    setPosition(pos.x, pos.y, pos.z);
    this.volume = volume;
  }

  @Override
  protected void entityInit() {
  }

  protected boolean canTriggerWalking() {
    return false;
  }

  @Override
  public AxisAlignedBB getBoundingBox() {
    return null;
  }

  @Override
  public void onUpdate() {
    super.onUpdate();
    lifeSpan--;
    if(lifeSpan == 0) {
      setDead();
    }
  }

  @Override
  protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
  }

  @Override
  protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
  }

}
