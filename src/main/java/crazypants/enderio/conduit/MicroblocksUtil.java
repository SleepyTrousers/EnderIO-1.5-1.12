package crazypants.enderio.conduit;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import mods.immibis.microblocks.api.EnumPosition;

public class MicroblocksUtil {

    private static boolean useCheckPerformed = false;
    private static boolean supportMicroblocks = false;

    public static boolean supportMicroblocks() {
        if (!useCheckPerformed) {
            supportMicroblocks = Loader.isModLoaded("ImmibisMicroblocks");
            useCheckPerformed = true;
        }
        return supportMicroblocks;
    }

    @Method(modid = "ImmibisMicroblocks")
    public static ForgeDirection posToDir(EnumPosition pos) {
        switch (pos) {
            case FaceNX:
                return ForgeDirection.WEST;
            case FaceNY:
                return ForgeDirection.DOWN;
            case FaceNZ:
                return ForgeDirection.NORTH;
            case FacePX:
                return ForgeDirection.EAST;
            case FacePY:
                return ForgeDirection.UP;
            case FacePZ:
                return ForgeDirection.SOUTH;
            default:
                return null;
        }
    }
}
