package thaumcraft.api.research;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ScanBlock implements IScanThing {
	
	String research;	
	Block[] blocks;
	
	public ScanBlock(Block block) {
		this("!"+block.getRegistryName().toString(),new Block[] {block});
	}

	public ScanBlock(String research, Block ... blocks) {
		this.research = research;
		this.blocks = blocks;
		for (Block block:blocks)
			ScanningManager.addScannableThing(new ScanItem(research, new ItemStack(block)));
	}		
	
	@Override
	public boolean checkThing(EntityPlayer player, Object obj) {		
		if (obj!=null && obj instanceof BlockPos) {
			for (Block block:blocks) 
				if (player.world.getBlockState((BlockPos) obj).getBlock()==block) 
					return true;
		}
		return false;
	}
	
	@Override
	public String getResearchKey(EntityPlayer player, Object object) {		
		return research;
	}
}
