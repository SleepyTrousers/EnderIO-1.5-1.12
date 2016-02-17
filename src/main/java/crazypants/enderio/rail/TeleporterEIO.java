package crazypants.enderio.rail;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterEIO extends Teleporter {

  public TeleporterEIO(WorldServer p_i1963_1_) {
    super(p_i1963_1_);
  }

  @Override
  public boolean makePortal(Entity p_makePortal_1_) {
    return true;
  }

  @Override
  public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
    return true;
  }

  @Override
  public void placeInPortal(Entity entity, float rotationYaw) {   
    int x = MathHelper.floor_double(entity.posX);
    int y = MathHelper.floor_double(entity.posY) - 1;
    int z = MathHelper.floor_double(entity.posZ);
    
    entity.setLocationAndAngles(x, y, z, entity.rotationPitch, entity.rotationYaw);
    entity.motionX = 0;
    entity.motionY = 0;
    entity.motionZ = 0;
  }

  @Override
  public void removeStalePortalLocations(long p_removeStalePortalLocations_1_) {
  }

}
