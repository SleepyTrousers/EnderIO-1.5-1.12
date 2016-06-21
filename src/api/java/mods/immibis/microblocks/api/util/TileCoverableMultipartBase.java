package mods.immibis.microblocks.api.util;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import mods.immibis.core.api.multipart.IPartContainer;
import mods.immibis.microblocks.api.IMicroblockIntegratedTile;

public abstract class TileCoverableMultipartBase extends TileCoverableBase implements IPartContainer, IMicroblockIntegratedTile {
	protected abstract int getNumTileOwnedParts();
	
	private static MovingObjectPosition getCloserMOP(MovingObjectPosition a, MovingObjectPosition b, Vec3 src) {
		if(a == null) return b;
		if(b == null) return a;
		
		double ciDistSq = a.hitVec.squareDistanceTo(src);
		double normalDistSq = b.hitVec.squareDistanceTo(src);
		
		return ciDistSq < normalDistSq ? a : b;
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(Vec3 src, Vec3 dst) {
		MovingObjectPosition result = null;
		
		for(int k = 0, num = getNumTileOwnedParts(); k < num; k++) {
			AxisAlignedBB partBB = getPartAABBFromPool(k);
			if(partBB != null) {
				partBB.offset(xCoord, yCoord, zCoord);
				MovingObjectPosition intersect = partBB.calculateIntercept(src, dst);
				if(intersect != null) {
					intersect.subHit = k;
					intersect.typeOfHit = MovingObjectPosition.MovingObjectType.BLOCK;
					intersect.blockX = xCoord;
					intersect.blockY = yCoord;
					intersect.blockZ = zCoord;
					result = getCloserMOP(result, intersect, src);
				}
			}
		}
		
		return result;
	}
	
	@Override
	public void getCollidingBoundingBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity entity) {
		for(int k = 0, num = getNumTileOwnedParts(); k < num; k++) {
			AxisAlignedBB partBB = getPartAABBFromPool(k);
			if(partBB != null) {
				partBB.offset(xCoord, yCoord, zCoord);
				if(partBB.intersectsWith(mask))
					list.add(partBB);
			}
		}
	}
	
}
