package thaumcraft.api.casters;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class FocusModSplit extends FocusMod {
	
	private ArrayList<FocusPackage> packages = new ArrayList<>(); 

	public final ArrayList<FocusPackage> getSplitPackages() {
		return packages;
	}
	
	public void deserialize(NBTTagCompound nbt) {
		NBTTagList nodelist = nbt.getTagList("packages", (byte)10);
		packages.clear();
		for (int x=0;x<nodelist.tagCount();x++) {
			NBTTagCompound nodenbt = (NBTTagCompound) nodelist.getCompoundTagAt(x);
			FocusPackage fp = new FocusPackage();
			fp.deserialize(nodenbt);
			packages.add(fp);
		}
	}
	
	public NBTTagCompound serialize() {
		NBTTagCompound nbt = new NBTTagCompound();		
		NBTTagList nodelist = new NBTTagList();
		for (FocusPackage node:packages) {
			nodelist.appendTag(node.serialize());
		}
		nbt.setTag("packages", nodelist);		
		return nbt;
	}
	
	@Override
	public float getPowerMultiplier() {
		return .75f;
	}

}
