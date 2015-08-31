package mods.immibis.microblocks.api;

import java.io.DataInput;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface PartType<PartClass extends Part> {
	public EnumPartClass getPartClass();
	public double getSize();
	public int getID();
	
	public String getLocalizedName(ItemStack stack);
	
	public boolean canHarvest(EntityPlayer ply, PartClass part);
	
	/**
	 * Renders the preview of a part of this type.
	 * Previews are shown when the player is holding a microblock stack of this type.
	 * 
	 * The modelview matrix is set so that 0,0,0 and 1,1,1 are the corners of the block to render in.
	 * No other OpenGL state is set - in particular blending is disabled.
	 * 
	 * @param context The RenderGlobal object.
	 * @param pos The microblock position to render the preview at.
	 * @param stack The item stack the player is holding.
	 */
	@SideOnly(Side.CLIENT)
	public void renderPreview(RenderGlobal context, EnumPosition pos, ItemStack stack);
	
	/**
	 * Renders the part as an item.
	 * Rendering should be centered within the box (0,0,0) to (1,1,1).
	 * 
	 * @param context The RenderBlocks object.
	 * @param stack The item stack.
	 */
	@SideOnly(Side.CLIENT)
	void renderPartInv(RenderBlocks context, ItemStack stack);
	
	/**
	 * Returns the item stack dropped when a part with this type is broken.
	 * 
	 * @param part The part being broken.
	 * @param ply The player who broke the part, or null if unknown.
	 */
	public ItemStack getDroppedStack(Part part, EntityPlayer ply);
	
	/**
	 * @see net.minecraft.block.Block#getPlayerRelativeBlockHardness(EntityPlayer, net.minecraft.world.World, int, int, int)
	 */
	public float getPlayerRelativeHardness(Part part, EntityPlayer ply);
	
	/**
	 * Returns the item picked when a player middle-clicks on this part in creative.
	 */
	public ItemStack getPickItem(Part part);
	
	/**
	 * Renders a part with this type in the world.
	 * @param render The RenderBlocks object.
	 * @param p The part to render.
	 * @param x The X coordinate of the block containing the part.
	 * @param y The Y coordinate of the block containing the part.
	 * @param z The Z coordinate of the block containing the part.
	 * @param dontRenderSides An array of booleans corresponding to directions.
	 *        If dontRenderSides[direction] is true, then that side should not be rendered if it touches the edge of the block space.
	 */
	@SideOnly(Side.CLIENT)
	public void renderPartWorld(RenderBlocks render, Part p, int x, int y, int z, boolean[] dontRenderSides);

	/**
	 * Creates a part with this type and deserializes any extra data that you may have written in {@link Part#writeExtraData()}.
	 * Intended to allow mods to use custom subclasses of Part.
	 */
	@SideOnly(Side.CLIENT)
	public Part createPart(EnumPosition pos, DataInput data);
	
	/**
	 * Creates a part with this type.
	 * Intended to allow mods to use custom subclasses of Part.
	 */
	public Part createPart(EnumPosition pos);
	
	/**
	 * Creates a part with this type and deserializes any extra data from NBT.
	 * Intended to allow mods to use custom subclasses of Part.
	 */
	public Part createPart(EnumPosition pos, NBTTagCompound c);
	
	/**
	 * Returns true if this part is completely opaque.
	 */
	public boolean isOpaque();
	
	/**
	 * Returns true if this player can harvest this part.
	 */
	public boolean canHarvest(Part part, EntityPlayer entityPlayer);
}
