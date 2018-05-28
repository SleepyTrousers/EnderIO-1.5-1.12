package crazypants.enderio.zoo.entity;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.material.material.Material;
import crazypants.enderio.base.teleport.RandomTeleportUtil;
import crazypants.enderio.zoo.config.Config;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class EntityConcussionCreeper extends EntityCreeper implements IEnderZooMob {

  public static final String NAME = "concussioncreeper";
  public static final int EGG_BG_COL = 0x56FF8E;
  public static final int EGG_FG_COL = 0xFF0A22;

  private Field fTimeSinceIgnited;
  private Field fFuseTime;

  public EntityConcussionCreeper(World world) {
    super(world);
    try {
      fTimeSinceIgnited = ReflectionHelper.findField(EntityCreeper.class, "timeSinceIgnited", "field_70833_d");
      fFuseTime = ReflectionHelper.findField(EntityCreeper.class, "fuseTime", "field_82225_f");
    } catch (Exception e) {
      Log.error("Could not create ender creeper logic as fields not found");
    }
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    MobInfo.CONCUSSION_CREEPER.applyAttributes(this);
  }

  @Override
  public void onUpdate() {

    if (isEntityAlive()) {
      int timeSinceIgnited = getTimeSinceIgnited();
      int fuseTime = getFuseTime();

      if (timeSinceIgnited >= fuseTime - 1) {
        setTimeSinceIgnited(0);

        int range = Config.concussionCreeperExplosionRange;
        AxisAlignedBB bb = new AxisAlignedBB(posX - range, posY - range, posZ - range, posX + range, posY + range, posZ + range);
        List<EntityLivingBase> ents = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
        for (EntityLivingBase ent : ents) {
          if (ent != this) {
            if (!world.isRemote) {
              boolean done = false;
              for (int i = 0; i < 20 && !done; i++) {
                done = RandomTeleportUtil.teleportRandomly(ent.world, ent, false, true, Config.concussionCreeperMaxTeleportRange);
              }
            }
            if (ent instanceof EntityPlayer) {
              world.playSound(ent.posX, ent.posY, ent.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
              EnderZoo.proxy.setInstantConfusionOnPlayer((EntityPlayer) ent, Config.concussionCreeperConfusionDuration);
            }
          }
        }

        world.playSound(posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 4.0F,
            (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F, false);
        world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, posX, posY, posZ, 1.0D, 0.0D, 0.0D);
        setDead();
      }
    }

    super.onUpdate();

  }

  @Override
  protected void dropFewItems(boolean hitByPlayer, int looting) {
    int j = rand.nextInt(3);
    if (looting > 0) {
      j += rand.nextInt(looting + 1);
    }
    for (int k = 0; k < j; ++k) {
      dropItem(getDropItem(), 1);
    }
  }

  @Override
  protected @Nonnull Item getDropItem() {
    int num = rand.nextInt(3);
    if (num == 0) {
      return Material.SHARD_ENDER.getStack();
    } else if (num == 1) {
      return EnderZoo.itemConfusingDust;
    } else {
      return Items.GUNPOWDER;
    }
  }

  @Override
  @Nullable
  protected ResourceLocation getLootTable() {
    return null; // use getDropItem() instead
  }

  private void setTimeSinceIgnited(int i) {
    if (fTimeSinceIgnited == null) {
      return;
    }
    try {
      fTimeSinceIgnited.setInt(this, i);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private int getTimeSinceIgnited() {
    if (fTimeSinceIgnited == null) {
      return 0;
    }
    try {
      return fTimeSinceIgnited.getInt(this);
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  private int getFuseTime() {
    if (fFuseTime == null) {
      return 0;
    }
    try {
      return fFuseTime.getInt(this);
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

}
