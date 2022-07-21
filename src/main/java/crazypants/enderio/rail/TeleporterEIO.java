package crazypants.enderio.rail;

import net.minecraft.entity.Entity;
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
    public boolean placeInExistingPortal(
            Entity p_placeInExistingPortal_1_,
            double p_placeInExistingPortal_2_,
            double p_placeInExistingPortal_4_,
            double p_placeInExistingPortal_6_,
            float p_placeInExistingPortal_8_) {
        return true;
    }

    @Override
    public void placeInPortal(Entity entity, double x, double y, double z, float p_77185_8_) {
        entity.setLocationAndAngles(x, y, z, entity.rotationPitch, entity.rotationYaw);
        entity.motionX = 0;
        entity.motionY = 0;
        entity.motionZ = 0;
    }

    @Override
    public void removeStalePortalLocations(long p_removeStalePortalLocations_1_) {}
}
