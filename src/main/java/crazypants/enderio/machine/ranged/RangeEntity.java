package crazypants.enderio.machine.ranged;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

import com.enderio.core.common.util.BlockCoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RangeEntity extends Entity {

  int animTime = 20;
  int lifeSpan = animTime;
  float range;  
  private IRanged spawnGuard;
  private boolean shrink = false;

  public RangeEntity(IRanged sg) {
    super(sg.getWorld());
    spawnGuard = sg;
    BlockCoord bc = spawnGuard.getLocation();
    setPosition(bc.x + 0.5, bc.y + 0.5, bc.z + 0.5);
    ignoreFrustumCheck = true;
    range = sg.getRange() + 0.5f;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isInRangeToRender3d(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
    return true;
  }

  @Override
  protected void entityInit() {
  }

  @Override
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
    if (spawnGuard.getRange() + 0.5f != range) {
      lifeSpan = animTime;
      range = spawnGuard.getRange() + 0.5f;
    }
    if (shrink) {
      lifeSpan = Math.min(animTime + 1, lifeSpan + 1);
    } else {
      lifeSpan = Math.max(0, lifeSpan - 1);
    }
    BlockCoord bc = spawnGuard.getLocation();
    if(!(worldObj.getTileEntity(bc.x, bc.y, bc.z) instanceof IRanged)) {
      lifeSpan = animTime;
    }
    shrink = !spawnGuard.isShowingRange();
  }

  @Override
  protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
  }

  @Override
  protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
  }
}
