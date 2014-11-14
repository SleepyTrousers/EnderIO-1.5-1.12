package appeng.api.networking.security;

import java.util.EnumSet;
import java.util.HashMap;

import appeng.api.config.SecurityPermissions;

/**
 * Implemented on Security Terminal to interface with security cache.
 */
public interface ISecurityProvider
{

	/**
	 * used to represent the security key for the network, should be based on a unique timestamp.
	 * 
	 * @return unique key.
	 */
	long getSecurityKey();

	/**
	 * Push permission data into security cache.
	 * 
	 * @param playerPerms
	 */
	void readPermissions(HashMap<Integer, EnumSet<SecurityPermissions>> playerPerms);

	/**
	 * @return is security on or off?
	 */
	boolean isSecurityEnabled();

	/**
	 * @return player ID for who placed the security provider.
	 */
	int getOwner();

}
