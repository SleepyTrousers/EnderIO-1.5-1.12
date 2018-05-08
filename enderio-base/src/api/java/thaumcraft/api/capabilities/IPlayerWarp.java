package thaumcraft.api.capabilities;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 
 * @author Azanor
 * 
 * Unless there are specific tasks you need to perform, you are better off using <b>addWarpToPlayer</b> 
 * from the <b>IInternalMethodHandler</b>. It does most of the heavy lifting for you. 
 *
 */
public interface IPlayerWarp extends INBTSerializable<NBTTagCompound>
{

    /**
     * Clears all warp. 
     */
    void clear();
    

    /**
     * @param type The warp type to query
     * @return the amount of warp the player has
     */
    int get(@Nonnull EnumWarpType type);
    
    /**
     * @param type The type of warp to set
     * @param amount how much to set it to
     */
    void set(@Nonnull EnumWarpType type, int amount);

    /**
     * @param type The type of warp to add
     * @param amount how much to add
     * @return the new total
     */
    int add(@Nonnull EnumWarpType type, int amount);
    
    
    /**
     * @param type The type of warp to reduce
     * @param amount how much to reduce
     * @return the new total
     */
    int reduce(@Nonnull EnumWarpType type, int amount);
    
    public enum EnumWarpType {
    	PERMANENT, NORMAL, TEMPORARY;
    }
    
	
	/**
     * @param player the player to sync
     */
	void sync(EntityPlayerMP player);
	
	/**
     * @return the counter that is used to keep track of warp gains
     */
    int getCounter();
    
    /**
     * @param amount how much to set the counter it to
     */
    void setCounter(int amount);
	
}
