package thaumcraft.api.internal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;
import thaumcraft.api.capabilities.IPlayerWarp.EnumWarpType;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.api.research.ResearchCategory;

public class DummyInternalMethodHandler implements IInternalMethodHandler {
	
	@Override
	public boolean completeResearch(EntityPlayer player, String researchkey) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addWarpToPlayer(EntityPlayer player, int amount, EnumWarpType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AspectList getObjectAspects(ItemStack is) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectList generateTags(ItemStack is) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float drainVis(World world, BlockPos pos, float amount, boolean simulate) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float drainFlux(World world, BlockPos pos, float amount, boolean simulate) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addVis(World world, BlockPos pos, float amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addFlux(World world, BlockPos pos, float amount, boolean showEffect) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getTotalAura(World world, BlockPos pos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getVis(World world, BlockPos pos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getFlux(World world, BlockPos pos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAuraBase(World world, BlockPos pos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void registerSeal(ISeal seal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ISeal getSeal(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISealEntity getSealEntity(int dim, SealPos pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addGolemTask(int dim, Task task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean shouldPreserveAura(World world, EntityPlayer player,
			BlockPos pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ItemStack getSealStack(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean doesPlayerHaveRequisites(EntityPlayer player, String researchkey) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addKnowledge(EntityPlayer player, EnumKnowledgeType type, ResearchCategory field, int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean progressResearch(EntityPlayer player, String researchkey) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getActualWarp(EntityPlayer player) {
		// TODO Auto-generated method stub
		return 0;
	}

	

	
}
