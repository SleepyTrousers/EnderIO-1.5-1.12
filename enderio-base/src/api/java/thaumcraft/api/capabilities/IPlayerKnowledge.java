package thaumcraft.api.capabilities;

import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import thaumcraft.api.research.ResearchCategory;

/**
 * 
 * @author Azanor
 * 
 * Unless there are specific tasks you need to perform, you are better off using <b>completeResearch</b> and <b>addKnowledge</b>
 * from the <b>IInternalMethodHandler</b>. It does most of the heavy lifting for you in regards to normal TC research. 
 * These methods are purely for manipulation of the players knowledge list without any checks or crosschecks.
 *
 */
public interface IPlayerKnowledge extends INBTSerializable<NBTTagCompound>
{

    /**
     * Clears all research and knowledge.  
     */
    void clear();
    
    /**
     * @param research The research to query 
     * @return if the research is known and if it is completed or still in progress
     */
    EnumResearchStatus getResearchStatus(@Nonnull String research);
    
    /**
     * @param research The research to query 
     * @return true if the research has its status set as COMPLETE
     */
    boolean isResearchComplete(String research);
    
    /**
     * @param research The research to query.<br>
     * You may also pass in research as 'example@2'. This will check if you know the 
     * research 'example' and if it is at least at stage 2<br>
     * Passing in an empty trying will always return true (used by crafting recipes)
     * @return true if the research has its status <i>not</i> set as UNKNOWN
     */
    boolean isResearchKnown(String res);
    
    enum EnumResearchStatus {
    	UNKNOWN, COMPLETE, IN_PROGRESS;
    }
    
    /**
     * @param research The research to query 
     * @return The stage you have progressed to. 
     * Stages start at 1 and research is considered 'complete' if the set stage exceeds the number of 
     * stages in the research entry.
     * Returns 0 if research is known, but contains no stages,
     * -1 if research is not known at all.
     */
    int getResearchStage(@Nonnull String research);
    

    /**
     * In nearly ALL circumstances <code>IInternalMethodHandler.progressResearch</code> or 
     * <code>IInternalMethodHandler.completeResearch</code> should be used instead of this method.
     * This simply sets the raw data without some of the other things required.
     * @param research The research to add
     * @return Whether the operation was successful
     */
    boolean addResearch(@Nonnull String research);
    
    /**
     * In nearly ALL circumstances <code>IInternalMethodHandler.progressResearch</code> or 
     * <code>IInternalMethodHandler.completeResearch</code> should be used instead of this method.
     * This simply sets the raw data without some of the other things required.
     * @param research The research to modify
     * @param stage The stage to set it to
     * @return Whether the operation was successful
     */
    boolean setResearchStage(@Nonnull String research, int stage);
        

    /**
     * @param research The research to remove 
     * @return Whether the operation was successful
     */
    boolean removeResearch(@Nonnull String research);
    

    /**
     * @return An unmodifiable but live view of the research list.
     */
    @Nonnull Set<String> getResearchList();
    
    
    /**
     * @param research the research to update 
     * @param flag the flag to set. 
     */
    boolean setResearchFlag(@Nonnull String research, @Nonnull EnumResearchFlag flags);
    
    
    /**
     * @param research the research to update
     * @param flag the flag you wish to clear.
     */
    boolean clearResearchFlag(@Nonnull String research, @Nonnull EnumResearchFlag flag);
    
    
    /** 
     * @param research the research for which you want to check the flag status
     * @param flag the flag you wish to check.
     */
    boolean hasResearchFlag(@Nonnull String research, @Nonnull EnumResearchFlag flag);
    
    
    /**
     * In nearly ALL circumstances <code>IInternalMethodHandler.addKnowledge</code> 
     * should be used instead of this method.
     * This simply sets the raw data without some of the other things required.
     * @param type the knowledge type
     * @param category the knowledge category. Null value will map to the 'NONE' type
     * @param amount how much raw knowledge points to add. Can be negative
     * @return if it was successfully added
     */
    boolean addKnowledge(@Nonnull EnumKnowledgeType type, ResearchCategory category, int amount);
      
    /**
     * 
     * @param type the knowledge type
     * @param field the knowledge field. Null value will map to the 'NONE' type
     * @return returns knowledge points divided by progression rounded down. In other words, the full amount of knowledge the player has of that type. 
     */
    int getKnowledge(@Nonnull EnumKnowledgeType type, ResearchCategory category);
    	
    /**
     * 
     * @param type the knowledge type
     * @param field the knowledge field. Null value will map to the 'NONE' type
     * @return returns the raw knowledge points the player possesses
     */
    int getKnowledgeRaw(@Nonnull EnumKnowledgeType type, ResearchCategory category);
	
	/**
     * @param player the player to sync
     */
	void sync(EntityPlayerMP player);
   
	public enum EnumKnowledgeType {
		THEORY(32,true,"T"),
		OBSERVATION(16,true,"O");		
		
		private short progression;
		private boolean hasFields;
		private String abbr;

		private EnumKnowledgeType(int progression, boolean hasFields, String abbr) {
			this.progression = (short) progression;
			this.hasFields = hasFields;
			this.abbr = abbr;
		}
		
		public int getProgression() {
			return progression;
		}
		
		public boolean hasFields() {
			return hasFields;
		}
		
		public String getAbbreviation() {
			return abbr;
		}
		
	}	
		
	public enum EnumResearchFlag {
		/** This research has a new page associated with it. */ 
		PAGE, 
		/** This research is new and has not been examined yet. */ 
		RESEARCH, 
		/** This research should trigger a GUI popup when synced. Flag will be cleared once sync occurs */ 
		POPUP;
	}

	

	
	
}
