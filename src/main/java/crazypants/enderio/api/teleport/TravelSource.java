package crazypants.enderio.api.teleport;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.teleport.TravelController;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public enum TravelSource {
    BLOCK() {
        @Override
        public int getMaxDistanceTravelled() {
            return Config.travelAnchorMaxDistance;
        }
    },
    STAFF() {
        @Override
        public int getMaxDistanceTravelled() {
            return Config.travelStaffMaxDistance;
        }

        @Override
        public float getPowerCostPerBlockTraveledRF() {
            return Config.travelStaffPowerPerBlockRF;
        }
    },
    STAFF_BLINK() {
        @Override
        public int getMaxDistanceTravelled() {
            return Config.travelStaffMaxBlinkDistance;
        }

        @Override
        public float getPowerCostPerBlockTraveledRF() {
            return Config.travelStaffPowerPerBlockRF;
        }
    },
    TELEPAD(EnderIO.DOMAIN + ":telepad.teleport");

    public static int getMaxDistance() {
        return STAFF.getMaxDistanceTravelledSq();
    }

    public static int getMaxDistanceSq() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (player == null) {
            return 0;
        }

        return TravelController.instance.isTravelItemActive(player)
                ? TravelSource.STAFF.getMaxDistanceTravelledSq()
                : TravelSource.BLOCK.getMaxDistanceTravelledSq();
    }

    public final String sound;

    private TravelSource() {
        this("mob.endermen.portal");
    }

    private TravelSource(String sound) {
        this.sound = sound;
    }

    public boolean getConserveMomentum() {
        return this == STAFF_BLINK;
    }

    public int getMaxDistanceTravelled() {
        return 0;
    }

    public int getMaxDistanceTravelledSq() {
        return getMaxDistanceTravelled() * getMaxDistanceTravelled();
    }

    public float getPowerCostPerBlockTraveledRF() {
        return 0;
    }
}
