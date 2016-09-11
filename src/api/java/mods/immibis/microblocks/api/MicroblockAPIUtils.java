package mods.immibis.microblocks.api;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MicroblockAPIUtils {
	private static IMicroblockSystem cachedIMS;
	private static boolean haveCachedIMS;
	public static synchronized IMicroblockSystem getMicroblockSystem() {
		if(!haveCachedIMS) {
			try {
				cachedIMS = (IMicroblockSystem)Class.forName("mods.immibis.microblocks.MicroblockSystem").getField("instance").get(null);
				haveCachedIMS = true;
			} catch(ClassNotFoundException e) {
				cachedIMS = null;
				haveCachedIMS = true;
			} catch(RuntimeException e) {
				throw e;
			} catch(Exception e) {
				throw (AssertionError)new AssertionError("should not happen").initCause(e);
			}
		}
		return cachedIMS;
	}
	
	public static IMicroblockCoverSystem createMicroblockCoverSystem(IMicroblockSupporterTile tile) {
		IMicroblockSystem ims = getMicroblockSystem();
		return ims == null ? null : ims.createMicroblockCoverSystem(tile);
	}

	public static Block getMicroblockContainerBlock() {
		IMicroblockSystem ims = getMicroblockSystem();
		return ims == null ? null : ims.getMicroblockContainerBlock();
	}
	
	public static PartType<?> getPartTypeByID(int i) {
		IMicroblockSystem ims = getMicroblockSystem();
		return ims == null ? null : ims.getPartTypeByID(i);
	}
	
	/**
	 * If there is a microblock container block at the specified coordinates, this function will save the parts in that block,
	 * place a new block, restore the parts, and return true.
	 * If there is not a microblock container block at the specified coordinates, or placing the block fails, it will
	 * return false.
	 * The new block must have a tile entity which implements IMicroblockSupporterTile and has a non-null IMicroblockCoverSystem.
	 */
	public static boolean mergeIntoMicroblockContainer(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int l, Block newBlock, int newMetadata)
	{
		Block microblockContainerBlock = getMicroblockContainerBlock();
		if(microblockContainerBlock == null || world.getBlock(x, y, z) != microblockContainerBlock)
			return false;

		IMicroblockSupporterTile tm = (IMicroblockSupporterTile)world.getTileEntity(x, y, z);
		IMicroblockCoverSystem oldCI = tm.getCoverSystem();
		if(!world.setBlock(x, y, z, newBlock, newMetadata, 2))
			return false;
		
		IMicroblockSupporterTile tcb = (IMicroblockSupporterTile)world.getTileEntity(x, y, z);
		IMicroblockCoverSystem newCI = tcb.getCoverSystem();
		
		for(Part p : oldCI.getAllParts())
			newCI.addPart(p);
		
        newBlock.onBlockPlacedBy(world, x, y, z, entityplayer, itemstack);
		return true;
	}
}
