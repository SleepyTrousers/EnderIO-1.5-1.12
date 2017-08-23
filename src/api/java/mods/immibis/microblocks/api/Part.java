package mods.immibis.microblocks.api;

import java.io.DataOutput;

import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Part {
	public final PartType<?> type;
	public final EnumPosition pos;
	
	public Part(PartType<?> type, EnumPosition pos) {
		this.type = type;
		this.pos = pos;
	}
	
	private static double getMinFromAxisPosition(EnumAxisPosition ap, double size) {
		switch(ap) {
		case Span:
		case Negative:
			return 0;
		case Positive:
			return 1 - size;
		case Centre:
			return (1 - size) / 2;
		default:
			throw new IllegalArgumentException("ap");
		}
	}
	private static double getMaxFromAxisPosition(EnumAxisPosition ap, double size) {
		switch(ap) {
		case Span:
		case Positive:
			return 1;
		case Negative:
			return size;
		case Centre:
			return (1 + size) / 2;
		default:
			throw new IllegalArgumentException("ap");
		}
	}
	
	public AxisAlignedBB getBoundingBoxFromPool() {
		return getBoundingBoxFromPool(pos, type.getSize());
	}
	
	public static AxisAlignedBB getBoundingBoxFromPool(EnumPosition pos, double size) {
		double minx = getMinFromAxisPosition(pos.x, size);
		double miny = getMinFromAxisPosition(pos.y, size);
		double minz = getMinFromAxisPosition(pos.z, size);
		double maxx = getMaxFromAxisPosition(pos.x, size);
		double maxy = getMaxFromAxisPosition(pos.y, size);
		double maxz = getMaxFromAxisPosition(pos.z, size);
		return AxisAlignedBB.getBoundingBox(minx, miny, minz, maxx, maxy, maxz);
	}

	public NBTBase writeToNBT() {
		NBTTagCompound c = new NBTTagCompound();
		c.setInteger("type", type.getID());
		c.setByte("pos", (byte)pos.ordinal());
		return c;
	}

	public static Part readFromNBT(NBTBase tag) {
		NBTTagCompound c = (NBTTagCompound)tag;
		PartType<?> type = MicroblockAPIUtils.getMicroblockSystem().getPartTypeByID(c.getInteger("type"));
		if(type == null)
			return null;
		EnumPosition pos = EnumPosition.values()[c.getByte("pos")];
		Part p = type.createPart(pos, c);
		return p;
	}

	/**
	 * You can override this in subclasses of Part to send extra data to clients in the description packet.
	 * As this data is included in every description packet, try to keep it short.
	 * The data is received by {@link PartType#createPart(EnumPosition, java.io.DataInput)}.
	 */
	public void writeExtraData(DataOutput data) {
	}
	
	/**
	 * Implement this to have block destroy particles.
	 */
	@SideOnly(Side.CLIENT)
	public void addPartDestroyEffects(IMicroblockSupporterTile tile, EffectRenderer er) {
	}

	/**
	 * Implement this to have block hit particles.
	 */
	@SideOnly(Side.CLIENT)
	public void addPartHitEffects(IMicroblockSupporterTile tile, int sideHit, EffectRenderer er) {
	}
}
