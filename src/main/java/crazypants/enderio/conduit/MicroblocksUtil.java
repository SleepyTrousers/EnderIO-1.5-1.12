package crazypants.enderio.conduit;

import net.minecraftforge.fml.common.Loader;

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
  
//  @Method(modid = "ImmibisMicroblocks")
//  public static EnumFacing posToDir(EnumPosition pos) {
//    switch(pos) {
//    case FaceNX:
//      return EnumFacing.WEST;
//    case FaceNY:
//      return EnumFacing.DOWN;
//    case FaceNZ:
//      return EnumFacing.NORTH;
//    case FacePX:
//      return EnumFacing.EAST;
//    case FacePY:
//      return EnumFacing.UP;
//    case FacePZ:
//      return EnumFacing.SOUTH;
//    default:
//      return null;
//    }
//  }
}
