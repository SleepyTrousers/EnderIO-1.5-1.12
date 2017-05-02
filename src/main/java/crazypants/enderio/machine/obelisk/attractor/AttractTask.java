package crazypants.enderio.machine.obelisk.attractor;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;

class AttractTask extends EntityAIBase {

  private EntityLiving mob;
  BlockPos coord;
  private FakePlayer target;
  private int updatesSincePathing;

  private boolean started = false;

  AttractTask(EntityLiving mob, FakePlayer target, BlockPos coord) {
    this.mob = mob;
    this.coord = coord;
    this.target = target;
  }

  @Override
  public boolean shouldExecute() {
    return continueExecuting();
  }

  @Override
  public void resetTask() {
    started = false;
    updatesSincePathing = 0;
  }

  @Override
  public boolean continueExecuting() {
    boolean res = false;
    TileEntity te = mob.world.getTileEntity(coord);
    if (te instanceof TileAttractor) {
      TileAttractor attractor = (TileAttractor) te;
      res = !attractor.isInvalid() && attractor.isActive() && attractor.canAttract(mob);
    }
    return res;
  }

  @Override
  public boolean isInterruptible() {
    return true;
  }

  @Override
  public void updateTask() {
    if (!started || updatesSincePathing > 20) {
      started = true;
      int speed = 1;
      // mob.getNavigator().setAvoidsWater(false);
      boolean res = mob.getNavigator().tryMoveToEntityLiving(target, speed);
      if (!res) {
        for (EnumFacing dir : EnumFacing.values()) {
          if (!res) {
            res = mob.getNavigator().tryMoveToXYZ(target.posX + dir.getFrontOffsetX(), target.posY + dir.getFrontOffsetY(), target.posZ + dir.getFrontOffsetZ(),
                speed);
          }
        }
      }
      updatesSincePathing = 0;
    } else {
      updatesSincePathing++;
    }
  }

}