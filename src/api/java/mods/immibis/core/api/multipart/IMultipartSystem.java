package mods.immibis.core.api.multipart;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Provides methods allowing blocks to implement microblock ("covering") support.
 * All of the methods in this class do nothing, but are overridden if Immibis Core is installed.
 */
public class IMultipartSystem {
	
	/** Immibis Core sets this to an actual implementation during load time */
	public static @Nonnull IMultipartSystem instance = new IMultipartSystem();

	/** Call this from addCollisionBoxesToList for cover-supporting blocks.
	 * Returns true if the tile entity implements IPartContainer.
	 */
	public boolean hook_addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity entity) {return false;}

	/** Call this from getPickBlock for cover-supporting blocks. If it returns a non-null value, then return that. */
	public ItemStack hook_getPickBlock(MovingObjectPosition trace, World world, int blockX, int blockY, int blockZ, EntityPlayer player) {return null;}
	
	/** Call this from isSideSolid for cover-supporting blocks. If it returns true, then return true. */
	public boolean hook_isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {return false;}
	
	/** Call this from addHitEffects for cover-supporting blocks. If it returns true, then return true without adding any custom effects. */
	@SideOnly(Side.CLIENT)
	public boolean hook_addHitEffects(World world, MovingObjectPosition trace, EffectRenderer effectRenderer) {return false;}
	
	/** Call this from addDestroyEffects for cover-supporting blocks. If it returns true, then return true without adding any custom effects. */
	@SideOnly(Side.CLIENT)
	public boolean hook_addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {return false;}
	
	/** Call this from collisionRayTrace for cover-supporting blocks, before returning. Pass it the result you were going to return (even if that was null). */
	public MovingObjectPosition hook_collisionRayTrace(MovingObjectPosition normalResult, World world, int x, int y, int z, Vec3 src, Vec3 dst) {return normalResult;}

	/** Call this from getDrops for cover-supporting blocks, before returning. Pass it the result you were going to return. */
	public ArrayList<ItemStack> hook_getDrops(List<ItemStack> drops, World world, int x, int y, int z, int metadata, int fortune) {return drops instanceof ArrayList<?> ? (ArrayList<ItemStack>)drops : new ArrayList<ItemStack>(drops);}

	/** Call this from your renderer's renderWorldBlock for cover-supporting blocks, before returning.
	 * If it returns true (indicating it rendered something), then return true.
	 * (Render your custom stuff regardless of what this returns)
	 * 
	 * If there is a tile entity implementing ICoverableTile with a non-null cover system, it will call renderPartContainer on that.
	 * If there is a tile entity implementing IPartContainer, it will call renderPartContainer on that.
	 */
	@SideOnly(Side.CLIENT)
	public boolean renderMultiparts(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {return false;}
}
