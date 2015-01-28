package crazypants.util;

import net.minecraftforge.common.util.ForgeDirection;

public class Rotation {
	
	
	private static byte[][] CCWRotationSideMap=new byte[][] {
		{0,1,4,5,3,2},
		{0,1,5,4,2,3},
		{5,4,2,3,0,1},
		{4,5,2,3,1,0},
		{2,3,1,0,4,5},
		{3,2,0,1,4,5}
	};
	
	public static byte newSide(int side, int axisRotatedAround) {
		return CCWRotationSideMap[axisRotatedAround][side];
	}
	
	public static byte newSide(int side, ForgeDirection axis) {
		return newSide(side, axis.ordinal());
	}

}
