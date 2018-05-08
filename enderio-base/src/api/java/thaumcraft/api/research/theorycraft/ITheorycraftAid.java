package thaumcraft.api.research.theorycraft;

/**
 * See AidBookshelf for an example
 * 
 * @author Azanor
 *
 */
public interface ITheorycraftAid {
	
	/**
	 * The block, dropped item or entity class that will trigger the Aid cards to be added.
	 * A 9x9x3 area around table is checked. 
	 * This method should return an entity class, block or itemstack - itemstack is based on items block will drop when broken.
	 * @return
	 */
	public Object getAidObject();
		
	/**
	 * The cards that are added to the draw rotation. Each time a card 
	 * is draw there is a chance it is one of the Aid cards.
	 * Once drawn the card is removed from the Aid list.
	 * Each card is added once, but you can add the card  
	 * more than once by simply adding it to the array multiple times.
	 * @return
	 */
	public Class<TheorycraftCard>[] getCards();

}
