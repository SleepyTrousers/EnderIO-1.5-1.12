package thaumcraft.api.casters;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import thaumcraft.api.aspects.Aspect;

public class FocusMediumRoot extends FocusMedium {
		
	public FocusMediumRoot() {
		super();
	}
	
	Trajectory[] trajectories=null;
	RayTraceResult[] targets=null;
	
	public FocusMediumRoot(Trajectory[] trajectories, RayTraceResult[] targets) {
		super();
		this.trajectories = trajectories;
		this.targets = targets;
	}
	
	@Override
	public String getResearch() {
		return "BASEAUROMANCY";
	}

	@Override
	public String getKey() {
		return "ROOT";
	}
	
	@Override
	public int getComplexity() {
		return 0;
	}
	
	@Override
	public EnumSupplyType[] willSupply() {
		return new EnumSupplyType[] {EnumSupplyType.TARGET, EnumSupplyType.TRAJECTORY};
	}

	@Override
	public RayTraceResult[] supplyTargets() {
		return targets;
	}

	@Override
	public Trajectory[] supplyTrajectories() {
		return trajectories;
	}
	
	public void setupFromCaster (EntityLivingBase caster) {
		trajectories = new Trajectory[] { new Trajectory(generateSourceVector(caster), caster.getLookVec()) };
		targets = new RayTraceResult[] { new RayTraceResult(caster) };
	}
	
	/**
	 * Useful if you want to cast stuff from something other than the player and where their getLookVec() is often not accurate.
	 * @param caster
	 * @param target
	 * @param offset use to aim above or below the target
	 */
	public void setupFromCasterToTarget(EntityLivingBase caster, Entity target, double offset) {
		Vec3d sv = generateSourceVector(caster);	
		double d0 = target.posX - sv.x;
        double d1 = target.getEntityBoundingBox().minY + (double)(target.height / 2.0F) - sv.y;
        double d2 = target.posZ - sv.z;
        Vec3d lv = new Vec3d(d0,d1 + offset,d2);
		trajectories = new Trajectory[] { new Trajectory(sv, lv.normalize()) };
		targets = new RayTraceResult[] { new RayTraceResult(caster) };
	}
	
	/**
	 * Useful if you want to cast stuff from something other than the player and where their getLookVec() is often not accurate.
	 * @param caster
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setupFromCasterToTargetLoc(EntityLivingBase caster, double x, double y, double z) {
		Vec3d sv = generateSourceVector(caster);
		double d0 = x - sv.x;
        double d1 = y - sv.y;
        double d2 = z - sv.z;
        Vec3d lv = new Vec3d(d0,d1,d2);
		trajectories = new Trajectory[] { new Trajectory(sv, lv.normalize()) };
		targets = new RayTraceResult[] { new RayTraceResult(caster) };
	}
	
	private Vec3d generateSourceVector(EntityLivingBase e) {
		Vec3d v = e.getPositionVector();		
		v = v.addVector(0, e.getEyeHeight() - 0.10000000149011612D, 0);		
		return v;
	}

	@Override
	public Aspect getAspect() {
		return null;
	}

	
	
}
