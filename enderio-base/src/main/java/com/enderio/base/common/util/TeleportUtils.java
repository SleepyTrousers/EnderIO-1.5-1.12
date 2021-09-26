package com.enderio.base.common.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class TeleportUtils {

    // chorusfruit based random teleport
    public static void randomTeleport(LivingEntity entity, double range) {
        if (!entity.level.isClientSide) {

            for (int i = 0; i < 16; ++i) {
                double d3 = entity.getX() + entity.getRandom().nextDouble() * range;
                double d4 = Mth.clamp(entity.getY() + (entity.getRandom().nextDouble()) * range / 2.0D,
                        (double) entity.level.getMinBuildHeight(), (double) (entity.level.getMinBuildHeight()
                                + ((ServerLevel) entity.level).getLogicalHeight() - 1));
                double d5 = entity.getZ() + entity.getRandom().nextDouble() * range;
                if (entity.isPassenger()) {
                    entity.stopRiding();
                }

                if (entity.randomTeleport(d3, d4, d5, true)) {
                    break;
                }
            }
        }
    }
}
