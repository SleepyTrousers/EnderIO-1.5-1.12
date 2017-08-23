package mods.immibis.microblocks.api.util;


import mods.immibis.microblocks.api.IMicroblockCoverSystem;
import mods.immibis.microblocks.api.IMicroblockSupporterTile;
import mods.immibis.microblocks.api.IMicroblockSystem;
import mods.immibis.microblocks.api.MicroblockAPIUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * Convenience class for microblock supporting tiles.
 * Handles creating an ICoverSystem, saving and loading it,
 * and implements getCollidingBoundingBoxes and collisionRayTrace.
 */
public abstract class TileCoverableBase extends TileEntity implements IMicroblockSupporterTile {
	
	protected IMicroblockCoverSystem cover;
	
	public TileCoverableBase() {
		IMicroblockSystem ims = MicroblockAPIUtils.getMicroblockSystem();
		if(ims != null)
			cover = ims.createMicroblockCoverSystem(this);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if(cover != null)
			cover.writeToNBT(tag);
	}
	
	@Override
	public Packet getDescriptionPacket() {
		if(cover == null)
			return null;
		
		NBTTagCompound tag = new NBTTagCompound();
		tag.setByteArray("C", cover.writeDescriptionBytes());
		S35PacketUpdateTileEntity p = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
		return p;
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		if(cover != null)
			cover.readDescriptionBytes(pkt.func_148857_g().getByteArray("C"), 0);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if(cover != null)
			cover.readFromNBT(tag);
	}
	
	@Override
	public IMicroblockCoverSystem getCoverSystem() {
		return cover;
	}
	
	@Override
	public void onMicroblocksChanged() {
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public void convertToCoverContainerBlock() {
		if(getCoverSystem() != null)
			getCoverSystem().convertToContainerBlock();
		else
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
	}
	
}
