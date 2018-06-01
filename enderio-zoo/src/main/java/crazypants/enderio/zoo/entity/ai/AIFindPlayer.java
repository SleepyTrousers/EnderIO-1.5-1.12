package crazypants.enderio.zoo.entity.ai;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.zoo.entity.EntityEnderminy;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;

public class AIFindPlayer extends EntityAINearestAttackableTarget<EntityPlayer> {

  private EntityPlayer targetPlayer;
  private int stareTimer;
  private int teleportDelay;
  private final @Nonnull EntityEnderminy enderminy;

  public AIFindPlayer(@Nonnull EntityEnderminy enderminy) {
    super(enderminy, EntityPlayer.class, true);
    this.enderminy = enderminy;
  }

  /**
   * Returns whether the EntityAIBase should begin execution.
   */
  @Override
  public boolean shouldExecute() {
    double d0 = getTargetDistance();
    List<EntityPlayer> list = taskOwner.world.getEntitiesWithinAABB(EntityPlayer.class, taskOwner.getEntityBoundingBox().expand(d0, 4.0D, d0),
        targetEntitySelector);
    Collections.sort(list, this.sorter);
    if (list.isEmpty()) {
      return false;
    } else {
      targetPlayer = list.get(0);
      return true;
    }
  }

  /**
   * Execute a one shot task or start executing a continuous task
   */
  @Override
  public void startExecuting() {
    stareTimer = 5;
    teleportDelay = 0;
  }

  /**
   * Resets the task
   */
  @Override
  public void resetTask() {
    targetPlayer = null;
    enderminy.setScreaming(false);
    IAttributeInstance iattributeinstance = enderminy.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
    iattributeinstance.removeModifier(EntityEnderminy.getAttackingspeedboostmodifier());
    super.resetTask();
  }

  /**
   * Returns whether an in-progress EntityAIBase should continue executing
   */
  @Override
  public boolean shouldContinueExecuting() {
    if (targetPlayer != null) {
      if (!enderminy.shouldAttackPlayer(targetPlayer)) {
        return false;
      } else {
        enderminy.setAggressive(true);
        enderminy.faceEntity(targetPlayer, 10.0F, 10.0F);
        return true;
      }
    } else {
      return super.shouldContinueExecuting();
    }
  }

  /**
   * Updates the task
   */
  @Override
  public void updateTask() {
    if (targetPlayer != null) {
      if (--stareTimer <= 0) {
        targetEntity = targetPlayer;
        targetPlayer = null;
        super.startExecuting();
        enderminy.playSound(SoundEvents.ENTITY_ENDERMEN_STARE, 1.0F, 1.0F);
        enderminy.setScreaming(true);
        IAttributeInstance iattributeinstance = enderminy.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        iattributeinstance.applyModifier(EntityEnderminy.getAttackingspeedboostmodifier());
      }
    } else {
      if (targetEntity != null) {
        if (enderminy.shouldAttackPlayer(this.targetEntity)) {
          if (targetEntity.getDistanceSq(enderminy) < 16.0D) {
            enderminy.teleportRandomly();
          }
          teleportDelay = 0;
        } else if (targetEntity.getDistanceSq(enderminy) > 256.0D && this.teleportDelay++ >= 30 && enderminy.teleportToEntity(targetEntity)) {
          teleportDelay = 0;
        }
      }
      super.updateTask();
    }
  }
}