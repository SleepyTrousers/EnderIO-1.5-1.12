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

/**
 * 
 * @author Azanor
 *
 * @see IInternalMethodHandler#addKnowledge
 * @see IInternalMethodHandler#progressResearch
 * @see IInternalMethodHandler#completeResearch
 * @see IInternalMethodHandler#doesPlayerHaveRequisites
 * @see IInternalMethodHandler#addWarpToPlayer
 * @see IInternalMethodHandler#getObjectAspects
 * @see IInternalMethodHandler#generateTags
 * @see IInternalMethodHandler#drainVis
 * @see IInternalMethodHandler#drainFlux
 * @see IInternalMethodHandler#addVis
 * @see IInternalMethodHandler#addFlux
 * @see IInternalMethodHandler#getTotalAura
 * @see IInternalMethodHandler#getVis
 * @see IInternalMethodHandler#getFlux
 * @see IInternalMethodHandler#getAuraBase
 * @see IInternalMethodHandler#shouldPreserveAura
 * @see IInternalMethodHandler#registerSeal
 * @see IInternalMethodHandler#getSeal
 * @see IInternalMethodHandler#getSealEntity
 * @see IInternalMethodHandler#addGolemTask 
 * @see IInternalMethodHandler#getSealStack
 */
public interface IInternalMethodHandler {
	
	/**
	 * Add raw knowledge points (not whole knowledges) to the given player.
	 * This method will trigger appropriate gui notifications, etc.
	 * @param player
	 * @param type
	 * @param category
	 * @param amount
	 * @return if the knowledge was added
	 */
	public boolean addKnowledge(EntityPlayer player, EnumKnowledgeType type, ResearchCategory category, int amount);
	
	/**
	 * Progresses research with all the proper bells and whistles (popups, sounds, warp, etc)
	 * If the research is linked to a research entry with stages the player's current stage will be increased 
	 * by 1, or set to 1 if the research was not known before.
	 * @param player
	 * @param researchkey
	 * @return if operation succeeded
	 */
	boolean progressResearch(EntityPlayer player, String researchkey);
	
	/**
	 * Completes research with all the proper bells and whistles (popups, sounds, warp, etc)
	 * This automatically sets all its stages as complete. 
	 * Most of the time you should probably use progressResearch instead.
	 * @param player
	 * @param researchkey
	 * @return if operation succeeded
	 */
	public boolean completeResearch(EntityPlayer player, String researchkey);
	
	/**
	 * @param player 
	 * @param researchkey the key of the research you want to check
	 * @return does the player have all the required knowledge to complete the passed researchkey
	 */
	boolean doesPlayerHaveRequisites(EntityPlayer player, String researchkey);
	
	/**
	 * Adds warp with all the proper bells and whistles (text, sounds, etc)
	 * @param player
	 * @param researchkey
	 * @return
	 */
	public void addWarpToPlayer(EntityPlayer player, int amount, EnumWarpType type);
	
	/**
	 * The total of the players normal + permanent warp. NOT temporary warp.
	 * @param player
	 * @return
	 */
	public int getActualWarp(EntityPlayer player);

	public AspectList getObjectAspects(ItemStack is);
	public AspectList generateTags(ItemStack is);
	
	public float drainVis(World world, BlockPos pos, float amount, boolean simulate);
	public float drainFlux(World world, BlockPos pos, float amount, boolean simulate);	
	public void addVis(World world, BlockPos pos, float amount);
	public void addFlux(World world, BlockPos pos, float amount, boolean showEffect);			
	
	/**
	 * returns the aura and flux in a chunk added together
	 * @param world
	 * @param pos
	 * @return
	 */
	public float getTotalAura(World world, BlockPos pos);
	public float getVis(World world, BlockPos pos);
	public float getFlux(World world, BlockPos pos);
	public int getAuraBase(World world, BlockPos pos);	
	
	public void registerSeal(ISeal seal);
	public ISeal getSeal(String key);
	public ISealEntity getSealEntity(int dim, SealPos pos);
	public void addGolemTask(int dim, Task task);
	public boolean shouldPreserveAura(World world, EntityPlayer player, BlockPos pos);
	public ItemStack getSealStack(String key);

	

	

	

	
	
	
}
