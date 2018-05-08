package thaumcraft.api.casters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.minecraft.util.math.RayTraceResult;
import thaumcraft.api.aspects.Aspect;

public abstract class FocusNode implements IFocusElement { 
	
	public FocusNode() {
		super();
		initialize();
	}
	
	public String getUnlocalizedName() {
		return getKey()+".name";
	}

	public String getUnlocalizedText() {
		return getKey()+".text";
	}

	public abstract int getComplexity();
	
	public abstract Aspect getAspect();
		
	public abstract EnumSupplyType[] mustBeSupplied();
	public abstract EnumSupplyType[] willSupply();
	
	public boolean canSupply(EnumSupplyType type) {
		if (willSupply()!=null)
		for (EnumSupplyType st:willSupply()) {
			if (st==type) return true;
		}
		return false;
	}
	
	public enum EnumSupplyType {
		TARGET, TRAJECTORY; 		
	}
	
	
	public RayTraceResult[] supplyTargets() { return null;}	
	
	public Trajectory[] supplyTrajectories() { return null;}
	
	FocusPackage pack;
	
	public final void setPackage(FocusPackage pack) {
		this.pack = pack;
	}
	
	public final FocusPackage getPackage() {
		return pack;
	}
	
	public final FocusPackage getRemainingPackage() {
		FocusPackage p = getPackage();
		List<IFocusElement> l = p.nodes.subList(p.index+1, p.nodes.size());
		List<IFocusElement> l2 = Collections.synchronizedList(new ArrayList<>());	
		for (IFocusElement fe:l) l2.add(fe);
		FocusPackage p2 = new FocusPackage();
		p2.setUniqueID(p.getUniqueID());
		p2.world = p.world;
		p2.multiplyPower(p.getPower());
		p2.nodes = l2;
		p2.setCasterUUID(p.getCasterUUID());		
		return l2.isEmpty() ? null : p2;
	}
	
	private FocusNode parent;
	
	public final FocusNode getParent() {
		return parent;
	}

	final HashMap<String, NodeSetting> settings = new HashMap<>();
	
	public final Set<String> getSettingList() {
		return settings.keySet();
	}	 	
	
	public final NodeSetting getSetting(String key) {
		return settings.get(key);
	}
	
	public final int getSettingValue(String key) {
		return settings.containsKey(key) ? settings.get(key).getValue() : 0;
	}
	
	public NodeSetting[] createSettings() {
		return null;
	}
	
	public final void initialize() {
		NodeSetting[] set = createSettings();
		if (set!=null) {
			for (NodeSetting setting : set) {
				settings.put(setting.key, setting);
			}
		}
	}

	public void setParent(FocusNode parent) {
		this.parent = parent;
	}
	
	public float getPowerMultiplier() {
		return 1;
	}
	
	
}
