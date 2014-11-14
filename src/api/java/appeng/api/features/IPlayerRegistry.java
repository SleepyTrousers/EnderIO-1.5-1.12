package appeng.api.features;

import net.minecraft.entity.player.EntityPlayer;

import com.mojang.authlib.GameProfile;

/**
 * Maintains a save specific list of userids and username combinations this greatly simplifies storage internally and
 * gives a common place to look up and get IDs for the security framework.
 */
public interface IPlayerRegistry
{

	/**
	 * @param player
	 * @return user id of a username.
	 */
	int getID(GameProfile gameProfile);

	/**
	 * @param player
	 * @return user id of a player entity.
	 */
	int getID(EntityPlayer player);

}
