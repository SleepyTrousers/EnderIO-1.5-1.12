package mods.immibis.core.api.multipart.util;


import java.util.ArrayList;
import java.util.List;

import mods.immibis.core.api.multipart.IMultipartRenderingBlockMarker;
import mods.immibis.core.api.multipart.IMultipartSystem;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public abstract class BlockMultipartBase extends BlockContainer implements IMultipartRenderingBlockMarker {
	
	protected BlockMultipartBase(Material mat) {
		super(mat);
		
		setHardness(1); // the block hardness must not be zero.
		// if using IPartContainer tiles, the block hardness should be irrelevant, but still can't be zero.
	}
	
	
	
	
	@Override public final boolean isOpaqueCube() {return false;}
	@Override public final boolean renderAsNormalBlock() {return false;}
	@Override public boolean shouldSideBeRendered(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {return true;}
	
	
	
	
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
		ItemStack coverPick = IMultipartSystem.instance.hook_getPickBlock(target, world, x, y, z, player);
		if(coverPick != null)
			return coverPick;
		
		return super.getPickBlock(target, world, x, y, z, player);
	}
	
	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		if(IMultipartSystem.instance.hook_isSideSolid(world, x, y, z, side))
			return true;
		
		return super.isSideSolid(world, x, y, z, side);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
		if(IMultipartSystem.instance.hook_addDestroyEffects(world, x, y, z, meta, effectRenderer))
			return true;
		
		return super.addDestroyEffects(world, x, y, z, meta, effectRenderer);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
		if(IMultipartSystem.instance.hook_addHitEffects(worldObj, target, effectRenderer))
			return true;
		
		return super.addHitEffects(worldObj, target, effectRenderer);
    }
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 src, Vec3 dst) {
		return IMultipartSystem.instance.hook_collisionRayTrace(super.collisionRayTrace(world, x, y, z, src, dst), world, x, y, z, src, dst);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
		// Note: if you're implementing this yourself (instead of extending this class) you don't need to check the
		// return value of hook_addCollisionBlocksToList.
		// The check is to allow BlockMultipartBase to work with both multipart and normal blocks.
		if(!IMultipartSystem.instance.hook_addCollisionBoxesToList(world, x, y, z, mask, list, entity))
			super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
	}
	
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return IMultipartSystem.instance.hook_getDrops(super.getDrops(world, x, y, z, metadata, fortune), world, x, y, z, metadata, fortune);	
	}
}
