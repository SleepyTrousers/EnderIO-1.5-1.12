package thaumcraft.api.research;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class ScanMaterial implements IScanThing {
	
	String research;	
	Material[] mats;
	
	public ScanMaterial(Material mat) {
		research = "!"+mat.getClass().getTypeName();
		this.mats = new Material[] {mat};
	}

	public ScanMaterial(String research, Material ... mats) {
		this.research = research;
		this.mats = mats;
	}
	
	@Override
	public boolean checkThing(EntityPlayer player, Object obj) {		
		if (obj!=null && obj instanceof BlockPos) {
			for (Material mat:mats) 
				if (player.world.getBlockState((BlockPos) obj).getMaterial()==mat) 
					return true;
		}
		return false;
	}
	
	@Override
	public String getResearchKey(EntityPlayer player, Object object) {		
		return research;
	}
}
