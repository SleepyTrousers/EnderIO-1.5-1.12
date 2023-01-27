package crazypants.enderio.api.teleport;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

    TELEPORT_STAFF() {
        @Override
        public int getMaxDistanceTravelled() {
            return Config.teleportStaffMaxDistance;
        }

        @Override
        public float getPowerCostPerBlockTraveledRF() {
            return 0f;
        }
    },
    TELEPORT_STAFF_BLINK() {
        @Override
        public int getMaxDistanceTravelled() {
            return Config.teleportStaffMaxBlinkDistance;
        }

        @Override
        public float getPowerCostPerBlockTraveledRF() {
            return 0f;
        }
    },
    TELEPAD(EnderIO.DOMAIN + ":telepad.teleport");

    public static int getMaxDistance() {
        return STAFF.getMaxDistanceTravelledSq();
    }

    @SideOnly(Side.CLIENT)
    public static int getMaxDistanceSq() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (player == null) {
            return 0;
        }

        TravelSource source = TravelController.instance.getTravelItemTravelSource(player);
        if (source == null) {
            return TravelSource.BLOCK.getMaxDistanceTravelledSq();
        } else {
            return source.getMaxDistanceTravelledSq();
        }
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
