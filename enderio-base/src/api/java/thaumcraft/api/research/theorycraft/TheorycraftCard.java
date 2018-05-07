package thaumcraft.api.research.theorycraft;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * See CardAnalyze for an example
 * 
 * @author Azanor
 *
 */
public abstract class TheorycraftCard { 
	
	private long seed=-1;
	
	/**
	 * A seed value used to determine random attributes associated with the card
	 * @return
	 */
	public long getSeed() {
		if (seed<0) this.setSeed(System.nanoTime());
		return seed;
	}

	/**
	 * This method is run when card is initially created.
	 * @param player
	 * @param data
	 * @return if the card can not be initialized for some reason it will be discarded and a new one created.
	 */
	public boolean initialize(EntityPlayer player, ResearchTableData data) { 
		return true;
	}

	/**
	 * If true this card cannot come up in the normal draw rotation - it only appears if added by a mutator block
	 * @return
	 */
	public boolean isAidOnly() {
		return false;
	}
	
	/**
	 * How much inspiration this card costs to activate. Can be zero. Negative numbers will return inspiration.
	 * @return
	 */
	public abstract int getInspirationCost();
	
	/**
	 * The research category this card is associated with. Can be null if it is not linked to anything.
	 * @return
	 */
	public String getResearchCategory() {
		return null;
	}
	
	/**
	 * Localized name of the card. Will be localized in gui
	 * @return
	 */
	public abstract String getLocalizedName();

	/**
	 * Localized text name of the card. Will be localized in gui
	 * @return
	 */
	public abstract String getLocalizedText();


	/**
	 * The items required to complete this operation. 
	 * If a null is returned no items are required. The array itself can contain null itemstacks - 
	 * that signifies an item is required, but it will display as a ? in the GUI.
	 * You need to take care of consuming and checking for those items yourself in the activate method (see below). 
	 * Non-null items will be handled by automatically.
	 * @return
	 */
	public ItemStack[] getRequiredItems() {
		return null;
	}
	
	/**
	 * Will the listed items be consumed when the card is picked.  
	 * @return
	 */
	public boolean[] getRequiredItemsConsumed() {
		if (getRequiredItems()!=null) {
			boolean[] b = new boolean[getRequiredItems().length];
			Arrays.fill(b, false);
			return b;
		}
		return null;
	}
		
	/**
	 * Perform the cards functionality on the current research table data.
	 * You need to do all the proper checks for items carried and so forth in this method, 
	 * as well as consuming them where needed.
	 * @param player
	 * @param data
	 * @return if the action was successful
	 */
	public abstract boolean activate(EntityPlayer player, ResearchTableData data);
	
	
	/**
	 * Internal use only. This should not be called unless you want to mess things up.
	 * @param seed
	 */
	public void setSeed(long seed) {
		this.seed = Math.abs(seed);
	}
	
	/**
	 * Called when card is saved
	 * @return
	 */
	public NBTTagCompound serialize() {
		NBTTagCompound nbt = new NBTTagCompound();		
		nbt.setLong("seed", seed);
		return nbt;
	}
	
	/**
	 * Called when card is loaded
	 * @param nbt
	 */
	public void deserialize(NBTTagCompound nbt) {	
		if (nbt == null) return;	
		seed = nbt.getLong("seed");		
	}
	
	
}
