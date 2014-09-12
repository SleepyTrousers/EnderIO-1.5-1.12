package crazypants.enderio.machine.spawnguard;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RangeEntity extends Entity {

  int totalLife = 80;
  int lifeSpan = totalLife;
  float range;  
  private TileSpawnGuard spawnGuard;

  public RangeEntity(TileSpawnGuard sg) {
    super(sg.getWorldObj());
    spawnGuard = sg;
    setPosition(sg.xCoord + 0.5, sg.yCoord + 0.5, sg.zCoord + 0.5);
    ignoreFrustumCheck = true;
    this.range = sg.getRange() + 0.5f;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isInRangeToRender3d(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
    return true;
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
    if(spawnGuard.isInvalid() || !spawnGuard.isShowingRange()) {
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
