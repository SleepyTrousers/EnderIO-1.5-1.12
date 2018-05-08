package thaumcraft.api.casters;

public abstract class FocusMedium extends FocusNode {
	

	@Override
	public EnumUnitType getType() {
		return EnumUnitType.MEDIUM;
	}

	@Override
	public final EnumSupplyType[] mustBeSupplied() {
		return this instanceof FocusMediumRoot ? null : new EnumSupplyType[] {EnumSupplyType.TRAJECTORY};
	}
	
	@Override
	public EnumSupplyType[] willSupply() {
		return new EnumSupplyType[] {EnumSupplyType.TARGET};
	}
	
	public boolean hasIntermediary() {
		return false;
	}

	public boolean execute(Trajectory trajectory) {
		return true;
	}
	
	public boolean isExclusive() {
		return false;
	}
}
