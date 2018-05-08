package thaumcraft.api.casters;

import net.minecraft.util.math.Vec3d;

public class Trajectory {
	
	public Vec3d source;
	public Vec3d direction;
	
	public Trajectory(Vec3d source, Vec3d direction) {
		this.source = source;
		this.direction = direction;
	}

}
