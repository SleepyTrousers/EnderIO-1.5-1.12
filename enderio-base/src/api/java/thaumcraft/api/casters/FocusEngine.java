package thaumcraft.api.casters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;

public class FocusEngine {	
	
	public static HashMap<String,Class<IFocusElement>> elements = new HashMap<>();
	private static HashMap<String,ResourceLocation> elementIcons = new HashMap<>();
	private static HashMap<String,Integer> elementColor = new HashMap<>();
	
	public static void registerElement(Class element, ResourceLocation icon, int color) {  
		try {
			IFocusElement fe = (IFocusElement) element.newInstance();		
			elements.put(fe.getKey(), element);
			elementIcons.put(fe.getKey(), icon);
			elementColor.put(fe.getKey(), color);
		} catch (Exception e) {	} 
	}
	
	public static IFocusElement getElement(String key) {
		try {
			return elements.get(key).newInstance();
		} catch (Exception e) {	}
		return null;
	}
	
	public static ResourceLocation getElementIcon(String key) {
		return elementIcons.get(key);
	}
	
	public static int getElementColor(String key) {
		return elementColor.get(key);
	}
	
	public static boolean doesPackageContainElement(FocusPackage focusPackage, String key) {
		for (IFocusElement node: focusPackage.nodes) {
			if (node.getKey().equals(key)) return true;
		}
		return false;
	}
	
	
	
	/**
	 * 
	 * @param caster
	 * @param focusPackage
	 * @param nocopy set to true only if the focus package passed in is temporary and not attached to an actual focus. 
	 * Use this to preserve any settings, targets, etc that has been set during package construction
	 */
	public static void castFocusPackage(EntityLivingBase caster, FocusPackage focusPackage, boolean nocopy) {
		FocusPackage focusPackageCopy;
		if (nocopy) 
			focusPackageCopy = focusPackage;
		else
			focusPackageCopy = focusPackage.copy(caster);
		
		focusPackageCopy.initialize(caster);		
		focusPackageCopy.setUniqueID(UUID.randomUUID());
		for (FocusEffect effect:focusPackageCopy.getFocusEffects()) {
			effect.onCast(caster);
		}	
		
		runFocusPackage(focusPackageCopy, null, null);
	}
	
	/**
	 * Overrides castFocusPackage(EntityLivingBase caster, FocusPackage focusPackage, boolean nocopy) with nocopy = false
	 * @param caster
	 * @param focusPackage
	 */
	public static void castFocusPackage(EntityLivingBase caster, FocusPackage focusPackage) {
		castFocusPackage(caster,focusPackage,false);
	}
	
	public static void runFocusPackage(FocusPackage focusPackage, Trajectory[] trajectories, RayTraceResult[] targets) {
		
		Trajectory[] prevTrajectories = trajectories;
		RayTraceResult[] prevTargets = targets;
		
		synchronized (focusPackage.nodes) {

			if (!(focusPackage.nodes.get(0) instanceof FocusMediumRoot)) {
				focusPackage.nodes.add(0,new FocusMediumRoot(trajectories,targets));
			}		
		
			for (int idx=0;idx<focusPackage.nodes.size();idx++) {
				
				focusPackage.setExecutionIndex(idx);
				
				IFocusElement node = focusPackage.nodes.get(idx);
				if (idx>0 && ((FocusNode)node).getParent()==null) {
					IFocusElement nodePrev = focusPackage.nodes.get(idx-1);
					if (node instanceof FocusNode && nodePrev instanceof FocusNode) {
						((FocusNode)node).setParent((FocusNode)nodePrev);
					}
				}
				
				if (node instanceof FocusNode && ((FocusNode)node).getPackage()==null) {
					((FocusNode)node).setPackage(focusPackage);
				}	
				
				if (node instanceof FocusNode) {			
					focusPackage.multiplyPower(((FocusNode)node).getPowerMultiplier());
				} 
				
				if (node instanceof FocusPackage) {				
					runFocusPackage((FocusPackage) node, prevTrajectories, prevTargets);				
					break;
				} 
				else			
				if (node instanceof FocusMedium) {	
					FocusMedium medium = (FocusMedium) node;
					if (prevTrajectories!=null)
						for (Trajectory trajectory : prevTrajectories) {
							medium.execute(trajectory);
						}
					
					if (medium.hasIntermediary()) break;
				} 
				else
				if (node instanceof FocusMod) {						
					if (node instanceof FocusModSplit) {
						FocusModSplit split = (FocusModSplit) node;
						for (FocusPackage sp : split.getSplitPackages() ) {
							split.setPackage(sp);
							sp.multiplyPower(focusPackage.getPower());
							split.execute();
							/*returnLast =*/ runFocusPackage(sp,split.supplyTrajectories(),split.supplyTargets());
						}
						break;
					} else {
						((FocusMod) node).execute();
					}
				}
				else
				if (node instanceof FocusEffect) {	
					FocusEffect effect = (FocusEffect) node;
					if (prevTargets!=null) {
						int num=0;
						for (RayTraceResult target : prevTargets) {		
							if (target.entityHit!=null) {
								String k = target.entityHit.getEntityId() + focusPackage.getUniqueID().toString();
								if (damageResistList.contains(k) && target.entityHit.hurtResistantTime>0) {
									target.entityHit.hurtResistantTime=0;
								} else {
									if (damageResistList.size()>10) damageResistList.remove(0);
									damageResistList.add(k);
								}
							}
							Trajectory tra = prevTrajectories!=null? ((prevTrajectories.length==prevTargets.length) ? prevTrajectories[num] : prevTrajectories[0]) : null;
							effect.execute(target, tra, focusPackage.getPower(), num);
							num++;
						}
					}
				}				
				
				if (node instanceof FocusNode) {
					prevTrajectories = ((FocusNode)node).supplyTrajectories();
					prevTargets = ((FocusNode)node).supplyTargets();
				}
				
			}
		}
		
	}
	
	private static ArrayList<String> damageResistList = new ArrayList<>();

}
