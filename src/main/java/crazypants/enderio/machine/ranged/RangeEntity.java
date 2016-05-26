package crazypants.enderio.machine.ranged;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.vecmath.Vector4f;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RangeEntity extends Entity {

  int totalLife = 20;
  int lifeSpan = totalLife;
  private IRanged spawnGuard;
  private Vector4f color = new Vector4f(1, 1, 1, 0.4f);

  public RangeEntity(IRanged sg) {
    super(sg.getRangeWorldObj());
    spawnGuard = sg;
    BlockCoord bc = spawnGuard.getLocation();
    setPosition(bc.x, bc.y, bc.z);
    ignoreFrustumCheck = true;
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

  private final AxisAlignedBB noBB = new AxisAlignedBB(0, -1, 0, 0, -1, 0);

  @Override
  public AxisAlignedBB getEntityBoundingBox() {
    return noBB;
  }

  @Override
  public void onUpdate() {
    super.onUpdate();
    lifeSpan--;
    BlockCoord bc = spawnGuard.getLocation();
    if (!(worldObj.getTileEntity(bc.getBlockPos()) instanceof IRanged)) {
      setDead();
    }
    if (!spawnGuard.isShowingRange()) {
      setDead();
    }
  }

  @Override
  protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
  }

  @Override
  protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
  }

  float getRange() {
    return spawnGuard.getRange() + 1.001f;
  }

  BoundingBox getRangeBox() {
    return spawnGuard.getRangeBox();
  }

  public Vector4f getColor() {
    return color;
  }

  public void setColor(Vector4f color) {
    this.color = color;
  }
}
