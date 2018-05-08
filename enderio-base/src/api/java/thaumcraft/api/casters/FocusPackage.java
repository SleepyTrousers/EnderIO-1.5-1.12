package thaumcraft.api.casters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EntitySelectors;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class FocusPackage implements IFocusElement {
	
	@Override
	public String getResearch() {
		return null;
	}
	
	public World world;
	private EntityLivingBase caster;	
	private UUID casterUUID;
	
	private float power = 1;
	private int complexity = 0;
	
	int index;
	UUID uid;
	
	public List<IFocusElement> nodes =  Collections.synchronizedList(new ArrayList<>());	
	
	public FocusPackage() {	}

	public FocusPackage(EntityLivingBase caster) {
		super();
		this.world = caster.world;
		this.caster = caster;
		this.casterUUID = caster.getUniqueID();
	}	
		
	@Override
	public String getKey() {
		return "thaumcraft.PACKAGE";
	}

	@Override
	public EnumUnitType getType() {
		return EnumUnitType.PACKAGE;
	}
	
	public int getComplexity() {
		return complexity;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}

	public UUID getUniqueID() {
		return uid;
	}

	public void setUniqueID(UUID id) {
		this.uid = id;
	}
	
	public int getExecutionIndex() {
		return index;
	}

	public void setExecutionIndex(int idx) {
		this.index = idx;
	}
	
	public void addNode(IFocusElement e) {
		nodes.add(e);
	}
	
	public UUID getCasterUUID() {
		if (caster!=null) casterUUID = caster.getUniqueID();
		return casterUUID;
	}

	public void setCasterUUID(UUID casterUUID) {
		this.casterUUID = casterUUID;
	}	
	
	public EntityLivingBase getCaster() {
		try {
			if (caster==null) {
				caster = world.getPlayerEntityByUUID(getCasterUUID());
			}
			if (caster==null) {
				for (EntityLivingBase e : world.getEntities(EntityLivingBase.class, EntitySelectors.IS_ALIVE)) {
					if (getCasterUUID().equals(e.getUniqueID())) {
						caster = e;
						break;
					}
				}
			}
		} catch (Exception e) {}
		return caster;
	}
	
	public FocusEffect[] getFocusEffects() {		
		return getFocusEffectsPackage(this);
	}
	
	private FocusEffect[] getFocusEffectsPackage(FocusPackage fp) {
		ArrayList<FocusEffect> out = new ArrayList<>();
		for (IFocusElement el:fp.nodes) {
			if (el instanceof FocusEffect) out.add((FocusEffect) el);
			else
			if (el instanceof FocusPackage) {
				for (FocusEffect fep:getFocusEffectsPackage((FocusPackage) el))
					out.add(fep);
			} else 
			if (el instanceof FocusModSplit) {
				for (FocusPackage fsp:((FocusModSplit)el).getSplitPackages())
					for (FocusEffect fep:getFocusEffectsPackage(fsp))
						out.add(fep);
			}
		}
		return out.toArray(new FocusEffect[]{});
	}

	public void deserialize(NBTTagCompound nbt) {
		uid = nbt.getUniqueId("uid");		
		index = nbt.getInteger("index");
		int dim = nbt.getInteger("dim");
		world = DimensionManager.getWorld(dim);
		setCasterUUID(nbt.getUniqueId("casterUUID"));
		power = nbt.getFloat("power");
		complexity = nbt.getInteger("complexity");
				
		NBTTagList nodelist = nbt.getTagList("nodes", (byte)10);
		nodes.clear();
		for (int x=0;x<nodelist.tagCount();x++) {
			NBTTagCompound nodenbt = (NBTTagCompound) nodelist.getCompoundTagAt(x);
			EnumUnitType ut = EnumUnitType.valueOf(nodenbt.getString("type"));
			if (ut!=null) {
				if (ut==EnumUnitType.PACKAGE) {
					FocusPackage fp = new FocusPackage();
					fp.deserialize(nodenbt.getCompoundTag("package"));
					nodes.add(fp);
					break;
				} else {
					IFocusElement fn = FocusEngine.getElement(nodenbt.getString("key")); 
					if (fn!=null) {						
						if (fn instanceof FocusNode) {
							((FocusNode)fn).initialize();
							if (((FocusNode)fn).getSettingList()!=null)
								for (String ns : ((FocusNode)fn).getSettingList()) {
									((FocusNode)fn).getSetting(ns).setValue(nodenbt.getInteger("setting."+ns));
								}
							
							if (fn instanceof FocusModSplit) {								
								((FocusModSplit)fn).deserialize(nodenbt.getCompoundTag("packages"));		
							}
						}
						this.addNode(fn);
					}
				}
			}
		}
		
	}

	public NBTTagCompound serialize() {
		NBTTagCompound nbt = new NBTTagCompound();
		if (uid!=null) nbt.setUniqueId("uid", uid);
		nbt.setInteger("index", index);
		if (getCasterUUID() != null) nbt.setUniqueId("casterUUID", getCasterUUID());
		if (world!=null) nbt.setInteger("dim", world.provider.getDimension());
		nbt.setFloat("power", power);
		nbt.setInteger("complexity", complexity);
		
		//nodes
		NBTTagList nodelist = new NBTTagList();
		synchronized (nodes) {
			for (IFocusElement node:nodes) {
				NBTTagCompound nodenbt = new NBTTagCompound();
				nodenbt.setString("type", node.getType().name());
				nodenbt.setString("key", node.getKey());
				if (node.getType()==EnumUnitType.PACKAGE) {
					nodenbt.setTag("package", ((FocusPackage)node).serialize());
					nodelist.appendTag(nodenbt);
					break;
				} else {				
					if (node instanceof FocusNode && ((FocusNode)node).getSettingList()!=null)
						for (String ns : ((FocusNode)node).getSettingList()) {
							nodenbt.setInteger("setting."+ns, ((FocusNode)node).getSettingValue(ns));
						}
					if (node instanceof FocusModSplit) {	
						nodenbt.setTag("packages", ((FocusModSplit)node).serialize());	
					}
					nodelist.appendTag(nodenbt);
				}			
			}
		}
		nbt.setTag("nodes", nodelist);					
		
		return nbt;
	}

	public float getPower() {
		return power;
	}

	public void multiplyPower(float pow) {
		this.power *= pow;
	}

	public FocusPackage copy(EntityLivingBase caster) {
		FocusPackage fp = new FocusPackage(caster);
		fp.deserialize(this.serialize());
		return fp;
	}
	
	public void initialize(EntityLivingBase caster) {
		world=caster.getEntityWorld();
		IFocusElement node = nodes.get(0);
		if (node instanceof FocusMediumRoot && ((FocusMediumRoot)node).supplyTargets()==null) {
			((FocusMediumRoot)node).setupFromCaster(caster);
		}
	}

	public int getSortingHelper() {
		String s="";
		for (IFocusElement k:this.nodes) {
			s+=k.getKey();
		}
		return s.hashCode();
	}

	
	
	

	
	
	
	
	

}
