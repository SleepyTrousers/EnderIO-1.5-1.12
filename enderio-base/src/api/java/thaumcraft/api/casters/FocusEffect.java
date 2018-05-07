package thaumcraft.api.casters;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class FocusEffect extends FocusNode {
	
	@Override
	public EnumUnitType getType() {
		return EnumUnitType.EFFECT;
	}

	@Override
	public final EnumSupplyType[] mustBeSupplied() {
		return new EnumSupplyType[] {EnumSupplyType.TARGET};
	}

	@Override
	public EnumSupplyType[] willSupply() {
		return null;
	}

	public abstract boolean execute(RayTraceResult target, @Nullable Trajectory trajectory, float finalPower, int num);
	
	public float getDamageForDisplay(float finalPower) {
		return 0;
	}
	
	public abstract void renderParticleFX(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ);

	public void onCast(Entity caster) {	
		
	}
}
