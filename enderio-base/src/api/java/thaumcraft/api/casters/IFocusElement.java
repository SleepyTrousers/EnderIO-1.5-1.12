package thaumcraft.api.casters;

public interface IFocusElement {
	
	public String getKey();
	
	public String getResearch();
	
	public EnumUnitType getType();
	
	enum EnumUnitType {
		EFFECT, MEDIUM, MOD, PACKAGE; 		
	}
	
	
}
