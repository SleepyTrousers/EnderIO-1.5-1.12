package thaumcraft.api.research;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.oredict.OreDictionary;

public class ScanOreDictionary implements IScanThing {
	
	String research;	
	String[] entries;	

	public ScanOreDictionary(String research, String ... entries) {
		this.research = research;
		this.entries = entries;
	}
	
	@Override
	public boolean checkThing(EntityPlayer player, Object obj) {	
		ItemStack stack = null;
		if (obj!=null && obj instanceof BlockPos) {
			IBlockState state = player.world.getBlockState((BlockPos) obj);
			stack = state.getBlock().getItem(player.world, (BlockPos) obj, state);			
		}
		if (obj!=null && obj instanceof ItemStack) 
			stack = (ItemStack) obj;
		if (obj!=null && obj instanceof EntityItem && ((EntityItem)obj).getItem()!=null) 
			stack = ((EntityItem)obj).getItem();
		
		if (stack!=null && !stack.isEmpty()) {
			int[] ids = OreDictionary.getOreIDs(stack);
			for (String entry:entries) {
				for (int id:ids) {
					if (OreDictionary.getOreName(id).equals(entry)) return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public String getResearchKey(EntityPlayer player, Object object) {		
		return research;
	}
}
