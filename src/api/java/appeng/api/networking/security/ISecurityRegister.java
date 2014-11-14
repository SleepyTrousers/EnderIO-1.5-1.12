package appeng.api.networking.security;

import java.util.EnumSet;

import appeng.api.config.SecurityPermissions;

/**
 * Used by vanilla Security Terminal to post biometric data into the security cache.
 */
public interface ISecurityRegister
{

	/**
	 * Submit Permissions into the register.
	 * 
	 * @param PlayerID
	 * @param permissions
	 */
	void addPlayer(int PlayerID, EnumSet<SecurityPermissions> permissions);

}
