package crazypants.enderio.machines.machine.obelisk.attractor.handlers;

import javax.annotation.Nonnull;

import crazypants.enderio.machines.machine.obelisk.attractor.TileAttractor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;

class AttractTask extends EntityAIBase {

  protected final @Nonnull EntityLiving mob;
  protected final @Nonnull BlockPos coord;
  protected final @Nonnull FakePlayer target;
  private int updatesSincePathing;

  AttractTask(@Nonnull EntityLiving mob, @Nonnull FakePlayer target, @Nonnull BlockPos coord) {
    this.mob = mob;
    this.coord = coord;
    this.target = target;
    setMutexBits(2);
  }

  @Override
  public boolean shouldExecute() {
    return shouldContinueExecuting();
  }

  @Override
  public void resetTask() {
    updatesSincePathing = 0;
  }

  @Override
  public boolean shouldContinueExecuting() {
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
    if (--updatesSincePathing <= 0) {
      doUpdateTask();
      updatesSincePathing = 20;
    }
  }

  protected void doUpdateTask() {
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
  }

}