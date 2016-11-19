package crazypants.util;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class MagnetUtil {

  public static final String EIO_PULLER_TAG = "EIOpuller";

  public static boolean shouldAttract(@Nullable BlockPos pullerPos, @Nullable Entity entity) {

    if (entity == null || entity.isDead) {
      return false;
    }
    if (entity instanceof IProjectile && entity.motionY > 0.01) {
      return false;
    }

    NBTTagCompound data = entity.getEntityData();

    if (!data.hasKey(EIO_PULLER_TAG)) {
      // if it is not being pulled already, pull it
      if (pullerPos != null) {
        data.setLong(EIO_PULLER_TAG, pullerPos.toLong());
      }
      return true;
    }

    if (pullerPos == null) {
      // it is already being pulled, so with no further info we are done
      return true;
    }

    long posL = data.getLong(EIO_PULLER_TAG);
    if (posL == pullerPos.toLong()) {
      // item already pulled from pullerPos so done
      return true;
    }

    // it is being pulled by something else, so check to see if we are closer
    BlockPos curOwner = BlockPos.fromLong(posL);
    double distToCur = curOwner.distanceSqToCenter(entity.posX, entity.posY, entity.posZ);
    double distToMe = pullerPos.distanceSqToCenter(entity.posX, entity.posY, entity.posZ);
    if (distToMe + 1 < distToCur) {
      // only take over if it is clearly nearer to us
      data.setLong(EIO_PULLER_TAG, pullerPos.toLong());
      return true;
    }
    return false;
  }

  public static void release(@Nullable Entity entity) {
    if (entity != null && !entity.isDead) {
      NBTTagCompound data = entity.getEntityData();
      data.removeTag(EIO_PULLER_TAG);
    }
  }

}
